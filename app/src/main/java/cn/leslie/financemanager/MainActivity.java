package cn.leslie.financemanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fortysevendeg.swipelistview.SwipeListView;

import java.util.List;

import cn.leslie.financemanager.data.DataManager;
import cn.leslie.financemanager.data.Record;


public class MainActivity extends Activity implements RecordEditorFragment.OnSaveListener {
    private RecordAdapter mRecordAdapter;
    private SwipeListView mSwipeListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, RecordEditorFragment.newInstance(0),
                            RecordEditorFragment.class.getName())
                    .commit();
        }

        mSwipeListView = (SwipeListView) findViewById(R.id.list_record);
        mRecordAdapter = new RecordAdapter(DataManager.getInstance().getRecords());
        mSwipeListView.setAdapter(mRecordAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment fragment = getFragmentManager().findFragmentByTag(
                RecordEditorFragment.class.getName());
        if (fragment.onOptionsItemSelected(item)) {
            return true;
        }
        if (item.getItemId() == R.id.action_manage_cate) {
            CategoryManageActivity.show(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateRecordList();
    }

    @Override
    public void onSave() {
        updateRecordList();
    }

    private void updateRecordList() {
        mRecordAdapter.setRecords(DataManager.getInstance().getRecords());
    }

    private class RecordAdapter extends BaseAdapter {
        private List<Record> mRecords;

        RecordAdapter(List<Record> records) {
            setRecords(records);
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
                view = LayoutInflater.from(MainActivity.this).inflate(
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
                    MainActivity.this, record.getType()) + " : " + record.getAmount());
            viewHolder.mCategory.setText(Utility.toCategoryStr(record.getCategory())
                    + ":" + Utility.toSubCategoryStr(record.getSubCategory()));
            viewHolder.mDatetime.setText(record.getCreatedTimeText(MainActivity.this));
            viewHolder.mPerson.setText(Utility.toPersonStr(MainActivity.this, record.getPerson()));
            viewHolder.mEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSwipeListView.closeOpenedItems();
                    EditRecordActivity.show(MainActivity.this, getItemId(position));
                }
            });
            viewHolder.mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(R.string.warning_delete);
                    builder.setCancelable(true);
                    builder.setNegativeButton(R.string.cancel_delete, null);
                    builder.setPositiveButton(R.string.confirm_delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (DataManager.getInstance().deleteRecord(record)) {
                                mSwipeListView.closeOpenedItems();
                                updateRecordList();
                                Toast.makeText(MainActivity.this,
                                        R.string.operation_success, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this,
                                        R.string.something_wrong, Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    TextView textView = new TextView(MainActivity.this);
                    textView.setPadding(20, 20, 20, 20);
                    textView.setText(R.string.confirm_delete_record);
                    builder.setView(textView);
                    builder.show();
                }
            });
            return view;
        }
    }

    private static class ViewHolder {
        TextView mTitle;
        TextView mCategory;
        TextView mDatetime;
        TextView mPerson;
        ImageButton mEdit;
        ImageButton mDelete;
    }
}
