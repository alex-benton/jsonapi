package net.frogrock.jsonapi.objects;

/**
 * Object that represents a JSON API document for a single document resource,
 * according to the specification defined
 * <a href="http://jsonapi.org/format/">here</a>.
 * 
 * @author abenton
 *
 */
public class SingleDocument extends BaseObject {

    // the single data resource
    private ResourceObject data;

    /**
     * @return the data
     */
    public ResourceObject getData() {
        return data;
    }

    /**
     * @param data
     *            the data to set
     */
    public void setData(ResourceObject data) {
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
        builder.append("SingleDocument [");
        if (data != null)
            builder.append("data=").append(data).append(", ");
        if (getMeta() != null)
            builder.append("getMeta()=").append(getMeta()).append(", ");
        if (getLinks() != null)
            builder.append("getLinks()=").append(getLinks()).append(", ");
        if (getErrors() != null)
            builder.append("getErrors()=").append(getErrors()).append(", ");
        if (getIncluded() != null)
            builder.append("getIncluded()=").append(getIncluded());
        builder.append("]");
        return builder.toString();
    }

}
