package cn.leslie.financemanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.List;

import cn.leslie.financemanager.data.DataManager;
import cn.leslie.financemanager.data.Record;


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
    private EditText mAmount;

    private Calendar mRecordTime;
    private RecordAdapter mRecordAdapter;

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
        mAmount = (EditText) findViewById(R.id.input_amount);

        mCategory.setOnClickListener(this);
        mSubCategory.setOnClickListener(this);
        mDate.setOnClickListener(this);
        mTime.setOnClickListener(this);

        ListView listRecord = (ListView) findViewById(R.id.list_record);
        mRecordAdapter = new RecordAdapter(DataManager.getInstance().getRecords());
        listRecord.setAdapter(mRecordAdapter);
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
            save();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private int getType() {
        if (mIncome.isChecked()) {
            return Record.TYPE_INCOME;
        } else {
            return Record.TYPE_OUTCOME;
        }
    }

    private int getPerson() {
        if (mMale.isChecked()) {
            return Record.PERSON_MALE;
        } else if (mFemale.isChecked()) {
            return Record.PERSON_FEMALE;
        } else {
            return Record.PERSON_ALL;
        }
    }

    private void save() {
        Record record = new Record();
        record.setAmount(Float.parseFloat(mAmount.getText().toString()));
        record.setCreated(mRecordTime.getTimeInMillis());
        record.setUpdated(mRecordTime.getTimeInMillis());
        record.setCategory(1);
        record.setSubCategory(2);
        record.setType(getType());
        record.setPerson(getPerson());
        DataManager.getInstance().addRecord(record);
        mRecordAdapter.setRecords(DataManager.getInstance().getRecords());
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
        datePickerDialog.setTitle(R.string.set_time);
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
        public Object getItem(int i) {
            return mRecords.get(i);
        }

        @Override
        public long getItemId(int i) {
            return mRecords.get(i).getId();
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
            viewHolder.mTitle.setText(record.getType() == Record.TYPE_INCOME
                    ? getString(R.string.income) : getString(R.string.outcome)
                    + " : " + record.getAmount());
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
