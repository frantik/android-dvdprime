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
package com.dvdprime.android.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;

import com.dvdprime.android.app.db.DpDB;
import com.dvdprime.android.app.db.DBHelper;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;

/**
 * Dp content provider.
 */
public class DpContentProvider extends ContentProvider {

	private DBHelper mOpenHelper = null;
	private static final UriMatcher uriMatcher;

	private static final int ACCOUNT = 900;
	
	private static final int BBS = 100;
	private static final int BBS_COUNT = 101;
	private static final int BBS_TOP_ID = 102;
	private static final int BBS_CAT_ID = 103;
	private static final int BBS_TOP_ALL = 104;
	private static final int BBS_WIG_ID = 105;
	private static final int BBS_ID = 106;
	
	private static final int ARTICLE = 200;
	private static final int ARTICLE_COUNT = 201;
	private static final int ARTICLE_ID = 202;
	
	private static final int CONTENT = 300;
	private static final int CONTENT_COUNT = 301;

	private static final int COMMENT = 400;
	private static final int COMMENT_COUNT = 401;
	private static final int COMMENT_ID = 402;

	private static final int SCRAP = 500;
	private static final int SCRAP_COUNT = 501;
	private static final int SCRAP_ID = 502;
	
	private static final int RECEIVED_PHOTO_FILES_ITEM = 600;
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SqlArguments args = new SqlArguments(uri, selection, selectionArgs);

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count = db.delete(args.table, args.where, args.args);
		if (count > 0)
			sendNotify(uri);

