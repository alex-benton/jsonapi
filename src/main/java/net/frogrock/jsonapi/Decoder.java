package net.frogrock.jsonapi;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.CaseFormat;

import net.frogrock.jsonapi.config.LinkGenerator;
import net.frogrock.jsonapi.config.MapperOptions;
import net.frogrock.jsonapi.config.ResourceMetadata;
import net.frogrock.jsonapi.config.ResourceMetadata.ClassMetadata;
import net.frogrock.jsonapi.config.ResourceMetadata.RelationshipMetadata;
import net.frogrock.jsonapi.objects.BaseObject;
import net.frogrock.jsonapi.objects.ManyRelationshipObject;
import net.frogrock.jsonapi.objects.MultiDocument;
import net.frogrock.jsonapi.objects.RelationshipObject;
import net.frogrock.jsonapi.objects.ResourceIdentifier;
import net.frogrock.jsonapi.objects.ResourceObject;
import net.frogrock.jsonapi.objects.SingleDocument;
import net.frogrock.jsonapi.objects.SingleRelationshipObject;

/**
 * Helper class. Converts JSON API documents into annotated Java objects. This
 * class shouldn't be used directly - construct a JsonApiMapper instead.
 * 
 * @author alex
 *
 * @param <T>
 *            the class to decode into
 */
final class Decoder<T> {

    private final ResourceMetadata metadata;
    private final LinkGenerator generator;
    private final MapperOptions options;

    private final Class<T> destination;

    private Map<String, Map<Object, Object>> includedMap = new HashMap<>();
    private List<RelationshipResolver> relationships = new ArrayList<>();

    /**
     * <p>
     * Construct a Decoder class with a destination class and the various
     * JsonApiMapper configuration objects.
     * </p>
     * 
     * <p>
     * A new Decoder object should be created for each encoding task.
     * </p>
     * 
     * @param source
     *            the source object
     * @param destination
     *            the destination class
     * @param metadata
     *            the metadata store
     * @param generator
     *            the linkgenerator config
     * @param options
     *            the mapper options
     */
    Decoder(Class<T> destination, ResourceMetadata metadata, LinkGenerator generator, MapperOptions options) {
        this.destination = destination;

        this.metadata = metadata;
        this.generator = generator;
        this.options = options;
    }

