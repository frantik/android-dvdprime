package com.dvdprime.android.app.manager;

import com.dvdprime.android.app.task.DataEvent;

public class CommentManager extends ConsumerManager {
	
	public static final String TAG = "CommentManager";

	private static CommentManager instance;
	
	private boolean loaded;
	
	private CommentManager() {
		super();
		loaded = false;
	}
	
	public static synchronized CommentManager getInstance() {
		if (instance == null) {
			instance = new CommentManager();
		}
		return instance;
	}
	
	public void write(String bbsId, String text) {
		DataManager.getInstance().commentWrite(this, DataEvent.COMMENT_WRITE, bbsId, text);
	}
	
	public void childWrite(String bbsId, String cmtId, String text) {
		DataManager.getInstance().commentChildWrite(this, DataEvent.CHILD_CMT_WRITE, bbsId, cmtId, text);
	}
	
	public void recommend(String cmtId) {
		DataManager.getInstance().commentRecommend(this, DataEvent.COMMENT_RECOMMEND, cmtId);
	}
	
	public void delete(String bbsId, String cmtId) {
		DataManager.getInstance().commentDelete(this, DataEvent.COMMENT_DELETE, bbsId, cmtId);
	}
	
	@Override
	public void handleEvent(final DataEvent event) {
		
		if (event.getData() != null) {
			
			switch (event.getType()) {
			case DataEvent.COMMENT_WRITE:
			case DataEvent.CHILD_CMT_WRITE:
			case DataEvent.COMMENT_RECOMMEND:
			case DataEvent.COMMENT_DELETE:
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
