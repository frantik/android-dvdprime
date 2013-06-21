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
package com.dvdprime.android.app.db;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.dvdprime.android.app.ContextHolder;
import com.dvdprime.android.app.constants.Const;
import com.dvdprime.android.app.constants.MessageHelper;
import com.dvdprime.android.app.model.Article;
import com.dvdprime.android.app.model.Comment;
import com.dvdprime.android.app.model.Memo;
import com.dvdprime.android.app.model.Scrap;
import com.dvdprime.android.app.util.DpUtil;
import com.dvdprime.android.app.util.StringUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.util.Log;

public class DBAdapter {

	private static DBAdapter instance;
	
	private Context	mAppContext;
	private DBHelper		mOpenHelper;
	private String			mUriString;
	private boolean			mbConstraintStop;
	private SQLiteDatabase	mDb;

	public DBAdapter() {
		mAppContext = ContextHolder.getInstance().getContext();
		mOpenHelper = DBHelper.getInstance(mAppContext);
		mUriString = "content://"+ DpDB.AUTHORITY +"/";
		mbConstraintStop = false;
		mDb = null;
	}

	public static synchronized DBAdapter getInstance() {
		if (instance == null) {
			instance = new DBAdapter();
		}
		return instance;
	}
	
	public DBAdapter open() throws SQLException {
		return this;
	}

	public boolean getConstraintStopFlag() {
		return mbConstraintStop;
	}

	public void close() {
		if (mDb != null) {
			DBHelper.decreaseDBRefCount();
			if (DBHelper.getDBRefCount() < 1) {
				DBHelper.resetDBRefCount();
			}
		}
		mDb = null;
	}

	private void getReadableDatabase() throws SQLiteException {
		if (mDb == null) {
			try {
				mDb = mOpenHelper.getReadableDatabase();
				DBHelper.increaseDBRefCount();
			} catch (SQLiteException e) {
				throw e;
			}
		}
	}

	private void getWritableDatabase() throws SQLiteException {
		if (mDb == null) {
			try {
				mDb = mOpenHelper.getWritableDatabase();
				DBHelper.increaseDBRefCount();
			} catch (SQLiteException e) {
				throw e;
			}
		}
	}

	/**
	 * This method return false if the table is empty.
	 * 
	 * @param table table name
	 * @return return false if the talbe is empty 
	 */
	public boolean hasDataInDB(String table) {
		boolean b = true;
		Uri uri = Uri.parse("content://"+ DpDB.AUTHORITY +"/" + table);
		Cursor c = mAppContext.getContentResolver().query(uri, new String[] {"_id"}, null, null, null);
		if(c != null) {
			if(c.getCount() <= 0) {
				b = false;
			}
			c.close();
		}
		return b;
	}

	/**
	 * Drop all tables in DP.db then recreate tables. (except use_contact table)
	 * @return always true
	 */
	public boolean dropAllTables() {
		try {
			getWritableDatabase();
		} catch (SQLiteException e) {
			return false;
		}

		mOpenHelper.upgradeTables(mDb);
		return true;
	}

	/**
	 * Delete records on specific tables by service provider
	 */
	public void clearData() {

		final String TABLES[] = {
				DpDB.ARTICLE_TABLE_NAME,
				DpDB.CONTENT_TABLE_NAME,
				DpDB.COMMENT_TABLE_NAME,
				DpDB.RECEIVCED_PHOTO_TABLE_NAME
		};

		try {
			getWritableDatabase();
		} catch (SQLiteException e) {
			return;
		}        

		mDb.execSQL("BEGIN IMMEDIATE TRANSACTION");
		try {
			StringBuilder whereString = new StringBuilder(); 
			for(int i=0; i<TABLES.length; i++) {
				whereString.delete(0, whereString.length());
				mDb.delete(TABLES[i], whereString.toString(), null);
			}
			mDb.execSQL("COMMIT TRANSACTION");
		} catch (Exception e) {
			mDb.execSQL("ROLLBACK TRANSACTION");
		}
		
		// delete received photo files
		DpUtil.deleteReceivedFiles(mAppContext);
	}
	
