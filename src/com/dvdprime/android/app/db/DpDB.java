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

import android.net.Uri;
import android.provider.BaseColumns;

public interface DpDB {

	public static final String DATABASE_NAME = "dpDB.db";
	public static final int DATABASE_VERSION = 12;

	public static final String AUTHORITY = "com.dvdprime.android.app";
	
	// Main Tables
	public static final String ACCOUNT_TABLE_NAME	= "account";
	public static final String BBS_TABLE_NAME		= "bbs";
	public static final String ARTICLE_TABLE_NAME	= "article";
	public static final String CONTENT_TABLE_NAME	= "content";
	public static final String COMMENT_TABLE_NAME	= "comment";
	public static final String MEMO_TABLE_NAME		= "memo";
	public static final String SCRAP_TABLE_NAME		= "scrap"; 
	public static final String PARAMETER_NOTIFY		= "notify";
	
	// for photo cache.
	public static final String RECEIVCED_PHOTO_TABLE_NAME = "receivedphoto";

	// default column in ANDROID.
	public static final String KEY_ID = "_id";
	
	/**
	 * Account
	 */
	public static final class Account implements BaseColumns, DpDB.AccountColumns {
		// hide constructor.
		private Account() {};
		
		public static final Uri CONTENT_URI = Uri.parse("content://"+ AUTHORITY +"/" + ACCOUNT_TABLE_NAME);
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.dvdprime.android.app.account";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.dvdprime.android.app.account";
	}
	
	/**
	 * Account columns
	 */
	public static interface AccountColumns {
		/**
		 * Account Name
		 * <P>Type: TEXT
		 */
		final String ACCOUNT_NAME = "account_name";
		/**
		 * Account Avartar
		 * <P>Type: TEXT
		 */
		final String ACCOUNT_AVARTAR = "account_avartar";
	}

	/**
	 * Bulletin Board
	 */
	public static final class Bbs implements BaseColumns, DpDB.BbsColumns {
		// hide constructor.
		private Bbs() {};

		public static final Uri CONTENT_URI = Uri.parse("content://"+ AUTHORITY +"/" + BBS_TABLE_NAME);
		public static final Uri CATEGORY_URI = Uri.parse("content://"+ AUTHORITY +"/" + BBS_TABLE_NAME + "/category");
		public static final Uri BBS_URI = Uri.parse("content://"+ AUTHORITY +"/" + BBS_TABLE_NAME + "/bbs");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.dvdprime.android.app.bbs";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.dvdprime.android.app.bbs";
		public static final String DEFAULT_SORT_ORDER = BBS_ID + " ASC";
		public static final String DESC_SORT_ORDER = BBS_ID + " DESC";

	}

	/**
	 * Bulletin Board columns
	 */
	public static interface BbsColumns {
		/**
		 * Top ID
		 * <P>Type: INTEGER
		 */
		final String TOP_ID = "top_id";
		/**
		 * Category ID
		 * <P>Type: INTEGER
		 */
		final String CAT_ID = "cat_id";
		/**
		 * BBS ID
		 * <P>Type: TEXT
		 */
		final String BBS_ID = "bbs_id";
		/**
		 * Bbs title
		 * <P>Type: TEXT
		 */
		final String TITLE = "title";
		/**
		 * Major
		 * <P>Type: TEXT
		 * Content Value : major
		 */
		final String MAJOR = "major";
		/**
		 * Minor
		 * <P>Type: TEXT
		 * Content Value : minor
		 */
		final String MINOR = "minor";
		/**
		 * Master Id
		 * <P>Type: TEXT
		 * Content Value : master_id
		 */
		final String MASTER_ID = "master_id";
		/**
		 * Attachment URL
		 * <P>Type: TEXT
		 */
		final String TARGET_URL = "target_url";
		/**
		 * Login check
		 * <P>Type: Boolean
		 */
		final String LOGIN_CHECK = "login_check";
	}

	/**
	 * Article of Bulletin Board
	 */
	public static final class Article implements BaseColumns, DpDB.ArticleColumns {
		// hide constructor.
		private Article() {};

		public static final Uri CONTENT_URI = Uri.parse("content://"+ AUTHORITY +"/" + ARTICLE_TABLE_NAME);
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.dvdprime.android.app.article";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.dvdprime.android.app.article";
		public static final String DEFAULT_SORT_ORDER = _ID + " ASC";

	}

	/**
	 * Article columns
	 */
	public static interface ArticleColumns {
		/**
		 * No
		 * <P>Type: INTEGER
		 */
		final String ATC_NO = "no";
		/**
		 * Title
		 * <P>Type: INTEGER
		 */
		final String ATC_TITLE = "title";
		/**
		 * URL
		 * <P>Type: TEXT
		 */
		final String ATC_URL = "target_url";
		/**
		 * User Id
		 * <P>Type: TEXT
		 */
		final String ATC_USER_ID = "user_id";
		/**
		 * User Name
		 * <P>Type: TEXT
		 */
		final String ATC_USER_NAME = "user_name";
		/**
		 * Date
		 * <P>Type: TEXT
		 */
		final String ATC_DATE = "date";
		/**
		 * Comment Count
		 * <P>Type: TEXT
		 */
		final String ATC_COMMENT = "comment";
		/**
		 * Recommend
		 * <P>Type: INTEGER
		 */
		final String ATC_RCMD = "recommend";
		/**
		 * Count
		 * <P>Type: INTEGER
		 */
		final String ATC_RCNT = "count";
	}

	/**
	 * Article of Bulletin Board
	 */
	public static final class Content implements BaseColumns, DpDB.ContentColumns {
		// hide constructor.
		private Content() {};

