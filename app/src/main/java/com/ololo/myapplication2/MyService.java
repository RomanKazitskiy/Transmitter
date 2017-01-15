package com.ololo.myapplication2;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
//import android.util.Log;

public class MyService extends Service {
	
	//private final static String LOG_TAG = "myLogs";
	
	public final static String ACTION_DATA_AVAILABLE = 
			UDPThread.ACTION_DATA_AVAILABLE;
	public final static String EXTRA_DATA = 
			UDPThread.EXTRA_DATA;
	
	public final static String ACTION_TIMER_AVAILABLE = 
			TimerSecond.ACTION_TIMER_AVAILABLE;
	public final static String EXTRA_TIMER = 
			TimerSecond.EXTRA_TIMER;
	
	public static String DEVICE_IP_ADDRESS = "10.14.56.113";//"192.168.69.124";
	public static String SOCKET = "5555";
	public static String Intensity = "100";
	public static int periodSend;
	public static boolean flag;
	
	private ScheduledExecutorService scheduledExecutor;
	public static ScheduledFuture<?> sfUdpTread;
	public static ScheduledFuture<?> sfTimerSeconds;
    
    private Context context;
    public UDPThread udp;
    public TimerSecond timerSecond;
		
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flag, int startId) {
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	public class LocalBinder extends Binder {
		MyService getService() {
            return MyService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
	
    private final IBinder mBinder = new LocalBinder();
    
    
        
    public void startMyServce() {
    	flag = true;
    	context =  getBaseContext();
    	
    	if(scheduledExecutor==null) {
    		scheduledExecutor = Executors.newScheduledThreadPool(2);
    	}

    	timerSecond = new TimerSecond(context);
    	
    	sfTimerSeconds = scheduledExecutor.scheduleAtFixedRate(
    			timerSecond, 0, 1000,TimeUnit.MILLISECONDS);
    	
    	udp = new UDPThread(context);
    	
    	sfUdpTread = scheduledExecutor.scheduleAtFixedRate(
    			udp, 0, periodSend,TimeUnit.MILLISECONDS);
    	
    }
}
