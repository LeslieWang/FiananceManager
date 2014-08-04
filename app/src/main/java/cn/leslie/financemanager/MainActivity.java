package cn.leslie.financemanager;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cn.leslie.financemanager.data.DataManager;
import cn.leslie.financemanager.data.Record;


public class MainActivity extends Activity implements RecordEditorFragment.OnSaveListener {
    private RecordAdapter mRecordAdapter;

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

        ListView listRecord = (ListView) findViewById(R.id.list_record);
        mRecordAdapter = new RecordAdapter(DataManager.getInstance().getRecords());
        listRecord.setAdapter(mRecordAdapter);
        listRecord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                EditRecordActivity.show(MainActivity.this, id);
            }
        });
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
        public View getView(int position, View convertView, ViewGroup parent) {
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
                viewHolder.mSubCategory = (TextView) view.findViewById(R.id.text_sub_category);
                view.setTag(viewHolder);
            }

            Record record = (Record) getItem(position);
            String typeStr = record.getType() == Record.TYPE_INCOME
                    ? getString(R.string.income) : getString(R.string.outcome);
            viewHolder.mTitle.setText(typeStr + " : " + record.getAmount());
            viewHolder.mCategory.setText(record.getCategory() + "");
            viewHolder.mDatetime.setText(record.getCreatedTimeText(MainActivity.this));
            viewHolder.mSubCategory.setText(record.getSubCategory() + "");
            return view;
        }
    }

    private static class ViewHolder {
        TextView mTitle;
        TextView mCategory;
        TextView mDatetime;
        TextView mSubCategory;
    }
}
