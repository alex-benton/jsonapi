package net.frogrock.jsonapi;

import java.util.List;

import net.frogrock.jsonapi.Encoder.EncoderOption;
import net.frogrock.jsonapi.config.LinkGenerator;
import net.frogrock.jsonapi.config.MapperOptions;
import net.frogrock.jsonapi.config.ResourceMetadata;
import net.frogrock.jsonapi.objects.BaseObject;
import net.frogrock.jsonapi.objects.MultiDocument;
import net.frogrock.jsonapi.objects.SingleDocument;

/**
 * <p>
 * Encode annotated Java objects into JSON API documents and decode JSON API documents into annotated Java objects.
 * </p>
 * 
 * @author alex
 */
public class JsonApiMapper {
    
    private ResourceMetadata metadata;
    private LinkGenerator generator;
    private MapperOptions options;
    
    /**
     * Construct a default JsonApiMapper instance.
     */
    public JsonApiMapper() {
        options = MapperOptions.defaultConfiguration();
        metadata = ResourceMetadata.defaultConfiguration();
        generator = LinkGenerator.defaultConfiguration();
    }
    
    /**
     * Encode a source object into a JSON API document, suitable for serializing into JSON.
     * 
     * @param source
     *            an annotated Java object
     * @return a JSON API document object
     */
    public BaseObject encode(Object source) {
        Encoder e = new Encoder(source, metadata, generator, options, EncoderOption.INCLUDE_RELATED_RESOURCES);
        return e.encode();
    }
    
    /**
     * Decode a source JSON API document representing a single resource object into a new instance of the destination class.
     * 
     * @param source
     *            a JSON API document
     * @param destination
     *            the destination resource class
     * @return a new instance of the destination class
     */
    public <T> T decode(SingleDocument source, Class<T> destination) {
        Decoder<T> d = new Decoder<>(destination, metadata, generator, options);
        return d.decode(source);
    }
    
    /**
     * Decode a source JSON API document representing multiple resource objects into a List containing new instances of the destination
     * class.
     * 
     * @param source
     *            a JSON API document
     * @param destination
     *            the destination resource class
     * @return a list containing new instances of the destination class
     */
    public <T> List<T> decodeMultiple(MultiDocument source, Class<T> destination) {
        Decoder<T> d = new Decoder<>(destination, metadata, generator, options);
        return d.decodeMultiple(source);
    }
    
}
