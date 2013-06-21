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

import com.dvdprime.android.app.ContextHolder;
import com.dvdprime.android.app.R;
import com.dvdprime.android.app.constants.Const;
import com.dvdprime.android.app.db.DBAdapter;
import com.dvdprime.android.app.db.DpDB;
import com.dvdprime.android.app.util.StringUtil;

import android.content.ContentValues;
import android.content.Context;

public class DocumentRetrieveThread extends Thread {
	
	private Context mContext;
	private Throwable mException = null;
	private InputStream mIs = null;
	private boolean mMore = false;
	
	public DocumentRetrieveThread(String threadName, InputStream is, boolean isMore) {
		super(threadName);
		mContext = ContextHolder.getInstance().getContext();
		mIs = is; 
		mMore = isMore;
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
    		boolean isWritable = false;
    		ContentValues values = null;
    		int fstIdx = 0;
    		int lstIdx = 0;
    		int iCount = 0;
    		
    		DBAdapter dbAdapter = DBAdapter.getInstance();
			// article list header amount 6,500 characters
			br.skip(6500);

    		while(true){
    			buf = br.readLine();
    			if(buf == null) break;
    			else {
    				if (buf.contains("#EBEBEB")) {
    					
    					if (!mMore) {
	    					//delete past scrap content
							dbAdapter.deleteScrapRetrieve();
    					} else {
        					//delete more record
        					dbAdapter.deleteScrapMore();
    					}

    					isWritable = true;
    					buf = br.readLine();
    				}

    				if (isWritable) {
    					if (iCount==0 && buf.contains("tr height")) {
    						iCount++;
    						values = new ContentValues();
    						buf = br.readLine();
    						fstIdx = buf.indexOf(";")+1;
    						lstIdx = buf.lastIndexOf("<");
    						values.put(DpDB.Scrap.TYPE, buf.substring(fstIdx, lstIdx));
    					} else if (iCount==1 && buf.contains("move_page")) {
    						iCount++;
    						fstIdx = buf.indexOf("\'")+1;
    						lstIdx = buf.indexOf("\'", buf.indexOf("\'")+1);
        					values.put(DpDB.Scrap.URL, buf.substring(fstIdx, lstIdx));
	    					values.put(DpDB.Scrap.TITLE, buf.substring(lstIdx+10, buf.indexOf("</a>")));
    					} else if (iCount==2 && buf.contains("pdate")) {
    						iCount = 0;
    						fstIdx = buf.lastIndexOf('\"')+2;
    						lstIdx = buf.lastIndexOf("</font");
	    					values.put(DpDB.Scrap.DATE, buf.substring(fstIdx, lstIdx));
    						
        					dbAdapter.insertScrap(values);
    					}
    				}
    				
    				if (isWritable && buf.contains("dot_prepre.gif")) {
    					// add more info
    					values = new ContentValues();
    					values.put(DpDB.Scrap.TYPE, "DP");
    					values.put(DpDB.Scrap.TITLE, mContext.getResources().getString(R.string.bbs_progressbar_more));
    					
    					String tmpStr = "";
    					lstIdx = buf.indexOf("dot_next.gif");
    					fstIdx = lstIdx - 70;
    					tmpStr = buf.substring(fstIdx, lstIdx);
    					tmpStr = StringUtil.substringAfter(tmpStr, "Page=");
    					tmpStr = StringUtil.substringBefore(tmpStr, ">");
    					
    					if (StringUtil.isNotBlank(tmpStr)) {
	    					values.put(DpDB.Scrap.URL, Const.MY_ARTICLE_URL + tmpStr);
	    					dbAdapter.insertScrap(values);
    					}
    				}

    				if (isWritable && buf.contains("/TABLE")) {
    					
    					isWritable = false;
    					break;
    				}
    			}
    		}
    		// DB Close
			dbAdapter.close();
//			new DpHtmlParsedData(mIs);
    	} catch (Exception e){
    		mException = e;  
    	}
	}
}
