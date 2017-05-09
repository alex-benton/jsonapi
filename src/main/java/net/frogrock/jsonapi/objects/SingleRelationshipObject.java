package net.frogrock.jsonapi.objects;

/**
 * <p>
 * Represents a JSON API "relationship object," as defined in <a href=
 * "http://jsonapi.org/format/#document-resource-object-relationships">the JSON
 * API specification</a>, for a to-one single object relationship.
 * </p>
 * 
 * 
 * @author alex
 *
 */
public class SingleRelationshipObject extends RelationshipObject {

	private ResourceIdentifier data;

	/**
	 * @return the data
	 */
	public ResourceIdentifier getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(ResourceIdentifier data) {
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
		builder.append("SingleRelationshipObject [");
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
