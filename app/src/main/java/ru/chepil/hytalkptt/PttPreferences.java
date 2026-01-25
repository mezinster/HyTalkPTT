package ru.chepil.hytalkptt;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * PTT keycode stored in app sandbox (SharedPreferences).
 * Default 228 (Motorola LEX F10), overridable via PttKeySetupActivity.
 */
public final class PttPreferences {

    private static final String PREFS_NAME = "ru.chepil.hytalkptt.ptt_prefs";
    private static final String KEY_PTT_KEYCODE = "ptt_keycode";
    /** Default PTT keycode for Motorola LEX F10. */
    public static final int DEFAULT_PTT_KEYCODE = 228;

    private PttPreferences() {}

    public static int getPttKeyCode(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getInt(KEY_PTT_KEYCODE, DEFAULT_PTT_KEYCODE);
    }

    public static void setPttKeyCode(Context context, int keyCode) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_PTT_KEYCODE, keyCode)
                .apply();
    }

    /**
     * Ensures default PTT keycode (228) is stored if none exists yet.
     * Call on app start.
     */
    public static void ensureDefault(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (!prefs.contains(KEY_PTT_KEYCODE)) {
            prefs.edit().putInt(KEY_PTT_KEYCODE, DEFAULT_PTT_KEYCODE).apply();
        }
    }
}
