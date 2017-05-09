package net.frogrock.jsonapi.objects;

import java.util.Map;

/**
 * <p>
 * Represents a JSON API "resource identifier" object.
 * </p>
 * 
 * <p>
 * Per
 * <a href="http://jsonapi.org/format/#document-resource-identifier-objects">the
 * JSON API specification</a>:
 * </p>
 * 
 * <p>
 * <i>A “resource identifier object” is an object that identifies an individual
 * resource.</i>
 * <p>
 * 
 * <p>
 * <i>A “resource identifier object” MUST contain type and id members.</i>
 * <p>
 * 
 * <p>
 * <i>A “resource identifier object” MAY also include a meta member, whose value
 * is a meta object that contains non-standard meta-information.</i>
 * <p>
 * 
 * @author alex
 *
 */
public class ResourceIdentifier {

	private Object id;
	private String type;

	// the meta object can contain unstructured key/value pairs.
	private Map<String, ?> meta;

	/**
	 * @return the id
	 */
	public Object getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Object id) {
		this.id = id;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
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
		builder.append("ResourceIdentifier [");
		if (id != null)
			builder.append("id=").append(id).append(", ");
		if (type != null)
			builder.append("type=").append(type).append(", ");
		if (meta != null)
			builder.append("meta=").append(meta);
		builder.append("]");
		return builder.toString();
	}
}
