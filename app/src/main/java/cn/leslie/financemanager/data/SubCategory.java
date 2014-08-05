package cn.leslie.financemanager.data;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Data object that describes a sub category.
 */
public class SubCategory extends Model implements Comparable<SubCategory> {

    @JsonProperty("name")
    private String mName;

    @JsonProperty("order")
    private int mOrder;

    @JsonProperty("category_id")
    private long mCategoryId;

    /**
     * @return the name.
     */
    public String getName() {
        return mName;
    }

    /**
     * set the name.
     */
    public void setName(String name) {
        mName = name;
    }

    /**
     * @return the order.
     */
    public int getOrder() {
        return mOrder;
    }

    /**
     * set the order.
     */
    public void setOrder(int order) {
        mOrder = order;
    }

    /**
     * @return the category id.
     */
    public long getCategoryId() {
        return mCategoryId;
    }

    /**
     * set the category id.
     */
    public void setCategoryId(long id) {
        mCategoryId = id;
    }

    @Override
    public int compareTo(SubCategory another) {
        return getOrder() - another.getOrder();
    }

    @Override
    public String toString() {
        return "[SubCategory-" + mId + "] amount:" + mName + " order:" + mOrder;
    }
}
