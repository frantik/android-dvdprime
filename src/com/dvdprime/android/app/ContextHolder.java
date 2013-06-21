package com.dvdprime.android.app;

import android.app.Activity;
import android.content.Context;

/**
 * ���ø����̼��� ����������� Context �� Activity �� ���� �Ҽ� �ֵ��� �� Ŭ����
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