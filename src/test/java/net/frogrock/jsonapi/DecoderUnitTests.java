package net.frogrock.jsonapi;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map.Entry;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CaseFormat;

import net.frogrock.jsonapi.config.LinkGenerator;
import net.frogrock.jsonapi.config.MapperOptions;
import net.frogrock.jsonapi.config.ResourceMetadata;
import net.frogrock.jsonapi.config.ResourceMetadata.ClassMetadata;
import net.frogrock.jsonapi.config.ResourceMetadata.RelationshipMetadata;
import net.frogrock.jsonapi.objects.ResourceObject;
import net.frogrock.jsonapi.objects.SingleDocument;
import net.frogrock.jsonapi.sample.Article;

/**
 * <p>
 * Test the basic decoding functionality.
 * </p>
 * 
 * @author alex
 *
 */
public class DecoderUnitTests {

    private final ObjectMapper map = new ObjectMapper();
    private final ResourceMetadata metadata = ResourceMetadata.defaultConfiguration();

    /**
     * Test - decode a single JSON object into a Java Object.
     * 
     * @throws Exception
     */
    @Test
    public void testDecodeSingle() throws Exception {
        SingleDocument document = map.readValue(new File("src/test/resources/test-full-document.json"), SingleDocument.class);

        Decoder<Article> decoder = new Decoder<Article>(Article.class, metadata, LinkGenerator.defaultConfiguration(),
                MapperOptions.defaultConfiguration());

        Article result = decoder.decode(document);

        this.assertSame(document, result);
    }

    /**
     * Evaluate whether the 'result' Article contains the same data as the
     * source document.
     * 
     * @param source
     * @param result
     */
    private void assertSame(SingleDocument source, Article result) throws Exception {
        ResourceObject data = source.getData();
        ClassMetadata<Article> meta = metadata.forClass(Article.class);

        assertEquals("Source id must equal result id.", data.getId(), result.getId());

        // validate attributes
        for (Field field : meta.getAttributeFields()) {
            String fieldName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, field.getName());
            field.setAccessible(true);
            if (data.getAttributes().containsKey(fieldName)) {
                assertEquals("JSON attribute: " + fieldName + " must equal Java field: " + field.getName(),
                        data.getAttributes().get(fieldName), field.get(result));
            } else {
                field.setAccessible(true);
                assertNull("Java field: " + field.getName() + " must be null.", field.get(result));
            }
        }
        
        // validate relationships
        for (Entry<String, RelationshipMetadata> relation : meta.getRelationships().entrySet()) {
            RelationshipMetadata relationship = relation.getValue();
            Field field = relationship.getField();
            String fieldName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, relation.getKey());

            field.setAccessible(true);
            if (data.getRelationships().containsKey(fieldName)) {
                
            } else {
                assertNull("Java field: " + field.getName() + " must be null.", field.get(result));
            }
        }
        
    }
}
