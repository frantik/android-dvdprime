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

import com.dvdprime.android.app.db.DBAdapter;
import com.dvdprime.android.app.db.DpDB;

import android.content.ContentValues;
import android.content.Context;

public class ProfileGetThread extends Thread {
	
	private Throwable mException = null;
	private InputStream mIs = null;
	
	public ProfileGetThread(Context context, String threadName, InputStream is) {
		super(threadName);
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
    		ContentValues values = new ContentValues();
    		int fstIdx = 0;
    		int lstIdx = 0;
    		
    		DBAdapter dbAdapter = DBAdapter.getInstance();
			// article content header amount 2,500 characters
			br.skip(2500);

    		while(true){
    			buf = br.readLine();
    			if(buf == null) break;
    			else {
    				if (buf.contains("E5E5E5")) {
    					fstIdx = buf.indexOf("</b>")+4;
    					lstIdx = buf.indexOf("<a")-8;
    					values.put(DpDB.Account.ACCOUNT_NAME, buf.substring(fstIdx, lstIdx));
    				}
    				else if (buf.contains("a_img")) {
    					fstIdx = buf.indexOf("src=\"")+5;
    					lstIdx = buf.indexOf("alt=")-1;
    					values.put(DpDB.Account.ACCOUNT_AVARTAR, buf.substring(fstIdx, lstIdx));
    							
    					dbAdapter.updateAccount(values);
    					break;
    				}
    			}
    		}
    		// DB Close
			dbAdapter.close();
    	} catch (Exception e){
    		mException = e;  
    	}
	}
}

