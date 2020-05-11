
package eui.lighthttp.util;

import android.text.TextUtils;
import android.util.Log;

/**
 * API for sending log output.
 * Logger is generally used like this:
 *
 * <pre>
 * String final MODULE = "XXModule";
 * String final TAG = "MyActivity";
 * LogUtils logUtils = LogUtils.getInstance(MODULE, TAG);
 * logUtils.d("say something");
 * </pre>
 * Output is like this:
 *
 * <pre>
 * W/Launcher(16528): [XXModule][MyActivity]:say something
 * </pre>
 */
public final class LogUtils {
    public static String sTag = "LightHttp";
    // default module
    private static final String MODULE_DEFAULT = "default";

    private static boolean sLogable = true;
    private static boolean LOGV_ON = true && sLogable;
    private static boolean LOGD_ON = true && sLogable;
    private static boolean LOGI_ON = true && sLogable;
    private static boolean LOGW_ON = true && sLogable;
    private static boolean LOGWTF_ON = true && sLogable;
    private static boolean LOGE_ON = true && sLogable;

    private static boolean sSaveable = false;
    private static String sLogLock = "";

    private String mModuleTag;

    private LogUtils() {

    }

    private LogUtils(String module, String className) {
        mModuleTag = "[" + module + "][" + className + "]:";
    }

    /**
     * Get a Logger instance
     * 
     * @param className Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
     * @return LogUtils instance
     */
    public static LogUtils getInstance(String className) {
        if (TextUtils.isEmpty(className)) {
            throw new IllegalArgumentException();
        }
        return new LogUtils(MODULE_DEFAULT, className);
    }

    /**
     * Get a Logger instance
     * 
     * @param module plugin desktop name
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
     * @return LogUtils instance
     */
    public static LogUtils getInstance(String module, String tag) {
        if (TextUtils.isEmpty(module) || TextUtils.isEmpty(tag)) {
            throw new IllegalArgumentException();
        }
        return new LogUtils(module, tag);
    }

    /**
     * Send a VERBOSE log message.
     * 
     * @param msg The message you would like logged.
     */
    public void v(String msg) {
        if (LOGV_ON) {
            msg = mModuleTag + msg;
            Log.v(sTag, msg);
            if (sSaveable) {
                log2File(msg);
            }
        }
    }

    /**
     * Send a VERBOSE log message and log the exception.
     * 
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public void v(String msg, Throwable tr) {
        if (LOGV_ON) {
            msg = mModuleTag + msg;
            Log.v(sTag, msg, tr);
            if (sSaveable) {
                if (null != tr) {
                    log2File(msg + "\n" + tr.getMessage());
                } else {
                    log2File(msg);
                }
            }
        }
    }

    /**
     * Send a DEBUG log message.
     * 
     * @param msg The message you would like logged.
     */
    public void d(String msg) {
        if (LOGD_ON) {
            msg = mModuleTag + msg;
            Log.d(sTag, msg);
            if (sSaveable) {
                log2File(msg);
            }
        }
    }

    /**
     * Send a DEBUG log message and log the exception.
     * 
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public void d(String msg, Throwable tr) {
        if (LOGD_ON) {
            msg = mModuleTag + msg;
            Log.d(sTag, msg, tr);
            if (sSaveable) {
                if (null != tr) {
                    log2File(msg + "\n" + tr.getMessage());
                } else {
                    log2File(msg);
                }
            }
        }
    }

    /**
     * Send an INFO log message.
     * 
     * @param msg The message you would like logged.
     */
    public void i(String msg) {
        if (LOGI_ON) {
            msg = mModuleTag + msg;
            Log.i(sTag, msg);
            if (sSaveable) {
                log2File(msg);
            }
        }
    }

    /**
     * Send a INFO log message and log the exception.
     * 
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public void i(String msg, Throwable tr) {
        if (LOGI_ON) {
            msg = mModuleTag + msg;
            Log.i(sTag, msg, tr);
            if (sSaveable) {
                if (null != tr) {
                    log2File(msg + "\n" + tr.getMessage());
                } else {
                    log2File(msg);
                }
            }
        }
    }

    /**
     * Send a WARN log message.
     * 
     * @param msg The message you would like logged.
     */
    public void w(String msg) {
        if (LOGW_ON) {
            msg = mModuleTag + msg;
            Log.w(sTag, msg);
            if (sSaveable) {
                log2File(msg);
            }
        }
    }

