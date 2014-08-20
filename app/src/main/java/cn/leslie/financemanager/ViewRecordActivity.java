package cn.leslie.financemanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.fortysevendeg.swipelistview.SwipeListView;

import cn.leslie.financemanager.data.DataManager;

public class ViewRecordActivity extends Activity implements RecordEditorFragment.OnSaveListener {
    private static final String EXTRA_START = "extra_start";
    private static final String EXTRA_END = "extra_end";
    private static final String EXTRA_CATE_ID = "extra_cate_id";

    private RecordListAdapter mRecordAdapter;
    private long mStart = DataManager.INVALID_TIMESTAMP;
    private long mEnd = DataManager.INVALID_TIMESTAMP;
    private long mCateId = DataManager.INVALID_CATE_ID;

    public static void show(Context context, long start, long end, long cateId) {
        Intent intent = new Intent(context, ViewRecordActivity.class);
        intent.putExtra(EXTRA_START, start);
        intent.putExtra(EXTRA_END, end);
        intent.putExtra(EXTRA_CATE_ID, cateId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_record);

        mStart = getIntent().getLongExtra(EXTRA_START, DataManager.INVALID_TIMESTAMP);
        mEnd = getIntent().getLongExtra(EXTRA_END, DataManager.INVALID_TIMESTAMP);
        mCateId = getIntent().getLongExtra(EXTRA_CATE_ID, DataManager.INVALID_CATE_ID);

        SwipeListView listView = (SwipeListView) findViewById(R.id.list_record);
        mRecordAdapter = new RecordListAdapter(listView, new RecordListAdapter.OnDeleteListener() {
            @Override
            public void onDelete() {
                updateRecordList();
            }
        });
        updateRecordList();
        listView.setAdapter(mRecordAdapter);
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
        mRecordAdapter.setRecords(DataManager.getInstance().getRecords(mStart, mEnd, mCateId));
    }
}
