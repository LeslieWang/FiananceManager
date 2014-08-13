package cn.leslie.financemanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.leslie.financemanager.data.Category;
import cn.leslie.financemanager.data.DataManager;
import cn.leslie.financemanager.data.Record;

/**
 * Helper class to handle some common functions.
 */
public class Utility {
    private static final int ONE_DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final float THRESHOLD_NOT_DISPLAY = 0.1f;

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
        long offset = (time1 / ONE_DAY_IN_MILLIS) - (time2 / ONE_DAY_IN_MILLIS);
        return (int) offset;
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

    public static Map<String, Float> calculateAmountByCategory(
            Context context, List<Record> records) {
        HashMap<String, Float> res = new HashMap<String, Float>();

        if (records != null && records.size() > 0) {
            float total = 0f;
            for (Record record : records) {
                if (record.getCategory() != Category.FIXED_OUTCOME_INCOME) {
                    total += record.getAmount();
                    String name = getCategoryName(record);
                    if (res.containsKey(name)) {
                        res.put(name, res.get(name) + record.getAmount());
                    } else {
                        res.put(name, record.getAmount());
                    }
                }
            }

            float threshold = total * THRESHOLD_NOT_DISPLAY;
            List<String> list = new ArrayList<String>(res.keySet());
            float otherAmount = 0f;
            for (String name : list) {
                float amount = res.get(name);
                if (amount < threshold) {
                    otherAmount += amount;
                    res.remove(name);
                }
            }
            res.put(context.getString(R.string.statistics_others_portion), otherAmount);
        }
        return res;
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
}
