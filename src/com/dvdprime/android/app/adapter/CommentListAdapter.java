package com.dvdprime.android.app.adapter;

import java.util.List;

import com.dvdprime.android.app.R;
import com.dvdprime.android.app.constants.Const;
import com.dvdprime.android.app.constants.PreferenceKeys;
import com.dvdprime.android.app.manager.ImageManager;
import com.dvdprime.android.app.model.BaseModel;
import com.dvdprime.android.app.model.Comment;
import com.dvdprime.android.app.task.DownloadConsumer;
import com.dvdprime.android.app.util.DpUtil;
import com.dvdprime.android.app.util.PrefUtil;
import com.dvdprime.android.app.util.StringUtil;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.util.Linkify;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentListAdapter extends BaseModelAdapter {
	
	public static final String TAG = CommentListAdapter.class.getSimpleName();
	
	private Activity activity;
	private PrefUtil prefs;

	private List< ? extends BaseModel> items ;
	
	public CommentListAdapter( final Activity activity, 
							   final int textViewResourceId,
							   final List< ? extends BaseModel> items) {
		super(activity, textViewResourceId, items);
		
		this.activity = activity;
		this.items = items;
		prefs = PrefUtil.getInstance();
	}
	
	/**
     * The number of items in the list is determined by the number of speeches
     * in our array.
     *
     * @see android.widget.ListAdapter#getCount()
     */
    public int getCount() {
        return items.size();
    }
    /**
     * Since the data comes from an array, just returning the index is
     * sufficent to get at the data. If we were using a more complex data
     * structure, we would return whatever object represents one row in the
     * list.
     *
     * @see android.widget.ListAdapter#getItem(int)
     */
    public BaseModel getItem(int position) {
    	return items.get(position);
    }

	@Override
	protected final View createView(final BaseModel model) {
		View view = View.inflate(getContext(), R.layout.article_comment_list_row, null);
		
		ViewHolder vh = new ViewHolder();
        
        vh.avatar_image = (ImageView) view.findViewById(R.id.article_comment_list_row_avatar_imageView);
        vh.attach_image = (ImageView) view.findViewById(R.id.article_comment_list_row_media_imageView);
        vh.comment_text = (TextView) view.findViewById(R.id.article_comment_list_row_nameNcontents_textView);
        vh.comment_time = (TextView) view.findViewById(R.id.article_comment_list_row_time_textView);

        // 리스트에 테마적용
        if (StringUtil.equals(prefs.getString(PreferenceKeys.DP_THEME, Const.BLACK_THEME), Const.BLACK_THEME)) {
        	view.setBackgroundResource(R.drawable.theme_black_list_selector);
        } else {
        	view.setBackgroundResource(R.drawable.theme_white_list_selector);
        }
        
        view.setTag(vh);
		
		return view;
	}
	
	@Override
	protected final void initView(final View view, final BaseModel model) {
		final Comment product = (Comment) model;
		
		if (StringUtil.isEmpty(product.getUserName())) {
			view.setVisibility(View.GONE);
			return;
		}

		ViewHolder holder = (ViewHolder) view.getTag();
		
		String mUserName = StringUtil.removeHtmlTags(product.getUserName());
		String mAvartarUrl = product.getAvatarUrl();
		if (StringUtil.isNotEmpty(mAvartarUrl))
			mAvartarUrl = Const.HOMEPAGE_URL + mAvartarUrl;
		String mDate = product.getDate();
		String mContent = StringUtil.decode(product.getContent());
		final String[] mstrMediaThumbnailUrl = DpUtil.valueTagImg(mContent);
		mContent = StringUtil.removeHtmlTags(mContent);
		int miMediaCount;
		if (mstrMediaThumbnailUrl != null)
			miMediaCount = mstrMediaThumbnailUrl.length;
		else
			miMediaCount = 0;

		
		String str = mUserName;
		int start = 0;
		
		if(mUserName == null && mContent == null)
		{
			str = "";
			start = 0;
		}
		else if(mUserName == null && mContent != null)
		{
			str = mContent;
			start = 0;
		}
		else if(mUserName != null && mContent != null)
		{
			str = mUserName + " " + mContent;
			start = mUserName.length();
		}
		
		// 아바타 URL이 없을경우 이미지 없앰
		if (StringUtil.isBlank(mAvartarUrl))
			holder.avatar_image.setVisibility(View.INVISIBLE);
		else {
			holder.avatar_image.setVisibility(View.VISIBLE);

			final String url = product.getAvatarUrl();
			String thumbUrl = (url != null) ? Const.HOMEPAGE_URL + url : null;
			
			// values/tags.xml
			holder.avatar_image.setImageBitmap(null);
			holder.avatar_image.setTag(R.id.tagImageURL, thumbUrl);

			if( url!= null) ImageManager.getInstance().setImage(thumbUrl, holder.avatar_image, this, DownloadConsumer.BITMAP);
		}
		// 첨부 이미지가 있을 경우 보여준다.
		// 텍스트만 보기 모드인지 여부 확인
		if (!prefs.getBoolean(PreferenceKeys.CONTENT_TEXT_ONLY, false) && miMediaCount > 0) {
			holder.attach_image.setVisibility(View.VISIBLE);

			// values/tags.xml
			holder.attach_image.setImageBitmap(null);
			holder.attach_image.setTag(R.id.tagImageURL, mstrMediaThumbnailUrl[0]);
			holder.attach_image.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(mstrMediaThumbnailUrl[0] != null)
					{
						Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(mstrMediaThumbnailUrl[0]));
						activity.startActivity(i);
					}
				}
			});

			if( mstrMediaThumbnailUrl[0] != null) 
				ImageManager.getInstance().setImage(mstrMediaThumbnailUrl[0], holder.attach_image, this, DownloadConsumer.BITMAP);

		} else {
			holder.attach_image.setVisibility(View.GONE);
		}
		SpannableStringBuilder ssb = new SpannableStringBuilder(str);
        // 리스트에 테마적용
        if (StringUtil.equals(prefs.getString(PreferenceKeys.DP_THEME, Const.BLACK_THEME), Const.BLACK_THEME)) {
        	holder.comment_text.setTextAppearance(activity, R.style.BlackThemeCommentText);
			ssb.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.white)),
					start, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
        	holder.comment_text.setTextAppearance(activity, R.style.WhiteThemeCommentText);
			ssb.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.dp_color046)),
					start, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
		holder.comment_text.setText(ssb);
		//WEB_URLS
		Linkify.addLinks(holder.comment_text, Linkify.WEB_URLS);
		holder.comment_time.setText(mDate);
	}
	
	@Override 
    public void notifyDataSetChanged() 
    { 
        super.notifyDataSetChanged();
	}
	
	private class ViewHolder {
		ImageView avatar_image;
        ImageView attach_image;
        TextView comment_text;
        TextView comment_time;
    }
}
