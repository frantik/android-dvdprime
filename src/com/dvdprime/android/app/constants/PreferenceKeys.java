package com.dvdprime.android.app.constants;

/**
 * PreferenceKeys는 설정의 저장값을 관리하기 위한 키가 들어 있습니다. 
 */
public final class PreferenceKeys {
	
	private PreferenceKeys() {
	}
	
	/**
	 * <pre>
	 * 사용자 아이디
	 * Preference Type : String
	 * </pre>
	 */
	public static final String ACCOUNT_ID = "account_id";
	/**
	 * <pre>
	 * 사용자 비밀번호
	 * Preference Type : String
	 * </pre>
	 */
	public static final String ACCOUNT_PW = "account_pw";
	/**
	 * <pre>
	 * 사용자 마지막 로그인 시간
	 * Preference Type : Long
	 * </pre>
	 */
	public static final String ACCOUNT_TIME = "account_time";
	/**
	 * <pre>
	 * 자동 로그인 사용여부
	 * Preference Type : Boolean
	 * </pre>
	 */
	public static final String AUTO_LOGIN_ENABLED = "auto_login_enabled";
	/**
	 * <pre>
	 * 설정 테마
	 * Preference Type : String
	 * </pre>
	 */
	public static final String DP_THEME = "dp_theme";
	/**
	 * <pre>
	 * 본문 폰트
	 * Preference Type : String
	 * </pre>
	 */
	public static final String CONTENT_FONTSIZE = "content_fontsize";
	/**
	 * <pre>
	 * 첨부 사진 크기
	 * Preference Type : String
	 * </pre>
	 */
	public static final String FILE_WIDTHSIZE = "file_widthsize";
	/**
	 * <pre>
	 * 게시판 목록 바로가기 사용여부
	 * Preference Type : Boolean
	 * </pre>
	 */
	public static final String DIRECT_BBS_ENABLED = "direct_bbs_enabled";
	/**
	 * <pre>
	 * 쪽지 자동 알림 사용여부
	 * Preference Type : Boolean
	 * </pre>
	 */
	public static final String ALERT_MEMO_ENABLED = "alert_memo_enabled";
	/**
	 * <pre>
	 * 덧플 클립보드 저장 사용여부
	 * Preference Type : Boolean
	 * </pre>
	 */
	public static final String SAVE_COMMENT_ENABLED = "save_comment_enabled";
	/**
	 * <pre>
	 * 새로운 쪽지 갯수
	 * Preference Type : Integer
	 * </pre>
	 */
	public static final String NEW_MEMO_COUNT = "new_memo_count";
	/**
	 * <pre>
	 * 본문의 위젯 사용여부
	 * Preference Type : Boolean
	 * </pre>
	 */
	public static final String WIDGET_NAVI_ENABLED = "widget_navi_enabled";
	/**
	 * <pre>
	 * 본문의 텍스트만 보기
	 * Preference Type : Boolean
	 * </pre>
	 */
	public static final String CONTENT_TEXT_ONLY = "content_text_only";
	/**
	 * <pre>
	 * 본문에 플러그인 사용여부
	 * Preference Type : Boolean
	 * </pre>
	 */
	public static final String WEBVIEW_PLUGIN_ENABLED = "webview_plugin_enabled";
	/**
	 * <pre>
	 * 종료 확인 다이얼로그 사용여부
	 * Preference Type : Boolean
	 * </pre>
	 */
	public static final String CLOSE_DIALOG_ENABLED = "close_dialog_enabled";
	/**
	 * <pre>
	 * 광고 요청 횟수
	 * Preference Type : Integer
	 * </pre>
	 */
	public static final String REQUEST_AD_COUNT = "request_ad_count";
	/**
	 * <pre>
	 * 필터링 아이디 목록
	 * Preference Type : String
	 * </pre>
	 */
	public static final String FILTERING_ID_LIST = "filtering_id_list";
	/**
	 * <pre>
	 * 필터링 닉네임 목록
	 * Preference Type : String
	 * </pre>
	 */
	public static final String FILTERING_NICK_LIST = "filtering_nick_list";
	/**
	 * <pre>
	 * 필터링 적용 항목
	 * Preference Type : String
	 * </pre>
	 */
	public static final String FILTERING_ITEM_LIST = "filtering_item_list";
}