		return count;
	}

	@Override
	public String getType(Uri uri) {

		switch (uriMatcher.match(uri)) {
			case ACCOUNT:
				return DpDB.Account.CONTENT_TYPE;
			case BBS:
				return DpDB.Bbs.CONTENT_TYPE;
			case ARTICLE:
				return DpDB.Article.CONTENT_TYPE;
			case CONTENT:
				return DpDB.Content.CONTENT_TYPE;
			case COMMENT:
				return DpDB.Comment.CONTENT_TYPE;
			case SCRAP:
				return DpDB.Scrap.CONTENT_TYPE;
			case RECEIVED_PHOTO_FILES_ITEM:
				return DpDB.ReceivedPhoto.CONTENT_ITEM_TYPE;
			default:
				throw new IllegalArgumentException("Unknown URI : " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		SqlArguments args = new SqlArguments(uri);

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final long rowId = db.insert(args.table, null, initialValues);
		if (rowId <= 0)
			return null;

		uri = ContentUris.withAppendedId(uri, rowId);
		sendNotify(uri);

		return uri;
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = DBHelper.getInstance(getContext());
		return true;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SqlArguments args = new SqlArguments(uri, selection, selectionArgs);

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count = db.update(args.table, values, args.where, args.args);
		if (count > 0)
			sendNotify(uri);

		return count;
	}

	private void sendNotify(Uri uri) {
		String notify = uri.getQueryParameter(DpDB.PARAMETER_NOTIFY);
		if (notify == null || "true".equals(notify)) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
	}
	
	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {

		if (uriMatcher.match(uri) == RECEIVED_PHOTO_FILES_ITEM) {
			String filePrefix = "file://";
			SQLiteDatabase db = null;
			String where = DpDB.KEY_ID + "=" + uri.getLastPathSegment();
			int _id = Integer.parseInt(uri.getLastPathSegment());
			boolean bFail = false;
			String path = null;

			try {
				DBHelper.increaseDBRefCount();
				db = mOpenHelper.getReadableDatabase();
			} catch (SQLiteException e) {
				e.printStackTrace();
				DBHelper.decreaseDBRefCount();
				bFail = true;
			}
			
			if (bFail == false) {
				Cursor c = db.query(DpDB.RECEIVCED_PHOTO_TABLE_NAME, new String[] { DpDB.ReceivedPhoto.FILEPATH }, where, null, null, null, null);

				if (c != null) {
					try {
						if ( c.moveToFirst() ) {
							path = c.getString(0);
						} else {
							throw new Exception("FilePath is null");
						}

						// update access time, lazy update.
						HashMap<Integer, Long> lastAccessTimeMap = DBHelper.getLATMap();
						lastAccessTimeMap.put(_id, System.currentTimeMillis());

					} catch (Exception e) {
						e.printStackTrace();
						bFail = true;
					} finally {
						c.close();
					}
				}
				
				// close db.
				DBHelper.decreaseDBRefCount();
				if (DBHelper.getDBRefCount() < 1) {	
					DBHelper.resetDBRefCount();
				}
			}
			
			// throw exception
			if (bFail) {
				throw new FileNotFoundException("INVALID URI : " + uri.toString());
			}
			
			File file = new File(URI.create(filePrefix + path));
			ParcelFileDescriptor parcelFile = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY | ParcelFileDescriptor.MODE_WORLD_READABLE);

			return parcelFile;

		} else {
			throw new FileNotFoundException("INVALID URI : " + uri.toString());
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		String orderBy = null;
		String groupby = null;
		
		String [] cntColumn = {
                "count(*) as count"
        };
		
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		int match = uriMatcher.match(uri);
		
		List<String> pathSegmentList = uri.getPathSegments();
		
		switch (match) {
			case BBS :
				queryBuilder.setTables(DpDB.BBS_TABLE_NAME);
				queryBuilder.setProjectionMap(sBbsProjectionMap);
		
				break;
			case BBS_COUNT:
	            queryBuilder.setTables(DpDB.BBS_TABLE_NAME);
	            projection = cntColumn;

	            break;
			case BBS_TOP_ID:
				queryBuilder.setTables(DpDB.BBS_TABLE_NAME);
				queryBuilder.setProjectionMap(sBbsProjectionMap);
				queryBuilder.appendWhere(DpDB.Bbs.TOP_ID + " = ");
				queryBuilder.appendWhere(pathSegmentList.get(1));
				queryBuilder.appendWhere(" AND ");
				queryBuilder.appendWhere(DpDB.Bbs.BBS_ID + " < 0 ");

				if (TextUtils.isEmpty(sortOrder)) {
					orderBy = DpDB.Bbs.DEFAULT_SORT_ORDER;
				} else {
					orderBy = sortOrder;
				}

				break;
			case BBS_CAT_ID:
				queryBuilder.setTables(DpDB.BBS_TABLE_NAME);
				queryBuilder.setProjectionMap(sBbsProjectionMap);
				queryBuilder.appendWhere(DpDB.Bbs.CAT_ID + " = ");
				queryBuilder.appendWhere(pathSegmentList.get(2));
				queryBuilder.appendWhere(" AND ");
				queryBuilder.appendWhere(DpDB.Bbs.BBS_ID + " > -1 ");
				queryBuilder.appendWhere(" AND ");
				queryBuilder.appendWhere(DpDB.Bbs.LOGIN_CHECK + " < ");
				queryBuilder.appendWhere(pathSegmentList.get(3));

				if (TextUtils.isEmpty(sortOrder)) {
					orderBy = DpDB.Bbs.DEFAULT_SORT_ORDER;
				} else {
					orderBy = sortOrder;
				}

				break;
			case BBS_TOP_ALL:
				queryBuilder.setTables(DpDB.BBS_TABLE_NAME);
				queryBuilder.setProjectionMap(sBbsProjectionMap);
				queryBuilder.appendWhere(DpDB.Bbs.BBS_ID + " < 0 ");
				queryBuilder.appendWhere(" AND ");
				queryBuilder.appendWhere(DpDB.Bbs.BBS_ID + " not in (-8, -11, -13) ");

				if (TextUtils.isEmpty(sortOrder)) {
					orderBy = DpDB.Bbs.DEFAULT_SORT_ORDER;
				} else {
					orderBy = sortOrder;
				}

				break;
			case BBS_WIG_ID:
				queryBuilder.setTables(DpDB.BBS_TABLE_NAME);
				queryBuilder.setProjectionMap(sBbsProjectionMap);
				queryBuilder.appendWhere(DpDB.Bbs.CAT_ID + " = ");
				queryBuilder.appendWhere(pathSegmentList.get(2));
				queryBuilder.appendWhere(" AND ");
				queryBuilder.appendWhere(DpDB.Bbs.BBS_ID + " > -1 ");
				queryBuilder.appendWhere(" AND ");
				queryBuilder.appendWhere(DpDB.Bbs.LOGIN_CHECK + " < ");
				queryBuilder.appendWhere(pathSegmentList.get(3));
				queryBuilder.appendWhere(" AND ");
				queryBuilder.appendWhere(DpDB.Bbs.BBS_ID + " not in (1) ");

				if (TextUtils.isEmpty(sortOrder)) {
					orderBy = DpDB.Bbs.DEFAULT_SORT_ORDER;
				} else {
					orderBy = sortOrder;
				}

				break;
			case BBS_ID:
				queryBuilder.setTables(DpDB.BBS_TABLE_NAME);
				queryBuilder.setProjectionMap(sBbsProjectionMap);
				queryBuilder.appendWhere(DpDB.Bbs.BBS_ID + " = ");
				queryBuilder.appendWhere(pathSegmentList.get(2));
				
				break;
			case ARTICLE :
				queryBuilder.setTables(DpDB.ARTICLE_TABLE_NAME);
				queryBuilder.setProjectionMap(sArticleProjectionMap);
		
				break;
			case ARTICLE_COUNT:
	            queryBuilder.setTables(DpDB.ARTICLE_TABLE_NAME);
	            projection = cntColumn;

	            break;
			case ARTICLE_ID:
				queryBuilder.setTables(DpDB.ARTICLE_TABLE_NAME);
				queryBuilder.setProjectionMap(sArticleProjectionMap);
				queryBuilder.appendWhere(DpDB.Article._ID + " = ");
				queryBuilder.appendWhere(pathSegmentList.get(1));

				if (TextUtils.isEmpty(sortOrder)) {
					orderBy = DpDB.Article.DEFAULT_SORT_ORDER;
				} else {
					orderBy = sortOrder;
				}

				break;
			case CONTENT :
				queryBuilder.setTables(DpDB.CONTENT_TABLE_NAME);
				queryBuilder.setProjectionMap(sContentProjectionMap);
		
				break;
			case CONTENT_COUNT:
	            queryBuilder.setTables(DpDB.CONTENT_TABLE_NAME);
	            projection = cntColumn;

	            break;
			case COMMENT :
				queryBuilder.setTables(DpDB.COMMENT_TABLE_NAME);
				queryBuilder.setProjectionMap(sCommentProjectionMap);
		
				if (TextUtils.isEmpty(sortOrder)) {
					orderBy = DpDB.Comment.DEFAULT_SORT_ORDER;
				} else {
					orderBy = sortOrder;
				}

				break;
			case COMMENT_COUNT:
	            queryBuilder.setTables(DpDB.COMMENT_TABLE_NAME);
	            projection = cntColumn;

	            break;
			case COMMENT_ID:
				queryBuilder.setTables(DpDB.COMMENT_TABLE_NAME);
				queryBuilder.setProjectionMap(sCommentProjectionMap);
				queryBuilder.appendWhere(DpDB.Comment.CMT_UPPER + " = ");
				queryBuilder.appendWhere(pathSegmentList.get(1));
				queryBuilder.appendWhere(" AND ");
				queryBuilder.appendWhere(DpDB.Comment.CMT_URL + " is null ");
// comment detail list query changed (2010.10.16)
//				queryBuilder.appendWhere(DpDB.Comment._ID + " > ");				
//				queryBuilder.appendWhere(pathSegmentList.get(2));

				if (TextUtils.isEmpty(sortOrder)) {
					orderBy = DpDB.Comment.DEFAULT_SORT_ORDER;
				} else {
					orderBy = sortOrder;
				}

				break;
			case SCRAP :
				queryBuilder.setTables(DpDB.SCRAP_TABLE_NAME);
				queryBuilder.setProjectionMap(sScrapProjectionMap);
		
				break;
			case SCRAP_COUNT:
	            queryBuilder.setTables(DpDB.SCRAP_TABLE_NAME);
	            projection = cntColumn;

	            break;
			case SCRAP_ID:
				queryBuilder.setTables(DpDB.SCRAP_TABLE_NAME);
				queryBuilder.setProjectionMap(sScrapProjectionMap);
				queryBuilder.appendWhere(DpDB.Scrap._ID + " = ");
				queryBuilder.appendWhere(pathSegmentList.get(1));

				if (TextUtils.isEmpty(sortOrder)) {
					orderBy = DpDB.Scrap.DEFAULT_SORT_ORDER;
				} else {
					orderBy = sortOrder;
				}

				break;
			default:
				throw new IllegalArgumentException("Unknown URI : " + uri);
		}
		SQLiteDatabase db = null;
		boolean bFail = false;
		Cursor c = null;

		try {
			DBHelper.increaseDBRefCount();
			db = mOpenHelper.getReadableDatabase();
		} catch (SQLiteException e) {
			e.printStackTrace();
			DBHelper.decreaseDBRefCount();
			bFail = true;
		}
		
		if (bFail == false) {
			c = queryBuilder.query(db, projection, selection, selectionArgs, groupby, null, orderBy);
			// Tell the cursor what URI to watch, so it knows when its source data changes
			if (c != null) {
				c.setNotificationUri(getContext().getContentResolver(), uri);
			}

			// close db.
			DBHelper.decreaseDBRefCount();
			if (DBHelper.getDBRefCount() < 1) {	
				DBHelper.resetDBRefCount();
			}
		}

		return c;
	}

	private static HashMap<String, String> sBbsProjectionMap;
	private static HashMap<String, String> sArticleProjectionMap;
	private static HashMap<String, String> sContentProjectionMap;
	private static HashMap<String, String> sCommentProjectionMap;
	private static HashMap<String, String> sScrapProjectionMap;

	static class SqlArguments {
		public final String table;
		public final String where;
		public final String[] args;

		SqlArguments(Uri url, String where, String[] args) {
			if (url.getPathSegments().size() == 1) {
				this.table = url.getPathSegments().get(0);
				this.where = where;
				this.args = args;
			} else if (url.getPathSegments().size() != 2) {
				throw new IllegalArgumentException("Invalid URI: " + url);
			} else if (!TextUtils.isEmpty(where)) {
				throw new UnsupportedOperationException(
						"WHERE clause not supported: " + url);
			} else {
				this.table = url.getPathSegments().get(0);
				this.where = "_id=" + ContentUris.parseId(url);
				this.args = null;
			}
		}

		SqlArguments(Uri url) {
			if (url.getPathSegments().size() == 1) {
				table = url.getPathSegments().get(0);
				where = null;
				args = null;
			} else {
				throw new IllegalArgumentException("Invalid URI: " + url);
			}
		}
	}

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

		uriMatcher.addURI(DpDB.AUTHORITY, DpDB.BBS_TABLE_NAME, BBS);
		uriMatcher.addURI(DpDB.AUTHORITY, DpDB.BBS_TABLE_NAME + "/count", BBS_COUNT);
		uriMatcher.addURI(DpDB.AUTHORITY, DpDB.BBS_TABLE_NAME + "/#", BBS_TOP_ID);
		uriMatcher.addURI(DpDB.AUTHORITY, DpDB.BBS_TABLE_NAME + "/category/#/#", BBS_CAT_ID);
		uriMatcher.addURI(DpDB.AUTHORITY, DpDB.BBS_TABLE_NAME + "/all", BBS_TOP_ALL);
		uriMatcher.addURI(DpDB.AUTHORITY, DpDB.BBS_TABLE_NAME + "/widget/#/#", BBS_WIG_ID);
		uriMatcher.addURI(DpDB.AUTHORITY, DpDB.BBS_TABLE_NAME + "/bbs/#", BBS_ID);

		uriMatcher.addURI(DpDB.AUTHORITY, DpDB.ARTICLE_TABLE_NAME, ARTICLE);
		uriMatcher.addURI(DpDB.AUTHORITY, DpDB.ARTICLE_TABLE_NAME + "/count", ARTICLE_COUNT);
		uriMatcher.addURI(DpDB.AUTHORITY, DpDB.ARTICLE_TABLE_NAME + "/#", ARTICLE_ID);

		uriMatcher.addURI(DpDB.AUTHORITY, DpDB.CONTENT_TABLE_NAME, CONTENT);
		uriMatcher.addURI(DpDB.AUTHORITY, DpDB.CONTENT_TABLE_NAME + "/count", CONTENT_COUNT);

		uriMatcher.addURI(DpDB.AUTHORITY, DpDB.COMMENT_TABLE_NAME, COMMENT);
		uriMatcher.addURI(DpDB.AUTHORITY, DpDB.COMMENT_TABLE_NAME + "/count", COMMENT_COUNT);
		uriMatcher.addURI(DpDB.AUTHORITY, DpDB.COMMENT_TABLE_NAME + "/#/#", COMMENT_ID);

		uriMatcher.addURI(DpDB.AUTHORITY, DpDB.SCRAP_TABLE_NAME, SCRAP);
		uriMatcher.addURI(DpDB.AUTHORITY, DpDB.SCRAP_TABLE_NAME + "/count", SCRAP_COUNT);
		uriMatcher.addURI(DpDB.AUTHORITY, DpDB.SCRAP_TABLE_NAME + "/#", SCRAP_ID);

		uriMatcher.addURI(DpDB.AUTHORITY, DpDB.RECEIVCED_PHOTO_TABLE_NAME + "/*", RECEIVED_PHOTO_FILES_ITEM);

		HashMap<String, String> map = new HashMap<String, String>();
		map.put(DpDB.Bbs._ID, DpDB.Bbs._ID);
		map.put(DpDB.Bbs.TOP_ID, DpDB.Bbs.TOP_ID);
		map.put(DpDB.Bbs.CAT_ID, DpDB.Bbs.CAT_ID);
		map.put(DpDB.Bbs.BBS_ID, DpDB.Bbs.BBS_ID);
		map.put(DpDB.Bbs.TITLE, DpDB.Bbs.TITLE);
		map.put(DpDB.Bbs.MAJOR, DpDB.Bbs.MAJOR);
		map.put(DpDB.Bbs.MINOR, DpDB.Bbs.MINOR);
		map.put(DpDB.Bbs.MASTER_ID, DpDB.Bbs.MASTER_ID);
		map.put(DpDB.Bbs.TARGET_URL, DpDB.Bbs.TARGET_URL);
		map.put(DpDB.Bbs.LOGIN_CHECK, DpDB.Bbs.LOGIN_CHECK);
		sBbsProjectionMap = map;
		
		map = new HashMap<String, String>();
		map.put(DpDB.Article._ID, DpDB.Article._ID);
		map.put(DpDB.Article.ATC_NO, DpDB.Article.ATC_NO);
		map.put(DpDB.Article.ATC_TITLE, DpDB.Article.ATC_TITLE);
		map.put(DpDB.Article.ATC_URL, DpDB.Article.ATC_URL);
		map.put(DpDB.Article.ATC_USER_ID, DpDB.Article.ATC_USER_ID);
		map.put(DpDB.Article.ATC_USER_NAME, DpDB.Article.ATC_USER_NAME);
		map.put(DpDB.Article.ATC_DATE, DpDB.Article.ATC_DATE);
		map.put(DpDB.Article.ATC_COMMENT, DpDB.Article.ATC_COMMENT);
		map.put(DpDB.Article.ATC_RCMD, DpDB.Article.ATC_RCMD);
		map.put(DpDB.Article.ATC_RCNT, DpDB.Article.ATC_RCNT);
		sArticleProjectionMap = map;
		
		map = new HashMap<String, String>();
		map.put(DpDB.Content._ID, DpDB.Content._ID);
		map.put(DpDB.Content.CTT_TITLE, DpDB.Content.CTT_TITLE);
		map.put(DpDB.Content.CTT_CONTENT, DpDB.Content.CTT_CONTENT);
		map.put(DpDB.Content.CTT_URL, DpDB.Content.CTT_URL);
		map.put(DpDB.Content.CTT_TAG, DpDB.Content.CTT_TAG);
		sContentProjectionMap = map;

		map = new HashMap<String, String>();
		map.put(DpDB.Comment._ID, DpDB.Comment._ID);
		map.put(DpDB.Comment.CMT_USER_ID, DpDB.Comment.CMT_USER_ID);
		map.put(DpDB.Comment.CMT_USER_NAME, DpDB.Comment.CMT_USER_NAME);
		map.put(DpDB.Comment.CMT_CONTENT, DpDB.Comment.CMT_CONTENT);
		map.put(DpDB.Comment.CMT_URL, DpDB.Comment.CMT_URL);
		map.put(DpDB.Comment.CMT_DATE, DpDB.Comment.CMT_DATE);
		map.put(DpDB.Comment.CMT_RCMD, DpDB.Comment.CMT_RCMD);
		map.put(DpDB.Comment.CMT_COMMENT_ID, DpDB.Comment.CMT_COMMENT_ID);
		map.put(DpDB.Comment.CMT_UPPER, DpDB.Comment.CMT_UPPER);
		sCommentProjectionMap = map;

		map = new HashMap<String, String>();
		map.put(DpDB.Scrap._ID, DpDB.Scrap._ID);
		map.put(DpDB.Scrap.TYPE, DpDB.Scrap.TYPE);
		map.put(DpDB.Scrap.TITLE, DpDB.Scrap.TITLE);
		map.put(DpDB.Scrap.URL, DpDB.Scrap.URL);
		map.put(DpDB.Scrap.USER_NAME, DpDB.Scrap.USER_NAME);
		map.put(DpDB.Scrap.DATE, DpDB.Scrap.DATE);
		map.put(DpDB.Scrap.COMMENT, DpDB.Scrap.COMMENT);
		map.put(DpDB.Scrap.RCMD, DpDB.Scrap.RCMD);
		map.put(DpDB.Scrap.RCNT, DpDB.Scrap.RCNT);
		sScrapProjectionMap = map;
	}

}
