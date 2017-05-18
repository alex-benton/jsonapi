package net.frogrock.jsonapi.config;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;

import net.frogrock.jsonapi.annotations.Attribute;
import net.frogrock.jsonapi.annotations.Id;
import net.frogrock.jsonapi.annotations.Relationship;
import net.frogrock.jsonapi.annotations.ResourceType;

/**
 * <p>
 * Contains resource maps and metadata that describe fields for objects that can
 * be serialized/deserialized into JSON API data structures.
 * </p>
 * 
 * @author abenton
 */
public class ResourceMetadata {

    // classes with this annotation will be managed by this object
    private static final Class<ResourceType> RESOURCE_TYPE = ResourceType.class;

    /**
     * <p>
     * Construct a ResourceMetadata from the provided package prefix.
     * </p>
     * 
     * <p>
     * Finds all resources (classes tagged with the @ResourceType annotation)
     * located in all packages that match the provided prefix string and indexes
     * them.
     * </p>
     * 
     * @param prefix
     *            the package prefix where resource classes are located, ex:
     *            "net.frogrock.jsonapi"
     * @return an initialized ResourceMetadata, containing metadata about the
     *         resource classes/annotated fields.
     */
    public static ResourceMetadata fromPackage(String prefix) {
        Set<Class<?>> resources = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage(prefix))
                .addScanners(new TypeAnnotationsScanner()).filterInputsBy(new FilterBuilder().includePackage(prefix)))
                        .getTypesAnnotatedWith(RESOURCE_TYPE);
        return new ResourceMetadata(resources);
    }

    /**
     * <p>
     * Construct a default ResourceMetadata using the entire project.
     * </p>
     * 
     * <p>
     * Finds all resources (classes tagged with the @ResourceType annotation)
     * located in all packages and indexes them.
     * </p>
     * 
     * @return an initialized ResourceMetadata, containing metadata about the
     *         resource classes/annotated fields.
     */
    public static ResourceMetadata defaultConfiguration() {
        return new ResourceMetadata(new Reflections().getTypesAnnotatedWith(RESOURCE_TYPE));
    }

    private BiMap<String, Class<?>> resourceMap;
    private Map<String, ClassMetadata<?>> resourceMetadataMap;

    /**
     * <p>
     * Private constructor.
     * </p>
     * 
     * <p>
     * Accepts the set of classes that will be managed by this ResourceMetadata
     * object.
     * </p>
     * 
     * @param resources
     *            the set of classes
     */
    private ResourceMetadata(Set<Class<?>> resources) {
        this.initialize(resources);
    }

    /**
     * <p>
     * Initialize the metadata indexes.
     * </p>
     * 
     * @param resources
     *            the set of classes
     */
    private void initialize(Set<Class<?>> resources) {
        ImmutableBiMap.Builder<String, Class<?>> resourceMapBuilder = ImmutableBiMap.<String, Class<?>>builder();
        ImmutableMap.Builder<String, ClassMetadata<?>> metadataMapBuilder = ImmutableMap
                .<String, ClassMetadata<?>>builder();

        for (Class<?> resourceClass : resources) {
            String type = resourceClass.getAnnotation(RESOURCE_TYPE).value();
            resourceMapBuilder.put(type, resourceClass);
            metadataMapBuilder.put(type, new ClassMetadata<>(resourceClass));
        }

        resourceMap = resourceMapBuilder.build();
        resourceMetadataMap = metadataMapBuilder.build();
    }

    /**
     * <p>
     * Get the ClassMetadata object for the provided class.
     * </p>
     * 
     * @param c
     * @return a ClassMetadata object
     */
    @SuppressWarnings("unchecked")
    public <T> ClassMetadata<T> forClass(Class<T> c) {
        return (ClassMetadata<T>) forType(resourceMap.inverse().get(c));
    }

    /**
     * <p>
     * Get the ClassMetadata object for the provided type string.
     * </p>
     * 
     * @param type
     *            the type
     * @return a ClassMetadata object
     */
    public ClassMetadata<?> forType(String type) {
        return resourceMetadataMap.get(type);
    }

    /**
     * <p>
     * Get the 'resourceType' value for the provided class.
     * </p>
     * 
     * @param c
     *            the class
     * @return the 'type' of the class
     */
    public String getType(Class<?> c) {
        return resourceMap.inverse().get(c);
    }

    /**
     * <p>
     * Contains helpful information.
     * </p>
     * 
     * @author abenton
     */
    public static class ClassMetadata<T> {

        // the class that this metadata object is for
        private Class<T> self;

        // the string resource type value
        private String type;

        // the fields for this class with the tag @Id
        // there should probably only be one of these, but who knows.
        private Field idField;

        // the fields for this class with the tag @Attribute
        private Set<Field> attributeFields;

        // the fields for this class with the tag @Relationship, mapped by the
        // name of the field
        private Map<String, RelationshipMetadata> relationships;

        /**
         * Construct a ClassMetadata object for a given class.
         * 
         * @param resourceClass
         */
        @SuppressWarnings("unchecked")
        private ClassMetadata(Class<T> resourceClass) {

            self = resourceClass;
            type = resourceClass.getAnnotation(ResourceType.class).value();

            attributeFields = ReflectionUtils.getAllFields(resourceClass, (ref) -> {
                return ref.getAnnotation(Attribute.class) != null;
            });

            Set<Field> idFields = ReflectionUtils.getAllFields(resourceClass, (ref) -> {
                return ref.getAnnotation(Id.class) != null;
            });

            if (idFields.size() == 0) {
                // throw an exception.
            } else if (idFields.size() > 1) {
                // throw an exception.
            } else {
                idField = (Field) idFields.toArray()[0];
            }

            relationships = ReflectionUtils.getAllFields(resourceClass, (ref) -> {
                return ref.getAnnotation(Relationship.class) != null;
            }).stream().collect(HashMap::new, (map, field) -> {
                map.put(field.getName(), new RelationshipMetadata(field));
            }, HashMap::putAll);

        }

        /**
         * <p>
         * Get the class that is supported by this ClassMetadata object.
         * </p>
         * 
         * @return the class
         */
        public Class<T> self() {
            return self;
        }

        /**
         * <p>
         * Return the resource type represented by this ClassMetadata object.
         * This will be the same value that's defined as the @ResourceType
         * annotation for the class.
         * </p>
         * 
         * @return
         */
        public String type() {
            return type;
        }

        /**
         * <p>
         * Get the unique id field for this class.
         * </p>
         * 
         * @return the set
         */
        public Field getIdField() {
            return idField;
        }

        /**
         * <p>
         * Get a set containing the attribute fields for this class.
         * </p>
         * 
         * @return the set
         */
        public Set<Field> getAttributeFields() {
            return attributeFields;
        }

        /**
         * <p>
         * Get a map containing relationship data for this class (mapped to
         * field name).
         * </p>
         * 
         * @return the set
         */
        public Map<String, RelationshipMetadata> getRelationships() {
            return relationships;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("ClassMetadata [");
            if (self != null)
                builder.append("self=").append(self).append(", ");
            if (idField != null)
                builder.append("idField=").append(idField).append(", ");
            if (attributeFields != null)
                builder.append("attributeFields=").append(attributeFields).append(", ");
            if (relationships != null)
                builder.append("relationships=").append(relationships);
            builder.append("]");
            return builder.toString();
        }

    }

    /**
     * <p>
     * Contains metadata about resource relationships.
     * </p>
     * 
     * @author alex
     */
    public static class RelationshipMetadata {

        private final Field field;
        private final Class<?> relatedClass;
        private final boolean hasMany;

        private RelationshipMetadata(Field field) {
            this.field = field;

            if (Collection.class.isAssignableFrom(field.getType())) {

                this.hasMany = true;
                Type[] types = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();

                if (types.length != 1) {
                    // TODO: create custom exceptions
                    throw new RuntimeException("Couldn't decode relationship field: " + field.toGenericString());
                }

                Class<?> found = (Class<?>) types[0];

                if (!found.isAnnotationPresent(RESOURCE_TYPE)) {
                    throw new RuntimeException("Couldn't decode relationship field: " + field.toGenericString());
                }

                this.relatedClass = found;
            } else {
                this.hasMany = false;
                this.relatedClass = field.getType();
            }
        }

        public Field getField() {
            return field;
        }

        public Class<?> getRelatedClass() {
            return relatedClass;
        }

        public boolean hasMany() {
            return hasMany;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("RelationshipMetadata [");
            if (field != null)
                builder.append("field=").append(field).append(", ");
            if (relatedClass != null)
                builder.append("relatedClass=").append(relatedClass).append(", ");
            builder.append("hasMany=").append(hasMany).append("]");
            return builder.toString();
        }

    }

}
