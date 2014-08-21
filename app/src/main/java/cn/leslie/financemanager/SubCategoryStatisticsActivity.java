package cn.leslie.financemanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import cn.leslie.financemanager.data.DataManager;

public class SubCategoryStatisticsActivity extends Activity {
    private static final String ARG_NAME_RES_ID = "name_res_id";
    private static final String ARG_CATE_ID= "cate_id";

    public static void show(Context context, int nameResId, long cateId) {
        Intent intent = new Intent(context, SubCategoryStatisticsActivity.class);
        intent.putExtra(ARG_NAME_RES_ID, nameResId);
        intent.putExtra(ARG_CATE_ID, cateId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_statistics);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, StatisticsFragment.newInstance(
                            getIntent().getIntExtra(ARG_NAME_RES_ID, -1),
                            getIntent().getLongExtra(ARG_CATE_ID, DataManager.INVALID_CATE_ID)))
                    .commit();
        }
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
