package com.dvdprime.android.app.task;

public class DataEvent {

	/** 데이터 이벤트 타입코드 정의 */

	
	/** 게시물 목록 */
	public static final int ARTICLE_LIST = 1;
	/** 게시물 추가 목록 */
	public static final int ARTICLE_LIST_MORE = 2;
	/** 게시물 내용 */
	public static final int ARTICLE_CONTENT = 3;
	/** 게시물 추천 */
	public static final int ARTICLE_RECOMMEND = 4;
	/** 게시물 작성 */
	public static final int ARTICLE_WRITE = 5;
	/** 게시물 수정 */
	public static final int ARTICLE_MODIFY = 6;
	/** 게시물 삭제 */
	public static final int ARTICLE_DELETE = 7;
	/** 게시물 작성시 이미지 첨부 */
	public static final int ARTICLE_ADD_IMAGE = 8;
	/** 게시물 MY 디피에 저장 */
	public static final int ARTICLE_SAVE_MYDP = 9;
	
	/** 로그인 */
	public static final int LOGIN = 10;
	/** 로그인 여부 확인 - 쪽지목록으로... */
	public static final int LOGIN_CHECK = 11;
	
	/** 댓글 쓰기 */
	public static final int COMMENT_WRITE = 12;
	/** 덧글 쓰기 */
	public static final int CHILD_CMT_WRITE = 13;
	/** 댓글 추천 */
	public static final int COMMENT_RECOMMEND = 14;
	/** 댓글 삭제 */
	public static final int COMMENT_DELETE = 15;
	
	/** 쪽지 목록 */
	public static final int MEMO_LIST = 16;
	/** 쪽지 삭제 */
	public static final int MEMO_DELETE = 17;
	/** 쪽지 쓰기 */
	public static final int MEMO_WRITE = 18;
	/** 쪽지 보관 */
	public static final int MEMO_STORAGE = 19;
	/** 쪽지 체크 */
	public static final int MEMO_CHECK = 20;
	
	/** 스크랩창고 목록 */
	public static final int SCRAP_LIST = 21;
	/** 스크랩창고 더보기 목록 */
	public static final int SCRAP_LIST_MORE = 22;
	/** 글창고 목록 */
	public static final int DOCUMENT_LIST = 23;
	/** 글창고 더보기 목록 */
	public static final int DOCUMENT_LIST_MORE = 24;
	/** 댓글창고 목록 */
	public static final int COMMENT_LIST = 25;
	/** 댓글창고 더보기 목록 */
	public static final int COMMENT_LIST_MORE = 26;
	
	/** 게시물 단축 URL 가져오기 */
	public static final int SHORTLY_URL = 27;

	/**
	 * 결과 데이터 오브젝트.
	 */
	private Object data;

	/**
	 * Event type.
	 */
	private int type;

	/**
	 * Flag indicating whether delivered data has changed. 
	 */
	private boolean dataChanged;

	public DataEvent(final Object pData, final int pType) {
		data = pData;
		type = pType;
		dataChanged = true;
	}
	
	public DataEvent(final Object pData, final int pType, final boolean pDataChanged) {
		data = pData;
		type = pType;
		dataChanged = pDataChanged;
	}

	/**
	 * Gets the delivered data.
	 * @return  data object
	 */
	public final Object getData() {
		return data;
	}

	/**
	 * Gets the event type.
	 * @return  event type
	 */
	public final int getType() {
		return type;
	}

	/**
	 * Gets data chaged indicating flag.  결과 데이터에 변화가 있는지 가르치는 변수 flag
	 * @return  flag indicating whether delivered data has changed
	 */
	public boolean isDataChanged() {
		return dataChanged;
	}

	/**
	 * Sets data chaged indicating flag. 
	 * @param pDataChanged  <code>true</code>, if data has changed
	 */
	public void setDataChanged(final boolean pDataChanged) {
		dataChanged = pDataChanged;
	}
	
}