		public static final Uri CONTENT_URI = Uri.parse("content://"+ AUTHORITY +"/" + CONTENT_TABLE_NAME);
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.dvdprime.android.app.content";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.dvdprime.android.app.content";

	}

	/**
	 * Content columns
	 */
	public static interface ContentColumns {
		/**
		 * Title
		 * <p>Type: TEXT
		 */
		final String CTT_TITLE = "title";
		/**
		 * Content
		 * <P>Type: TEXT
		 */
		final String CTT_CONTENT = "content";
		/**
		 * URL
		 * <P>Type: TEXT
		 */
		final String CTT_URL = "image_url";
		/**
		 * TAG
		 * <P>Type: TEXT
		 */
		final String CTT_TAG = "tag";
	}

	/**
	 * Comment of article
	 */
	public static final class Comment implements BaseColumns, DpDB.CommentColumns {
		// hide constructor.
		private Comment() {};

		public static final Uri CONTENT_URI = Uri.parse("content://"+ AUTHORITY +"/" + COMMENT_TABLE_NAME);
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.dvdprime.android.app.comment";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.dvdprime.android.app.comment";
		public static final String DEFAULT_SORT_ORDER = CMT_UPPER + " ASC, " + _ID + " ASC";

	}

	/**
	 * Comment columns
	 */
	public static interface CommentColumns {
		/**
		 * User Id
		 * <P>Type: TEXT
		 */
		final String CMT_USER_ID = "user_id";
		/**
		 * User Name
		 * <P>Type: TEXT
		 */
		final String CMT_USER_NAME = "user_name";
		/**
		 * Content
		 * <P>Type: TEXT
		 */
		final String CMT_CONTENT = "content";
		/**
		 * Avatar URL
		 * <P>Type: TEXT
		 */
		final String CMT_URL = "avatar_url";
		/**
		 * Date
		 * <P>Type: TEXT
		 */
		final String CMT_DATE = "date";
		/**
		 * Recommend
		 * <P>Type: INTEGER
		 */
		final String CMT_RCMD = "recommend";
		/**
		 * Comment ID
		 * <P>Type : TEXT
		 */
		final String CMT_COMMENT_ID = "comment_id";
		/**
		 * Upper Id
		 * <P>Type : INTEGER
		 */
		final String CMT_UPPER = "upper";
	}

	/**
	 * Comment of article
	 */
	public static final class Memo implements BaseColumns, DpDB.MemoColumns {
		// hide constructor.
		private Memo() {};

		public static final Uri CONTENT_URI = Uri.parse("content://"+ AUTHORITY +"/" + MEMO_TABLE_NAME);
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.dvdprime.android.app.memo";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.dvdprime.android.app.memo";
		public static final String DEFAULT_SORT_ORDER = _ID + " ASC";

	}

	/**
	 * Comment columns
	 */
	public static interface MemoColumns {
		/**
		 * Memo Id
		 * <P>Type: TEXT
		 */
		final String MEMO_ID = "memo_id";
		/**
		 * User Id
		 * <P>Type: TEXT
		 */
		final String MEMO_USER_ID = "user_id";
		/**
		 * User Name
		 * <P>Type: TEXT
		 */
		final String MEMO_USER_NAME = "user_name";
		/**
		 * Content
		 * <P>Type: TEXT
		 */
		final String MEMO_CONTENT = "content";
		/**
		 * Date
		 * <P>Type: TEXT
		 */
		final String MEMO_DATE = "date";
	}

	/**
	 * My Saved Contents
	 */
	public static final class Scrap implements BaseColumns, DpDB.ScrapColumns {
		// hide constructor.
		private Scrap() {};

		public static final Uri CONTENT_URI = Uri.parse("content://"+ AUTHORITY +"/" + SCRAP_TABLE_NAME);
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.dvdprime.android.app.scrap";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.dvdprime.android.app.scrap";
		public static final String DEFAULT_SORT_ORDER = _ID + " ASC";

	}

	/**
	 * Article columns
	 */
	public static interface ScrapColumns {
		/**
		 * No
		 * <P>Type: TEXT
		 */
		final String TYPE = "type";
		/**
		 * Title
		 * <P>Type: TEXT
		 */
		final String TITLE = "title";
		/**
		 * URL
		 * <P>Type: TEXT
		 */
		final String URL = "target_url";
		/**
		 * User Name
		 * <P>Type: TEXT
		 */
		final String USER_NAME = "user_name";
		/**
		 * Date
		 * <P>Type: TEXT
		 */
		final String DATE = "date";
		/**
		 * Comment Count
		 * <P>Type: TEXT
		 */
		final String COMMENT = "comment";
		/**
		 * Recommend
		 * <P>Type: INTEGER
		 */
		final String RCMD = "recommend";
		/**
		 * Count
		 * <P>Type: INTEGER
		 */
		final String RCNT = "count";
	}

	/**
	 * Received photo
	 */
	public static final class ReceivedPhoto implements BaseColumns, DpDB.ReceivedPhotoColumns {
		// hide constructor.
		private ReceivedPhoto() {};

		public static final int MAX_ROW_COUNT = 1000;
		public static final Uri CONTENT_URI = Uri.parse("content://"+ AUTHORITY +"/" + RECEIVCED_PHOTO_TABLE_NAME);
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.dvdprime.android.app.receivedfile";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.dvdprime.android.app.receivedfile";

	}

	/**
	 * Received photo columns
	 */
	public static interface ReceivedPhotoColumns {
		/**
		 * Received photo URL
		 * <P>Type: TEXT
		 */
		final String URL = "url";
		/**
		 * File path
		 * <P>Type: TEXT
		 */
		final String FILEPATH = "file_path";
		/**
		 * Last access time
		 * <P>Type: INTEGER
		 */
		final String LAST_ACCESS_TIME = "last_access_time";
	}
}