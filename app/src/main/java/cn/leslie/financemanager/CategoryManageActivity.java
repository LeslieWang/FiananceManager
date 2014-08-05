package cn.leslie.financemanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cn.leslie.financemanager.data.Category;
import cn.leslie.financemanager.data.DataManager;
import cn.leslie.financemanager.data.SubCategory;

public class CategoryManageActivity extends Activity {
    private CategoryAdapter mCategoryAdapter;
    private SubCategoryAdapter mSubCategoryAdapter;
    private int mCurrentCatePos = 0;

    public static void show(Activity activity) {
        activity.startActivity(new Intent(activity, CategoryManageActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_manage);

        final ListView listCate = (ListView) findViewById(R.id.list_cate);
        mCategoryAdapter = new CategoryAdapter(DataManager.getInstance().getCategories());
        listCate.setAdapter(mCategoryAdapter);
        listCate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == adapterView.getCount() - 1) {
                    showAddCategoryDialog();
                } else {
                    adapterView.getChildAt(mCurrentCatePos).setBackgroundColor(
                            getResources().getColor(android.R.color.holo_orange_light));
                    onSelectCategory(position);
                    adapterView.getChildAt(mCurrentCatePos).setBackgroundColor(
                            getResources().getColor(android.R.color.holo_blue_dark));
                }
            }
        });

        ListView listSubCate = (ListView) findViewById(R.id.list_sub_cate);
        mSubCategoryAdapter = new SubCategoryAdapter(
                DataManager.getInstance().getSubCategories(getCurrentCateId()));
        listSubCate.setAdapter(mSubCategoryAdapter);
        listSubCate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == adapterView.getCount() - 1) {
                    showAddSubCategoryDialog();
                }
            }
        });

        if (mCategoryAdapter.getCount() > 1) {
            onSelectCategory(0);
        }
    }

    private void onSelectCategory(int pos) {
        mCurrentCatePos = pos;
        mSubCategoryAdapter.setData(DataManager.getInstance().getSubCategories(
                mCategoryAdapter.getItemId(mCurrentCatePos)));
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_cate);
        final EditText editText = new EditText(this);
        builder.setView(editText);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = editText.getText().toString();
                if (!TextUtils.isEmpty(name)) {
                    if (DataManager.getInstance().addCategory(name)) {
                        mCategoryAdapter.setData(DataManager.getInstance().getCategories());
                    }
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setCancelable(true);
        builder.create().show();
    }

    private long getCurrentCateId() {
        return mCategoryAdapter.getItemId(mCurrentCatePos);
    }

    private void showAddSubCategoryDialog() {
        if (getCurrentCateId() <= 0) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_sub_cate);
        final EditText editText = new EditText(this);
        builder.setView(editText);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = editText.getText().toString();
                if (!TextUtils.isEmpty(name)) {
                    if (DataManager.getInstance().addSubCategory(getCurrentCateId(), name)) {
                        mSubCategoryAdapter.setData(
                                DataManager.getInstance().getSubCategories(getCurrentCateId()));
                    }
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setCancelable(true);
        builder.create().show();
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
            return mCategories.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            if (position == mCategories.size() || position < 0) {
                return null;
            }
            return mCategories.get(position);
        }

        @Override
        public long getItemId(int position) {
            if (position == mCategories.size() || position < 0) {
                return 0;
            }
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

            if (position == mCurrentCatePos) {
                view.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
            } else {
                view.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
            }

            if (position == mCategories.size()) {
                ((TextView) view).setText("* " + getString(R.string.add_cate) + " *");
            } else {
                ((TextView) view).setText(mCategories.get(position).getName());
            }

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
            return mSubCategories.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            if (position == mSubCategories.size() || position < 0) {
                return null;
            }
            return mSubCategories.get(position);
        }

        @Override
        public long getItemId(int position) {
            if (position == mSubCategories.size() || position < 0) {
                return 0;
            }
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

            if (position == mSubCategories.size()) {
                ((TextView) view).setText("* " + getString(R.string.add_sub_cate) + " *");
            } else {
                ((TextView) view).setText(mSubCategories.get(position).getName());
            }

            return view;
        }
    }
}
