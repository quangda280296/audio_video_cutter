package jack.com.servicekeep.service.androidO;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

import jack.com.servicekeep.utils.ServiceUtils;
import jack.com.servicekeep.utils.LogUtils;

@TargetApi(21)
public class WorkServiceO extends JobService {

    private JobParameters mJobParameters;
    private final String TAG = "KeepAliveJobSchedulerService";
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            // 返回true，表示该工作耗时，同时工作处理完成后需要调用jobFinished销毁
            LogUtils.d(TAG, "KeepAliveJobSchedulerService ------ onStartJob");
            mJobParameters = (JobParameters) msg.obj;
            if (mJobParameters != null) {
                LogUtils.d(TAG, "onStartJob params ---------- " + mJobParameters);
            }
            //执行需要保活的工作
            //ServiceManager.INSTANCE.needKeepAlive(getApplicationContext());
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    LogUtils.d(TAG, "WorkService ---------- WorkServiceO Service工作了");
                    if (!ServiceUtils.isScreenOn(getApplicationContext()) &&
                            ServiceUtils.isMyServiceRunning(getApplicationContext(), AdsServiceO.class)) {
                        JobScheduler tm = (JobScheduler) getApplicationContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);
                        getApplicationContext().stopService(new Intent(getApplicationContext(), AdsServiceO.class));
                        tm.cancelAll();
                    }
                }
            };
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(timerTask, 1000, 1000);
            return true;
        }
    });


    @Override
    public boolean onStartJob(JobParameters params) {
        LogUtils.d(TAG, "KeepAliveJobSchedulerService-----------onStartJob");
        Message message = Message.obtain();
        message.obj = params;
        mHandler.sendMessage(message);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        LogUtils.d(TAG, "KeepAliveJobSchedulerService-----------onStopJob");
        mHandler.removeCallbacksAndMessages(null);
        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


}
