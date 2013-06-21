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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

/**
 * @author  Frantik
 */
public class DBUtil {
	
	// hide constructor.
	private DBUtil() {}
	/**
	 */
	private static DBHelper mDbHelper = null;
    
    /**
     * Dp db instance return
     * @param context context
     * @return snsdb instance return
     */
    public static SQLiteDatabase getDatabase(Context context) {
    	SQLiteDatabase db = null;
    	
        if (mDbHelper == null) {
        	mDbHelper = DBHelper.getInstance(context);        	
        }
        
        try {
        	DBHelper.increaseDBRefCount();
        	db = mDbHelper.getWritableDatabase();
        } catch (SQLiteException e) {
        	e.printStackTrace();
        	DBHelper.decreaseDBRefCount();
        }

        return db;
    }
}
