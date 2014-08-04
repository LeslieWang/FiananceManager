package cn.leslie.financemanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TimePicker;

import java.util.Calendar;


public class MainActivity extends Activity implements View.OnClickListener {
    private static final String[] CATE_ITEMS = new String[]{"Cate1", "Cate2", "Cate3", "Cate4"};
    private static final String[] SUB_CATE_ITEMS = new String[]{"SubCate1", "SubCate2", "SubCate3", "SubCate4"};

    private Button mCategory;
    private Button mSubCategory;
    private Button mDate;
    private Button mTime;
    private RadioButton mIncome;
    private RadioButton mOutcome;
    private RadioButton mAll;
    private RadioButton mMale;
    private RadioButton mFemale;

    private Calendar mRecordTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCategory = (Button) findViewById(R.id.btn_category);
        mSubCategory = (Button) findViewById(R.id.btn_sub_category);
        mDate = (Button) findViewById(R.id.btn_date);
        mTime = (Button) findViewById(R.id.btn_time);
        mIncome = (RadioButton) findViewById(R.id.radio_income);
        mOutcome = (RadioButton) findViewById(R.id.radio_outcome);
        mAll = (RadioButton) findViewById(R.id.radio_all);
        mMale = (RadioButton) findViewById(R.id.radio_male);
        mFemale = (RadioButton) findViewById(R.id.radio_female);

        mCategory.setOnClickListener(this);
        mSubCategory.setOnClickListener(this);
        mDate.setOnClickListener(this);
        mTime.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRecordTime = Calendar.getInstance();
        onRecordTimeUpdated();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_save) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onRecordTimeUpdated() {
        if (mRecordTime == null) {
            return;
        }
        mDate.setText(mRecordTime.get(Calendar.YEAR)
                + "-" + (mRecordTime.get(Calendar.MONTH) + 1)
                + "-" + mRecordTime.get(Calendar.DAY_OF_MONTH));
        mTime.setText(mRecordTime.get(Calendar.HOUR_OF_DAY) + " : " + mRecordTime.get(Calendar.MINUTE));
    }

    private void showCategoryPicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose_category);
        builder.setItems(CATE_ITEMS, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mCategory.setText(CATE_ITEMS[i]);
                showSubCategoryPicker();
            }
        });
        builder.create().show();
    }

    private void showSubCategoryPicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose_sub_category);
        builder.setItems(SUB_CATE_ITEMS, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mSubCategory.setText(SUB_CATE_ITEMS[i]);
            }
        });
        builder.create().show();
    }

    private void showDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear,int dayOfMonth) {
                mRecordTime.set(year, monthOfYear, dayOfMonth);
                onRecordTimeUpdated();
            }
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setTitle(R.string.set_date);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog datePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                mRecordTime.set(Calendar.HOUR_OF_DAY, hour);
                mRecordTime.set(Calendar.MINUTE, minute);
                onRecordTimeUpdated();
            }
        }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
        datePickerDialog.setTitle(R.string.set_date);
        datePickerDialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_date:
                showDatePicker();
                break;
            case R.id.btn_time:
                showTimePicker();
                break;
            case R.id.btn_category:
                showCategoryPicker();
                break;
            case R.id.btn_sub_category:
                showSubCategoryPicker();
                break;
            default:
                break;
        }
    }
}
