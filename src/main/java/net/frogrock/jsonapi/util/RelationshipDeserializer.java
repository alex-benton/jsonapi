package net.frogrock.jsonapi.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import net.frogrock.jsonapi.objects.ManyRelationshipObject;
import net.frogrock.jsonapi.objects.RelationshipObject;
import net.frogrock.jsonapi.objects.SingleRelationshipObject;

/**
 * <p>
 * Custom deserializer to allow Jackson to distinguish between types of
 * RelationshipObjects.
 * </p>
 * 
 * @author alex
 *
 */
public class RelationshipDeserializer extends JsonDeserializer<RelationshipObject> {

    private static final String DATA = "data";

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml
     * .jackson.core.JsonParser,
     * com.fasterxml.jackson.databind.DeserializationContext)
     */
    @Override
    public RelationshipObject deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        ObjectNode root = mapper.readTree(p);

        if (root.has(DATA)) {
            JsonNode data = root.get(DATA);
            if (data.isArray()) {
                return mapper.readValue(root.toString(), ManyRelationshipObject.class);
            }
        }

        return mapper.readValue(root.toString(), SingleRelationshipObject.class);
    }

}
