package z.houbin.webtest;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ActivityUtils {
    /**
     * 获取当前运行的activity
     */
    public static Activity getTopActivity(Context context) {
        WindowLifecycle windowLifecycle = new WindowLifecycle(context);
        Activity currentActivity = windowLifecycle.getCurrentActivity();
        if (currentActivity != null) {
            return currentActivity;
        }
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            //16~18 HashMap
            //19~27 ArrayMap
            Map<Object, Object> activities;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                activities = (HashMap<Object, Object>) activitiesField.get(activityThread);
            } else {
                activities = (ArrayMap<Object, Object>) activitiesField.get(activityThread);
            }
            if (activities.size() < 1) {
                return null;
            }
            for (Object activityRecord : activities.values()) {
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    Activity activity = (Activity) activityField.get(activityRecord);
                    return activity;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 监听Application中Activity生命周期，获取顶部Activity
     */
    public static class WindowLifecycle implements Application.ActivityLifecycleCallbacks, Closeable {
        private Activity currentActivity;

        public WindowLifecycle(Context context) {
            if (context == null) {
                return;
            }
            if (context instanceof Application) {
                ((Application) context).registerActivityLifecycleCallbacks(this);
            } else {
                ((Application) context.getApplicationContext()).registerActivityLifecycleCallbacks(this);
            }
        }

        /**
         * 获取当前运行的activity
         */
        public Activity getCurrentActivity() {
            Log.i("ActivityLifecycle", "getCurrentActivity");
            return this.currentActivity;
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            this.currentActivity = activity;
        }

        @Override
        public void onActivityPaused(Activity activity) {
            this.currentActivity = null;
        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }


        @Override
        public void close() throws IOException {
            if (currentActivity != null) {
                currentActivity = null;
            }
        }

        /**
         * 销毁资源
         */
        public void destroyed() {
            if (currentActivity != null) {
                currentActivity = null;
            }
        }
    }

}
