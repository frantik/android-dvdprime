package com.dvdprime.android.app.constants;

import com.dvdprime.android.app.db.DpDB;

/**
 * RequestCode는 클래스 요청 코드를 포함하는 상수. 
 * Used by start new activity for result. 
 */
public final class RequestCode {

	private RequestCode() {

	}
	
	////////////////////////////////////////////////////
	//
	// Response Service Code
	//
	////////////////////////////////////////////////////
	// 글쓰기 요청
	public static final int REQ_CODE_ARTICLE_WRITE		= 1;
	// 글수정 요청
	public static final int REQ_CODE_ARTICLE_MODIFY		= 2;
	// 글삭제 요청
	public static final int REQ_CODE_ARTICLE_DELETE		= 3;
	// 사진찍기 요청
	public static final int REQ_CODE_TAKE_PHOTO			= 4;
	// 사진앨범 요청
	public static final int REQ_CODE_PICK_PICTURE		= 5;
	

    public static final String[] BbsCols = new String[] {
		DpDB.Bbs._ID, //0
		DpDB.Bbs.TOP_ID, //1
		DpDB.Bbs.CAT_ID, //2
		DpDB.Bbs.BBS_ID, //3
		DpDB.Bbs.TITLE, //4
		DpDB.Bbs.MAJOR, //5
		DpDB.Bbs.MINOR, //6
		DpDB.Bbs.MASTER_ID, //7
		DpDB.Bbs.TARGET_URL, //8
		DpDB.Bbs.LOGIN_CHECK //9
};

}
