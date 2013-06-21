package com.dvdprime.android.app.manager;

import com.dvdprime.android.app.task.DataEvent;

public class AccountManager extends ConsumerManager {
	
	public static final String TAG = "AccountManager";

	private static AccountManager instance;
	
	private boolean loaded;
	
	private AccountManager() {
		super();
		loaded = false;
	}
	
	public static synchronized AccountManager getInstance() {
		if (instance == null) {
			instance = new AccountManager();
		}
		return instance;
	}
	
	public void login(String userId, String userPw) {
		DataManager.getInstance().login(this, DataEvent.LOGIN, userId, userPw);
	}

	public void loginCheck() {
		DataManager.getInstance().loginCheck(this, DataEvent.LOGIN_CHECK);
	}

	@Override
	public void handleEvent(final DataEvent event) {
		
		if (event.getData() != null) {
			
			switch (event.getType()) {
				case DataEvent.LOGIN:
				case DataEvent.LOGIN_CHECK:
					dispatch(event);
					break;
				default:
					break;
			}
		} 
		else
			dispatch(event);
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
