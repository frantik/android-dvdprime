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
import com.dvdprime.android.app.constants.PreferenceKeys;
import com.dvdprime.android.app.db.DBAdapter;
import com.dvdprime.android.app.db.DpDB;
import com.dvdprime.android.app.util.DpUtil;
import com.dvdprime.android.app.util.PrefUtil;
import com.dvdprime.android.app.util.StringUtil;

import android.content.ContentValues;
import android.content.Context;

public class MessageRetrieveThread extends Thread {
	
	private Context mContext;
	private Throwable mException = null;
	private InputStream mIs = null;
	private String[] filterIds;
	private String[] filterItems;
	
	public MessageRetrieveThread(String threadName, InputStream is) {
		super(threadName);
		mContext = ContextHolder.getInstance().getContext();
		mIs = is; 
		filterIds = StringUtil.split(PrefUtil.getInstance().getString(PreferenceKeys.FILTERING_ID_LIST, null), Const.DEFAULT_SEPARATOR);
		filterItems = StringUtil.split(PrefUtil.getInstance().getString(PreferenceKeys.FILTERING_ITEM_LIST, null), Const.DEFAULT_SEPARATOR);
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
			// article list header amount 32,000 characters
			br.skip(32000);

    		while(true){
    			buf = br.readLine();
    			if(buf == null) break;
    			else {
    				if (buf.contains("#EBEBEB")) {
    					//delete past article content
						dbAdapter.deleteArticleRetrieve();

    					isWritable = true;
    					buf = br.readLine();
    				}

    				if (isWritable) {
    					if (iCount==0 && buf.contains("\"center\"")) {
    						iCount++;
    						String atcNo = br.readLine().trim();
    						values = new ContentValues();
    						if (StringUtil.startsWith(atcNo, "<img") && atcNo.contains("hot")) {
        						values.put(DpDB.Article.ATC_NO, "HOT");
    						} else if (StringUtil.startsWith(atcNo, "<img") && atcNo.contains("cool")) {
        						values.put(DpDB.Article.ATC_NO, "COOL");
    						} else {
    							if (StringUtil.isNotBlank(atcNo)) {
    								values.put(DpDB.Article.ATC_NO, atcNo);
    							} else {
    								iCount = 0;
    							}
    						}
    					} else if (iCount==1 && buf.contains("view.asp")
    									&& (StringUtil.equals(values.getAsString(DpDB.Article.ATC_NO),
    										mContext.getResources().getString(R.string.notice_no))
    										||
    										StringUtil.equals(values.getAsString(DpDB.Article.ATC_NO), "COOL"))) {
    						iCount++;
    						fstIdx = buf.indexOf("\"")+1;
    						lstIdx = buf.indexOf("\"", buf.indexOf("\"")+1);
        					values.put(DpDB.Article.ATC_URL, buf.substring(fstIdx, lstIdx));
	    					values.put(DpDB.Article.ATC_TITLE, buf.substring(lstIdx+2, buf.indexOf("</a>")));
	    					if (buf.lastIndexOf("(") > 0 && buf.lastIndexOf("(") > buf.indexOf("</a>"))
	    						values.put(DpDB.Article.ATC_COMMENT, buf.substring(buf.lastIndexOf("(")+1, buf.lastIndexOf(")")));
    					} else if (iCount==1 && buf.contains("boardtitle")) {
    						iCount++;
    						buf = br.readLine();
    						fstIdx = buf.indexOf("\"")+1;
    						lstIdx = buf.indexOf("\"", buf.indexOf("\"")+1);
        					values.put(DpDB.Article.ATC_URL, buf.substring(fstIdx, lstIdx));
	    					values.put(DpDB.Article.ATC_TITLE, buf.substring(lstIdx+2, buf.indexOf("</a>")));
	    					if (buf.lastIndexOf("(") > 0 && buf.lastIndexOf("(") > buf.indexOf("</a>"))
	    						values.put(DpDB.Article.ATC_COMMENT, buf.substring(buf.lastIndexOf("(")+1, buf.lastIndexOf(")")));
    					} else if (iCount==2 && buf.contains("id_show_menu")) {
    						iCount++;
    						fstIdx = buf.indexOf("'")+1;
    						lstIdx = buf.indexOf("'", buf.indexOf("'")+1);
    						String atcUserName = buf.substring(buf.lastIndexOf(";\">")+3, buf.indexOf("</p>"));
	    						atcUserName = StringUtil.removeAllTags(atcUserName);
        					values.put(DpDB.Article.ATC_USER_ID, buf.substring(fstIdx, lstIdx));
	    					values.put(DpDB.Article.ATC_USER_NAME, atcUserName);
    					} else if (iCount==3 && buf.contains("\"center\"")) {
    						iCount++;
	    					values.put(DpDB.Article.ATC_DATE, buf.substring(buf.lastIndexOf("\"")+2, buf.indexOf("</td>")));
    					} else if (iCount==4 && buf.contains("\"center\"")) {
    						iCount++;
    						if (buf.contains("font"))
    							values.put(DpDB.Article.ATC_RCMD, buf.substring(buf.lastIndexOf("'")+2, buf.indexOf("</font>")));
    						else {
    							if (DpUtil.isLogined() && buf.contains("</a>"))
    								values.put(DpDB.Article.ATC_RCMD, buf.substring(buf.lastIndexOf("\"")+2, buf.indexOf("</a>")));
    							else
        							values.put(DpDB.Article.ATC_RCMD, buf.substring(buf.lastIndexOf("\"")+2, buf.indexOf("</td>")));
    						}
    					} else if (iCount==5 && buf.contains("\"center\"")) {
    						iCount = 0;
    						if (buf.contains("font"))
    							values.put(DpDB.Article.ATC_RCNT, buf.substring(buf.lastIndexOf("'")+2, buf.indexOf("</font>")));
    						else
   								values.put(DpDB.Article.ATC_RCNT, buf.substring(buf.lastIndexOf("\"")+2, buf.indexOf("</td>")));
    						
    						// 필터 적용
    						if (filterIds != null 
    								&& filterIds.length > 0
    								&& StringUtil.contains(filterIds, values.getAsString(DpDB.Article.ATC_USER_ID))
    								&& filterItems != null
    								&& filterItems.length > 0
    								&& StringUtil.contains(filterItems, "article")) {
    						} else {
    							dbAdapter.insertArticle(values);
    						}
    					}
    				}
    				
    				if (isWritable && buf.contains("dot_prepre.gif")) {
    					// add more info
    					values = new ContentValues();
    					values.put(DpDB.Article.ATC_NO, "DP");
    					values.put(DpDB.Article.ATC_TITLE, mContext.getResources().getString(R.string.bbs_progressbar_more));
    					
    					String tmpStr = "";
    					lstIdx = buf.indexOf("dot_next.gif");
    					fstIdx = lstIdx - 200;
    					tmpStr = buf.substring(fstIdx, lstIdx);
    					tmpStr = StringUtil.substringAfter(tmpStr, "href=");
    					tmpStr = StringUtil.substringBefore(tmpStr, ">");
    					
    					if (StringUtil.isNotBlank(tmpStr)) {
	    					values.put(DpDB.Article.ATC_URL, tmpStr);
	    					dbAdapter.insertArticle(values);
    					}
    				}

    				if (isWritable && buf.contains("\"frmSort\"")) {
    					
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
