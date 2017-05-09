package net.frogrock.jsonapi.objects;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>
 * Represents a JSON API "resource object," as defined in
 * <a href="http://jsonapi.org/format/#document-resource-objects">the JSON API
 * specification</a>:
 * </p>
 * 
 * <p>
 * <i>“Resource objects” appear in a JSON API document to represent
 * resources.</i>
 * </p>
 * 
 * <p>
 * <i>A resource object MUST contain at least the following top-level
 * members:</i>
 * </p>
 * 
 * <ul>
 * <li><i>id</i></li>
 * <li><i>type</i></li>
 * </ul>
 * 
 * <p>
 * <i>Exception: The id member is not required when the resource object
 * originates at the client and represents a new resource to be created on the
 * server.</i>
 * </p>
 * 
 * <p>
 * <i>In addition, a resource object MAY contain any of these top-level
 * members:</i>
 * </p>
 * 
 * <ul>
 * <li><i>attributes: an attributes object representing some of the resource’s
 * data.</i></li>
 * <li><i>relationships: a relationships object describing relationships between
 * the resource and other JSON API resources.</i></li>
 * <li><i>links: a links object containing links related to the
 * resource.</i></li>
 * <li><i>meta: a meta object containing non-standard meta-information about a
 * resource that can not be represented as an attribute or
 * relationship.</i></li>
 * </ul>
 * 
 * @author alex
 *
 */
public class ResourceObject extends ResourceIdentifier {

	// the specification also supports "link objects", which can also contain
	// meta information. for now, this is not supported.
	private Map<String, String> links;

	private Map<String, Object> attributes;
	private Map<String, RelationshipObject> relationships;

	public ResourceObject() {
		attributes = new LinkedHashMap<>();
		relationships = new LinkedHashMap<>();
	}

	/**
	 * <p>
	 * Add an attribute to this ResourceObject.
	 * </p>
	 * 
	 * @param key
	 *            the attribute key
	 * @param value
	 *            the attribute value
	 */
	public void addAttribute(String key, Object value) {
		attributes.put(key, value);
	}
	
	public void addRelationship(String key, RelationshipObject value) {
		relationships.put(key, value);
	}

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
	 * @return the attributes
	 */
	public Map<String, ?> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes
	 *            the attributes to set
	 */
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	/**
	 * @return the relationships
	 */
	public Map<String, RelationshipObject> getRelationships() {
		return relationships;
	}

	/**
	 * @param relationships
	 *            the relationships to set
	 */
	public void setRelationships(Map<String, RelationshipObject> relationships) {
		this.relationships = relationships;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ResourceObject [");
		if (links != null)
			builder.append("links=").append(links).append(", ");
		if (attributes != null)
			builder.append("attributes=").append(attributes).append(", ");
		if (relationships != null)
			builder.append("relationships=").append(relationships);
		builder.append("]");
		return builder.toString();
	}

}
