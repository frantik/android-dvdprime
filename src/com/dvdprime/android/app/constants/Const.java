package com.dvdprime.android.app.constants;

import java.io.File;

import com.dvdprime.android.app.ContextHolder;
import com.dvdprime.android.app.R;

import android.content.Context;
import android.os.Environment;

public final class Const {
	
	static Context ctx = ContextHolder.getInstance().getContext();
	
	public static final String UTF8 = "utf-8";
	
	/** sdcard 경로 */
	public static final String SDCARD_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath();
	
	/** external package path */
	public static final String EXTERNAL_STORAGE_PATH = SDCARD_DIRECTORY + File.separator
													+ "Android" + File.separator
													+ "data" + File.separator
													+ ctx.getPackageName();
	
	/** external cache path */
	public static final String CACHE_PATH = EXTERNAL_STORAGE_PATH + File.separator + "cache";

	/** cache dir */
	public static final File CACHE_DIR = new File(CACHE_PATH);
	
	public static final String HOMEPAGE_URL		= "http://dvdprime.donga.com";
	public static final String CONTENT_URL		= "http://dvdprime.donga.com/bbs";
	public static final String BOXWEB_URL		= "http://m.boxweb.net/c/dvdprime/list.php";
//	public static final String LOGIN_URL		= "https://dvdprime.donga.com:443/membership/login_proc.asp";
//	public static final String WRITE_URL		= "https://dvdprime.donga.com:443/bbs/writeok.asp";
//	public static final String MODIFY_URL		= "https://dvdprime.donga.com:443/bbs/editOk.asp";
//	public static final String DELETE_URL		= "https://dvdprime.donga.com:443/bbs/deleteOk.asp";
//	public static final String MEMO_EXEC_URL	= "https://dvdprime.donga.com:443/note/src/Memo_Tran_ExecX.asp";
//	public static final String MEMO_SENDEXEC_URL= "https://dvdprime.donga.com:443/note/src/Send_Memo_ExecX.asp";
	public static final String LOGIN_URL		= "http://dvdprime.donga.com/membership/login_proc.asp";
	public static final String LOGIN_CHECK_URL	= "http://dvdprime.donga.com/note/src/Recv_Memo_List.asp?flag=sv";
	public static final String RECOMMEND_URL	= "http://dvdprime.donga.com/bbs/recommend/writeOk.asp";
	public static final String WRITE_URL		= "http://dvdprime.donga.com/bbs/writeok.asp";
	public static final String MODIFY_URL		= "http://dvdprime.donga.com/bbs/editOk.asp";
	public static final String DELETE_URL		= "http://dvdprime.donga.com/bbs/deleteOk.asp";
	public static final String COMMENT_WRITE_URL= "http://dvdprime.donga.com/bbs/memo/writeOk.asp";
	public static final String COMMENT_RCMD_URL = "http://dvdprime.donga.com/bbs/recommend/RwriteOk.asp";
	public static final String COMMENT_DEL_URL  = "http://dvdprime.donga.com/bbs/memo/deleteOk.asp";
	public static final String MEMO_RECEIVED_URL= "http://dvdprime.donga.com/note/src/Recv_Memo_List.asp?flag=rv";
	public static final String MEMO_SEND_URL	= "http://dvdprime.donga.com/note/src/Recv_Memo_List.asp?flag=sd";
	public static final String MEMO_STORAGE_URL = "http://dvdprime.donga.com/note/src/Recv_Memo_List.asp?flag=sv";
	public static final String MEMO_EXEC_URL	= "http://dvdprime.donga.com/note/src/Memo_Tran_ExecX.asp";
	public static final String MEMO_SENDEXEC_URL= "http://dvdprime.donga.com/note/src/Send_Memo_ExecX.asp";
	public static final String MEMO_CHECK_URL	= "http://dvdprime.donga.com/note/src/IntiSession.asp";
	public static final String MYDP_SAVE_URL	= "http://dvdprime.donga.com/bbs/option/scrapOk.asp";
	public static final String SCRAP_LIST_URL	= "http://dvdprime.donga.com/mydp/popMyDPScrapList.asp?Page=";
	public static final String MY_ARTICLE_URL	= "http://dvdprime.donga.com/mydp/popMyDPArticle.asp?Page=";
	public static final String MY_COMMENT_URL	= "http://dvdprime.donga.com/mydp/popMyDPMemo.asp?Page=";
	
	public static final String FACEBOOK_SHARER_URL	= "http://www.facebook.com/sharer/sharer.php?u=";
	public static final String TWITTER_SHARER_URL	= "http://twitter.com/intent/tweet?url=%1$s&text=%2$s&via=dvdprime";
	
	public static final String BITLY_REQUEST_URL	= "http://api.bit.ly/v3/shorten";
	
	// 단축 URL 관련 정보
	public static final String BITLY_LOGIN	= "frantik";
	public static final String BITLY_APIKEY	= "R_81c75cc0309324c464c5886de13ecf29";
	public static final String BITLY_FORMAT	= "txt";
	
	/** URL 정의 */
	// 검색 옵션 값
	public static final String SEARCH_OPTION_URL = "&fword_sel=&SortMethod=0&SearchCondition=&image2.x=0&image2.y=0&SearchConditionTxt=";
	// 본문 스타일 - 블랙테마
	public static final String BLACK_THEME_STYLE = "<style type=\"text/css\">a:link, a:active, a:visited {color:white;}, a:hover {color:yellow;}</style>";
	// 이미지 업로드 
	public static final String IMAGE_UPLOAD_URL = "http://dvdprime.donga.com/bbs/PopAttachImageX.asp";
	
	public static final String DEFAULT_SEPARATOR = ","; 

	/** Response Code 정의 */
	
	// 응답 완료/성공 
	public static final int OK 						= 200;
	public static final int BAD_REQUEST 			= 401;
	public static final int NOT_FOUND 	    		= 404;
	public static final int REQUEST_TIMEOUT			= 408;
	public static final int INTERNAL_SERVER_ERROR	= 500;
	public static final int SERVICE_UNAVAILABLE		= 503;
	//-- user defined code
	public static final int LOGIN_FAILED			= 600;
	public static final int ALREADY_REQUEST			= 601;
	
	// Image Cache Size
	public static final int CACHE_IMAGE_SIZE		= 2000000;
	
	// 스크랩 타입 값
	public static final String TYPE_SCRAP = "S";
	public static final String TYPE_DOC   = "D";
	public static final String TYPE_CMT   = "C";
	
	// 테마 설정 값
	public static final String BLACK_THEME = "Black";
	public static final String WHITE_THEME = "White";
	
	/**
	 * Response Code 에 적합한 에러 메세지를 가져옴.
	 * @param errorCode
	 * @return
	 */
    public static String getErrMessage(int errorCode) {
    	
        String errorStr = "";

        switch (errorCode) {
        case BAD_REQUEST:
            errorStr = ctx.getString(R.string.bad_request_error_message);
            break;
        case NOT_FOUND:
            errorStr = ctx.getString(R.string.not_found_error_message);
            break;
        case REQUEST_TIMEOUT:
            errorStr = ctx.getString(R.string.request_timeout_error_message);
            break;
        case INTERNAL_SERVER_ERROR:
        	errorStr = ctx.getString(R.string.internal_server_error_message);
        	break;
        case SERVICE_UNAVAILABLE:
            errorStr = ctx.getString(R.string.service_unavailable_error_message);
            break;
        default:
            errorStr = ctx.getString(R.string.unknown_error_message);
            break;
        }

        return errorStr;
    }
}
