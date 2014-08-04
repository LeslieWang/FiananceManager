package cn.leslie.financemanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import cn.leslie.financemanager.data.DataManager;
import cn.leslie.financemanager.data.Record;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link cn.leslie.financemanager.RecordEditorFragment.OnSaveListener} interface
 * to handle save events.
 * Use the {@link RecordEditorFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class RecordEditorFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_RECORD_ID = "record_id";
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

    private Record mRecord;

    private OnSaveListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id the id of the record, pass 0 means create a new one.
     * @return A new instance of fragment RecordEditorFragment.
     */
    public static RecordEditorFragment newInstance(long id) {
        RecordEditorFragment fragment = new RecordEditorFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_RECORD_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    public RecordEditorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long id = 0;
        if (getArguments() != null) {
            id = getArguments().getLong(ARG_RECORD_ID);
        }

        if (id == 0) {
            mRecord = new Record();
            mRecord.setId(0);
        } else {
            mRecord = DataManager.getInstance().getRecordById(id);
            mRecordTime = Calendar.getInstance();
            mRecordTime.setTimeInMillis(mRecord.getCreated());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            save();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View res = inflater.inflate(R.layout.fragment_record_editor, container, false);
        mCategory = (Button) res.findViewById(R.id.btn_category);
        mSubCategory = (Button) res.findViewById(R.id.btn_sub_category);
        mDate = (Button) res.findViewById(R.id.btn_date);
        mTime = (Button) res.findViewById(R.id.btn_time);
        mIncome = (RadioButton) res.findViewById(R.id.radio_income);
        mOutcome = (RadioButton) res.findViewById(R.id.radio_outcome);
        mAll = (RadioButton) res.findViewById(R.id.radio_all);
        mMale = (RadioButton) res.findViewById(R.id.radio_male);
        mFemale = (RadioButton) res.findViewById(R.id.radio_female);
        mAmount = (EditText) res.findViewById(R.id.input_amount);

        mCategory.setOnClickListener(this);
        mSubCategory.setOnClickListener(this);
        mDate.setOnClickListener(this);
        mTime.setOnClickListener(this);

        if (!isCreate()) {
            onRecordUpdated();
        }
        return res;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isCreate()) {
            mRecordTime = Calendar.getInstance();
            onRecordTimeUpdated();
        }
    }

    private boolean isCreate() {
        return mRecord == null || mRecord.getId() == 0;
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
        mRecord.setAmount(Float.parseFloat(mAmount.getText().toString()));
        mRecord.setCreated(mRecordTime.getTimeInMillis());
        mRecord.setUpdated(mRecordTime.getTimeInMillis());
        mRecord.setCategory(1);
        mRecord.setSubCategory(2);
        mRecord.setType(getType());
        mRecord.setPerson(getPerson());

        boolean res;
        if (isCreate()) {
            res = DataManager.getInstance().addRecord(mRecord);
        } else {
            res = DataManager.getInstance().updateRecord(mRecord);
        }

        if (res) {
            if (mListener != null) {
                mListener.onSave();
            }
            mAmount.setText("");
        } else {
            Toast.makeText(getActivity(), R.string.save_record_failed, Toast.LENGTH_LONG).show();
        }
    }

    private void onRecordUpdated() {
        if (mRecord == null) {
            return;
        }

        if (mRecord.getType() == Record.TYPE_INCOME) {
            mIncome.setChecked(true);
        } else {
            mOutcome.setChecked(true);
        }

        if (mRecord.getPerson() == Record.PERSON_MALE) {
            mMale.setChecked(true);
        } else if (mRecord.getPerson() == Record.PERSON_FEMALE) {
            mFemale.setChecked(true);
        } else {
            mAll.setChecked(true);
        }

        mAmount.setText(mRecord.getAmount() + "");
        mCategory.setText(mRecord.getCategory() + "");
        mSubCategory.setText(mRecord.getSubCategory() + "");

        onRecordTimeUpdated();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
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
        TimePickerDialog datePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSaveListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnSaveListener {
        public void onSave();
    }

}
