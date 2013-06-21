package com.dvdprime.android.app.adapter;

import java.util.List;

import android.app.Activity;

import com.dvdprime.android.app.ContextHolder;
import com.dvdprime.android.app.R;
import com.dvdprime.android.app.constants.Const;
import com.dvdprime.android.app.constants.PreferenceKeys;
import com.dvdprime.android.app.db.DBAdapter;
import com.dvdprime.android.app.dialog.DialogBuilder;
import com.dvdprime.android.app.manager.AccountManager;
import com.dvdprime.android.app.manager.ArticleManager;
import com.dvdprime.android.app.manager.ScrapManager;
import com.dvdprime.android.app.model.Scrap;
import com.dvdprime.android.app.task.DataConsumer;
import com.dvdprime.android.app.task.DataEvent;
import com.dvdprime.android.app.util.PrefUtil;
import com.dvdprime.android.app.util.StringUtil;

public class ScrapListPaginator implements DataConsumer, Paginator {
	
	public static final String TAG = "ScrapListPaginator";
	
	/**
	 * The loading.
	 */
	private Boolean loading;
	/**
	 * Scrap Type
	 */
	private String type;
	/**
	 * The current adapter.
	 */
	private BaseModelAdapter adapter;
	
	/**
	 * 
	 * @param context
	 */
	public ScrapListPaginator() {
		loading = false;
	}
	
	@Override
	public final boolean getMore() {
		if (!loading && canLoadMore()) {
			AccountManager aMng = AccountManager.getInstance();
			aMng.addConsumer(this, DataEvent.LOGIN_CHECK);
			aMng.loginCheck();
			loading = true;
		}
		return loading;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public final void handleEvent(final DataEvent event) {
		if(event.getData() == null)
			loading = false;
		
		if (loading) {
			switch (event.getType()) {
			case DataEvent.LOGIN:
				if ((Integer)event.getData() == Const.OK) {
					requestListMore();
				} else {
					Activity activity = ContextHolder.getInstance().getCurrentActivity();
					DialogBuilder.createAlertDialog(activity, 
										activity.getString(R.string.login_failed_message))
								.show();
					loading = false;
				}
				break;
			case DataEvent.LOGIN_CHECK:
				if (!(Boolean)event.getData()) {
					AccountManager aMng = AccountManager.getInstance();
					aMng.addConsumer(this, DataEvent.LOGIN);
					aMng.login(PrefUtil.getInstance().getString(PreferenceKeys.ACCOUNT_ID, ""), 
							PrefUtil.getInstance().getString(PreferenceKeys.ACCOUNT_PW, ""));
				} else {
					requestListMore();
				}
				break;
			case DataEvent.SCRAP_LIST_MORE:
			case DataEvent.DOCUMENT_LIST_MORE:
			case DataEvent.COMMENT_LIST_MORE:
				List<Scrap> items = (List<Scrap>) adapter.getItems();
				items.clear();
				items.addAll(DBAdapter.getInstance().getScrapList(type));
				
				adapter.notifyDataSetChanged();
				loading = false;
				break;
			}
		}
	}
	
	/**
	 * 새로운 페이지 목록 요청
	 */
	private void requestListMore() {
		int eventType = DataEvent.SCRAP_LIST_MORE;
		
		if (StringUtil.equals(type, Const.TYPE_DOC))
			eventType = DataEvent.DOCUMENT_LIST_MORE;
		else if (StringUtil.equals(type, Const.TYPE_CMT))
			eventType = DataEvent.COMMENT_LIST_MORE;
		
		String url = DBAdapter.getInstance().getScrapMoreUrl();
		ScrapManager sm = ScrapManager.getInstance();
		sm.addConsumer(this, eventType);
		
		if (eventType == DataEvent.SCRAP_LIST_MORE)
			sm.listMore(url);
		else if (eventType == DataEvent.DOCUMENT_LIST_MORE)
			sm.documentListMore(url);
		else if (eventType == DataEvent.COMMENT_LIST_MORE)
			sm.commentListMore(url);
	}
	
	/**
	 * 추가데이터전송여부.
	 */
	public boolean canLoadMore() {
        return StringUtil.isNotEmpty(DBAdapter.getInstance().getScrapMoreUrl());
    }
	
	public boolean isLoading() {
		return loading;
	}
	
	@Override
	public final void invalidate() {
		ArticleManager.getInstance().removeConsumer();
	}
	
	/**
	 * @param pAdapter
	 */
	@Override
	public final void setAdapter(final BaseModelAdapter pAdapter) {
		this.adapter = pAdapter;
	}
	
	public void setType(String type) {
		this.type = type;
	}
}