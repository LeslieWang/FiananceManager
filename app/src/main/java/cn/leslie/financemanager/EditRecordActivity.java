package cn.leslie.financemanager;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class EditRecordActivity extends Activity implements RecordEditorFragment.OnSaveListener {
    private static final String ARG_RECORD_ID = "record_id";

    public static void show(Activity activity, long id) {
        Intent intent = new Intent(activity, EditRecordActivity.class);
        intent.putExtra(ARG_RECORD_ID, id);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_record);

        long id = getIntent().getLongExtra(ARG_RECORD_ID, 0);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, RecordEditorFragment.newInstance(id),
                            RecordEditorFragment.class.getName())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_record, menu);
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
    public void onSave() {
        finish();
    }
}
