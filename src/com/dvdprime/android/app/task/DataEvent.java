package com.dvdprime.android.app.task;

public class DataEvent {

	/** ������ �̺�Ʈ Ÿ���ڵ� ���� */

	
	/** �Խù� ��� */
	public static final int ARTICLE_LIST = 1;
	/** �Խù� �߰� ��� */
	public static final int ARTICLE_LIST_MORE = 2;
	/** �Խù� ���� */
	public static final int ARTICLE_CONTENT = 3;
	/** �Խù� ��õ */
	public static final int ARTICLE_RECOMMEND = 4;
	/** �Խù� �ۼ� */
	public static final int ARTICLE_WRITE = 5;
	/** �Խù� ���� */
	public static final int ARTICLE_MODIFY = 6;
	/** �Խù� ���� */
	public static final int ARTICLE_DELETE = 7;
	/** �Խù� �ۼ��� �̹��� ÷�� */
	public static final int ARTICLE_ADD_IMAGE = 8;
	/** �Խù� MY ���ǿ� ���� */
	public static final int ARTICLE_SAVE_MYDP = 9;
	
	/** �α��� */
	public static final int LOGIN = 10;
	/** �α��� ���� Ȯ�� - �����������... */
	public static final int LOGIN_CHECK = 11;
	
	/** ��� ���� */
	public static final int COMMENT_WRITE = 12;
	/** ���� ���� */
	public static final int CHILD_CMT_WRITE = 13;
	/** ��� ��õ */
	public static final int COMMENT_RECOMMEND = 14;
	/** ��� ���� */
	public static final int COMMENT_DELETE = 15;
	
	/** ���� ��� */
	public static final int MEMO_LIST = 16;
	/** ���� ���� */
	public static final int MEMO_DELETE = 17;
	/** ���� ���� */
	public static final int MEMO_WRITE = 18;
	/** ���� ���� */
	public static final int MEMO_STORAGE = 19;
	/** ���� üũ */
	public static final int MEMO_CHECK = 20;
	
	/** ��ũ��â�� ��� */
	public static final int SCRAP_LIST = 21;
	/** ��ũ��â�� ������ ��� */
	public static final int SCRAP_LIST_MORE = 22;
	/** ��â�� ��� */
	public static final int DOCUMENT_LIST = 23;
	/** ��â�� ������ ��� */
	public static final int DOCUMENT_LIST_MORE = 24;
	/** ���â�� ��� */
	public static final int COMMENT_LIST = 25;
	/** ���â�� ������ ��� */
	public static final int COMMENT_LIST_MORE = 26;
	
	/** �Խù� ���� URL �������� */
	public static final int SHORTLY_URL = 27;

	/**
	 * ��� ������ ������Ʈ.
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
	 * Gets data chaged indicating flag.  ��� �����Ϳ� ��ȭ�� �ִ��� ����ġ�� ���� flag
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