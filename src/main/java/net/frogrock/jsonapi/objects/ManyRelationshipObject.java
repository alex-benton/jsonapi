package net.frogrock.jsonapi.objects;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * <p>
 * Represents a JSON API "relationship object," as defined in <a href=
 * "http://jsonapi.org/format/#document-resource-object-relationships">the JSON
 * API specification</a>, for a to-many object relationship.
 * </p>
 * 
 * 
 * @author alex
 *
 */
@JsonDeserialize(as = ManyRelationshipObject.class)
public class ManyRelationshipObject extends RelationshipObject {

    /**
     * <p>
     * Construct a new, empty ManyRelationshipObject.
     * </p>
     */
    public ManyRelationshipObject() {
        data = new ArrayList<ResourceIdentifier>();
    }

    /**
     * <p>
     * Add a relationship to this ManyRelationshipObject.
     * </p>
     * 
     * @param relationship
     */
    public void add(ResourceIdentifier relationship) {
        data.add(relationship);
    }

    private List<ResourceIdentifier> data;

    /**
     * @return the data
     */
    public List<ResourceIdentifier> getData() {
        return data;
    }

    /**
     * @param data
     *            the data to set
     */
    public void setData(List<ResourceIdentifier> data) {
        this.data = data;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ManyRelationshipObject [");
        if (data != null)
            builder.append("data=").append(data).append(", ");
        if (getLinks() != null)
            builder.append("getLinks()=").append(getLinks()).append(", ");
        if (getMeta() != null)
            builder.append("getMeta()=").append(getMeta());
        builder.append("]");
        return builder.toString();
    }
}
