package cn.leslie.financemanager;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.fortysevendeg.swipelistview.SwipeListView;

import cn.leslie.financemanager.data.DataManager;

public class MainActivity extends Activity implements RecordEditorFragment.OnSaveListener {
    private static final int MAX_DISPLAY_DAY_OFFSET = -2; // display recent 3 days records.

    private RecordListAdapter mRecordAdapter;
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
        mRecordAdapter = new RecordListAdapter(mSwipeListView, new RecordListAdapter.OnDeleteListener() {
            @Override
            public void onDelete() {
                updateRecordList();
            }
        });
        updateRecordList();
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
        } else if (item.getItemId() == R.id.action_statistics) {
            StatisticsTabActivity.show(this);
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
        mRecordAdapter.setRecords(DataManager.getInstance().getRecords(
                TimeUtility.getStartTimeOfDay(MAX_DISPLAY_DAY_OFFSET),
                DataManager.INVALID_TIMESTAMP,
                DataManager.INVALID_CATE_ID,
                false));
    }
}
