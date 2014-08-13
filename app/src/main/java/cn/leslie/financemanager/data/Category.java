package cn.leslie.financemanager.data;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Data object that describes a category.
 */
public class Category extends Model implements Comparable<Category> {
    /**
     * do not calculate this category while statistics.
     * TODO: better solution
     */
    public static final int FIXED_OUTCOME_INCOME = 9;

    @JsonProperty("name")
    private String mName;

    @JsonProperty("order")
    private int mOrder;

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

    @Override
    public int compareTo(Category another) {
        return getOrder() - another.getOrder();
    }

    @Override
    public String toString() {
        return "[Category-" + mId + "] amount:" + mName + " order:" + mOrder;
    }
}
