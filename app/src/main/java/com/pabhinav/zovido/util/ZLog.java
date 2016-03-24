package com.pabhinav.zovido.util;

/**
 * @author pabhinav
 */

import android.util.Log;

/**
 * Wrapper util class over {@code android.util.Log}.
 *
 * @author pabhinav
 */
public class ZLog {

    public static void d(Object ob, String mssg){
        Log.d(makeLogNameSpace(ob) + " : ", mssg);
    }

    public static void w(Object ob, String mssg){
        Log.w(makeLogNameSpace(ob) + " : ", mssg);
    }

    public static void i(Object ob, String mssg){
        Log.i(makeLogNameSpace(ob) + " : ", mssg);
    }

    public static void e(Object ob, String mssg){
        Log.e(makeLogNameSpace(ob) + " : ", mssg);
    }

    private static String makeLogNameSpace(Object ob){
        return "Zovido => " + ob.getClass().getName();
    }
}

