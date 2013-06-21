package com.dvdprime.android.app.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.dvdprime.android.app.R;
import com.dvdprime.android.app.constants.Const;
import com.dvdprime.android.app.constants.PreferenceKeys;
import com.dvdprime.android.app.model.BaseModel;
import com.dvdprime.android.app.util.PrefUtil;
import com.dvdprime.android.app.util.StringUtil;
import com.dvdprime.android.app.view.WebImageView;


/**
 * 기본 아답터
 *
 * @author Kwang-myung,Choi (frantik@gmail.com)
 */
public abstract class BaseModelAdapter extends ArrayAdapter<Object> {

	/** The Constant TAG. */
	public static final String TAG = "BaseModelAdapter";

	/**
	 * The model items.
	 */
	private List< ? extends BaseModel> items;

	/**
	 * The paginator helper.
	 */
	private Paginator paginator;
	/**
	 * The activity.
	 */
	private Activity activity;
	
	private ArrayList<WeakReference<WebImageView>> wivs;
	
	private View mLoadingView;
	
	private PrefUtil prefs;


	/**
	 * Instantiates a new base model adapter.
	 * 
	 * @param pActivity
	 *            the activity
	 * @param textViewResourceId
	 *            the text view resource id
	 * @param pItems
	 *            the items
	 */
	public BaseModelAdapter(final Activity pActivity, 
							final int textViewResourceId,
							final List< ? extends BaseModel> pItems) {
		super(pActivity, textViewResourceId);
		this.items = pItems;
		this.activity = pActivity;
		prefs = PrefUtil.getInstance();
	}
	
	@Override
	public final View getView(final int position,
							View convertView,
							final ViewGroup parent) {
		View view = null;
		
		// Auto Next Page Loading
		if (paginator != null && position == getCount()-1 
				&& paginator.canLoadMore()) {// && !paginator.isLoading()) {
			if (paginator.getMore()) {
				if(this.mLoadingView != null) {
					return mLoadingView;
				} else
					Toast.makeText(getContext(), getContext().getResources().getString(R.string.bbs_progressbar_loading), Toast.LENGTH_SHORT).show();
			}
		}
		
		if (convertView == mLoadingView) {
            convertView = null;
        }
		
		BaseModel model = (BaseModel) getItem(position);
		
		if (convertView == null) {
			view = createView(model);
		} else {
			view = convertView;
		}
		
		initView(view, model);
		
		return view;
	}

	/**
	 * Creates the  view.
	 * 
	 * @param model
	 *            the model
	 * @return the view
	 */
	protected abstract View createView(BaseModel model);

	/**
	 * Inits the view.
	 * 
	 * @param view
	 *            the view
	 * @param model
	 *            the model
	 */
	protected abstract void initView(View view, BaseModel model);

	/**
	 * Sets the paginator helper object.
	 * @param pPaginator  the new paginator
	 */
	public final void setPaginator(final Paginator pPaginator) {
		this.paginator = pPaginator;
		pPaginator.setAdapter(this);
	}
    
    /**
     * Sets the loading View
     * @param loadingViewResourceId
     */
    public void setLoadingViewResourceId(final int loadingViewResourceId){
    	this.mLoadingView = LayoutInflater.from(getContext()).inflate(loadingViewResourceId, null);

        // 리스트에 테마적용
        if (StringUtil.equals(prefs.getString(PreferenceKeys.DP_THEME, Const.BLACK_THEME), Const.BLACK_THEME)) {
        	mLoadingView.setBackgroundColor(Color.BLACK);
    		((TextView) mLoadingView.findViewById(R.id.article_footer_title))
    			.setTextAppearance(getContext(), R.style.CommonTextAppearanceListContentsWhite);
    	} else {
    		mLoadingView.setBackgroundColor(Color.WHITE);
    		((TextView) mLoadingView.findViewById(R.id.article_footer_title))
				.setTextAppearance(getContext(), R.style.CommonTextAppearanceListContents);
    	}
    }
    
	/**
	 * Gets the model items.
	 * 
	 * @return the items
	 */
	public List< ? extends BaseModel> getItems() {
		return items;
	}

	/**
	 * Method for support image download cycle (show loading wheel, 
	 * create separate process for download, hide loading wheel after 
	 * success download, set image to UI view ).
	 * 
	 * @param url
	 *            the url
	 * @param bitmap
	 *            the bitmap
	 * @param view
	 *            the view
	 */
	public final void setBitmap(final String url, final Bitmap bitmap, final ImageView view) {
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (view.getTag(R.id.tagImageURL) != null && view.getTag(R.id.tagImageURL).toString().equals(url)) {
					view.setImageBitmap(bitmap);
				}
			}
		});
	}

	public final void addWebImageView(final WebImageView wiv) {
		if (wivs == null) {
			wivs = new ArrayList<WeakReference<WebImageView>>();
		}
		wivs.add(new WeakReference<WebImageView>(wiv));
	}
	
	public final void dispose(){
		if (wivs != null) {
			for (WeakReference<WebImageView> wr : wivs) {
				WebImageView wiv = wr.get();
				if (wiv != null) {
					wiv.dispose();
				}
			}
			wivs.clear();
			wivs = null;
		}
		items.clear();
		items = null;
	}
}
