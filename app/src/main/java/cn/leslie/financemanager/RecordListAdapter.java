package cn.leslie.financemanager;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fortysevendeg.swipelistview.SwipeListView;

import java.util.List;

import cn.leslie.financemanager.data.DataManager;
import cn.leslie.financemanager.data.Record;

public class RecordListAdapter extends BaseAdapter {
    private List<Record> mRecords;
    private SwipeListView mSwipeListView;
    private Context mContext;
    private OnDeleteListener mOnDeleteListener;

    public RecordListAdapter(SwipeListView listView, OnDeleteListener listener) {
        mSwipeListView = listView;
        mContext = listView.getContext();
        mOnDeleteListener = listener;
    }

    public void setRecords(List<Record> records) {
        mRecords = records;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mRecords == null ? 0 : mRecords.size();
    }

    @Override
    public Object getItem(int position) {
        return mRecords.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mRecords.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder;

        if (convertView != null) {
            view = convertView;
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            view = LayoutInflater.from(mContext).inflate(
                    R.layout.item_record, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mTitle = (TextView) view.findViewById(R.id.text_title);
            viewHolder.mCategory = (TextView) view.findViewById(R.id.text_category);
            viewHolder.mDatetime = (TextView) view.findViewById(R.id.text_datetime);
            viewHolder.mPerson = (TextView) view.findViewById(R.id.text_person);
            viewHolder.mEdit = (ImageButton) view.findViewById(R.id.btn_edit);
            viewHolder.mDelete = (ImageButton) view.findViewById(R.id.btn_delete);
            view.setTag(viewHolder);
        }

        final Record record = (Record) getItem(position);
        viewHolder.mTitle.setText(Utility.toRecordTypeStr(
                mContext, record.getType()) + " : " + record.getAmount());
        viewHolder.mCategory.setText(Utility.toCategoryStr(record.getCategory())
                + ":" + Utility.toSubCategoryStr(record.getSubCategory()));
        viewHolder.mDatetime.setText(record.getCreatedTimeText(mContext));
        viewHolder.mPerson.setText(Utility.toPersonStr(mContext, record.getPerson()));
        viewHolder.mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSwipeListView.closeOpenedItems();
                EditRecordActivity.show(mContext, getItemId(position));
            }
        });
        viewHolder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.showDeleteConfirmDialog(mContext, new Utility.OnDeleteConfirmListener() {
                    @Override
                    public boolean onDelete() {
                        if (DataManager.getInstance().deleteRecord(record)) {
                            mSwipeListView.closeOpenedItems();
                            if (mOnDeleteListener != null) {
                                mOnDeleteListener.onDelete();
                            }
                            return true;
                        }
                        return false;
                    }
                });
            }
        });
        return view;
    }

    public static class ViewHolder {
        TextView mTitle;
        TextView mCategory;
        TextView mDatetime;
        TextView mPerson;
        ImageButton mEdit;
        ImageButton mDelete;
    }

    public interface OnDeleteListener {
        public void onDelete();
    }
}