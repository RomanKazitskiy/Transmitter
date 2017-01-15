package com.ololo.myapplication2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class UDPThread implements Runnable {

	private final static String LOG_TAG = "myLogs";

	public final static String ACTION_DATA_AVAILABLE = "ACTION_DATA_AVAILABLE";
	public final static String EXTRA_DATA = "EXTRA_DATA";

	private DatagramSocket clientSocket;
	private InetAddress IPAddress;
	private int serverPort;
	private String sb;
	private String randMesage;
	private long packetNum;
	private Random r = new Random();
	private int len = 214;

	private Intent intent;
	private Context context;

	private static boolean flag;

	UDPThread(Context ccontext) {

		this.context = ccontext;

		try {
			IPAddress = InetAddress.getByName(MyService.DEVICE_IP_ADDRESS);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		//*
		try {
				clientSocket = new DatagramSocket();

			} catch (IOException e) {
				e.printStackTrace();
			}

		this.serverPort = Integer.valueOf(MyService.SOCKET);
		this.randMesage = "";
		this.packetNum = 0;

		this.intent = new Intent (ACTION_DATA_AVAILABLE);

		flag = true;
	}

	@Override
	public void run() {
		if (!flag) {
			Log.d(LOG_TAG,"UDPThread stop1");
			MyService.sfUdpTread.cancel(true);
			Log.d(LOG_TAG,"UDPThread stop2");
			clientSocket.close();//*
			return;
		}
			packetNum++;
			Log.d(LOG_TAG,String.valueOf(packetNum));
			sendUdp(packetNum);
	}

	public void stopThread() {
		flag = false;
	}

	private String randomString(final int length, final Random r) {
    	if (randMesage.length()>length||randMesage.length()==0) {
    		char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    		StringBuffer sb = new  StringBuffer();
    		for( int i = 0; i < length; i++ )
    		{
    			char c  = chars[r.nextInt(26)];
    			sb.append( c );
    		}

    		randMesage = sb.toString();
    	}

   		return randMesage;
	}

	private void sendUdp(long packetNum) {
		try {
		//	clientSocket = new DatagramSocket();

			sb = String.valueOf(packetNum);

			String message = sb + " "
					+ randomString(len - 1 - sb.length(),r);

			DatagramPacket clientPacket = new DatagramPacket(
					message.getBytes(), message.length(), IPAddress, serverPort );

			clientSocket.send(clientPacket);
		//	clientSocket.close();

			broatcastUpdate(ACTION_DATA_AVAILABLE,sb);

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void broatcastUpdate (final String action, String st) {
    	if (action.equals(ACTION_DATA_AVAILABLE)) {
    		context.sendBroadcast(intent.putExtra(EXTRA_DATA, st));
    	}
    }
}
