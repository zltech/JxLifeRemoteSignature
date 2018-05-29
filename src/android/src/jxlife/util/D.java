package jxlife.util;

import android.text.TextUtils;
import android.util.Log;

/**
 * 调试信息
 * 
 * @author lee
 */
public class D {


    public static String customTagPrefix = "";

    private D() {
    }

    public static boolean allowD = false;
    public static boolean allowE = true;
    public static boolean allowI = false;
    public static boolean allowV = false;
    public static boolean allowW = false;
    public static boolean allowWtf = false;

    private static String generateTag(StackTraceElement caller) {
        String tag = "%s.%s(L:%d)";
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber());
        tag = TextUtils.isEmpty(customTagPrefix) ? tag : customTagPrefix + ":" + tag;
        return tag;
    }

    public static CustomLogger customLogger;

    public interface CustomLogger {
        void d(String tag, String content);

        void d(String tag, String content, Throwable tr);

        void e(String tag, String content);

        void e(String tag, String content, Throwable tr);

        void i(String tag, String content);

        void i(String tag, String content, Throwable tr);

        void v(String tag, String content);

        void v(String tag, String content, Throwable tr);

        void w(String tag, String content);

        void w(String tag, String content, Throwable tr);

        void w(String tag, Throwable tr);

        void wtf(String tag, String content);

        void wtf(String tag, String content, Throwable tr);

        void wtf(String tag, Throwable tr);
    }

    public static void d(String content) {
        if (!allowD) return;
        StackTraceElement caller = OtherUtils.getCallerStackTraceElement();
        String tag = generateTag(caller);
        try{
        	
        	if (customLogger != null) {
        		customLogger.d(tag, content);
        	} else {
        		Log.d(tag, content);
        	}
        } catch(Exception e){
        	e.printStackTrace();
        	if(content != null){
    			System.err.println(content);        			
    		}
        }
    }

    public static void d(String content, Throwable tr) {
        if (!allowD) return;
        StackTraceElement caller = OtherUtils.getCallerStackTraceElement();
        String tag = generateTag(caller);

        try {
        	if (customLogger != null) {
        		customLogger.d(tag, content, tr);
        	} else {
        		Log.d(tag, content, tr);
        	}
			
		} catch (Exception e) {
			e.printStackTrace();
			if(content != null){
    			System.err.println(content);        			
    		}
		}
    }

    public static void e(String content) {
        if (!allowE) return;
        StackTraceElement caller = OtherUtils.getCallerStackTraceElement();
        String tag = generateTag(caller);

		try {
			if (customLogger != null) {
				customLogger.e(tag, content);
			} else {
				Log.e(tag, content);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (content != null) {
				System.err.println(content);
			}
		}
    }

    public static void e(String content, Throwable tr) {
        if (!allowE) return;
        StackTraceElement caller = OtherUtils.getCallerStackTraceElement();
        String tag = generateTag(caller);

        try {
			
        	if (customLogger != null) {
        		customLogger.e(tag, content, tr);
        	} else {
        		Log.e(tag, content, tr);
        	}
		} catch (Exception e) {
			e.printStackTrace();
			if (content != null) {
				System.err.println(content);
			}
		}
    }

    public static void i(String content) {
        if (!allowI) return;
        StackTraceElement caller = OtherUtils.getCallerStackTraceElement();
        String tag = generateTag(caller);

        try {
        	if (customLogger != null) {
        		customLogger.i(tag, content);
        	} else {
        		Log.i(tag, content);
        	}
			
		} catch (Exception e) {
			e.printStackTrace();
			if (content != null) {
				System.err.println(content);
			}
		}
    }

    public static void i(String content, Throwable tr) {
        if (!allowI) return;
        StackTraceElement caller = OtherUtils.getCallerStackTraceElement();
        String tag = generateTag(caller);

        try {
        	if (customLogger != null) {
        		customLogger.i(tag, content, tr);
        	} else {
        		Log.i(tag, content, tr);
        	}
			
		} catch (Exception e) {
			e.printStackTrace();
			if (content != null) {
				System.err.println(content);
			}
		}
    }

    public static void v(String content) {
        if (!allowV) return;
        StackTraceElement caller = OtherUtils.getCallerStackTraceElement();
        String tag = generateTag(caller);

        try {
        	if (customLogger != null) {
        		customLogger.v(tag, content);
        	} else {
        		Log.v(tag, content);
        	}
			
		} catch (Exception e) {
			e.printStackTrace();
			if (content != null) {
				System.err.println(content);
			}
		}
    }

    public static void v(String content, Throwable tr) {
        if (!allowV) return;
        StackTraceElement caller = OtherUtils.getCallerStackTraceElement();
        String tag = generateTag(caller);

        try {
        	if (customLogger != null) {
        		customLogger.v(tag, content, tr);
        	} else {
        		Log.v(tag, content, tr);
        	}
			
		} catch (Exception e) {
			e.printStackTrace();
			if (content != null) {
				System.err.println(content);
			}
		}
    }

    public static void w(String content) {
        if (!allowW) return;
        StackTraceElement caller = OtherUtils.getCallerStackTraceElement();
        String tag = generateTag(caller);

        try {
        	if (customLogger != null) {
        		customLogger.w(tag, content);
        	} else {
        		Log.w(tag, content);
        	}
			
		} catch (Exception e) {
			e.printStackTrace();
			if (content != null) {
				System.err.println(content);
			}
		}
    }

    public static void w(String content, Throwable tr) {
        if (!allowW) return;
        StackTraceElement caller = OtherUtils.getCallerStackTraceElement();
        String tag = generateTag(caller);

        try {
        	if (customLogger != null) {
        		customLogger.w(tag, content, tr);
        	} else {
        		Log.w(tag, content, tr);
        	}
			
		} catch (Exception e) {
			e.printStackTrace();
			if (content != null) {
				System.err.println(content);
			}
		}
    }

    public static void w(Throwable tr) {
        if (!allowW) return;
        StackTraceElement caller = OtherUtils.getCallerStackTraceElement();
        String tag = generateTag(caller);

        try {
        	if (customLogger != null) {
        		customLogger.w(tag, tr);
        	} else {
        		Log.w(tag, tr);
        	}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }


    public static void wtf(String content) {
        if (!allowWtf) return;
        StackTraceElement caller = OtherUtils.getCallerStackTraceElement();
        String tag = generateTag(caller);

        try {
        	if (customLogger != null) {
        		customLogger.wtf(tag, content);
        	} else {
        		Log.wtf(tag, content);
        	}
			
		} catch (Exception e) {
			e.printStackTrace();
			if (content != null) {
				System.err.println(content);
			}
		}
    }

    public static void wtf(String content, Throwable tr) {
        if (!allowWtf) return;
        StackTraceElement caller = OtherUtils.getCallerStackTraceElement();
        String tag = generateTag(caller);

        try {
        	if (customLogger != null) {
        		customLogger.wtf(tag, content, tr);
        	} else {
        		Log.wtf(tag, content, tr);
        	}
			
		} catch (Exception e) {
			e.printStackTrace();
			if (content != null) {
				System.err.println(content);
			}
		}
    }

    public static void wtf(Throwable tr) {
        if (!allowWtf) return;
        StackTraceElement caller = OtherUtils.getCallerStackTraceElement();
        String tag = generateTag(caller);

        try {
        	if (customLogger != null) {
        		customLogger.wtf(tag, tr);
        	} else {
        		Log.wtf(tag, tr);
        	}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }



	/***
	 * 打印长数据
	 */
	public static void largeLog(String content) {
		if (!allowE) return;
		int maxLogSize = 2000;

		for (int i = 0; i <= content.length() / maxLogSize; i++) {

			int start = i * maxLogSize;

			int end = (i + 1) * maxLogSize;

			end = end > content.length() ? content.length() : end;

			Log.e("test", content.substring(start, end));

		}
	}
}
