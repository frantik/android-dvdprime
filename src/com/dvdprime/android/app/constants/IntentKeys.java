package com.dvdprime.android.app.constants;

/**
 * IntentKeys는 클래스 의도 키를위한 상수가 들어 있습니다. 
 * Used by start new activity with parameters from this class in intent data. 
 */
public final class IntentKeys {
	
	private IntentKeys() {

	}
	
	/**
	 * 선택된 탭의 고유 ID 값
	 */
	public static final String TAB_ID = "tab_id";
	
	/**
	 * 게시판 제목
	 */
	public static final String BBS_TITLE = "bbs_title";
	/**
	 * 게시판 URL 
	 */
	public static final String BBS_URL = "bbs_url";
	/**
	 * 게시판 메인 ID
	 */
	public static final String BBS_MAJOR = "bbs_major";
	/**
	 * 게시판 보조 ID
	 */
	public static final String BBS_MINOR = "bbs_minor";
	/**
	 * 게시판 마스터 ID
	 */
	public static final String BBS_MASTER = "bbs_master";
	/**
	 * 게시물 테이블 ID
	 */
	public static final String ARTICLE_ID = "article_id";
	/**
	 * 스크랩 종류 (S:스크랩창고, D:글창고, C:댓글창고)
	 */
	public static final String SCRAP_TYPE = "scrap_type";
	/**
	 * 로그인이 필요한 게시판 여부
	 */
	public static final String IS_LOGIN_CHECK = "is_login_check";
	/**
	 * 글수정 모드 여부
	 */
	public static final String WRITE_MODE = "write_mode";
	/**
	 * 게시글 ID
	 */
	public static final String BBS_ID = "bbs_id";
	/**
	 * 게시글 작성일
	 */
	public static final String BBS_DATE = "bbs_date";
	/**
	 * 파일 경로
	 */
	public static final String URI = "path";
	/**
	 * 쪽지 목록 종류
	 */
	public static final String MEMO_TYPE = "memo_type";
	/**
	 * 쪽지 보낼 아이디
	 */
	public static final String MEMO_RECEIVER = "memo_receiver";
	/**
	 * 탭 설정용 인덱스
	 */
    public static final String TAB_INDEX = "tab_index";
}
