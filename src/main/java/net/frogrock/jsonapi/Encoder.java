package net.frogrock.jsonapi;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableSet;

import net.frogrock.jsonapi.config.LinkGenerator;
import net.frogrock.jsonapi.config.MapperOptions;
import net.frogrock.jsonapi.config.ResourceMetadata;
import net.frogrock.jsonapi.config.ResourceMetadata.ClassMetadata;
import net.frogrock.jsonapi.config.ResourceMetadata.RelationshipMetadata;
import net.frogrock.jsonapi.objects.BaseObject;
import net.frogrock.jsonapi.objects.ManyRelationshipObject;
import net.frogrock.jsonapi.objects.MultiDocument;
import net.frogrock.jsonapi.objects.ResourceIdentifier;
import net.frogrock.jsonapi.objects.ResourceObject;
import net.frogrock.jsonapi.objects.SingleDocument;
import net.frogrock.jsonapi.objects.SingleRelationshipObject;

/**
 * <p>
 * Helper class. Converts annotated Java objects into JSON API documents. This
 * class shouldn't be used directly - construct a JsonApiMapper instead.
 * </p>
 * 
 * <p>
 * A new Encoder object should be created for each encoding task.
 * </p>
 * 
 * @author alex
 */
final class Encoder {

    static enum EncoderOption {
        INCLUDE_RELATED_RESOURCES
    }

    private final ResourceMetadata metadata;
    private final LinkGenerator generator;
    private final MapperOptions mapperOptions;
    private final Set<EncoderOption> options;
    private final Object source;

    private List<ResourceObject> included = new ArrayList<>();
    private Map<String, Map<Object, ResourceObject>> includedMap = new HashMap<>();

    /**
     * <p>
     * Construct an Encoder class with a source object and the various
     * JsonApiMapper configuration objects.
     * </p>
     * 
     * <p>
     * A new Encoder object should be created for each encoding task.
     * </p>
     * 
     * @param source
     *            the source object
     * @param metadata
     *            the metadata store
     * @param generator
     *            the linkgenerator config
     * @param mapperOptions
     *            the mapper options
     * @param encoderOptions
     *            the encoder options
     */
    Encoder(Object source, ResourceMetadata metadata, LinkGenerator generator, MapperOptions mapperOptions,
            EncoderOption... encoderOptions) {
        this.source = source;

        this.metadata = metadata;
        this.generator = generator;
        this.mapperOptions = mapperOptions;
        this.options = ImmutableSet.copyOf(encoderOptions);
    }

    /**
     * <p>
     * Encode the source object contained in this encoder into a JSON API
     * document, suitable for serializing into JSON.
     * </p>
     * 
     * <p>
     * A new Encoder object should be created for each encoding task.
     * </p>
     * 
     * @return a JSON API document object
     */
    public BaseObject encode() {

        // reset the class-level state.
        included = new ArrayList<>();
        includedMap = new HashMap<>();

        try {
            // we can encode a single resource object or a collection of
            // resource objects.
            if (Collection.class.isAssignableFrom(source.getClass())) {
                return encodeMulti((Collection<?>) source);
            } else {
                return encodeSingle(source);
            }
        } catch (IllegalAccessException e) {
            // TODO: custom exceptions
            throw new RuntimeException("Exception while encoding object.", e);
        }
    }

    /**
     * <p>
     * Encode a source object (representing a single resource object) into a
     * JSON API document.
     * </p>
     * 
     * @param source
     *            the source object.
     * @return the JSON API document
     * @throws IllegalAccessException
     */
    private SingleDocument encodeSingle(Object source) throws IllegalAccessException {
        SingleDocument result = new SingleDocument();

        // parse the provided source object into a JSON API resource object.
        // if any relationships exist in the source object, they'll be added to
        // the 'included' list.
        ResourceObject resource = this.parseResource(source);

        result.setData(resource);

        if (options.contains(EncoderOption.INCLUDE_RELATED_RESOURCES)) {
            result.setIncluded(included);
        }

        return result;
    }

