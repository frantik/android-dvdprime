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

import android.content.ContentValues;

import com.dvdprime.android.app.ContextHolder;
import com.dvdprime.android.app.R;
import com.dvdprime.android.app.constants.Const;
import com.dvdprime.android.app.constants.PreferenceKeys;
import com.dvdprime.android.app.db.DBAdapter;
import com.dvdprime.android.app.db.DpDB;
import com.dvdprime.android.app.util.DpUtil;
import com.dvdprime.android.app.util.PrefUtil;
import com.dvdprime.android.app.util.StringUtil;

public class MessageGetThread extends Thread {
	
	private Throwable mException = null;
	private InputStream mIs = null;
	private PrefUtil prefs;
	private String[] filterIds;
	private String[] filterItems;
	
	public MessageGetThread(String threadName, InputStream is) {
		super(threadName);
		mIs = is; 
		prefs = PrefUtil.getInstance();
		filterIds = StringUtil.split(prefs.getString(PreferenceKeys.FILTERING_ID_LIST, null), Const.DEFAULT_SEPARATOR);
		filterItems = StringUtil.split(prefs.getString(PreferenceKeys.FILTERING_ITEM_LIST, null), Const.DEFAULT_SEPARATOR);
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
    		boolean isWritable1 = false;
    		boolean isWritable2 = false;
    		ContentValues values = null;
    		int upperId = 0;
    		int iCount = 0;
    		int fstIdx = 0;
    		int lstIdx = 0;
    		
    		DBAdapter dbAdapter = DBAdapter.getInstance();
			// article content header amount 20,000 characters
			br.skip(20000);

    		while(true){
    			buf = br.readLine();
    			if(buf == null) break;
    			else {
    				if (buf.contains("marquee")) {
    					//delete past article content
    					dbAdapter.deleteContentRetrieve();

    					isWritable1 = true;
    					buf = br.readLine();
    				}

    				if (isWritable1) {
    					if (iCount==0 && buf.contains("<img")) {
    						iCount++;
    						values = new ContentValues();
    						fstIdx = buf.indexOf("src=")+5;
    						lstIdx = buf.lastIndexOf("width=")-2;
    						values.put(DpDB.Content.CTT_URL, buf.substring(fstIdx, lstIdx));
    					}
    					else if ((iCount==0 || iCount==1) && buf.contains("<!--DCM_TITLE-->")) {
    						if (iCount==0) { // 탈퇴한 사용자일 경우 아바타 이미지가 없음.
    							iCount=1;
    							values = new ContentValues();
    						}
    						
    						iCount++;
    						fstIdx = buf.indexOf("-->")+3;
    						lstIdx = buf.lastIndexOf("<!");
    						values.put(DpDB.Content.CTT_TITLE, buf.substring(fstIdx, lstIdx));
    					} 
    					else if (iCount==2 && buf.contains("divStr")) {
    						iCount++;
    						String header = "";
    						String body = "";
    						String footer = "";
    						if (buf.contains("HTML")) {
	    						fstIdx = buf.indexOf("<body>");
	    						lstIdx = buf.lastIndexOf("</DIV>");
	    						// 본문이 /DIV로 한줄에 안 끝나는 경우 나올때까지 찾는다.
	    						if (lstIdx < 0) {
	    							int findIdx = 0;
	    							while(true) {
		    							buf += br.readLine();
			    						lstIdx = buf.lastIndexOf("</DIV>");
			    						if (lstIdx > -1 || findIdx > 50) {
			    							break;
			    						} else {
			    							findIdx++;
			    						}
	    							}
	    						}
	    						header = "<HTML><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">";
	    						body = buf.substring(fstIdx, lstIdx);
	    				        // 테마적용
	    				        if (StringUtil.equals(prefs.getString(PreferenceKeys.DP_THEME, Const.BLACK_THEME), Const.BLACK_THEME)) {
		    						header += Const.BLACK_THEME_STYLE;
		    						body = StringUtil.replace(body, "<body>", "<body bgcolor='#000000'>");
		    						body = StringUtil.replace(body, "<div id=\"DWMCOLOR\" style=\"background-color:#ffffff;\">", 
		    							"<font color='#dddddd'>");
		    						body = StringUtil.replace(body, "<div id=\"DWMCOLOR\" style=\"background-color:#FFFFFF;\">", 
	    								"<font color='#dddddd'>");
	    				        }
	    						body = StringUtil.replace(body, "\"/dpUserUpImg/", "\""+ Const.HOMEPAGE_URL +"/dpUserUpImg/");
	    						body = DpUtil.replaceImg(body);
	    						footer = "</body></HTML>";

	    						values.put(DpDB.Content.CTT_CONTENT, header+body+footer);
    						} else {
    							fstIdx = buf.indexOf("16px")+6;
	    						lstIdx = buf.lastIndexOf("</div>");
	    						header = "<HTML><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">";
	    				        // 테마적용
	    				        if (StringUtil.equals(prefs.getString(PreferenceKeys.DP_THEME, Const.BLACK_THEME), Const.BLACK_THEME)) { 
		    						header += Const.BLACK_THEME_STYLE;
	    				        	header += "<body bgcolor='#000000'><font color='#dddddd'>";
	    				        } else
	    				        	header += "<body>";
	    						body = buf.substring(fstIdx, lstIdx);
	    						body = StringUtil.replace(body, "\"/dpUserUpImg/", "\""+ Const.HOMEPAGE_URL +"/dpUserUpImg/");
	    						body = DpUtil.replaceImg(body);
	    						footer = "</body></HTML>";

	    						values.put(DpDB.Content.CTT_CONTENT, header+body+footer);
    						}
    					}
    					else if (iCount==3 && buf.contains("tag.gif")) {
    						if (buf.contains("Submit_SearchTag")) {
    							String tag = StringUtil.substringAfter(buf, "absmiddle\"> ");
    							tag = StringUtil.substringBeforeLast(tag, "</td>");
    							tag = DpUtil.deleteTagA(tag);
    							values.put(DpDB.Content.CTT_TAG, tag);
    						}
        					dbAdapter.insertContent(values);
    						isWritable1 = false;
    						iCount=0;
    					}
    				}
    				
    				if (buf.contains("name=\"frmcommend")) {
    					//delete past article content
    					dbAdapter.deleteCommentRetrieve();

    					isWritable2 = true;
    				}
    				
    				if (isWritable2) {
    					if ((iCount==0 || iCount==1) && buf.contains("id_show_menu")) {
    						if (iCount == 0)
    							values = new ContentValues();
    						
    						iCount++;
    						fstIdx = buf.indexOf("'")+1;
    						lstIdx = buf.indexOf("'", buf.indexOf("'")+1);
        					values.put(DpDB.Comment.CMT_USER_ID, buf.substring(fstIdx, lstIdx));
        					
        					fstIdx = buf.indexOf("<strong>")+8;
        					lstIdx = buf.lastIndexOf("</strong>");
        					values.put(DpDB.Comment.CMT_USER_NAME, StringUtil.removeAllTags(buf.substring(fstIdx, lstIdx)));
    					} else if ((iCount==0 || iCount==1) && buf.contains("anchor_")) {
    						if (iCount == 0)
    							values = new ContentValues();
    						
    						iCount++;
    						fstIdx = buf.indexOf("anchor_")+7;
    						lstIdx = buf.lastIndexOf("\">");
    						values.put(DpDB.Comment.CMT_COMMENT_ID, buf.substring(fstIdx, lstIdx));
    					} else if (iCount==2 && buf.contains("#999999")) {
    						iCount++;
    						fstIdx = buf.indexOf("#999999")+9;
    						lstIdx = buf.lastIndexOf("</font>");
    						values.put(DpDB.Comment.CMT_DATE, buf.substring(fstIdx, lstIdx));
    					} else if (iCount==3 && buf.contains("f_memorecommend")) {	//댓글용
    						iCount++;
    						fstIdx = buf.indexOf("title=")+9;
    						lstIdx = buf.indexOf("<a")-2;
//    						values.put(DpDB.Comment.CMT_RCMD, StringUtil.trim(buf.substring(fstIdx, lstIdx)));
    						values.put(DpDB.Comment.CMT_RCMD, "0");
    					} else if (iCount==4 && buf.contains("width=\"55")) {		//댓글용
    						iCount++;
    						fstIdx = buf.indexOf("src=")+5;
    						lstIdx = buf.lastIndexOf("width=")-2;
    						values.put(DpDB.Comment.CMT_URL, buf.substring(fstIdx, lstIdx));
    					} else if ((iCount==5 || iCount==3) && buf.contains("line-height:18px;word-break:")) {	// 덧플은 패스 3으로 체크
    						iCount = 0;
    						String cmtComment = "";
    						if (buf.contains("</font>")) {
	    						fstIdx = buf.indexOf("black\"")+8;
	    						lstIdx = buf.lastIndexOf("</font>");
    						} else {
    							while (true) {
    								buf += br.readLine();
    								if (buf.contains("</font>")) {
    		    						fstIdx = buf.indexOf("black\"")+8;
    		    						lstIdx = buf.lastIndexOf("</font>");
    		    						break;
    								}
    							}
    						}
    						cmtComment = buf.substring(fstIdx, lstIdx);
    						cmtComment = DpUtil.deleteTagA(cmtComment);
    						
    						if (StringUtil.isNotEmpty(values.getAsString(DpDB.Comment.CMT_URL)))
    							values.put(DpDB.Comment.CMT_UPPER, ++upperId);
    						else
    							values.put(DpDB.Comment.CMT_UPPER, upperId);
    							
    						
    						// 필터 적용
    						if (filterIds != null 
    								&& filterIds.length > 0
    								&& StringUtil.contains(filterIds, values.getAsString(DpDB.Comment.CMT_USER_ID))
    								&& filterItems != null
    								&& filterItems.length > 0
    								&& StringUtil.contains(filterItems, "comment")) {
    							// 댓글만 필터링된 댓글로 표시
    							// 댓글의 댓글은 삭제
    							if (values.containsKey(DpDB.Comment.CMT_URL)) {
    								values.put(DpDB.Comment.CMT_CONTENT, ContextHolder.getInstance().getContext().getString(R.string.filtered_comment_message));
        							dbAdapter.insertComment(values);
    							}
    						} else {
        						values.put(DpDB.Comment.CMT_CONTENT, cmtComment);
    							dbAdapter.insertComment(values);
    						}
    					}
    				}
//    				if (isWritable2 && buf.contains("frmReply_10_1518602")) {
//    					for (int i = 0; i < 11; i++) 
//    						buf = br.readLine();
//    					Log.i(DpLogTag.COMMON, "buf ------> " + buf);
//    					
//    					if (buf.contains("center")) {
//    						fstIdx = buf.indexOf("<b>") + 3;
//    						lstIdx = buf.indexOf("</b>");
//    						String userName = buf.substring(fstIdx, lstIdx);
//    						if (StringUtil.isNotBlank(userName))
//    							dbAdapter.updateAccount(userName);
//    					}
//    						
//    				}
    				if (isWritable2 && buf.contains("frmReply_10_1518602")) {
    					// 만약에 코멘트가 없을 경우 빈 코멘트를 넣어준다.
        				if (StringUtil.equals(dbAdapter.getCommentCount(), "1")) {
        					values = new ContentValues();
        					values.put(DpDB.Comment.CMT_USER_ID, "0");
        					dbAdapter.insertComment(values);
        				}
        				
    					isWritable2 = false;
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

