package cn.leslie.financemanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.fortysevendeg.swipelistview.SwipeListViewListener;

import java.util.List;

import cn.leslie.financemanager.data.Category;
import cn.leslie.financemanager.data.DataManager;
import cn.leslie.financemanager.data.SubCategory;

public class CategoryManageActivity extends Activity {
    private static final int INVALID_POS = -1;
    private CategoryAdapter mCategoryAdapter;
    private SubCategoryAdapter mSubCategoryAdapter;
    private SwipeListView mCategoryList;
    private SwipeListView mSubCategoryList;
    private int mCurrentCatePos = INVALID_POS;

    public static void show(Activity activity) {
        activity.startActivity(new Intent(activity, CategoryManageActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_manage);

        mCategoryList = (SwipeListView) findViewById(R.id.list_cate);
        mCategoryAdapter = new CategoryAdapter(DataManager.getInstance().getCategories());
        mCategoryList.setAdapter(mCategoryAdapter);
        mCategoryList.setSwipeListViewListener(new SwipeClickListener() {
            @Override
            public void onClickFrontView(int position) {
                selectCategory(position);
            }
        });

        if (mCategoryAdapter.getCount() > 0) {
            mCurrentCatePos = 0;
        }
        mSubCategoryList = (SwipeListView) findViewById(R.id.list_sub_cate);
        mSubCategoryAdapter = new SubCategoryAdapter(
                DataManager.getInstance().getSubCategories(getCurrentCateId()));
        mSubCategoryList.setAdapter(mSubCategoryAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.category_manage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_category) {
            Utility.showEditorDialog(this, null, new Utility.OnEditorDialogConfirmListener() {
                @Override
                public void onConfirm(String name) {
                    if (!TextUtils.isEmpty(name)) {
                        if (DataManager.getInstance().addCategory(name)) {
                            updateCategoryList();
                        }
                    }
                }
            });
            return true;
        } else if (item.getItemId() == R.id.action_add_sub_category) {
            final long cateId = getCurrentCateId();
            if (cateId > 0) {
                Utility.showEditorDialog(this, null, new Utility.OnEditorDialogConfirmListener() {
                    @Override
                    public void onConfirm(String name) {
                        if (!TextUtils.isEmpty(name)) {
                            if (DataManager.getInstance().addSubCategory(cateId, name)) {
                                updateSubCategoryList();
                            }
                        }
                    }
                });
                return true;
            } else {
                return false;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateCategoryBackground(View listItem, boolean isCurrent) {
        listItem.findViewById(R.id.swipe_front).setBackgroundColor(getResources().getColor(
                isCurrent ? android.R.color.holo_blue_dark : android.R.color.holo_orange_light));
    }

    private void selectCategory(int pos) {
        if (pos == mCurrentCatePos) {
            return;
        }
        if (mCurrentCatePos != INVALID_POS) {
            updateCategoryBackground(mCategoryList.getChildAt(mCurrentCatePos), false);
        }
        mCurrentCatePos = pos;
        updateSubCategoryList();
        updateCategoryBackground(mCategoryList.getChildAt(mCurrentCatePos), true);
    }

    private long getCurrentCateId() {
        return mCurrentCatePos == INVALID_POS ? 0 : mCategoryAdapter.getItemId(mCurrentCatePos);
    }

    private void updateCategoryList() {
        List<Category> categories = DataManager.getInstance().getCategories();
        if (mCurrentCatePos == INVALID_POS && categories.size() > 0) {
            mCurrentCatePos = 0;
        }
        mCategoryAdapter.setData(categories);
        mCategoryList.closeOpenedItems();
    }

    private void updateSubCategoryList() {
        mSubCategoryAdapter.setData(
                DataManager.getInstance().getSubCategories(getCurrentCateId()));
        mSubCategoryList.closeOpenedItems();
    }

    private void showEditCategoryDialog(final Category category) {
        Utility.showEditorDialog(this, category.getName(),
                new Utility.OnEditorDialogConfirmListener() {
                    @Override
                    public void onConfirm(String name) {
                        if (!TextUtils.isEmpty(name)) {
                            category.setName(name);
                            if (DataManager.getInstance().updateCategory(category)) {
                                updateCategoryList();
                            }
                        }
                    }
                }
        );
    }

    private void showEditSubCategoryDialog(final SubCategory subCategory) {
        Utility.showEditorDialog(this, subCategory.getName(),
                new Utility.OnEditorDialogConfirmListener() {
                    @Override
                    public void onConfirm(String name) {
                        if (!TextUtils.isEmpty(name)) {
                            subCategory.setName(name);
                            if (DataManager.getInstance().updateSubCategory(subCategory)) {
                                updateSubCategoryList();
                            }
                        }
                    }
                }
        );
    }

    private class CategoryAdapter extends BaseAdapter {
        private List<Category> mCategories;

        CategoryAdapter(List<Category> categories) {
            mCategories = categories;
        }

        void setData(List<Category> categories) {
            mCategories = categories;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mCategories.size();
        }

        @Override
        public Object getItem(int position) {
            return mCategories.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mCategories.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView != null) {
                view = convertView;
            } else {
                view = LayoutInflater.from(CategoryManageActivity.this).inflate(
                        R.layout.item_cate, parent, false);
            }

            updateCategoryBackground(view, position == mCurrentCatePos);

            final Category category = (Category) getItem(position);
            ((TextView) view.findViewById(R.id.swipe_front)).setText(category.getName());

            view.findViewById(R.id.btn_edit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showEditCategoryDialog(category);
                }
            });
            return view;
        }
    }

    private class SubCategoryAdapter extends BaseAdapter {
        private List<SubCategory> mSubCategories;

        SubCategoryAdapter(List<SubCategory> subCategories) {
            mSubCategories = subCategories;
        }

        void setData(List<SubCategory> subCategories) {
            mSubCategories = subCategories;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mSubCategories.size();
        }

        @Override
        public Object getItem(int position) {
            return mSubCategories.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mSubCategories.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView != null) {
                view = convertView;
            } else {
                view = LayoutInflater.from(CategoryManageActivity.this).inflate(
                        R.layout.item_cate, parent, false);
            }

            final SubCategory subCategory = (SubCategory) getItem(position);
            ((TextView) view.findViewById(R.id.swipe_front)).setText(subCategory.getName());
            view.findViewById(R.id.btn_edit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showEditSubCategoryDialog(subCategory);
                }
            });

            return view;
        }
    }

    private abstract class SwipeClickListener implements SwipeListViewListener {

        @Override
        public void onOpened(int i, boolean b) {
            // do nothing
        }

        @Override
        public void onClosed(int i, boolean b) {
            // do nothing
        }

        @Override
        public void onListChanged() {
            // do nothing
        }

        @Override
        public void onMove(int i, float v) {
            // do nothing
        }

        @Override
        public void onStartOpen(int i, int i2, boolean b) {
            // do nothing
        }

        @Override
        public void onStartClose(int i, boolean b) {
            // do nothing
        }

        @Override
        public void onClickBackView(int i) {
            // do nothing
        }

        @Override
        public void onDismiss(int[] ints) {
            // do nothing
        }

        @Override
        public int onChangeSwipeMode(int i) {
            return SwipeListView.SWIPE_MODE_DEFAULT;
        }

        @Override
        public void onChoiceChanged(int i, boolean b) {
            // do nothing
        }

        @Override
        public void onChoiceStarted() {
            // do nothing
        }

        @Override
        public void onChoiceEnded() {
            // do nothing
        }

        @Override
        public void onFirstListItem() {
            // do nothing
        }

        @Override
        public void onLastListItem() {
            // do nothing
        }
    }
}
