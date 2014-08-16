package cn.leslie.financemanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import cn.leslie.financemanager.data.Category;
import cn.leslie.financemanager.data.DataManager;
import cn.leslie.financemanager.data.Record;

/**
 * Helper class to handle some common functions.
 */
public class Utility {
    private static final int ONE_DAY_IN_MILLIS = 1000 * 60 * 60 * 24;

    private Utility() {
        // make it as private
    }

    public static String toRecordTypeStr(Context context, int type) {
        return type == Record.TYPE_INCOME ? context.getString(R.string.income)
                : context.getString(R.string.outcome);
    }

    public static String toCategoryStr(long id) {
        return DataManager.getInstance().getCategoryById(id).getName();
    }

    public static String toSubCategoryStr(long id) {
        return DataManager.getInstance().getSubCategoryById(id).getName();
    }

    public static String toPersonStr(Context context, int person) {
        switch (person) {
            case Record.PERSON_FEMALE:
                return context.getString(R.string.female);
            case Record.PERSON_MALE:
                return context.getString(R.string.male);
            case Record.PERSON_ALL:
                return context.getString(R.string.person_all);
        }
        return null;
    }

    public static int calculateDayOffset(long time1, long time2) {
        long offset = Calendar.getInstance().getTimeZone().getRawOffset();
        return (int) (((time1 + offset) / ONE_DAY_IN_MILLIS) - ((time2 + offset)/ ONE_DAY_IN_MILLIS));
    }

    public static String getRelativeDateName(Context context, long time) {
        int offset = calculateDayOffset(System.currentTimeMillis(), time);
        switch (offset) {
            case 0:
                return context.getString(R.string.today);
            case 1:
                return context.getString(R.string.yesterday);
            case 2:
                return context.getString(R.string.before_yesterday);
        }
        return null;
    }

    public static void showDeleteConfirmDialog(final Context context,
                                               final OnDeleteConfirmListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.warning_delete);
        builder.setCancelable(true);
        builder.setNegativeButton(R.string.cancel_delete, null);
        builder.setPositiveButton(R.string.confirm_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (listener.onDelete()) {
                    Toast.makeText(context,
                            R.string.operation_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context,
                            R.string.something_wrong, Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setMessage(R.string.confirm_delete_record);
        builder.show();
    }

    public static void showEditorDialog(Context context, String origName,
                                  final OnEditorDialogConfirmListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.input_name);
        final EditText editText = new EditText(context);
        if (!TextUtils.isEmpty(origName)) {
            editText.setText(origName);
        }
        editText.setPadding(50, 50, 50, 50);
        builder.setView(editText);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onConfirm(editText.getText().toString());
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setCancelable(true);
        builder.create().show();
    }

    public interface OnEditorDialogConfirmListener {
        public void onConfirm(String name);
    }

    public interface OnDeleteConfirmListener {
        public boolean onDelete();
    }

    public static ChartData toChartData(List<StatisticsData> datas) {
        ArrayList<Entry> series = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < datas.size(); i++) {
            StatisticsData data = datas.get(i);
            if (data.mId != Category.FIXED_OUTCOME_INCOME) {
                series.add(new Entry(data.mAmount, i));
                xVals.add(data.mName);
            }
        }

        return new ChartData(xVals, new DataSet(series, ""));
    }

    public static StatisticsResult analyzeRecordsByCategory(List<Record> records) {
        StatisticsResult result = new StatisticsResult();
        HashMap<Long, StatisticsData> res = new HashMap<Long, StatisticsData>();

        if (records != null && records.size() > 0) {
            for (Record record : records) {
                StatisticsData data;
                if (res.containsKey(record.getCategory())) {
                    data = res.get(record.getCategory());
                } else {
                    String name = getCategoryName(record);
                    data = new StatisticsData();
                    data.mId = record.getCategory();
                    data.mName = name;
                }

                if (record.getType() == Record.TYPE_INCOME) {
                    data.mAmount -= record.getAmount();
                    result.mIncome += record.getAmount();
                    if (record.getPerson() == Record.PERSON_MALE) {
                        result.mMaleIncome += record.getAmount();
                    } else if (record.getPerson() == Record.PERSON_FEMALE) {
                        result.mFemaleIncome += record.getAmount();
                    } else {
                        result.mAllIncome += record.getAmount();
                    }
                } else {
                    data.mAmount += record.getAmount();
                    result.mOutcome += record.getAmount();
                    if (record.getPerson() == Record.PERSON_MALE) {
                        result.mMaleOutcome += record.getAmount();
                    } else if (record.getPerson() == Record.PERSON_FEMALE) {
                        result.mFemaleOutcome += record.getAmount();
                    } else {
                        result.mAllOutcome += record.getAmount();
                    }
                }
                data.mRecordCount++;
                res.put(data.mId, data);
            }
        }
        result.mDatas = new ArrayList<StatisticsData>(res.values());
        return result;
    }

    private static String getCategoryName(Record record) {
        if (record == null) {
            return "";
        }
        Category category = DataManager.getInstance().getCategoryById(record.getCategory());
        if (category == null) {
            return "";
        } else {
            return category.getName();
        }
    }

    public static class StatisticsData {
        public float mAmount = 0f;
        public long mId;
        public String mName;
        public int mRecordCount = 0;
    }

    public static class StatisticsResult {
        public List<StatisticsData> mDatas;
        public float mMaleOutcome = 0f;
        public float mFemaleOutcome = 0f;
        public float mAllOutcome = 0f;
        public float mMaleIncome = 0f;
        public float mFemaleIncome = 0f;
        public float mAllIncome = 0f;
        public float mIncome = 0f;
        public float mOutcome = 0f;
    }
}