	/**
	 * Delete records on specific tables by service provider
	 */
	public void clearPhotoData() {

		final String TABLES[] = {
				DpDB.RECEIVCED_PHOTO_TABLE_NAME
		};

		try {
			getWritableDatabase();
		} catch (SQLiteException e) {
			return;
		}        

		mDb.execSQL("BEGIN IMMEDIATE TRANSACTION");
		try {
			StringBuilder whereString = new StringBuilder(); 
			for(int i=0; i<TABLES.length; i++) {
				whereString.delete(0, whereString.length());
				mDb.delete(TABLES[i], whereString.toString(), null);
			}
			mDb.execSQL("COMMIT TRANSACTION");
		} catch (Exception e) {
			mDb.execSQL("ROLLBACK TRANSACTION");
		}
		
		// delete received photo files
		DpUtil.deleteReceivedFiles(mAppContext);
	}
	
	/**
	 * Delete account record
	 * @return deleted records count
	 */
	public int clearAccount() {
		try {
			getWritableDatabase();
		} catch (SQLiteException e) {
			return 0;
		}

		int cnt = mDb.delete(DpDB.ACCOUNT_TABLE_NAME, null, null);

		return cnt;
	}

	/**
	 * 게시물 목록에 존재하는지 여부를 리턴
	 * @param no
	 * @param title
	 * @return
	 */
	public boolean isExistArticle(String no, String title) {

		boolean bExist = true;
		
		if (!StringUtil.isNumber(no))
			return false;

		try {
			getReadableDatabase();
		} catch (SQLiteException e) {
			return false;
		}
		
		StringBuilder where = new StringBuilder();
		where.append(DpDB.Article.ATC_NO + "=" + DatabaseUtils.sqlEscapeString(no));
		where.append(" AND ");
		where.append(DpDB.Article.ATC_TITLE + "=" + DatabaseUtils.sqlEscapeString(title));

		Cursor cursor = mDb.query(DpDB.ARTICLE_TABLE_NAME, new String[] { DpDB.Article._ID } , where.toString(), null, null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			bExist = true;
		} else {
			bExist = false;
		}
		cursor.close();

		return bExist;
	}