    /**
     * Send a WARN log message and log the exception.
     * 
     * @param tr An exception to log
     */
    public void w(Throwable tr) {
        if (LOGW_ON) {
            Log.w(sTag, mModuleTag, tr);
            if (null != tr && sSaveable) {
                log2File(mModuleTag + "\n" + tr.getMessage());
            }
        }
    }

    /**
     * Send a WARN log message and log the exception.
     * 
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public void w(String msg, Throwable tr) {
        if (LOGW_ON) {
            msg = mModuleTag + msg;
            Log.w(sTag, msg, tr);
            if (sSaveable) {
                if (null != tr) {
                    log2File(msg + "\n" + tr.getMessage());
                } else {
                    log2File(msg);
                }
            }
        }
    }

    /**
     * What a Terrible Failure: Report a condition that should never happen. The error will always be logged at level ASSERT with the call stack. Depending on system configuration, a report may be
     * added to the DropBoxManager and/or the process may be terminated immediately with an error dialog.
     * 
     * @param msg The message you would like logged.
     */
    public void wtf(String msg) {
        if (LOGWTF_ON) {
            msg = mModuleTag + msg;
            Log.wtf(sTag, msg);
            if (sSaveable) {
                log2File(msg);
            }
        }
    }

    /**
     * What a Terrible Failure: Report an exception that should never happen. Similar to wtf(Throwable), with a message as well.
     * 
     * @param tr An exception to log
     */
    public void wtf(Throwable tr) {
        if (LOGWTF_ON) {
            Log.wtf(sTag, mModuleTag, tr);
            if (null != tr && sSaveable) {
                log2File(mModuleTag + "\n" + tr.getMessage());
            }
        }
    }

    /**
     * What a Terrible Failure: Report an exception that should never happen. Similar to wtf(Throwable), with a message as well.
     * 
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public void wtf(String msg, Throwable tr) {
        if (LOGWTF_ON) {
            msg = mModuleTag + msg;
            Log.wtf(sTag, msg, tr);
            if (sSaveable) {
                if (null != tr) {
                    log2File(msg + "\n" + tr.getMessage());
                } else {
                    log2File(msg);
                }
            }
        }
    }

    /**
     * Send an ERROR log message.
     * 
     * @param msg The message you would like logged.
     */
    public void e(String msg) {
        if (LOGE_ON) {
            msg = mModuleTag + msg;
            Log.e(sTag, msg);
            if (sSaveable) {
                log2File(msg);
            }
        }
    }

    /**
     * log the exception.
     * 
     * @param tr An exception to log
     */
    public void e(Throwable tr) {
        if (LOGE_ON) {
            Log.e(sTag, mModuleTag, tr);
            if (null != tr && sSaveable) {
                log2File(mModuleTag + "\n" + tr.getMessage());
            }
        }
    }

    /**
     * Send a ERROR log message and log the exception.
     * 
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public void e(String msg, Throwable tr) {
        if (LOGE_ON) {
            msg = mModuleTag + msg;
            Log.e(sTag, msg, tr);
            if (sSaveable) {
                if (null != tr) {
                    log2File(msg + "\n" + tr.getMessage());
                } else {
                    log2File(msg);
                }
            }
        }
    }

    public static void setTag(String tag) {
        sTag = tag;
    }

    public static boolean isLogable() {
        return sLogable;
    }

    public static void setLogable(boolean enable) {
        sLogable = enable;
    }

    public static boolean isSaveable() {
        return sSaveable;
    }

    public static void setSaveable(boolean enable) {
        sSaveable = enable;
    }

    public void log2File(String log) {
        // synchronized (sLogLock) {
        // String path = StorageUtils.getLogDir();
        // if (null == path) {
        // return;
        // }
        //
        // String file = StringUtils.formatDate(System.currentTimeMillis(), "yyyyMMdd");
        // File f = new File(path + file);
        // FileUtils.saveFile(log, f);
        // }
    }
}