    /**
     * <p>
     * Decode a source JSON API document representing a single resource object
     * into a new instance of the destination class.
     * </p>
     * 
     * @param source
     *            a JSON API document
     * @return a new instance of the destination class
     */
    public T decode(SingleDocument source) {
        try {
            relationships = new ArrayList<>();
            includedMap = this.extractIncludedResources(source);

            T result = this.parseResourceObject(source.getData(), metadata.forClass(destination));

            relationships.forEach(RelationshipResolver::resolve);

            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 
     * @param document
     * @return
     * @throws ReflectiveOperationException
     */
    private Map<String, Map<Object, Object>> extractIncludedResources(BaseObject document)
            throws ReflectiveOperationException {
        Map<String, Map<Object, Object>> included = new HashMap<>();
        for (ResourceObject resource : document.getIncluded()) {
            ClassMetadata<?> meta = metadata.forType(resource.getType());
            Object r = this.parseResourceObject(resource, meta);

            if (!included.containsKey(resource.getType())) {
                included.put(resource.getType(), new HashMap<>());
            }
            included.get(resource.getType()).put(resource.getId(), r);
        }
        return included;
    }

    private <C> C parseResourceObject(ResourceObject resource, ClassMetadata<C> data)
            throws ReflectiveOperationException {
        C result = data.self().newInstance();

        Field idField = data.getIdField();
        idField.setAccessible(true);
        idField.set(result, resource.getId());

        this.extractAttributes(result, resource, data);
        this.extractRelationships(result, resource, data);

        return result;
    }

    /**
     * <p>
     * For each attribute field: find the corresponding attribute in the
     * provided resource object and set it in the result object.
     * </p>
     * 
     * @param result
     *            the result object
     * @param resource
     *            the resource object
     * @param data
     *            the class metadata
     * @throws ReflectiveOperationException
     */
    private <C> void extractAttributes(C result, ResourceObject resource, ClassMetadata<C> data)
            throws ReflectiveOperationException {
        for (Field f : data.getAttributeFields()) {
            String fieldName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, f.getName());
            if (resource.getAttributes().containsKey(fieldName)) {
                f.setAccessible(true);
                f.set(result, resource.getAttributes().get(fieldName));
            }
        }
    }

    /**
     * <p>
     * For each relationship field: create a RelationshipResolver object in
     * order to resolve the relationship.
     * </p>
     * 
     * @param result
     *            the result object
     * @param resource
     *            the resource object
     * @param data
     *            the class metadata object
     * @throws ReflectiveOperationException
     */
    private <C> void extractRelationships(C result, ResourceObject resource, ClassMetadata<C> data)
            throws ReflectiveOperationException {

        Map<String, RelationshipMetadata> fields = data.getRelationships();
        for (Entry<String, RelationshipObject> entry : resource.getRelationships().entrySet()) {
            RelationshipMetadata relationshipMetadata = fields.get(entry.getKey());
            this.relationships.add(new RelationshipResolver(result, entry.getValue(), relationshipMetadata));
        }
    }

    /**
     * <p>
     * Decode a source JSON API document representing multiple resource objects
     * into a List containing new instances of the destination class.
     * </p>
     * 
     * @param source
     *            a JSON API document
     * @return a list containing new instances of the destination class
     */
    public List<T> decodeMultiple(MultiDocument source) {
        return null;
    }

    /**
     * <p>
     * Stores object references in order to resolve relationships between
     * resources.
     * </p>
     * 
     * @author alex
     *
     */
    private class RelationshipResolver {

        private Object source;
        private RelationshipObject rel;
        private RelationshipMetadata relationshipMetadata;

        /**
         * <p>
         * Construct a RelationshipResolver, given information about a
         * relationship.
         * </p>
         * 
         * @param source
         *            the source object
         * @param relationship
         *            the relationship object
         * @param relationshipMetadata
         *            metadata about the relationship field
         */
        public RelationshipResolver(Object source, RelationshipObject relationship,
                RelationshipMetadata relationshipMetadata) {
            this.source = source;
            this.rel = relationship;
            this.relationshipMetadata = relationshipMetadata;
        }

        /**
         * Resolve the relationship contained in this object.
         */
        void resolve() {
            try {
                Field f = relationshipMetadata.getField();
                f.setAccessible(true);
                if (relationshipMetadata.hasMany()) {
                    ManyRelationshipObject relationship = (ManyRelationshipObject) rel;
                    List<Object> r = new ArrayList<>(relationship.getData().size());
                    for (ResourceIdentifier identifier : relationship.getData()) {
                        r.add(this.resolveSingle(identifier));
                    }
                    f.set(source, r);
                } else {
                    SingleRelationshipObject relationship = (SingleRelationshipObject) rel;
                    ResourceIdentifier identifier = relationship.getData();
                    f.set(source, this.resolveSingle(identifier));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Resolve a 'single' relationship.
         * 
         * @throws Exception
         */
        private Object resolveSingle(ResourceIdentifier identifier) {
            try {
                if (includedMap.containsKey(identifier.getType())
                        && includedMap.get(identifier.getType()).get(identifier.getId()) != null) {
                    return includedMap.get(identifier.getType()).get(identifier.getId());
                } else {
                    Object stub = relationshipMetadata.getRelatedClass().newInstance();
                    Field id = metadata.forClass(relationshipMetadata.getRelatedClass()).getIdField();
                    id.setAccessible(true);
                    id.set(stub, identifier.getId());
                    return stub;
                }
            } catch (Exception e) {
                // TODO: custom exception classes
                throw new RuntimeException(e);
            }
        }
    }
}
