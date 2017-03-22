package com.dih.keye.keyegank.utils;

import android.content.Context;
import android.content.res.Configuration;

/**
 * Created by zsj on 2016/5/9.
 */
public class ConfigUtils {

    public static boolean isOrientationPortrait(Context context) {
        if (context.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT) {
            return true;
        }
        return false;
    }

    public static boolean isOrientationLandscape(Context context) {
        if (context.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE) {
            return true;
        }
        return false;
    }
}
