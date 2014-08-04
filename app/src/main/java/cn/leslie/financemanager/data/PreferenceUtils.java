package cn.leslie.financemanager.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Utils class to manage private preferences.
 */
public final class PreferenceUtils {

    private static SharedPreferences sSharedPreferences;

    private PreferenceUtils() {
        // make it as private
    }

    /**
     * Get application globe shared preferences.
     *
     * @param context see {@link android.content.Context}
     * @return shared preferences for application.
     */
    public static SharedPreferences getSharedPreferences(Context context) {
        if (sSharedPreferences == null) {
            sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return sSharedPreferences;
    }
}
