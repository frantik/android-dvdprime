package com.dvdprime.android.app;

import com.dvdprime.android.app.activity.DpTabActivity;

import android.app.Activity;

/**
 * 전역 객체(어플리케이션의 어떤 액티비티에서든 어떤 객체에서든 접근 가능한 객체) 
 * 
 * @author Kwang-myung,Choi (frantik@helizet.com)
 */
public class BaseApplication extends android.app.Application {

	public static final String TAG = "BaseApplication";
	
	private DpTabActivity tabHostActivity;
	
	@SuppressWarnings("unused")
	private Activity tabActivity;
	
	private Integer currentTab;
	
	@Override
	public final void onCreate() {
		super.onCreate();
		ContextHolder.getInstance().setContext(this);
	}
	
	/**
	 * Sets the tab host activity.
	 * @param pTabHostActivity  the new tab host activity
	 */
	public final void setTabHostActivity(final DpTabActivity pTabHostActivity) {
		this.tabHostActivity = pTabHostActivity;

	}
	
	public DpTabActivity getTabHostActivity() {
		return this.tabHostActivity;
	}
	
	/**
	 * Sets the tab host activity
	 * @param pActivity 
	 */
	public final void setTabWidgetActivity(final Activity pActivity) {
		this.tabActivity = pActivity;
	}
	
	/**
	 * 공통의 탭호스트 로딩을 보일때 사용.
	 */
	public final void showLoadingIndicator() {
		
	}
	
	/**
	 * 공통의 탭호스트 로딩을 안보일때 사용.
	 */
	public final void hideLoadingIndicator() {
		
	}
	
	public Integer getCurrentTab() {
		return currentTab;
	}
	
	public void clearCurrentTab() {
		currentTab = null;
	}
}
