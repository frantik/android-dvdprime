package com.dvdprime.android.app;

import android.app.Activity;
import android.content.Context;

/**
 * 어플리케이션의 어느곳에서나 Context 및 Activity 를 참조 할수 있도록 한 클래스
 *
 * @author Kwang-myung,Choi (frantik@helizet.com)
 */
public final class ContextHolder {

	private static ContextHolder instance;

	private Context context;

	private Activity activity;

	private ContextHolder() {
	}
	
	public static synchronized ContextHolder getInstance() {
		if (instance == null) {
			instance = new ContextHolder();
		}
		return instance;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Singleton");
	}
	
	/**
	 * Gets the context.
	 * @return  the context
	 */
	public Context getContext() {
		return context;
	}
	
	/**
	 * Sets the context.
	 * @param pContext  the new context
	 */
	public void setContext(final Context pContext) {
		this.context = pContext;
	}

	public Activity getCurrentActivity(){
		return activity;
	}

	public void setCurrentActivity(Activity activity){
		this.activity = activity;
	}
}