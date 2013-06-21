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

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	
	private final String DP_DB_PRIMARY_KEY_TYPE	= "INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL";
	private final String DP_DB_INTEGER_TYPE		= "INTEGER";
	private final String DP_DB_ID_TYPE			= "VARCHAR(20)";
	private final String DP_DB_NAME_TYPE		= "VARCHAR(100)";
	private final String DP_DB_DATE_TYPE2		= "VARCHAR(30)";
	private final String DP_DB_TITLE_TYPE		= "TEXT";
	private final String DP_DB_CONTENT_TYPE		= "TEXT";
	private final String DP_DB_URL_TYPE			= "VARCHAR(1024)";
	private final String DP_DB_URL_FILEPATH		= "VARCHAR(512)";
	private final String DP_DB_TIME_TYPE		= "TIMESTAMP";
	
	private static DBHelper mInstance = null;
	private static final String TAG = "DpDB";
	private static HashMap<Integer, Long> mLATMap = new HashMap<Integer, Long>();
	private static int mDBRefCount = 0;

	private DBHelper(Context context) {
		super(context, DpDB.DATABASE_NAME, null, DpDB.DATABASE_VERSION);
	}

	/**
	 * Return DpDBHelper instance
	 * @param context context
	 * @return DpDBHelper instance
	 */
	public static synchronized DBHelper getInstance(Context context) {
	    if(mInstance == null) {
	        mInstance = new DBHelper(context);
	    }
	    return mInstance;
	}
	
	public static synchronized HashMap<Integer, Long> getLATMap() {
		return mLATMap;
	}
	
	public static synchronized void removeLATMap(int id) {
		mLATMap.remove(id);
	}
	
	public static synchronized int getDBRefCount() {
		return mDBRefCount;
	}

	public static synchronized void resetDBRefCount() {
		mDBRefCount = 0;
	}

	public static synchronized void increaseDBRefCount() {
		mDBRefCount++;
	}

	public static synchronized void decreaseDBRefCount() {
		mDBRefCount--;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		createTables(db);
	}

    /**
     * Create tables
     * @param db dpdb instance
     */
    public void createTables(SQLiteDatabase db) {
		db.beginTransaction();
		
		// Account
		db.execSQL("CREATE TABLE IF NOT EXISTS "	+ DpDB.ACCOUNT_TABLE_NAME		+ " (" 
				+ DpDB.Account._ID					+ " " + DP_DB_PRIMARY_KEY_TYPE	+ ","
				+ DpDB.Account.ACCOUNT_NAME			+ " " + DP_DB_NAME_TYPE			+ ","
				+ DpDB.Account.ACCOUNT_AVARTAR		+ " " + DP_DB_URL_TYPE
				+ ");" );
		
		Log.d(TAG, "DB TABLE(" + DpDB.ACCOUNT_TABLE_NAME + ") Has been created." );
		
		// BBS
		db.execSQL("CREATE TABLE IF NOT EXISTS "  + DpDB.BBS_TABLE_NAME   + " (" 
		        + DpDB.Bbs._ID           		  + " " + DP_DB_PRIMARY_KEY_TYPE  + ","
		        + DpDB.Bbs.TOP_ID			      + " " + DP_DB_INTEGER_TYPE      + ","
		        + DpDB.Bbs.CAT_ID			      + " " + DP_DB_INTEGER_TYPE      + ","
		        + DpDB.Bbs.BBS_ID		          + " " + DP_DB_INTEGER_TYPE      + ","
		        + DpDB.Bbs.TITLE			      + " " + DP_DB_TITLE_TYPE        + ","
		        + DpDB.Bbs.MAJOR				  + " " + DP_DB_ID_TYPE		      + ","
		        + DpDB.Bbs.MINOR				  + " " + DP_DB_ID_TYPE		      + ","
		        + DpDB.Bbs.MASTER_ID			  + " " + DP_DB_ID_TYPE		      + ","
		        + DpDB.Bbs.TARGET_URL	          + " " + DP_DB_URL_FILEPATH	  + ","
		        + DpDB.Bbs.LOGIN_CHECK			  + " " + DP_DB_INTEGER_TYPE	  + " DEFAULT 0"
		        + ");" );
		
		db.execSQL("CREATE INDEX catIdIndex ON " + DpDB.BBS_TABLE_NAME
		        + " (" + DpDB.Bbs.CAT_ID + ");");
		
		Log.d(TAG, "DB TABLE(" + DpDB.BBS_TABLE_NAME + ") Has been created." );
		
		// Article
		db.execSQL("CREATE TABLE IF NOT EXISTS "	+ DpDB.ARTICLE_TABLE_NAME		+ " (" 
				+ DpDB.Article._ID					+ " " + DP_DB_PRIMARY_KEY_TYPE	+ ","
                + DpDB.Article.ATC_NO	         	+ " " + DP_DB_INTEGER_TYPE     	+ ","
				+ DpDB.Article.ATC_TITLE			+ " " + DP_DB_TITLE_TYPE		+ ","
				+ DpDB.Article.ATC_URL				+ " " + DP_DB_URL_TYPE			+ ","
				+ DpDB.Article.ATC_USER_ID			+ " " + DP_DB_ID_TYPE			+ ","
				+ DpDB.Article.ATC_USER_NAME		+ " " + DP_DB_NAME_TYPE			+ ","
				+ DpDB.Article.ATC_DATE				+ " " + DP_DB_DATE_TYPE2		+ ","
				+ DpDB.Article.ATC_COMMENT			+ " " + DP_DB_INTEGER_TYPE		+ " DEFAULT 0,"
                + DpDB.Article.ATC_RCMD	         	+ " " + DP_DB_INTEGER_TYPE     	+ " DEFAULT 0,"
                + DpDB.Article.ATC_RCNT	         	+ " " + DP_DB_INTEGER_TYPE		+ " DEFAULT 0"
				+ ");" );

		db.execSQL("CREATE INDEX atcNoIndexOnArticle ON " + DpDB.ARTICLE_TABLE_NAME + " (" + DpDB.Article.ATC_NO + ");");

        Log.d(TAG, "DB TABLE(" + DpDB.ARTICLE_TABLE_NAME + ") Has been created." );
		
		// Content
		db.execSQL("CREATE TABLE IF NOT EXISTS "	+ DpDB.CONTENT_TABLE_NAME		+ " (" 
				+ DpDB.Content._ID					+ " " + DP_DB_PRIMARY_KEY_TYPE	+ ","
				+ DpDB.Content.CTT_TITLE			+ " " + DP_DB_TITLE_TYPE		+ ","
				+ DpDB.Content.CTT_CONTENT			+ " " + DP_DB_CONTENT_TYPE		+ ","
				+ DpDB.Content.CTT_URL				+ " " + DP_DB_URL_TYPE			+ ","
				+ DpDB.Content.CTT_TAG				+ " " + DP_DB_CONTENT_TYPE
				+ ");" );

        Log.d(TAG, "DB TABLE(" + DpDB.CONTENT_TABLE_NAME + ") Has been created." );
		
		// Comment
		db.execSQL("CREATE TABLE IF NOT EXISTS "	+ DpDB.COMMENT_TABLE_NAME		+ " (" 
				+ DpDB.Comment._ID					+ " " + DP_DB_PRIMARY_KEY_TYPE	+ ","
				+ DpDB.Comment.CMT_USER_ID			+ " " + DP_DB_ID_TYPE			+ ","
				+ DpDB.Comment.CMT_USER_NAME		+ " " + DP_DB_NAME_TYPE			+ ","
				+ DpDB.Comment.CMT_CONTENT			+ " " + DP_DB_CONTENT_TYPE		+ ","
				+ DpDB.Comment.CMT_URL				+ " " + DP_DB_URL_TYPE			+ ","
				+ DpDB.Comment.CMT_DATE				+ " " + DP_DB_DATE_TYPE2		+ ","
                + DpDB.Comment.CMT_RCMD	         	+ " " + DP_DB_INTEGER_TYPE		+ " DEFAULT 0,"
                + DpDB.Comment.CMT_COMMENT_ID		+ " " + DP_DB_ID_TYPE			+ ","
                + DpDB.Comment.CMT_UPPER			+ " " + DP_DB_INTEGER_TYPE
				+ ");" );

		db.execSQL("CREATE INDEX atcNoIndexOnComment ON " + DpDB.COMMENT_TABLE_NAME + " (" + DpDB.Comment._ID + ");");

		db.execSQL("CREATE INDEX atcUpperDateIndexOnComment ON " + DpDB.COMMENT_TABLE_NAME + " (" + DpDB.Comment.CMT_UPPER + "," + DpDB.Comment._ID + ");");

		Log.d(TAG, "DB TABLE(" + DpDB.COMMENT_TABLE_NAME + ") Has been created." );
		
		// Memo
		db.execSQL("CREATE TABLE IF NOT EXISTS "	+ DpDB.MEMO_TABLE_NAME			+ " (" 
				+ DpDB.Memo._ID						+ " " + DP_DB_PRIMARY_KEY_TYPE	+ ","
				+ DpDB.Memo.MEMO_ID					+ " " + DP_DB_ID_TYPE			+ ","
				+ DpDB.Memo.MEMO_USER_ID			+ " " + DP_DB_ID_TYPE			+ ","
				+ DpDB.Memo.MEMO_USER_NAME			+ " " + DP_DB_NAME_TYPE			+ ","
				+ DpDB.Memo.MEMO_CONTENT			+ " " + DP_DB_CONTENT_TYPE		+ ","
				+ DpDB.Memo.MEMO_DATE				+ " " + DP_DB_DATE_TYPE2
				+ ");" );

		Log.d(TAG, "DB TABLE(" + DpDB.MEMO_TABLE_NAME + ") Has been created." );
		
		// Scrap
		db.execSQL("CREATE TABLE IF NOT EXISTS "	+ DpDB.SCRAP_TABLE_NAME		+ " (" 
				+ DpDB.Scrap._ID					+ " " + DP_DB_PRIMARY_KEY_TYPE	+ ","
                + DpDB.Scrap.TYPE		         	+ " " + DP_DB_NAME_TYPE     	+ ","
				+ DpDB.Scrap.TITLE					+ " " + DP_DB_TITLE_TYPE		+ ","
				+ DpDB.Scrap.URL					+ " " + DP_DB_URL_TYPE			+ ","
				+ DpDB.Scrap.USER_NAME				+ " " + DP_DB_NAME_TYPE			+ ","
				+ DpDB.Scrap.DATE					+ " " + DP_DB_DATE_TYPE2		+ ","
				+ DpDB.Scrap.COMMENT				+ " " + DP_DB_INTEGER_TYPE		+ " DEFAULT 0,"
                + DpDB.Scrap.RCMD		         	+ " " + DP_DB_INTEGER_TYPE     	+ " DEFAULT 0,"
                + DpDB.Scrap.RCNT		         	+ " " + DP_DB_INTEGER_TYPE		+ " DEFAULT 0"
				+ ");" );

        Log.d(TAG, "DB TABLE(" + DpDB.SCRAP_TABLE_NAME + ") Has been created." );
		
		// ReceivedPhoto
		db.execSQL("CREATE TABLE IF NOT EXISTS "	+ DpDB.RECEIVCED_PHOTO_TABLE_NAME		+ " (" 
				+ DpDB.ReceivedPhoto._ID					+ " " + DP_DB_PRIMARY_KEY_TYPE	+ ","
				+ DpDB.ReceivedPhoto.URL					+ " " + DP_DB_URL_TYPE			+ ","
				+ DpDB.ReceivedPhoto.FILEPATH				+ " " + DP_DB_URL_FILEPATH		+ ","
				+ DpDB.ReceivedPhoto.LAST_ACCESS_TIME		+ " " + DP_DB_TIME_TYPE			+ ","
				+ "UNIQUE("+DpDB.ReceivedPhoto.URL+")"
				+ ");" );

		Log.d(TAG, "DB TABLE(" + DpDB.RECEIVCED_PHOTO_TABLE_NAME + ") Has been created." );
		
		db.execSQL("CREATE INDEX IF NOT EXISTS photoIdxIndex ON " 
				+ DpDB.RECEIVCED_PHOTO_TABLE_NAME + " (" + DpDB.ReceivedPhoto._ID + ");");

		db.execSQL("CREATE INDEX IF NOT EXISTS photoUrlIndex ON " 
				+ DpDB.RECEIVCED_PHOTO_TABLE_NAME + " (" + DpDB.ReceivedPhoto.URL + ");");

		Cursor c = db.query(DpDB.ACCOUNT_TABLE_NAME, new String[] {"count(*)"}, null, null, null, null, null);
		if (c != null && c.moveToFirst() && c.getInt(0) == 0) {
			ContentValues cv = new ContentValues();
			cv.put(DpDB.Account.ACCOUNT_NAME, "");
			cv.put(DpDB.Account.ACCOUNT_AVARTAR, "");
			db.insert(DpDB.ACCOUNT_TABLE_NAME, null, cv);
			Log.d(TAG, "DB TABLE(" + DpDB.ACCOUNT_TABLE_NAME + ") Has been inserted default data." );
		}
		if (c != null)
			c.close();
		
		// BBS Data Insert
		String[][] bbsData = { {"0", "0", "-1", "���߰����", "", "", "", "", ""},
				{"0", "0", "0", "������ ������", "ME", "E1", "40", "/bbs/list.asp?major=ME&minor=E1&master_id=40", "0"},
//				{"0", "0", "1", "140�� �Խ���", "140", "E1", "0", "/bbs/FreeTalk.asp", "0"},
				{"0", "0", "2", "�û�/��ġ/����", "ME", "E1", "172", "/bbs/list.asp?major=ME&minor=E1&master_id=172", "0"},
				{"0", "0", "3", "�г�Խ���", "ME", "E1", "173", "/bbs/list.asp?major=ME&minor=E1&master_id=173", "0"},
//				{"0", "0", "3", "DP�� ���� ȫ���Խ���", "ME", "E1", "177", "/bbs/list.asp?major=ME&minor=E1&master_id=177", "0"},
//				{"0", "0", "4", "���ưԽ���", "ME", "E1", "176", "/bbs/list.asp?major=ME&minor=E1&master_id=176", "0"},
				{"0", "0", "5", "�� ����� �´´�", "ME", "E1", "163", "/bbs/list.asp?major=ME&minor=E1&master_id=163", "0"},
				{"0", "0", "6", "��������", "ME", "E1", "193", "/bbs/list.asp?major=ME&minor=E1&master_id=193", "0"},
				{"0", "0", "7", "����/�丮(���ٹ�)", "ME", "E1", "195", "/bbs/list.asp?major=ME&minor=E1&master_id=195", "0"},
				{"0", "0", "8", "English/������", "ME", "E1", "175", "/bbs/list.asp?major=ME&minor=E1&master_id=175", "0"},
//				{"0", "0", "7", "������ ������", "ME", "E1", "183", "/bbs/list.asp?major=ME&minor=E1&master_id=183", "0"},
//				{"0", "0", "8", "�ֽİ� ����ũ", "ME", "E1", "174", "/bbs/list.asp?major=ME&minor=E1&master_id=174", "0"},
				{"0", "0", "9", "DP ģ��/��������", "ME", "E1", "50", "/bbs/list.asp?major=ME&minor=E1&master_id=50", "0"},
				{"0", "0", "10", "��� �̾߱�", "ME", "E1", "127", "/bbs/list.asp?major=ME&minor=E1&master_id=127", "1"},
				{"0", "1", "-2", "���", "", "", "", "", ""},
				{"0", "1", "11", "������ �״���", "ME", "E1", "180", "/bbs/list.asp?major=ME&minor=E1&master_id=180", "0"},
				{"0", "1", "12", "ī�޶�,�׸��� ����", "ME", "E1", "41", "/bbs/list.asp?major=ME&minor=E1&master_id=41", "0"},
				{"0", "1", "13", "�뷡/�Ǳ⿬��/���!", "ME", "E1", "205", "/bbs/list.asp?major=ME&minor=E1&master_id=205", "0"},
//				{"0", "1", "12", "���� �ܱ���", "ME", "E1", "175", "/bbs/list.asp?major=ME&minor=E1&master_id=175", "0"},
				{"0", "1", "14", "������ ������", "ME", "E1", "182", "/bbs/list.asp?major=ME&minor=E1&master_id=182", "0"},
				{"0", "1", "15", "å �̾߱�", "ME", "E1", "149", "/bbs/list.asp?major=ME&minor=E1&master_id=149", "0"},
				{"0", "1", "16", "Ŭ�� RPM", "ME", "E1", "185", "/bbs/list.asp?major=ME&minor=E1&master_id=185", "0"},
				{"0", "1", "17", "All That Game", "ME", "E1", "142", "/bbs/list.asp?major=ME&minor=E1&master_id=142", "0"},
				{"0", "1", "18", "������ ������", "ME", "E1", "196", "/bbs/list.asp?major=ME&minor=E1&master_id=196", "0"},
//				{"0", "1", "18", "Cafe De Printemps", "ME", "E1", "195", "/bbs/list.asp?major=ME&minor=E1&master_id=195", "0"},
				{"0", "1", "19", "�ƿ����� ������", "ME", "E1", "164", "/bbs/list.asp?major=ME&minor=E1&master_id=164", "0"},
//				{"0", "1", "20", "ī�޶�,�׸��� ����", "ME", "E1", "41", "/bbs/list.asp?major=ME&minor=E1&master_id=41", "0"},
//				{"0", "1", "21", "�����Խ���", "ME", "E1", "180", "/bbs/list.asp?major=ME&minor=E1&master_id=180", "0"},
//				{"0", "1", "22", "�� ������", "ME", "E1", "89", "/bbs/list.asp?major=ME&minor=E1&master_id=89", "0"},
				{"0", "2", "-3", "�߰� ����", "", "", "", "", ""},
				{"0", "2", "23", "��緹��/HD DVD", "ME", "E3", "194", "/bbs/list.asp?major=ME&minor=E3&master_id=194", "0"},
				{"0", "2", "24", "DVDŸ��Ʋ", "ME", "E3", "51", "/bbs/list.asp?major=ME&minor=E3&master_id=51", "0"},
//				{"0", "2", "25", "DVDŸ��Ʋ - ����", "ME", "E3", "52", "/bbs/list.asp?major=ME&minor=E3&master_id=52", "0"},
				{"0", "2", "26", "��������/Ȩ������", "ME", "E3", "53", "/bbs/list.asp?major=ME&minor=E3&master_id=53", "0"},
				{"0", "2", "27", "�׳� �帳�ϴ�", "ME", "E3", "57", "/bbs/list.asp?major=ME&minor=E3&master_id=57", "0"},
				{"0", "2", "28", "��Ÿ - ����Ʈ����", "ME", "E3", "54", "/bbs/list.asp?major=ME&minor=E3&master_id=54", "0"},
				{"0", "2", "29", "��Ÿ - �ϵ����", "ME", "E3", "55", "/bbs/list.asp?major=ME&minor=E3&master_id=55", "0"},
				{"0", "2", "30", "��üȫ�� �Խ���", "ME", "E3", "56", "/bbs/list.asp?major=ME&minor=E3&master_id=56", "0"},
				{"0", "3", "-4", "ģ�� ����", "", "", "", "", ""},
				{"0", "3", "31", "����", "ME", "E2", "71", "/bbs/list.asp?major=ME&minor=E2&master_id=71", "0"},
				{"0", "3", "32", "����", "ME", "E2", "72", "/bbs/list.asp?major=ME&minor=E2&master_id=72", "0"},
				{"0", "3", "33", "�λ�", "ME", "E2", "75", "/bbs/list.asp?major=ME&minor=E2&master_id=75", "0"},
				{"0", "3", "34", "��õ-��õ", "ME", "E2", "76", "/bbs/list.asp?major=ME&minor=E2&master_id=76", "0"},
				{"0", "3", "35", "�ϻ�-ȭ��", "ME", "E2", "77", "/bbs/list.asp?major=ME&minor=E2&master_id=77", "0"},
				{"0", "3", "36", "����-����", "ME", "E2", "78", "/bbs/list.asp?major=ME&minor=E2&master_id=78", "0"},
				{"0", "3", "37", "�Ȼ�-�Ⱦ�", "ME", "E2", "79", "/bbs/list.asp?major=ME&minor=E2&master_id=79", "0"},
//				{"0", "3", "38", "����", "ME", "E2", "80", "/bbs/list.asp?major=ME&minor=E2&master_id=80", "0"},
				{"0", "4", "-5", "���� ����", "", "", "", "", ""},
				{"0", "4", "39", "�ҽô�", "ME", "E1", "207", "/bbs/list.asp?major=ME&minor=E1&master_id=207", "0"},
				{"0", "4", "40", "DASA", "ME", "E2", "82", "/bbs/list.asp?major=ME&minor=E2&master_id=82", "0"},
				{"0", "4", "41", "������", "ME", "E2", "186", "/bbs/list.asp?major=ME&minor=E2&master_id=186", "1"},
				{"0", "4", "42", "ó��ó��", "ME", "M2", "215", "/bbs/list.asp?major=ME&minor=E2&master_id=215", "0"},
				{"0", "4", "43", "�������� �������", "ME", "E2", "197", "/bbs/list.asp?major=ME&minor=E2&master_id=197", "0"},
				{"0", "4", "44", "�뱸 AV", "ME", "E2", "109", "/bbs/list.asp?major=ME&minor=E2&master_id=109", "0"},
				{"0", "4", "45", "�ٵ��ٵ�", "ME", "E2", "83", "/bbs/list.asp?major=ME&minor=E2&master_id=83", "0"},
				{"0", "5", "-6", "DP � ����", "", "", "", "", ""},
				{"0", "5", "46", "��������", "ME", "E1", "47", "/bbs/list.asp?major=ME&minor=E1&master_id=47", "0"},
				{"0", "5", "47", "���̽��ڽ�", "ME", "E1", "136", "/bbs/list.asp?major=ME&minor=E1&master_id=136", "0"},
				{"1", "6", "-7", "�ϵ���� ����&������", "", "", "", "", ""},
				{"1", "6", "48", "3D ����", "MD", "D2", "214", "/bbs/list.asp?major=MD&minor=D2&master_id=214", "0"},
				{"1", "6", "49", "��������", "MD", "D2", "30", "/bbs/list.asp?major=MD&minor=D2&master_id=30", "0"},
				{"1", "6", "50", "������ TV", "MD", "D2", "31", "/bbs/list.asp?major=MD&minor=D2&master_id=31", "0"},
//				{"1", "6", "49", "LCD/PDP", "MD", "D2", "31", "/bbs/list.asp?major=MD&minor=D2&master_id=31", "0"},
//				{"1", "6", "50", "BRAVIA", "MD", "D2", "203", "/bbs/list.asp?major=MD&minor=D2&master_id=203", "0"},
				{"1", "6", "51", "AV����/����Ŀ", "MD", "D2", "28", "/bbs/list.asp?major=MD&minor=D2&master_id=28", "0"},
				{"1", "6", "52", "�÷��̾�", "MD", "D2", "29", "/bbs/list.asp?major=MD&minor=D2&master_id=29", "0"},
				{"1", "6", "53", "XBOX360/PS3", "MD", "D2", "146", "/bbs/list.asp?major=MD&minor=D2&master_id=146", "0"},
				{"1", "6", "54", "Ȩ�þ���PC", "MD", "D2", "33", "/bbs/list.asp?major=MD&minor=D2&master_id=33", "0"},
				{"1", "7", "-8", "������", "", "", "", "", ""},
				{"1", "7", "55", "���� Ȩ�þ���", "MD", "D2", "190", "/bbs/list.asp?major=MD&minor=D2&master_id=190", "0"},
				{"1", "7", "56", "���� ����ǰ", "MD", "D2", "36", "/bbs/list.asp?major=MD&minor=D2&master_id=36", "0"},
				{"1", "8", "-9", "�ѹ��� ����", "", "", "", "", ""},
				{"1", "8", "56", "��������[����]", "MD", "D3", "37", "/bbs/list.asp?major=MD&minor=D3&master_id=37", "0"},
				{"1", "8", "57", "���̽� �ڽ�", "MD", "D3", "38", "/bbs/list.asp?major=MD&minor=D3&master_id=38", "0"},
				{"1", "8", "58", "���θ�ũ �ڷ��", "MD", "D3", "39", "/bbs/list.asp?major=MD&minor=D3&master_id=39", "0"},
				{"2", "9", "-10", "����Ʈ����", "", "", "", "", ""},
				{"2", "9", "59", "DVD ����", "MD", "D1", "20", "/bbs/list.asp?major=MD&minor=D1&master_id=20", "0"},
				{"2", "9", "60", "��ȭ �̾߱�", "MD", "D1", "22", "/bbs/list.asp?major=MD&minor=D1&master_id=22", "0"},
				{"2", "9", "61", "�簳����", "MD", "D1", "208", "/bbs/list.asp?major=MD&minor=D1&master_id=208", "1"},
				{"2", "9", "62", "���� �̾߱�", "MD", "D1", "23", "/bbs/list.asp?major=MD&minor=D1&master_id=23", "0"},
				{"2", "9", "63", "TV/��� �̾߱�", "MD", "D1", "145", "/bbs/list.asp?major=MD&minor=D1&master_id=145", "0"},
				{"2", "9", "64", "�ִϸ��̼�", "MD", "D1", "24", "/bbs/list.asp?major=MD&minor=D1&master_id=24", "0"},
				{"2", "9", "65", "���� �̾߱�", "MD", "D1", "110", "/bbs/list.asp?major=MD&minor=D1&master_id=110", "0"},
				{"2", "10", "-11", "������", "", "", "", "", ""},
				{"2", "10", "66", "���� ���̽�", "MD", "D1", "27", "/bbs/list.asp?major=MD&minor=D1&master_id=27", "0"},
				{"2", "10", "67", "�ǱԾ�/������", "MD", "D1", "188", "/bbs/list.asp?major=MD&minor=D1&master_id=188", "0"},
				{"2", "11", "-12", "�ѹ��� ����", "", "", "", "", ""},
				{"2", "11", "68", "DVD ����", "MD", "D1", "4", "/bbs/list_fword.asp?major=MD&minor=D1&bbsfword_id=4", "0"},
				{"2", "11", "69", "���̽� �ڽ�", "MD", "D3", "38", "/bbs/list.asp?major=MD&minor=D3&master_id=38", "0"},
				{"2", "11", "70", "���θ�ũ�ڷ��", "MD", "D3", "39", "/bbs/list.asp?major=MD&minor=D3&master_id=39", "0"},
				{"3", "12", "-13", "��緹��", "", "", "", "", ""},
//				{"3", "12", "71", "��緹�� HTPC", "MD", "D4", "154", "/bbs/list.asp?major=MD&minor=D4&master_id=155", "0"},
				{"3", "12", "72", "��緹�� �÷��̾�", "MD", "D4", "156", "/bbs/list.asp?major=MD&minor=D4&master_id=156", "0"},
				{"3", "12", "73", "��緹�� �̾߱�", "MD", "D4", "157", "/bbs/list.asp?major=MD&minor=D4&master_id=157", "0"},
				{"3", "12", "74", "HD-DVD", "MD", "D4", "200", "/bbs/list.asp?major=MD&minor=D4&master_id=200", "0"},
				{"3", "13", "-14", "������", "", "", "", "", ""},
				{"3", "13", "75", "��緹�� �ý���", "MD", "D4", "158", "/bbs/list.asp?major=MD&minor=D4&master_id=158", "0"},
				{"3", "13", "76", "BD ���� ���̽�", "MD", "D4", "191", "/bbs/list.asp?major=MD&minor=D4&master_id=191", "0"},
				{"4", "14", "-15", "����Ʈ��", "", "", "", "", ""},
				{"4", "14", "77", "����Ʈ�� ����", "MD", "D7", "210", "/bbs/list.asp?major=MD&minor=D7&master_id=210", "0"},
				{"4", "14", "78", "���� ����", "MD", "D7", "213", "/bbs/list.asp?major=MD&minor=D7&master_id=213", "0"},
				{"4", "14", "79", "������ ����", "MD", "D7", "178", "/bbs/list.asp?major=MD&minor=D7&master_id=178", "0"},
//				{"4", "14", "80", "�����Խ���", "MD", "D7", "209", "/bbs/list.asp?major=MD&minor=D7&master_id=209", "0"},
				{"4", "14", "81", "������", "MD", "D7", "4211", "/bbs/list.asp?major=MD&minor=D7&master_id=211", "0"}
		};
		ContentValues [] values = new ContentValues[bbsData.length];
		for(int i = 0; i<bbsData.length; i++) {
			values[i] = new ContentValues();
			values[i].put(DpDB.Bbs.TOP_ID, bbsData[i][0]);
			values[i].put(DpDB.Bbs.CAT_ID, bbsData[i][1]);
			values[i].put(DpDB.Bbs.BBS_ID, bbsData[i][2]);
			values[i].put(DpDB.Bbs.TITLE, bbsData[i][3]);
			values[i].put(DpDB.Bbs.MAJOR, bbsData[i][4]);
			values[i].put(DpDB.Bbs.MINOR, bbsData[i][5]);
			values[i].put(DpDB.Bbs.MASTER_ID, bbsData[i][6]);
			values[i].put(DpDB.Bbs.TARGET_URL, bbsData[i][7]);
			values[i].put(DpDB.Bbs.LOGIN_CHECK, bbsData[i][8]);
			db.insert(DpDB.BBS_TABLE_NAME, null, values[i]);
		}
		Log.d(TAG, "DB TABLE(" + DpDB.BBS_TABLE_NAME + ") Has been inserted default data." );
		
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        upgradeTables(db);
	}

    /**
     * Upgrade Dp db tables
     * @param db dpdb instance
     */
    public void upgradeTables(SQLiteDatabase db) {
        
        // Main Tables
//        db.execSQL("DROP TABLE IF EXISTS " + DpDB.ACCOUNT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DpDB.BBS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DpDB.ARTICLE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DpDB.CONTENT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DpDB.COMMENT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DpDB.MEMO_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DpDB.SCRAP_TABLE_NAME);

        // photo cache Table
//        db.execSQL("DROP TABLE IF EXISTS " + DpDB.RECEIVCED_PHOTO_TABLE_NAME);

        createTables(db);
    }
    
}
