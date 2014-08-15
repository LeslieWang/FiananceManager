package cn.leslie.financemanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.fortysevendeg.swipelistview.SwipeListView;

import cn.leslie.financemanager.data.DataManager;

public class ViewRecordActivity extends Activity implements RecordEditorFragment.OnSaveListener {
    private RecordListAdapter mRecordAdapter;

    public static void show(Context context) {
        context.startActivity(new Intent(context, ViewRecordActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_record);

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
        mRecordAdapter.setRecords(DataManager.getInstance().getRecords());
    }
}
