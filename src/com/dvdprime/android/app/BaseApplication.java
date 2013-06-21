package com.dvdprime.android.app;

import com.dvdprime.android.app.activity.DpTabActivity;

import android.app.Activity;

/**
 * ���� ��ü(���ø����̼��� � ��Ƽ��Ƽ������ � ��ü������ ���� ������ ��ü) 
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
	 * ������ ��ȣ��Ʈ �ε��� ���϶� ���.
	 */
	public final void showLoadingIndicator() {
		
	}
	
	/**
	 * ������ ��ȣ��Ʈ �ε��� �Ⱥ��϶� ���.
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
