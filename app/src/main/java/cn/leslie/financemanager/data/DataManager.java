package cn.leslie.financemanager.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.CollectionType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class to save and update persistent data.
 */
public class DataManager {
    private static final String TAG = "DataManager";
    private static final String PREF_MAX_ID_PREFIX = "max_id_of_";

    private static DataManager sInstance;
    private Context mContext;
    private ObjectMapper mObjectMapper = new ObjectMapper();
    private JsonFactory mJsonFactory = new JsonFactory();

    private Map<Class, DataSet> mDataSets;

    /**
     * Construct a {@link DataManager} instance.
     *
     * @param context see {@link android.content.Context}.
     */
    private DataManager(Context context) {
        mContext = context;
        mObjectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        initDataSets();
    }

    /**
     * Create a singleton DataManager within a context
     *
     * @param context see {@link android.content.Context}.
     */
    public static void createInstance(Context context) {
        if (sInstance == null) {
            synchronized (DataManager.class) {
                if (sInstance == null) {
                    sInstance = new DataManager(context);
                }
            }
        }
    }

    /**
     * @return the singleton DataManager instance.
     */
    public static DataManager getInstance() {
        if (sInstance == null) {
            synchronized (DataManager.class) {
                if (sInstance == null) {
                    throw new IllegalStateException(
                            "DataManager::createInstance() needs to be called before DataManager::getInstance()");
                }
            }
        }
        return sInstance;
    }

    /**
     * @return all the records.
     */
    public List<Record> getRecords() {
        List<Record> list = getListWithFilter(mDataSets.get(Record.class), null);
        Collections.sort(list);
        return list;
    }

    /**
     * @return the record according given id.
     */
    public Record getRecordById(Long id) {
        return (Record) mDataSets.get(Record.class).getById(id);
    }

    public boolean addRecord(Record record) {
        return mDataSets.get(Record.class).addItem(record);
    }

    public boolean updateRecord(Record record) {
        return mDataSets.get(Record.class).updateItem(record);
    }

    public boolean deleteRecord(Record record) {
        return mDataSets.get(Record.class).deleteItem(record);
    }

    /**
     * @return all the categories.
     */
    public List<Category> getCategories() {
        List<Category> list = getListWithFilter(mDataSets.get(Category.class), null);
        Collections.sort(list);
        return list;
    }

    public Category getCategoryById(Long id) {
        return (Category) mDataSets.get(Category.class).getById(id);
    }

    public boolean addCategory(String name) {
        Category cate = new Category();
        cate.setName(name);
        cate.setOrder(mDataSets.get(Category.class).mData.size());
        return mDataSets.get(Category.class).addItem(cate);
    }

    public boolean updateCategory(Category cate) {
        return mDataSets.get(Category.class).updateItem(cate);
    }

    public boolean deleteCategory(Category cate) {
        return mDataSets.get(Category.class).deleteItem(cate);
    }

    /**
     * @return all the sub categories.
     */
    public List<SubCategory> getSubCategories(final long id) {
        List<SubCategory> list = getListWithFilter(mDataSets.get(SubCategory.class), new Filter() {
            @Override
            public boolean isMatch(Model model) {
                return id == ((SubCategory) model).getCategoryId();
            }
        });
        Collections.sort(list);
        return list;
    }

    public SubCategory getSubCategoryById(Long id) {
        return (SubCategory) mDataSets.get(SubCategory.class).getById(id);
    }

    public boolean addSubCategory(long cateId, String name) {
        SubCategory subCate = new SubCategory();
        subCate.setCategoryId(cateId);
        subCate.setName(name);
        subCate.setOrder(mDataSets.get(SubCategory.class).mData.size());
        return mDataSets.get(SubCategory.class).addItem(subCate);
    }

    public boolean updateSubCategory(SubCategory subCate) {
        return mDataSets.get(SubCategory.class).updateItem(subCate);
    }

    public boolean deleteSubCategory(SubCategory subCate) {
        return mDataSets.get(SubCategory.class).deleteItem(subCate);
    }

