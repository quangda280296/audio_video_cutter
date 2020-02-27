package jack.com.servicekeep.manager;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import java.util.concurrent.TimeUnit;

import jack.com.servicekeep.fork.NativeRuntime;
import jack.com.servicekeep.service.KeepAliveJobSchedulerService;
import jack.com.servicekeep.service.WorkService;
import jack.com.servicekeep.utils.FileUtils;
import jack.com.servicekeep.utils.LogUtils;

import static android.app.job.JobScheduler.RESULT_SUCCESS;
import static android.content.Context.JOB_SCHEDULER_SERVICE;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

public enum KeepAliveManager {

    INSTANCE;

    private final String TAG = "KeepAliveManager";
    private final int JOB_ID = 1;

    public void stopKeepAliveSerice(final Context context){
        WorkService.stopService(context);
    }

    public void startKeepAliveService(final Context context) {
        if (context != null) {
            if (Build.VERSION.SDK_INT < LOLLIPOP) {
                LogUtils.d(TAG, "initService-----------c fork");
                String executable = "libhelper.so";
                String aliasFile = "helper";
                final String serviceName = context.getPackageName() + "/jack.com.servicekeep.service.WorkService";
                LogUtils.d(TAG, "serviceName-----------" + serviceName);
                NativeRuntime.INSTANCE.runExecutable(context.getPackageName(), executable, aliasFile, serviceName);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            NativeRuntime.INSTANCE.startService(serviceName, FileUtils.createRootPath(context));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } else {
                startJobScheduler(context);
            }
        } else {
            LogUtils.e(TAG, "initService context is null !!!!");
        }

    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startJobScheduler(Context context) {
        if (context != null) {
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
            ComponentName jobService = new ComponentName(context.getPackageName(),
                    KeepAliveJobSchedulerService.class.getName());
            JobInfo jobInfo = new JobInfo.Builder(JOB_ID, jobService)
                    .setMinimumLatency(1000)
                    .setPersisted(true)
                    .setBackoffCriteria(TimeUnit.MILLISECONDS.toMillis(200), JobInfo.BACKOFF_POLICY_LINEAR) //线性重试方案
                    .build();
            int result = jobScheduler.schedule(jobInfo);
            if (result == RESULT_SUCCESS) {
                LogUtils.d(TAG, "startJobScheduler ------ success!!!");
            }else{
                LogUtils.d(TAG, "startJobScheduler ------ fail!!!");
            }
        }
    }
}
