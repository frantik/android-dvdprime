package com.dvdprime.android.app.manager;

import com.dvdprime.android.app.task.DataEvent;

public class ScrapManager extends ConsumerManager {
	
	public static final String TAG = "ScrapManager";

	private static ScrapManager instance;
	
	private boolean loaded;
	
	private ScrapManager() {
		super();
		loaded = false;
	}
	
	public static synchronized ScrapManager getInstance() {
		if (instance == null) {
			instance = new ScrapManager();
		}
		return instance;
	}
	
	public void list(String scrapUrl) {
		DataManager.getInstance().scrapList(this, DataEvent.SCRAP_LIST, scrapUrl);
	}
	
	public void listMore(String scrapUrl) {
		DataManager.getInstance().scrapList(this, DataEvent.SCRAP_LIST_MORE, scrapUrl);
	}
	
	public void documentList(String documentUrl) {
		DataManager.getInstance().documentList(this, DataEvent.DOCUMENT_LIST, documentUrl);
	}
	
	public void documentListMore(String documentUrl) {
		DataManager.getInstance().documentList(this, DataEvent.DOCUMENT_LIST_MORE, documentUrl);
	}
	
	public void commentList(String commentUrl) {
		DataManager.getInstance().commentList(this, DataEvent.COMMENT_LIST, commentUrl);
	}
	
	public void commentListMore(String commentUrl) {
		DataManager.getInstance().commentList(this, DataEvent.COMMENT_LIST_MORE, commentUrl);
	}
	
	@Override
	public void handleEvent(final DataEvent event) {
		
		if (event.getData() != null) {
			
			switch (event.getType()) {
			case DataEvent.SCRAP_LIST:
			case DataEvent.SCRAP_LIST_MORE:
			case DataEvent.DOCUMENT_LIST:
			case DataEvent.DOCUMENT_LIST_MORE:
			case DataEvent.COMMENT_LIST:
			case DataEvent.COMMENT_LIST_MORE:
				dispatch(event);
				break;
			default:
				break;
			}
			
		} else
		{
			dispatch(event);
		}
	}
	
	/**
	 * 서버 응답에서 데이터 가져오기.
	 */
	public void fetch() {
		loaded = true;
	}

	/**
	 * Checks if profile is loaded.
	 * @return  true, if is loaded
	 */
	public boolean isLoaded() {
		return loaded;
	}

	/**
	 * Sets profile loaded flag.
	 * @param flag  value to set
	 */
	public void setLoaded(final boolean flag) {
		loaded = flag;
	}
}
