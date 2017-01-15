package com.ololo.myapplication2;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TimerSecond implements Runnable {
	
	private final static String LOG_TAG = "myLogs";
	
	public final static String ACTION_TIMER_AVAILABLE = "ACTION_TIMER_AVAILABLE";
	public final static String EXTRA_TIMER = "EXTRA_TIMER";
	
	private int Hours;
	private int Minutes;
	private int Seconds;
	private String st;
	
	private Intent intent;
	private Context context;
	
	private boolean flag;
	
	TimerSecond(Context context) {
		Hours = 0;
		Minutes = 0;
		Seconds = 0;
		
		this.context = context;
		
		this.intent = new Intent (ACTION_TIMER_AVAILABLE);
		
		flag = true;
	}
	
	@Override
	public void run() {
		if (!flag) {
			Log.d(LOG_TAG,"TimerSecond stop1");
			MyService.sfTimerSeconds.cancel(true);
			Log.d(LOG_TAG,"TimerSecond stop2");
			return;
		}
		Seconds++;
		if(Seconds > 59) {
			Seconds = 0;
			Minutes++;
		}
		if(Minutes > 59) {
			Minutes = 0;
			Hours++;
		}
		
		st = String.format("%03d:%02d:%02d", Hours, Minutes, Seconds);
		broatcastUpdate(ACTION_TIMER_AVAILABLE, st);
	}
	
	public void stopThread() {
		flag = false;
	}
	
	private void broatcastUpdate (final String action, String st) {
    	if (action.equals(ACTION_TIMER_AVAILABLE)) {
    		context.sendBroadcast(intent.putExtra(EXTRA_TIMER, st));
    	}
    }
}