    /**
     * <p>
     * Encode the collection of source objects into a single JSON API document
     * (containing a list of resources).
     * </p>
     * 
     * @param objects
     *            the source objects.
     * @return the JSON API document
     * @throws IllegalAccessException
     */
    private MultiDocument encodeMulti(Collection<?> objects) throws IllegalAccessException {
        MultiDocument result = new MultiDocument();
        List<ResourceObject> data = new ArrayList<>(objects.size());

        // for each source object, parse it into a ResourceObject and add it to
        // the data list. if any relationships exist in the source objects,
        // they'll be added to the 'included' list.
        for (Object obj : objects) {
            data.add(this.parseResource(obj));
        }

        result.setData(data);
        result.setIncluded(included);

        return result;
    }

    /**
     * <p>
     * Parse an annotated Java object into a JSON API resource object.
     * </p>
     * 
     * <p>
     * A resource object contains an id, type, attributes, and relationships.
     * </p>
     * 
     * <p>
     * For each relationship included in the Java object (annotated with
     * the @Relationship annotation), add a corresponding ResourceObject to the
     * 'included' list (if it doesn't already exist).
     * </p>
     * 
     * <p>
     * Returns the parsed ResourceObject.
     * </p>
     * 
     * @param obj
     *            the object to parse
     * @return the created JSON API resource object
     * @throws IllegalAccessException
     */
    private ResourceObject parseResource(Object obj) throws IllegalAccessException {
        ClassMetadata<?> meta = metadata.forClass(obj.getClass());
        ResourceObject resource = new ResourceObject();
        Field idField = meta.getIdField();
        idField.setAccessible(true);

        resource.setId(idField.get(obj));
        resource.setType(meta.type());

        this.extractAttributes(obj, resource);
        this.extractRelationships(obj, resource);

        return resource;
    }