    private void initDataSets() {
        mDataSets = new HashMap<Class, DataSet>();
        mDataSets.put(Record.class, new DataSet<Record>(Record.class));
        mDataSets.put(Category.class, new DataSet<Category>(Category.class));
        mDataSets.put(SubCategory.class, new DataSet<SubCategory>(SubCategory.class));

        // XXX: dataset loading is to read the content from local file,
        // so it will block the calling thread a while
        // for the time being, this function is called from main thread.
        // if file is too big and too much loading it form background thread if has such side effect.
        for (DataSet dataset : mDataSets.values() ){
            dataset.load();
        }
    }

    private <T extends Model> List<T> getListWithFilter(DataSet<T> dataSet, Filter filter) {
        List<T> result = new ArrayList<T>();
        if (filter == null) {
            result.addAll(dataSet.getData().values());
        } else {
            for (T value : dataSet.getData().values()) {
                if (filter.isMatch(value)) {
                    result.add(value);
                }
            }
        }
        return result;
    }

    /**
     * Dump the dataset by given model class.
     *
     * @param clazz the model class
     */
    public <T extends Model> void dumpDataSet(Class<T> clazz) {
        mDataSets.get(clazz).dump();
    }

    interface Filter<T extends Model> {
        boolean isMatch(T model);
    }

    private class DataSet<T extends Model> {
        private Map<Long, T> mData;
        private Class<T> mClazz;
        private final CollectionType mCollectionType;

        private DataSet(Class<T> clazz) {
            mClazz = clazz;
            mCollectionType = mObjectMapper.getTypeFactory()
                    .constructCollectionType(ArrayList.class, mClazz);
        }

        public T getById(Long id) {
            return getData().get(id);
        }

        private String getName() {
            return mClazz.getSimpleName().toLowerCase();
        }

        @Override
        public String toString() {
            return "[DataSet] [" + getName() + "]";
        }

        /**
         * Clear all data.
         */
        private void clear() {
            mData.clear();
            File localFile = getLocalFile();
            if (localFile.exists()) {
                localFile.delete();
            }
            PreferenceUtils.getSharedPreferences(
                    mContext).edit().remove(getMaxIdPrefName()).commit();
        }

        private File getLocalFile() {
            return mContext.getFileStreamPath(getName() + ".json");
        }

        private Map<Long, T> getData() {
            return mData;
        }

        private void load() {
            Log.d(TAG, this.toString() + " loading ");
            mData = new HashMap<Long, T>();
            try {
                File localFile = getLocalFile();
                if (localFile.exists()) {
                    JsonParser jp = mJsonFactory.createJsonParser(localFile);
                    List<T> models = mObjectMapper.readValue(jp, mCollectionType);
                    for (T model : models) {
                        mData.put(model.getId(), model);
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "IOException occurs while loading data from local file.", e);
            }
        }

        private boolean deleteItem(T model) {
            Map<Long, T> data = mData;
            if (data.remove(model.getId()) == null) {
                return false;
            }
            return saveToLocal(data);
        }

        private boolean updateItem(T model) {
            Map<Long, T> data = mData;
            data.put(model.getId(), model);
            return saveToLocal(data);
        }

        private boolean addItem(T model) {
            long maxId = PreferenceUtils.getSharedPreferences(
                    mContext).getLong(getMaxIdPrefName(), 0);
            maxId++;
            model.setId(maxId);

            Map<Long, T> data = mData;
            data.put(maxId, model);
            boolean res =  saveToLocal(data);
            if (res) {
                SharedPreferences.Editor editor =
                        PreferenceUtils.getSharedPreferences(mContext).edit();
                editor.putLong(getMaxIdPrefName(), maxId);
                editor.commit();
            }
            return res;
        }

        private String getMaxIdPrefName() {
            return PREF_MAX_ID_PREFIX + mClazz.getName();
        }

        private boolean saveToLocal(Map<Long, T> data) {
            boolean res = false;
            try {
                //TODO: save the data in background thread if need.
                mObjectMapper.writeValue(getLocalFile(), data.values());
                // reset the cache and reload them.
                mData = data;
                res = true;
            } catch (IOException e) {
                Log.e(TAG, "IOException occurs while update model data from map.", e);
            }
            return res;
        }

        private void dump() {
            for (T value : getData().values()) {
                Log.d(TAG, value.toString());
            }
        }
    }
}
