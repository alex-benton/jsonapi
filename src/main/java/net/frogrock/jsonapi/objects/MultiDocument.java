package net.frogrock.jsonapi.objects;

import java.util.List;

/**
 * Object that represents a JSON API document for multiple resources, according
 * to the specification defined <a href="http://jsonapi.org/format/">here</a>.
 * 
 * @author abenton
 *
 */
public class MultiDocument extends BaseObject {

	// the data resources
	private List<ResourceObject> data;

	/**
	 * @return the data
	 */
	public List<ResourceObject> getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(List<ResourceObject> data) {
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
		builder.append("MultiDocument [data=").append(data).append("]");
		return builder.toString();
	}

}