    /**
     * <p>
     * Extract each attribute field from the provided source object. Creates
     * corresponding attributes in the destination ResourceObject.
     * </p>
     * 
     * @param src
     *            the source object
     * @param destination
     *            the destination object
     * @throws IllegalAccessException
     */
    private void extractAttributes(Object src, ResourceObject destination) throws IllegalAccessException {
        for (Field attributeField : metadata.forClass(src.getClass()).getAttributeFields()) {
            String attributeName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, attributeField.getName());
            attributeField.setAccessible(true);
            destination.addAttribute(attributeName, attributeField.get(src));
        }
    }

    /**
     * <p>
     * Extract each relationship field from the provided source object. Creates
     * corresponding relationship objects in the destination ResourceObject.
     * </p>
     * 
     * <p>
     * For each found relationship, if the full related entity is found in the
     * source object, creates a resource object for the related entity and adds
     * it to the list of 'included' elements.
     * </p>
     * 
     * @param src
     *            the source object
     * @param destination
     *            the destination object
     * @throws IllegalAccessException
     */
    private void extractRelationships(Object src, ResourceObject destination) throws IllegalAccessException {
        for (Entry<String, RelationshipMetadata> entry : metadata.forClass(src.getClass()).getRelationships()
                .entrySet()) {
            RelationshipMetadata relationship = entry.getValue();

            String fieldName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, entry.getKey());
            relationship.getField().setAccessible(true);

            if (relationship.hasMany()) {
                Collection<?> relatedObjects = (Collection<?>) relationship.getField().get(src);

                if (relatedObjects != null && !relatedObjects.isEmpty()) {
                    ManyRelationshipObject manyRelationship = new ManyRelationshipObject();

                    for (Object related : relatedObjects) {
                        ResourceIdentifier identifier = this.parseResourceIdentifier(related);
                        manyRelationship.add(identifier);

                        // if the related object has attribute fields, we can
                        // generate an 'included' resource object from it
                        if (options.contains(EncoderOption.INCLUDE_RELATED_RESOURCES)
                                && this.hasAttributeFields(related, metadata)) {
                            this.addIncludedRelationship(related, identifier);
                        }
                    }

                    destination.addRelationship(fieldName, manyRelationship);
                }

            } else {
                Object related = relationship.getField().get(src);
                ResourceIdentifier identifier = this.parseResourceIdentifier(related);

                SingleRelationshipObject singleRelationship = new SingleRelationshipObject();
                singleRelationship.setData(identifier);

                destination.addRelationship(fieldName, singleRelationship);

                // if the related object has attribute fields, we can generate
                // an 'included' resource object from it
                if (options.contains(EncoderOption.INCLUDE_RELATED_RESOURCES)
                        && this.hasAttributeFields(related, metadata)) {
                    this.addIncludedRelationship(related, identifier);
                }

            }
        }
    }

    /**
     * <p>
     * Add a related object to the list of 'included' resource objects.
     * </p>
     * 
     * @param related
     *            the related object
     * @param identifier
     *            the resource identifier for the related object
     * @throws IllegalAccessException
     */
    private void addIncludedRelationship(Object related, ResourceIdentifier identifier) throws IllegalAccessException {
        if (!includedMap.containsKey(identifier.getType())) {
            includedMap.put(identifier.getType(), new HashMap<>());
        }

        if (!includedMap.get(identifier.getType()).containsKey(identifier.getId())) {
            ResourceObject relatedResource = this.parseResource(related);
            includedMap.get(identifier.getType()).put(identifier.getId(), relatedResource);
            included.add(relatedResource);
        }
    }

    /**
     * <p>
     * Parse the provided resource object into a 'ResourceIdentifier' object,
     * used when constructing relationships in JSON API documents.
     * </p>
     * 
     * @param src
     *            the source resource object
     * @return a 'ResourceIdentifier' for that object
     * @throws IllegalAccessException
     */
    private ResourceIdentifier parseResourceIdentifier(Object src) throws IllegalAccessException {
        ResourceIdentifier resource = new ResourceIdentifier();
        ClassMetadata<?> meta = metadata.forClass(src.getClass());
        Field idField = meta.getIdField();
        idField.setAccessible(true);

        resource.setId(idField.get(src));
        resource.setType(meta.type());

        return resource;
    }

    /**
     * <p>
     * Does the provided resource have non-null attribute fields?
     * </p>
     * 
     * <p>
     * Used to determine if we can generate an 'included' resource from this
     * resource object.
     * </p>
     * 
     * @param resource
     *            the resource object
     * @param metadata
     *            a link to the metadata store
     * @return true if the resource object is 'identifier' only
     * @throws IllegalAccessException
     *             if the parameter isn't actually a resource object. if the id
     *             parameter is null TODO: custom exception
     */
    private boolean hasAttributeFields(Object resource, ResourceMetadata metadata) throws IllegalAccessException {
        ClassMetadata<?> meta = metadata.forClass(resource.getClass());

        if (meta == null) {
            // TODO: custom exception.
            throw new IllegalAccessException("Couldn't get metadata for class: " + resource.getClass());
        }

        if (meta.getIdField() == null) {
            // TODO: custom exception. also, move this check to on metadata
            // creation.
            throw new IllegalAccessException("No id field defined for class: " + resource.getClass());
        }

        meta.getIdField().setAccessible(true);
        if (meta.getIdField().get(resource) == null) {
            // TODO: custom exception.
            throw new IllegalAccessException("Id field for class: " + resource.getClass() + " was null.");
        }

        if (!meta.getAttributeFields().isEmpty()) {
            for (Field attributeField : meta.getAttributeFields()) {
                attributeField.setAccessible(true);
                if (attributeField.get(resource) != null) {
                    return true;
                }
            }
        }

        return false;
    }

}