	public Uri searchReceivedPhoto(String url) {
		String mUri = null;
		boolean bSuccess = true;
		// check if there exists more than MAX num of received file.
		try {
			getReadableDatabase();
		} catch (SQLiteException e) {
			return null;
		}

		Cursor cursor = null;
		try {
			cursor = mDb.query(DpDB.RECEIVCED_PHOTO_TABLE_NAME, 
					new String[] { DpDB.ReceivedPhoto._ID } , 
					DpDB.ReceivedPhoto.URL + "=" + DatabaseUtils.sqlEscapeString(java.net.URLEncoder.encode(url, Const.UTF8)), 
							null, null, null, null);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		if (cursor != null) {
			try {
				if (cursor.moveToFirst()) {
					mUri = mUriString + DpDB.RECEIVCED_PHOTO_TABLE_NAME + "/" + cursor.getString(0);
				} else {
					bSuccess = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				bSuccess = false;
			} finally {
				cursor.close();
			}
		}

		if (bSuccess)
			return Uri.parse(mUri);
		else
			return null;		
	}

	/**
	 * Insert Received Photo 
	 * @param url
	 * @param absPath
	 * @return
	 */
	public Uri insertReceivedPhoto(String url, String absPath) {

		String mUri = null;
		boolean bCheckMaxFailed = false;
		Long curTime = null;
		try {
			getWritableDatabase();
		} catch (SQLiteException e) {
			return null;
		} 

		// do update Last Access Time, if any
		HashMap<Integer, Long> latMap = DBHelper.getLATMap();
		if (!latMap.isEmpty()) {
			Set<Integer> idSet = latMap.keySet();
			HashSet<Integer> tempIdSet = new HashSet<Integer>();

			try {
				StringBuilder whereString = new StringBuilder();
				for (int id : idSet) {
					whereString.delete(0, whereString.length());
					whereString.append(DpDB.KEY_ID);
					whereString.append("=");
					whereString.append(id);
					ContentValues cv = new ContentValues();
					cv.put(DpDB.ReceivedPhoto.LAST_ACCESS_TIME, latMap.get(id));
					mDb.update(DpDB.RECEIVCED_PHOTO_TABLE_NAME, cv, whereString.toString(), null);
					tempIdSet.add(id);
				}
			} catch (Exception e) {
				if (MessageHelper.DEBUG) {
					Log.d("P", "insertReceivedPhoto : Exception occurred");
				}
			} finally {
				for (int id : tempIdSet)
					DBHelper.removeLATMap(id);
			}
		}

		// check if there exists more than MAX num of received file.
		Cursor c = mDb.query(DpDB.RECEIVCED_PHOTO_TABLE_NAME, 
				new String[] { DpDB.ReceivedPhoto._ID, DpDB.ReceivedPhoto.FILEPATH } , 
				null, null, null, null, DpDB.ReceivedPhoto.LAST_ACCESS_TIME + " ASC");
		if (c != null) {
			try {
				if (c.moveToFirst()) {
					// delete oldest one.
					if (c.getCount() >= DpDB.ReceivedPhoto.MAX_ROW_COUNT) {
						if ( mDb.delete(DpDB.RECEIVCED_PHOTO_TABLE_NAME, 
								DpDB.ReceivedPhoto._ID + "=" + 
								c.getString(c.getColumnIndex(DpDB.ReceivedPhoto._ID)), null) <= 0) {
							// discard current file.
							File file = new File(absPath);
							if (file.exists())
								file.delete();
							throw new Exception("Received Photo Max Reached !!!");

						} else {
							// delete oldest file.
							File file = new File(c.getString(c.getColumnIndex(DpDB.ReceivedPhoto.FILEPATH)));
							if (file.exists())
								file.delete();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				bCheckMaxFailed = true;
			} finally {
				c.close();
			}
		}

		if (bCheckMaxFailed == false) {
			try {
				ContentValues cv = new ContentValues();
				cv.put(DpDB.ReceivedPhoto.URL, java.net.URLEncoder.encode(url, Const.UTF8));
				cv.put(DpDB.ReceivedPhoto.FILEPATH, absPath);
				curTime = System.currentTimeMillis();
				cv.put(DpDB.ReceivedPhoto.LAST_ACCESS_TIME, curTime);

				// constraint fail.
				if (mDb.insert(DpDB.RECEIVCED_PHOTO_TABLE_NAME, null, cv) == -1) {
					// discard current file.
					File file = new File(absPath);
					if (file.exists())
						file.delete();
				}

				// check if there exists more than MAX num of received file.
				Cursor cursor = mDb.query(DpDB.RECEIVCED_PHOTO_TABLE_NAME, 
						new String[] { DpDB.ReceivedPhoto._ID } , 
									DpDB.ReceivedPhoto.URL + "=" + DatabaseUtils.sqlEscapeString(java.net.URLEncoder.encode(url, Const.UTF8)), 
									null, null, null, null);
				if (cursor != null) {
					try {
						cursor.moveToFirst();
						mUri = mUriString + DpDB.RECEIVCED_PHOTO_TABLE_NAME + "/" + cursor.getString(0);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						cursor.close();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return (mUri!=null)?Uri.parse(mUri):null;
	}


	public String getAccountName() {
		String result = "";
		StringBuilder where = new StringBuilder();
		where.append(DpDB.Account._ID + " = 1");

		try {
			getReadableDatabase();
		} catch (SQLiteException e) {
			return result;
		}

		Cursor c = mDb.query(DpDB.ACCOUNT_TABLE_NAME, new String[] {DpDB.Account.ACCOUNT_NAME}, where.toString(),
				null, null, null, null);

		if (c != null) {
			if (c.moveToNext()) {
				result = c.getString(0);
			}
			c.close();
		}

		return result;
	}

	public String getAccountAvartar() {
		String result = "";
		StringBuilder where = new StringBuilder();
		where.append(DpDB.Account._ID + " = 1");

		try {
			getReadableDatabase();
		} catch (SQLiteException e) {
			return result;
		}

		Cursor c = mDb.query(DpDB.ACCOUNT_TABLE_NAME, new String[] {DpDB.Account.ACCOUNT_AVARTAR}, where.toString(),
				null, null, null, null);

		if (c != null) {
			if (c.moveToNext()) {
				result = c.getString(0);
			}
			c.close();
		}

		return result;
	}

	public String getCommentCount() {
		String result = "1";

		try {
			getReadableDatabase();
		} catch (SQLiteException e) {
			return result;
		}

		Cursor c = mDb.query(DpDB.COMMENT_TABLE_NAME, new String[] {"count(*)+1 as count"}, null,
				null, null, null, null);

		if (c != null) {
			if (c.moveToNext()) {
				result = c.getString(0);
			}
			c.close();
		}

		return result;
	}

	/**
	 * 게시물 목록
	 * @return
	 */
	public List<Article> getArticleList() {
		try {
			getReadableDatabase();
		} catch (SQLiteException e) {
			return null;
		}
		
		List<Article> list = new ArrayList<Article>();
		
        String[] cols = new String[] {
        		DpDB.Article._ID,
        		DpDB.Article.ATC_NO,
        		DpDB.Article.ATC_TITLE,
        		DpDB.Article.ATC_URL,
        		DpDB.Article.ATC_USER_ID,
        		DpDB.Article.ATC_USER_NAME,
        		DpDB.Article.ATC_DATE,
        		DpDB.Article.ATC_COMMENT,
        		DpDB.Article.ATC_RCMD,
        		DpDB.Article.ATC_RCNT
        };
		StringBuilder where = new StringBuilder();
		where.append(DpDB.Article.ATC_NO + " != 'DP'");

		Cursor c = mDb.query(DpDB.ARTICLE_TABLE_NAME, cols, where.toString(),
				null, null, null, DpDB.Article.DEFAULT_SORT_ORDER);

		if (c != null && c.moveToFirst()) {
			do {
				Article article = new Article();
				
				article.setId(c.getString(0));
				article.setNo(c.getString(1));
				article.setTitle(c.getString(2));
				article.setUrl(c.getString(3));
				article.setUserId(c.getString(4));
				article.setUserName(c.getString(5));
				article.setDate(c.getString(6));
				article.setComment(c.getString(7));
				article.setRecommend(c.getString(8));
				article.setCount(c.getString(9));

				list.add(article);
			} while(c.moveToNext());
		}
		
		if (c != null)
			c.close();
		
		return list;
	}
	
	/**
	 * 댓글 목록
	 * @return
	 */
	public List<Comment> getCommentList() {
		try {
			getReadableDatabase();
		} catch (SQLiteException e) {
			return null;
		}
		
		List<Comment> list = new ArrayList<Comment>();
		
        String[] cols = new String[] {
        		DpDB.Comment._ID,
        		DpDB.Comment.CMT_USER_ID,
        		DpDB.Comment.CMT_USER_NAME,
        		DpDB.Comment.CMT_CONTENT,
        		DpDB.Comment.CMT_URL,
        		DpDB.Comment.CMT_DATE,
        		DpDB.Comment.CMT_RCMD,
        		DpDB.Comment.CMT_COMMENT_ID,
        		DpDB.Comment.CMT_UPPER
        };

		Cursor c = mDb.query(DpDB.COMMENT_TABLE_NAME, cols, null,
				null, null, null, DpDB.Comment.DEFAULT_SORT_ORDER);

		if (c != null && c.moveToFirst()) {
			do {
				Comment comment = new Comment();
				
				comment.setId(c.getString(0));
				comment.setUserId(c.getString(1));
				comment.setUserName(c.getString(2));
				comment.setContent(c.getString(3));
				comment.setAvatarUrl(c.getString(4));
				comment.setDate(c.getString(5));
				comment.setRecommend(c.getString(6));
				comment.setCommentId(c.getString(7));
				comment.setUpper(c.getString(8));

				list.add(comment);
			} while(c.moveToNext());
		}
		
		if (c != null)
			c.close();
		
		return list;
	}
	
	/**
	 * 댓글의 특정 아이디 까지의 개수 구하기
	 * @param cid
	 * @return
	 */
	public int getCommentBelowCount(String cid) {
		try {
			getReadableDatabase();
		} catch (SQLiteException e) {
			return 0;
		}
		
		int count = 0;
        String[] cols = new String[] {
        		DpDB.Comment._ID,
        		DpDB.Comment.CMT_COMMENT_ID
        };
        
		String where = DpDB.Comment.CMT_COMMENT_ID + " = '" + cid + "'";

		Cursor c = mDb.query(DpDB.COMMENT_TABLE_NAME, cols, where,
				null, null, null, DpDB.Comment.DEFAULT_SORT_ORDER);

		if (c != null && c.moveToFirst()) {
			where = DpDB.Comment._ID + "<=" + c.getInt(0);
			
			c = mDb.query(DpDB.COMMENT_TABLE_NAME, cols, where, null, null, null, null);
			
			if (c != null)
				count = c.getCount();
		}
		
		if (c != null)
			c.close();
		
		return count;
	}
	
	/**
	 * 쪽지 목록
	 * @return
	 */
	public List<Memo> getMemoList() {
		try {
			getReadableDatabase();
		} catch (SQLiteException e) {
			return null;
		}
		
		List<Memo> list = new ArrayList<Memo>();
		
        String[] cols = new String[] {
        		DpDB.Memo._ID,
        		DpDB.Memo.MEMO_ID,
        		DpDB.Memo.MEMO_USER_ID,
        		DpDB.Memo.MEMO_USER_NAME,
        		DpDB.Memo.MEMO_CONTENT,
        		DpDB.Memo.MEMO_DATE
        };

		Cursor c = mDb.query(DpDB.MEMO_TABLE_NAME, cols, null,
				null, null, null, DpDB.Article.DEFAULT_SORT_ORDER);

		if (c != null && c.moveToFirst()) {
			do {
				Memo memo = new Memo();
				
				memo.setId(c.getString(0));
				memo.setMemoId(c.getString(1));
				memo.setUserId(c.getString(2));
				memo.setUserName(c.getString(3));
				memo.setContent(c.getString(4));
				memo.setDate(c.getString(5));

				list.add(memo);
			} while(c.moveToNext());
		}
		
		if (c != null)
			c.close();
		
		return list;
	}
	
	/**
	 * 스크랩 목록
	 * @return
	 */
	public List<Scrap> getScrapList(String type) {
		try {
			getReadableDatabase();
		} catch (SQLiteException e) {
			return null;
		}
		
		List<Scrap> list = new ArrayList<Scrap>();
		
        String[] cols = new String[] {
        		DpDB.Scrap._ID,
        		DpDB.Scrap.TYPE,
        		DpDB.Scrap.TITLE,
        		DpDB.Scrap.URL,
        		DpDB.Scrap.USER_NAME,
        		DpDB.Scrap.DATE,
        		DpDB.Scrap.COMMENT,
        		DpDB.Scrap.RCMD,
        		DpDB.Scrap.RCNT
        };
		StringBuilder where = new StringBuilder();
		where.append(DpDB.Scrap.TYPE + " != 'DP'");

		Cursor c = mDb.query(DpDB.SCRAP_TABLE_NAME, cols, where.toString(),
				null, null, null, DpDB.Scrap.DEFAULT_SORT_ORDER);

		if (c != null && c.moveToFirst()) {
			do {
				Scrap scrap = new Scrap();
				
				scrap.setId(c.getString(0));
				scrap.setNo(c.getString(1));
				scrap.setTitle(c.getString(2));
				scrap.setUrl(c.getString(3));
				scrap.setUserName(c.getString(4));
				scrap.setDate(c.getString(5));
				scrap.setComment(c.getString(6));
				scrap.setRecommend(c.getString(7));
				scrap.setCount(c.getString(8));
				scrap.setType(type);

				list.add(scrap);
			} while(c.moveToNext());
		}
		
		if (c != null)
			c.close();
		
		return list;
	}
	
	/**
	 * 글 목록의 더보기 URL을 구해온다.
	 * @return
	 */
	public String getArticleMoreUrl() {
		try {
			getReadableDatabase();
		} catch (SQLiteException e) {
			return null;
		}
		
		String url = null;
        String[] cols = new String[] {
        		DpDB.Article.ATC_URL
        };
		StringBuilder where = new StringBuilder();
		where.append(DpDB.Article.ATC_NO + " = 'DP'");

		Cursor c = mDb.query(DpDB.ARTICLE_TABLE_NAME, cols, where.toString(),
				null, null, null, null);

		if (c != null && c.moveToNext()) {
			url = c.getString(0);
		}
		
		if (c != null)
			c.close();
		
		return url;
	}

	/**
	 * 글 목록의 더보기 URL을 구해온다.
	 * @return
	 */
	public String getScrapMoreUrl() {
		try {
			getReadableDatabase();
		} catch (SQLiteException e) {
			return null;
		}
		
		String url = null;
        String[] cols = new String[] {
        		DpDB.Scrap.URL
        };
		StringBuilder where = new StringBuilder();
		where.append(DpDB.Scrap.TYPE + " = 'DP'");

		Cursor c = mDb.query(DpDB.SCRAP_TABLE_NAME, cols, where.toString(),
				null, null, null, null);

		if (c != null && c.moveToNext()) {
			url = c.getString(0);
		}
		
		if (c != null)
			c.close();
		
		return url;
	}

	/**
	 * Update account records
	 * @param values article data
	 * @return last inserted article row Id
	 */
	public int updateAccount(ContentValues values){
		int rowId = 0;
		
		try {
			getWritableDatabase();
		} catch (SQLiteException e) {
			return rowId;
		}

		StringBuilder where = new StringBuilder();
		where.append(DpDB.Account._ID + " = 1");

		rowId = mDb.update(DpDB.ACCOUNT_TABLE_NAME, values, where.toString(), null);
		return rowId;
	}

	/**
	 * Update comment recommend records
	 * @param commentId commentId data
	 * @return last updated article row Id
	 */
	public int updateRecommend(String commentId){
		int rowId = 0;
		int recommend = 0;
		
		try {
			getWritableDatabase();
		} catch (SQLiteException e) {
			return rowId;
		}
		
		StringBuilder where = new StringBuilder();
		where.append(DpDB.Comment.CMT_COMMENT_ID + " = " + commentId);
		
		Cursor c = mDb.query(DpDB.COMMENT_TABLE_NAME, new String[] {DpDB.Comment.CMT_RCMD}, where.toString(), null, null, null, null);
		if (c != null && c.moveToFirst())
			recommend = c.getInt(0);

		ContentValues values = new ContentValues();
		values.put(DpDB.Comment.CMT_RCMD, recommend + 1);

		rowId = mDb.update(DpDB.COMMENT_TABLE_NAME, values, where.toString(), null);
		return rowId;
	}

	/**
	 * Insert article records
	 * @param values article data
	 * @return last inserted article row Id
	 */
	public long insertArticle(ContentValues values){
		long rowId = 0;
		rowId = mDb.insert(DpDB.ARTICLE_TABLE_NAME, null, values);
		return rowId;
	}

	/**
	 * Insert content records
	 * @param values article data
	 * @return last inserted article row Id
	 */
	public long insertContent(ContentValues values){
		long rowId = 0;
		rowId = mDb.insert(DpDB.CONTENT_TABLE_NAME, null, values);
		return rowId;
	}

	/**
	 * Insert comment records
	 * @param values article data
	 * @return last inserted article row Id
	 */
	public long insertComment(ContentValues values){
		long rowId = 0;
		rowId = mDb.insert(DpDB.COMMENT_TABLE_NAME, null, values);
		return rowId;
	}

	/**
	 * Insert memo records
	 * @param values memo data
	 * @return last inserted memo row Id
	 */
	public long insertMemo(ContentValues values){
		long rowId = 0;
		rowId = mDb.insert(DpDB.MEMO_TABLE_NAME, null, values);
		return rowId;
	}

	/**
	 * Insert scrap records
	 * @param values scrap data
	 * @return last inserted scrap row Id
	 */
	public long insertScrap(ContentValues values){
		long rowId = 0;
		rowId = mDb.insert(DpDB.SCRAP_TABLE_NAME, null, values);
		return rowId;
	}

	/**
	 * Delete article retrieve.
	 * @return deleted article retrieve
	 */
	public int deleteArticleRetrieve() {
		try {
			getWritableDatabase();
		} catch (SQLiteException e) {
			return 0;
		}

		return delete(DpDB.ARTICLE_TABLE_NAME, null, null);
	}

	/**
	 * Delete article more.
	 * @return deleted article retrieve
	 */
	public int deleteArticleMore() {
		try {
			getWritableDatabase();
		} catch (SQLiteException e) {
			return 0;
		}
		StringBuilder where = new StringBuilder();
		where.append(DpDB.Article.ATC_NO + " = 'DP'");

		return delete(DpDB.ARTICLE_TABLE_NAME, where.toString(), null);
	}

	/**
	 * Delete article retrieve.
	 * @return deleted article retrieve
	 */
	public int deleteContentRetrieve() {
		try {
			getWritableDatabase();
		} catch (SQLiteException e) {
			return 0;
		}

		return delete(DpDB.CONTENT_TABLE_NAME, null, null);
	}

	/**
	 * Delete comment retrieve.
	 * @return deleted article retrieve
	 */
	public int deleteCommentRetrieve() {
		try {
			getWritableDatabase();
		} catch (SQLiteException e) {
			return 0;
		}

		return delete(DpDB.COMMENT_TABLE_NAME, null, null);
	}

	/**
	 * Delete comment retrieve.
	 * @param commentId Comment ID
	 * @return deleted article retrieve
	 */
	public int deleteComment(String commentId) {
		try {
			getWritableDatabase();
		} catch (SQLiteException e) {
			return 0;
		}

		StringBuilder where = new StringBuilder();
		where.append(DpDB.Comment.CMT_COMMENT_ID + " = " + commentId);

		return delete(DpDB.COMMENT_TABLE_NAME, where.toString(), null);
	}

	/**
	 * Delete memo retrieve.
	 * @return deleted memo retrieve
	 */
	public int deleteMemoRetrieve() {
		try {
			getWritableDatabase();
		} catch (SQLiteException e) {
			return 0;
		}

		return delete(DpDB.MEMO_TABLE_NAME, null, null);
	}

	/**
	 * Delete scrap retrieve.
	 * @return deleted scrap retrieve
	 */
	public int deleteScrapRetrieve() {
		try {
			getWritableDatabase();
		} catch (SQLiteException e) {
			return 0;
		}

		return delete(DpDB.SCRAP_TABLE_NAME, null, null);
	}

	/**
	 * Delete scrap more.
	 * @return deleted scrap retrieve
	 */
	public int deleteScrapMore() {
		try {
			getWritableDatabase();
		} catch (SQLiteException e) {
			return 0;
		}
		StringBuilder where = new StringBuilder();
		where.append(DpDB.Scrap.TYPE + " = 'DP'");

		return delete(DpDB.SCRAP_TABLE_NAME, where.toString(), null);
	}

	/**
	 * Delete records on specific table.
	 * @param table table name
	 * @param where where condition
	 * @param whereArgs where argument
	 * @return deleted record count
	 */
	public int delete(String table, String where, String[] whereArgs) {
		return mDb.delete(table, where, whereArgs);
	}

	/**
	 * Delete received photo
	 * @return deleted received photo
	 */
	public int deleteReceivedPhoto(String url) {
		try {
			getWritableDatabase();
		} catch (SQLiteException e) {
			return 0;
		}
		StringBuilder where = new StringBuilder();
		try {
			where.append(DpDB.ReceivedPhoto.URL + "=" + DatabaseUtils.sqlEscapeString(java.net.URLEncoder.encode(url, Const.UTF8)));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return delete(DpDB.RECEIVCED_PHOTO_TABLE_NAME, where.toString(), null);
	}

	public static Uri getTypicalContentUriByReqType(int reqType, String arg0, String arg1) {
		String result = "content://"+ DpDB.AUTHORITY +"/";

		switch(reqType) {
//		case DpReqType.MESSAGE_RETRIEVE: {
//			result = result + DpDB.ARTICLE_TABLE_NAME;
//		}
//		break;
		default:
			result = null;
			break;
		}

		return (result!=null)?Uri.parse(result):null;
	}

}