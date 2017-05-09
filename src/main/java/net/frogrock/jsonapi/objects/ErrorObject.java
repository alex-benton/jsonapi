package net.frogrock.jsonapi.objects;

import java.util.Map;

/**
 * <p>
 * Represents a JSON API "error object," as defined in
 * <a href= "http://jsonapi.org/format/#errors">the JSON API specification</a>.
 * </p>
 * 
 * @author alex
 *
 */
public class ErrorObject {

	// the meta object can contain unstructured key/value pairs.
	private Map<String, ?> meta;

	// the specification also supports "link objects", which can also contain
	// meta information. for now, this is not supported.
	private Map<String, String> links;

	private String status;
	private String code;
	private String title;
	private String detail;

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
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the detail
	 */
	public String getDetail() {
		return detail;
	}

	/**
	 * @param detail
	 *            the detail to set
	 */
	public void setDetail(String detail) {
		this.detail = detail;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ErrorObject [");
		if (meta != null)
			builder.append("meta=").append(meta).append(", ");
		if (links != null)
			builder.append("links=").append(links).append(", ");
		if (status != null)
			builder.append("status=").append(status).append(", ");
		if (code != null)
			builder.append("code=").append(code).append(", ");
		if (title != null)
			builder.append("title=").append(title).append(", ");
		if (detail != null)
			builder.append("detail=").append(detail);
		builder.append("]");
		return builder.toString();
	}

}
