package net.frogrock.jsonapi.objects;

import java.util.List;
import java.util.Map;

/**
 * Object that represents a JSON API document, according to the specification
 * defined <a href="http://jsonapi.org/format/">here</a>.
 * 
 * <p>
 * This 'base' object includes the fields that shared between multiple JSON API
 * object types.
 * </p>
 * 
 * @author abenton
 *
 */
public class BaseObject {

	// the meta object can contain unstructured key/value pairs.
	private Map<String, ?> meta;

	// the specification also supports "link objects", which can also contain
	// meta information. for now, this is not supported.
	private Map<String, String> links;

	// errors go here.
	private List<ErrorObject> errors;

	// included resources (for data responses)
	private List<ResourceObject> included;

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
	 * @return the errors
	 */
	public List<ErrorObject> getErrors() {
		return errors;
	}

	/**
	 * @param errors
	 *            the errors to set
	 */
	public void setErrors(List<ErrorObject> errors) {
		this.errors = errors;
	}

	/**
	 * @return the included
	 */
	public List<ResourceObject> getIncluded() {
		return included;
	}

	/**
	 * @param included
	 *            the included to set
	 */
	public void setIncluded(List<ResourceObject> included) {
		this.included = included;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BaseObject [");
		if (meta != null)
			builder.append("meta=").append(meta).append(", ");
		if (links != null)
			builder.append("links=").append(links).append(", ");
		if (errors != null)
			builder.append("errors=").append(errors).append(", ");
		if (included != null)
			builder.append("included=").append(included);
		builder.append("]");
		return builder.toString();
	}

}
