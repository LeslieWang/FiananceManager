package cn.leslie.financemanager;

import android.content.Context;

import cn.leslie.financemanager.data.DataManager;
import cn.leslie.financemanager.data.Record;

/**
 * Helper class to handle some common functions.
 */
public class Utility {
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
}
