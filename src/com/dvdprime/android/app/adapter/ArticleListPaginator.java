package com.dvdprime.android.app.adapter;

import java.util.List;

import android.app.Activity;

import com.dvdprime.android.app.ContextHolder;
import com.dvdprime.android.app.R;
import com.dvdprime.android.app.constants.Const;
import com.dvdprime.android.app.constants.CurrentInfo;
import com.dvdprime.android.app.constants.PreferenceKeys;
import com.dvdprime.android.app.db.DBAdapter;
import com.dvdprime.android.app.dialog.DialogBuilder;
import com.dvdprime.android.app.manager.AccountManager;
import com.dvdprime.android.app.manager.ArticleManager;
import com.dvdprime.android.app.model.Article;
import com.dvdprime.android.app.task.DataConsumer;
import com.dvdprime.android.app.task.DataEvent;
import com.dvdprime.android.app.util.PrefUtil;
import com.dvdprime.android.app.util.StringUtil;

public class ArticleListPaginator implements DataConsumer, Paginator {
	
	public static final String TAG = "ArticleListPaginator";
	
	/**
	 * The loading.
	 */
	private Boolean loading;
	/**
	 * The current adapter.
	 */
	private BaseModelAdapter adapter;
//	private boolean mCanLoadMore = true;
	
	/**
	 * 
	 * @param context
	 */
	public ArticleListPaginator() {
		loading = false;
	}
	
	@Override
	public final boolean getMore() {
		if (!loading && canLoadMore()) {
			if (CurrentInfo.LOGIN_CHECK == 0) {
				requestListMore();
			} else {
				AccountManager aMng = AccountManager.getInstance();
				aMng.addConsumer(this, DataEvent.LOGIN_CHECK);
				aMng.loginCheck();
			}
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
			if (event.getType() == DataEvent.LOGIN_CHECK) {
				if (!(Boolean)event.getData()) {
					AccountManager aMng = AccountManager.getInstance();
					aMng.addConsumer(this, DataEvent.LOGIN);
					aMng.login(PrefUtil.getInstance().getString(PreferenceKeys.ACCOUNT_ID, ""), 
							PrefUtil.getInstance().getString(PreferenceKeys.ACCOUNT_PW, ""));
				} else {
					requestListMore();
				}
			} else if (event.getType() == DataEvent.LOGIN) {
				if ((Integer)event.getData() == Const.OK) {
					requestListMore();
				} else {
					Activity activity = ContextHolder.getInstance().getCurrentActivity();
					DialogBuilder.createAlertDialog(activity, 
										activity.getString(R.string.login_failed_message))
								.show();
					loading = false;
				}
			} else if (event.getType() == DataEvent.ARTICLE_LIST_MORE) {
				List<Article> items = (List<Article>) adapter.getItems();
				items.clear();
				items.addAll(DBAdapter.getInstance().getArticleList());
				
				adapter.notifyDataSetChanged();
				loading = false;
			}
		}
	}
	
	/**
	 * 새로운 페이지 목록 요청
	 */
	private void requestListMore() {
		String url = Const.HOMEPAGE_URL + DBAdapter.getInstance().getArticleMoreUrl();
		ArticleManager am = ArticleManager.getInstance();
		am.addConsumer(this, DataEvent.ARTICLE_LIST_MORE);
		am.listMore(url);
	}
	
	/**
	 * 추가데이터전송여부.
	 */
	public boolean canLoadMore() {
        return StringUtil.isNotEmpty(DBAdapter.getInstance().getArticleMoreUrl());
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
}