package cn.leslie.financemanager.data;

import android.content.Context;
import android.text.TextUtils;

import org.codehaus.jackson.annotate.JsonProperty;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.leslie.financemanager.R;
import cn.leslie.financemanager.Utility;

/**
 * Data object that describes an exposure.
 */
public class Record extends Model implements Comparable<Record> {
    public static final int TYPE_INCOME           = 1;
    public static final int TYPE_OUTCOME          = 2;
    public static final int PERSON_ALL            = 1;
    public static final int PERSON_MALE           = 2;
    public static final int PERSON_FEMALE         = 3;

    @JsonProperty("amount")
    private float mAmount;

    @JsonProperty("created")
    private long mCreated;

    @JsonProperty("category")
    private long mCategory;

    @JsonProperty("sub_category")
    private long mSubCategory;

    @JsonProperty("type")
    private int mType;

    @JsonProperty("person")
    private int mPerson;

    /**
     * @return the amount.
     */
    public float getAmount() {
        return mAmount;
    }

    /**
     * set the amount.
     */
    public void setAmount(float amount) {
        mAmount = amount;
    }

    /**
     * @return the created.
     */
    public long getCreated() {
        return mCreated;
    }

    /**
     * set the created.
     */
    public void setCreated(long created) {
        mCreated = created;
    }

    /**
     * @return the category.
     */
    public long getCategory() {
        return mCategory;
    }

    /**
     * set the category.
     */
    public void setCategory(long category) {
        mCategory = category;
    }

    /**
     * @return the sub category.
     */
    public long getSubCategory() {
        return mSubCategory;
    }

    /**
     * set the sub category.
     */
    public void setSubCategory(long subCategory) {
        mSubCategory = subCategory;
    }

    /**
     * @return the type.
     */
    public int getType() {
        return mType;
    }

    /**
     * set the type.
     */
    public void setType(int type) {
        mType = type;
    }

    /**
     * @return the person.
     */
    public int getPerson() {
        return mPerson;
    }

    /**
     * set the person.
     */
    public void setPerson(int person) {
        mPerson = person;
    }

    @Override
    public int compareTo(Record another) {
        return (int) ((another.getCreated() - getCreated()) / 1000);
    }

    @Override
    public String toString() {
        return "[record-" + mId + "] amount:" + mAmount + " created:" + mCreated;
    }

    /**
     * Get created time text of the record. Format is: yyyy-MM-dd HH:mm
     *
     * @param context which is given to create time text.
     *
     * @return the updated time text.
     */
    public String getCreatedTimeText(Context context) {
        String relativeDayName = Utility.getRelativeDateName(context, getCreated());
        if (TextUtils.isEmpty(relativeDayName)) {
            SimpleDateFormat format = new SimpleDateFormat(
                    context.getString(R.string.date_and_time_template));
            return format.format(new Date(getCreated()));
        } else {
            SimpleDateFormat format = new SimpleDateFormat(
                    context.getString(R.string.time_template));
            return relativeDayName + " " + format.format(new Date(getCreated()));
        }
    }
}
