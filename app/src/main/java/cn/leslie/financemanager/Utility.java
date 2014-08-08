package cn.leslie.financemanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

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
                return context.getString(R.string.all);
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
}
