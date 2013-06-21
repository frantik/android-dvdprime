package com.dvdprime.android.app.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dvdprime.android.app.R;
import com.dvdprime.android.app.constants.Const;
import com.dvdprime.android.app.constants.PreferenceKeys;
import com.dvdprime.android.app.model.Article;
import com.dvdprime.android.app.model.BaseModel;
import com.dvdprime.android.app.util.PrefUtil;
import com.dvdprime.android.app.util.StringUtil;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.Filterable;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class ArticleListAdapter extends BaseModelAdapter implements SectionIndexer, Filterable {
	
	public static final String TAG = ArticleListAdapter.class.getSimpleName();
	
	private Activity activity;
	private PrefUtil prefs;

	private List< ? extends BaseModel> items ;
	
	protected final Map<Integer, Integer> itemIndexToSectionIndex = new HashMap<Integer, Integer>();
	protected final Map<Integer, Integer> sectionIndexToItemIndex = new HashMap<Integer, Integer>();
	
	public ArticleListAdapter( final Activity activity, 
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
    /**
     * Use the array index as a unique id.
     *
     * @see android.widget.ListAdapter#getItemId(int)
     */
    public long getItemId(int position) {
        return position;
    }
	
	@Override
	protected final View createView(final BaseModel model) {
		View view = View.inflate(getContext(), R.layout.article_list_item, null);
		
		ViewHolder vh = new ViewHolder();
        
        vh.article_title = (TextView) view.findViewById(R.id.article_list_title);
        vh.article_no = (TextView) view.findViewById(R.id.article_list_no);
        vh.article_user_name = (TextView) view.findViewById(R.id.article_list_user_name);
        vh.article_count = (TextView) view.findViewById(R.id.article_list_count);
        vh.article_write_date = (TextView) view.findViewById(R.id.article_list_write_date);

        // 리스트에 테마적용
        if (StringUtil.equals(prefs.getString(PreferenceKeys.DP_THEME, Const.BLACK_THEME), Const.BLACK_THEME)) {
        	view.setBackgroundResource(R.drawable.theme_black_list_selector);
        	
        	// 각 값들의 스타일 변경
        	vh.article_title.setTextAppearance(activity, R.style.CommonTextAppearanceListContentsWhite);
        	vh.article_user_name.setTextAppearance(activity, R.style.GrayBaseSmallText);
        } else {
        	view.setBackgroundResource(R.drawable.theme_white_list_selector);

        	// 각 값들의 스타일 변경
        	vh.article_title.setTextAppearance(activity, R.style.CommonTextAppearanceListContents);
        	vh.article_user_name.setTextAppearance(activity, R.style.DarkGrayBaseSmallText);
        }

        view.setTag(vh);
		
        return view;
	}
	
	@Override
	protected final void initView(final View view, final BaseModel model) {
		final Article product = (Article) model;
		
		ViewHolder holder = (ViewHolder) view.getTag();
		
    	if (StringUtil.isBlank(product.getUserName())) {
        	holder.article_title.setText(product.getTitle());
        	holder.article_title.setGravity(Gravity.CENTER);//0x11); // center
        	holder.article_no.setText(product.getNo());
        	holder.article_user_name.setText(activity.getString(R.string.empty));
        	holder.article_count.setText(activity.getString(R.string.empty));
        	holder.article_write_date.setText(activity.getString(R.string.empty));
        	
//        	if (activity instanceof ArticleListActivity) {
//        		view.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View paramView) {
//						String url = Const.HOMEPAGE_URL + DBAdapter.getInstance().getArticleMoreUrl();
//		        		((ArticleListActivity)activity).loadMore(url);
//					}
//        		});
//        	}
        } else {
            String title = product.getTitle();
            if (StringUtil.isNotBlank(product.getComment())
            		&& !StringUtil.equals(product.getComment(), "0"))
            	title = title + "(" + product.getComment() + ")";
            
			holder.article_title.setText(StringUtil.decode(title));
            holder.article_title.setGravity(Gravity.LEFT);//0x03); // left
            
            String no = product.getNo();
            String name = product.getUserName();
            holder.article_no.setText(no);
            if (StringUtil.equals(no, "HOT"))
            	holder.article_no.setTextAppearance(activity, R.style.RedBaseSmallText);
            else if (StringUtil.equals(no, "COOL"))
            	holder.article_no.setTextAppearance(activity, R.style.BlueBaseSmallText);
            else if (StringUtil.equals(no, activity.getString(R.string.notice_no)))
            	holder.article_no.setTextAppearance(activity, R.style.OrangeBaseSmallText);
            else
            	holder.article_no.setTextAppearance(activity, R.style.GrayBaseSmallText);

            holder.article_user_name.setText(name);
            
            String recommend = product.getRecommend();
            if (recommend == null || recommend.equals(""))
            	recommend = "0";
            
            holder.article_count.setText(recommend + "/" + product.getCount());
            holder.article_write_date.setText(product.getDate());
        }
	}
	
	@Override 
	public int getPositionForSection(final int section) {
		final Integer position = sectionIndexToItemIndex.get(section);
		if (position == null) {
			return 0;
		}
		return position;
	}
	
	@Override 
	public int getSectionForPosition(final int position) {
		final Integer section = itemIndexToSectionIndex.get(position);
		if (section == null) {
			return 0;
		}
		return section;
	}
	
	@Override 
	public Object[] getSections() {
		return null;
	}
	
	@Override 
    public void notifyDataSetChanged() 
    { 
        super.notifyDataSetChanged();
	}
	
	private class ViewHolder {
        TextView article_title;
        TextView article_no;
        TextView article_user_name;
        TextView article_count;
        TextView article_write_date;
    }
}
