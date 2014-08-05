package cn.leslie.financemanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import cn.leslie.financemanager.data.Category;
import cn.leslie.financemanager.data.DataManager;
import cn.leslie.financemanager.data.Record;
import cn.leslie.financemanager.data.SubCategory;

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
    private long mSelectedCateId = 0;
    private long mSelectedSubCateId = 0;

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
            mRecord = null;
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
            loadRecord();
        } else {
            List<Category> categories = DataManager.getInstance().getCategories();
            if (categories.size() > 0) {
                mSelectedCateId = categories.get(0).getId();
                List<SubCategory> subCategories =
                        DataManager.getInstance().getSubCategories(mSelectedCateId);
                if (subCategories.size() > 0) {
                    mSelectedSubCateId = subCategories.get(0).getId();
                }
            }
            onCategoryUpdated();
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
        return mRecord == null;
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
        String amount = mAmount.getText().toString();
        if (TextUtils.isEmpty(amount) || mSelectedSubCateId == 0 || mSelectedCateId == 0) {
            return;
        }

        Record record;
        if (isCreate()) {
            record = new Record();
        } else {
            record = mRecord;
        }

        record.setAmount(Float.parseFloat(amount));
        record.setCreated(mRecordTime.getTimeInMillis());
        record.setCategory(mSelectedCateId);
        record.setSubCategory(mSelectedSubCateId);
        record.setType(getType());
        record.setPerson(getPerson());

        boolean res;
        if (isCreate()) {
            res = DataManager.getInstance().addRecord(record);
        } else {
            res = DataManager.getInstance().updateRecord(record);
        }

        if (res) {
            if (mListener != null) {
                mListener.onSave();
            }
            mAmount.setText("");

            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mAmount.getWindowToken(), 0);
        } else {
            Toast.makeText(getActivity(), R.string.save_record_failed, Toast.LENGTH_LONG).show();
        }
    }

    private void loadRecord() {
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
        mSelectedCateId = mRecord.getCategory();
        mSelectedSubCateId = mRecord.getSubCategory();

        onRecordTimeUpdated();
        onCategoryUpdated();
    }

    private void onCategoryUpdated() {
        Category category = DataManager.getInstance().getCategoryById(mSelectedCateId);
        if (category != null) {
            mCategory.setText(category.getName());
        } else {
            mCategory.setText(R.string.category);
        }
        SubCategory subCategory = DataManager.getInstance().getSubCategoryById(mSelectedSubCateId);
        if (subCategory != null) {
            mSubCategory.setText(subCategory.getName());
        } else {
            mSubCategory.setText(R.string.sub_category);
        }
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

        final List<Category> categories = DataManager.getInstance().getCategories();
        final String[] items = new String[categories.size()];
        for (int i = 0; i < categories.size(); i++) {
            items[i] = categories.get(i).getName();
        }
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mCategory.setText(items[i]);
                mSelectedCateId = categories.get(i).getId();
                List<SubCategory> subCategories =
                        DataManager.getInstance().getSubCategories(mSelectedCateId);
                if (subCategories.size() > 0) {
                    mSelectedSubCateId = subCategories.get(0).getId();
                } else {
                    mSelectedSubCateId = 0;
                }
                onCategoryUpdated();
                showSubCategoryPicker();
            }
        });
        builder.setCancelable(true);
        builder.create().show();
    }

    private void showSubCategoryPicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.choose_sub_category);

        final List<SubCategory> subCategories = DataManager.getInstance().getSubCategories(mSelectedCateId);
        final String[] items = new String[subCategories.size()];
        for (int i = 0; i < subCategories.size(); i++) {
            items[i] = subCategories.get(i).getName();
        }
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mSubCategory.setText(items[i]);
                mSelectedSubCateId = subCategories.get(i).getId();
                onCategoryUpdated();
            }
        });
        builder.setCancelable(true);
        builder.create().show();
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear,int dayOfMonth) {
                mRecordTime.set(year, monthOfYear, dayOfMonth);
                onRecordTimeUpdated();
            }
        }, mRecordTime.get(Calendar.YEAR), mRecordTime.get(Calendar.MONTH), mRecordTime.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setTitle(R.string.set_date);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog datePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                mRecordTime.set(Calendar.HOUR_OF_DAY, hour);
                mRecordTime.set(Calendar.MINUTE, minute);
                onRecordTimeUpdated();
            }
        }, mRecordTime.get(Calendar.HOUR_OF_DAY), mRecordTime.get(Calendar.MINUTE), true);
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
