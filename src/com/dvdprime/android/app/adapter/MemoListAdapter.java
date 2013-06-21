package com.dvdprime.android.app.adapter;

import java.util.List;

import com.dvdprime.android.app.R;
import com.dvdprime.android.app.constants.Const;
import com.dvdprime.android.app.constants.PreferenceKeys;
import com.dvdprime.android.app.model.BaseModel;
import com.dvdprime.android.app.model.Memo;
import com.dvdprime.android.app.util.PrefUtil;
import com.dvdprime.android.app.util.StringUtil;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

public class MemoListAdapter extends BaseModelAdapter {
	
	public static final String TAG = MemoListAdapter.class.getSimpleName();
	
	private Activity activity;
	private PrefUtil prefs;

	private List< ? extends BaseModel> items ;
	
	public MemoListAdapter( final Activity activity, 
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
		View view = View.inflate(getContext(), R.layout.memo_list_row, null);
		
		ViewHolder vh = new ViewHolder();
        
        vh.memo_user_id = (TextView) view.findViewById(R.id.memo_list_row_user_id_textView);
        vh.memo_content = (TextView) view.findViewById(R.id.memo_list_row_content_textView);
        vh.memo_date = (TextView) view.findViewById(R.id.memo_list_row_time_textView);

        // 리스트에 테마적용
        if (StringUtil.equals(prefs.getString(PreferenceKeys.DP_THEME, Const.BLACK_THEME), Const.BLACK_THEME)) {
        	view.setBackgroundResource(R.drawable.theme_black_list_selector);
        	
        	// 각 값들의 스타일 변경
        	vh.memo_user_id.setTextAppearance(activity, R.style.YellowBaseSecondaryText);
        	vh.memo_content.setTextAppearance(activity, R.style.WhiteBaseSecondaryText);
        } else {
        	view.setBackgroundResource(R.drawable.theme_white_list_selector);

        	// 각 값들의 스타일 변경
        	vh.memo_user_id.setTextAppearance(activity, R.style.EmeraldBaseSecondaryText);
        	vh.memo_content.setTextAppearance(activity, R.style.BlackBaseSecondaryText);
        }

        view.setTag(vh);
		
        return view;
	}
	
	@Override
	protected final void initView(final View view, final BaseModel model) {
		final Memo product = (Memo) model;
		
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.memo_user_id.setText(product.getUserId());
        holder.memo_content.setText(StringUtil.decode(product.getContent()));
        holder.memo_date.setText(product.getDate());
	}
	
	@Override 
    public void notifyDataSetChanged() 
    { 
        super.notifyDataSetChanged();
	}
	
	private class ViewHolder {
        TextView memo_user_id;
        TextView memo_content;
        TextView memo_date;
    }
}
