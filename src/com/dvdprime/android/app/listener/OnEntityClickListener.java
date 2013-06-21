package com.dvdprime.android.app.listener;

import com.dvdprime.android.app.activity.ArticleContentActivity;
import com.dvdprime.android.app.constants.CurrentInfo;
import com.dvdprime.android.app.constants.IntentKeys;
import com.dvdprime.android.app.constants.RequestCode;
import com.dvdprime.android.app.model.Article;
import com.dvdprime.android.app.model.BaseModel;
import com.dvdprime.android.app.model.Scrap;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * OnEntityClickListener is a class for listen click on list view item.
 */
public class OnEntityClickListener implements OnItemClickListener {

	private Activity activity;

	public OnEntityClickListener(final Activity pActivity) {
		super();
		this.activity = pActivity;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public final void onItemClick(final AdapterView parent, final View v,
			final int position, final long id) {
		BaseModel model = (BaseModel) parent.getAdapter().getItem(position);
		Intent intent = null;
		if (model == null)
			return;
		
		switch (model.getModelType()) {
		case BaseModel.ARTICLE:
			Article atc = (Article) model;
			intent = new Intent(activity, ArticleContentActivity.class);
			intent.putExtra(IntentKeys.ARTICLE_ID, atc.getId());
			activity.startActivityForResult(intent, RequestCode.REQ_CODE_ARTICLE_DELETE);
			break;
		case BaseModel.SCRAP:
			Scrap scrap = (Scrap) model;
			CurrentInfo.BBS_TITLE = scrap.getNo();
			intent = new Intent(activity, ArticleContentActivity.class);
			intent.putExtra(IntentKeys.ARTICLE_ID, scrap.getId());
			intent.putExtra(IntentKeys.SCRAP_TYPE, scrap.getType());
			activity.startActivity(intent);
			break;
		default:
			break;
		}
	}
}