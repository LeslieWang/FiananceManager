package cn.leslie.financemanager.data;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Base Data object which contains key information like id and some common function.
 */
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public abstract class Model {

    @JsonProperty("id")
    protected long mId;

    /**
     * @return the id.
     */
    public long getId() {
        return mId;
    }

    /**
     * set the id.
     */
    public void setId(long id) {
        mId = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof Model)) {
            return false;
        }
        return this.getId() == ((Model) o).getId();
    }

    @Override
    public int hashCode() {
        return (int) getId() * 31;
    }
}
