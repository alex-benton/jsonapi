package net.frogrock.jsonapi.objects;

import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import net.frogrock.jsonapi.util.RelationshipDeserializer;

/**
 *
 * <p>
 * Represents a JSON API "relationship object," as defined in <a href=
 * "http://jsonapi.org/format/#document-resource-object-relationships">the JSON
 * API specification</a>:
 * </p>
 * 
 * <i>
 * <p>
 * A “relationship object” MUST contain at least one of the following:
 * </p>
 * 
 * <ul>
 * <li>links: a links object containing at least one of the following:
 * <ul>
 * <li>self: a link for the relationship itself (a “relationship link”)</li>
 * <li>related: a related resource link</li>
 * </ul>
 * </li>
 * <li>data: resource linkage</li>
 * <li>meta: a meta object that contains non-standard meta-information about the
 * relationship.</li>
 * </ul>
 * </i>
 * 
 * @author abenton
 *
 */
@JsonDeserialize(using = RelationshipDeserializer.class)
public abstract class RelationshipObject {

    // the specification also supports "link objects", which can also contain
    // meta information. for now, this is not supported.
    private Map<String, String> links;

    // the meta object can contain unstructured key/value pairs.
    private Map<String, ?> meta;

    /**
     * @return the links
     */
    public Map<String, String> getLinks() {
        return links;
    }

    /**
     * @param links
     *            the links to set
     */
    public void setLinks(Map<String, String> links) {
        this.links = links;
    }

    /**
     * @return the meta
     */
    public Map<String, ?> getMeta() {
        return meta;
    }

    /**
     * @param meta
     *            the meta to set
     */
    public void setMeta(Map<String, ?> meta) {
        this.meta = meta;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RelationshipObject [");
        if (links != null)
            builder.append("links=").append(links).append(", ");
        if (meta != null)
            builder.append("meta=").append(meta).append(", ");
        builder.append("]");
        return builder.toString();
    }

}
