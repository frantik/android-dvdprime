package com.dvdprime.android.app.manager;

import com.dvdprime.android.app.task.DataEvent;

public class MemoManager extends ConsumerManager {
	
	public static final String TAG = "MemoManager";

	private static MemoManager instance;
	
	private boolean loaded;
	
	private MemoManager() {
		super();
		loaded = false;
	}
	
	public static synchronized MemoManager getInstance() {
		if (instance == null) {
			instance = new MemoManager();
		}
		return instance;
	}
	
	public void list(String url) {
		DataManager.getInstance().memoList(this, DataEvent.MEMO_LIST, url);
	}
	
	public void delete(String memoId, String pageFlag) {
		DataManager.getInstance().memoDelete(this, DataEvent.MEMO_DELETE, memoId, pageFlag);
	}
	
	public void write(String receiver, String content, String sendCheck) {
		DataManager.getInstance().memoWrite(this, DataEvent.MEMO_WRITE, receiver, content, sendCheck);
	}

	public void moveStorage(String memoId, String pageFlag) {
		DataManager.getInstance().memoMoveStorage(this, DataEvent.MEMO_STORAGE, memoId, pageFlag);
	}

	public void check() {
		DataManager.getInstance().memoCheck(this, DataEvent.MEMO_CHECK);
	}
	
	@Override
	public void handleEvent(final DataEvent event) {
		
		if (event.getData() != null) {
			
			switch (event.getType()) {
			case DataEvent.MEMO_LIST:
			case DataEvent.MEMO_DELETE:
			case DataEvent.MEMO_WRITE:
			case DataEvent.MEMO_STORAGE:
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
