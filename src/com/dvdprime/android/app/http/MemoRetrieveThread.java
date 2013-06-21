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

import com.dvdprime.android.app.constants.CurrentInfo;
import com.dvdprime.android.app.db.DBAdapter;
import com.dvdprime.android.app.db.DpDB;
import com.dvdprime.android.app.util.StringUtil;

import android.content.ContentValues;

public class MemoRetrieveThread extends Thread {
	
	private Throwable mException = null;
	private InputStream mIs = null;
	
	public MemoRetrieveThread(String threadName, InputStream is) {
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
    		boolean isWritable = false;
    		ContentValues values = null;
    		
    		DBAdapter dbAdapter = DBAdapter.getInstance();
			// memo list header amount 6,500 characters
			br.skip(6500);

    		while(true){
    			buf = br.readLine();
    			if(buf == null) break;
    			else {
    				if (buf.contains("#ce0000")) {
    					//쪽지 총 개수를 구한다.
    					String totalCnt = StringUtil.substringAfter(buf, "<b>");
    					totalCnt = StringUtil.substringBeforeLast(totalCnt, "</b>");
    					
    					CurrentInfo.MEMO_TATAL_CNT = StringUtil.trimToEmpty(totalCnt);
    				}

    				if (buf.contains("<form name=\"frmList")) {
    					//delete past article content
						dbAdapter.deleteMemoRetrieve();

    					isWritable = true;
    					buf = br.readLine();
    				}

    				if (isWritable) {
    					if (buf.contains("\"memosn\"")) {
    						String memoId = StringUtil.substringAfter(buf, "value=\"");
    						memoId = StringUtil.substringBefore(memoId, "\"");
    						values = new ContentValues();
    						values.put(DpDB.Memo.MEMO_ID, memoId);
    					} else if (buf.contains("<td class=bn colspan=2>")) {
    						String userId = br.readLine().trim();
    						values.put(DpDB.Memo.MEMO_USER_ID, userId);
    					} else if (buf.contains("<td class=txt colspan=2>")) {
    						String content = br.readLine().trim();
    						content = StringUtil.replace(content, "<br>", "\n");
    						values.put(DpDB.Memo.MEMO_CONTENT, content);
    					} else if (buf.contains("<td class=date>")) {
    						String date = StringUtil.substringAfter(buf, ">");
    						date = StringUtil.substringBefore(date, "<");
    						values.put(DpDB.Memo.MEMO_DATE, date);
    						
        					dbAdapter.insertMemo(values);
    					}
    				}
    				
    				if (buf.contains("<!--쪽지 bottom -->"))
    					isWritable = false;
    				
    				if (!isWritable && buf.contains("ico_bbs_pre.gif")) {
    					CurrentInfo.MEMO_PAGE_CNT = StringUtil.split(buf, '[').length - 1;
    					String[] pages = StringUtil.split(buf, '|');
    					CurrentInfo.MEMO_NEXT_EXIST = StringUtil.contains(pages[pages.length-1], "<a");

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
