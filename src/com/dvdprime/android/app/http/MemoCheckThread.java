/**
 * Copyright (C) 2010 inHim. All rights reserved.
 *
 * This software and its documentation are confidential and proprietary
 * information of inHim.  No part of the software and
 * documents may be copied, reproduced, transmitted, translated, or reduced to
 * any electronic medium or machine-readable form without the prior written
 * consent of inHim.
 *
 * inHim makes no representations with respect to the contents,
 * and assumes no responsibility for any errors that might appear in the
 * software and documents. This publication and the contents here of are subject
 * to change without notice.
 */
package com.dvdprime.android.app.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.dvdprime.android.app.ContextHolder;
import com.dvdprime.android.app.R;
import com.dvdprime.android.app.activity.MemoTabActivity;
import com.dvdprime.android.app.constants.PreferenceKeys;
import com.dvdprime.android.app.util.PrefUtil;
import com.dvdprime.android.app.util.StringUtil;
import com.dvdprime.android.app.util.SystemUtil;

public class MemoCheckThread extends Thread {
	
	private Context mContext;
	private Throwable mException = null;
	private InputStream mIs = null;
	
	public MemoCheckThread(String threadName, InputStream is) {
		super(threadName);
		mContext = ContextHolder.getInstance().getContext();
		mIs = is; 
	}
	
	public Throwable getException() {
		return mException;
	}
	
	public void run() {
		mException = null;
		try {
    		String buf = null;
    		InputStreamReader isr = new InputStreamReader(mIs, "EUC-KR");
    		BufferedReader br = new BufferedReader(isr);
    		boolean isLoggined = true;
    		
        	int icon = R.drawable.ic_notification;
        	String title = mContext.getString(R.string.app_name);
        	String message;
        	long when = System.currentTimeMillis();

        	buf = br.readLine();
			if(buf == null || buf.contains("Microsoft")) 
				isLoggined = false;
			
			br.skip(400);

			while(isLoggined){
				buf = br.readLine();
				
				if (buf.contains("style=")) {
   					buf = br.readLine();
   					buf = br.readLine();
   					
   					if (buf.contains("style=")) {
   						int count = StringUtil.toNumber(StringUtil.substringBefore(StringUtil.substringAfter(buf, ">"), "<"));
   						message = String.format(mContext.getString(R.string.memo_alert_new_message), count);
   						
   						if (count > PrefUtil.getInstance().getInt(PreferenceKeys.NEW_MEMO_COUNT, 0)) {
   	   						PrefUtil.getInstance().setInt(PreferenceKeys.NEW_MEMO_COUNT, count);
   	   						
   	   						if (SystemUtil.getApiLevel() < 11) {
   		   						Notification notification = new Notification(icon, message, when);
								notification.defaults = Notification.DEFAULT_SOUND;
								notification.flags |= Notification.FLAG_AUTO_CANCEL;
								notification.number = count;
								
								Intent notificationIntent = new Intent(mContext, MemoTabActivity.class);
								PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
								notification.setLatestEventInfo(mContext, title, message, contentIntent);
						        NotificationManager nm = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
						        nm.notify(R.string.alert_memo, notification);
   	   						} else {
   		   						Notification notification = new Notification.Builder(mContext)
									.setContentTitle("새로운 쪽지가 있습니다.")
									.setContentText(message)
									.setSmallIcon(icon).getNotification();
								notification.defaults = Notification.DEFAULT_SOUND;
								notification.flags |= Notification.FLAG_AUTO_CANCEL;
								notification.number = count;
								
								Intent notificationIntent = new Intent(mContext, MemoTabActivity.class);
								PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
								notification.setLatestEventInfo(mContext, title, message, contentIntent);
						        NotificationManager nm = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
						        nm.notify(R.string.alert_memo, notification);
   	   						}
   						}
   					}
   						
   					break;
   				}
				else if (buf.contains("frmMemo"))
					break;
    		}

    	} catch (Exception e){
    		mException = e;  
    	}
	}
}
