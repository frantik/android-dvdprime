package com.dvdprime.android.app.view;

import com.dvdprime.android.app.R;
import com.dvdprime.android.app.manager.ImageManager;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * The Class WebImageView.
 * 
 * Support logic for download images from backend and set it to UI element,
 * shoe loading wheel while image loading.  
 */
public class WebImageView {

	/**
	 * The bitmap.
	 */
	private Bitmap bitmap;

	/**
	 * The image view.
	 */
	private ImageView imageView;

	/**
	 * The url.
	 */
	private String url;

	/**
	 * Instantiates a new web image view.
	 */
	public WebImageView() {
	}

	/**
	 * Instantiates a new web image view.
	 * 
	 * @param pImageView
	 *            the image view
	 */
	public WebImageView(final ImageView pImageView) {
		this.imageView = pImageView;
	}
	
	/**
	 * Sets a new web image view.
	 * 
	 * @param pImageView
	 */
	public final void setImageView(final ImageView pImageView) {
		this.imageView = pImageView;
	}

	/**
	 * Gets the bitmap.
	 * @return  the bitmap
	 */
	public final Bitmap getBitmap() {
		return bitmap;
	}

	/**
	 * Sets the bitmap to UI component and hide loading wheel.
	 * @param pBitmap  the new bitmap
	 */
	public final void setBitmap(final Bitmap pBitmap) {
		this.bitmap = pBitmap;

		imageView.setImageBitmap(pBitmap);
	}

	/**
	 * Gets the image url.
	 * 
	 * @return the image url
	 */
	public final String getImageUrl() {
		return url;
	}

	/**
	 * Sets the image url.
	 * 
	 * @param pUrl
	 *            the new image url
	 */
	public final void setImageUrl(final String pUrl) {
		if (this.url == null || !this.url.equals(pUrl)) {
			this.url = pUrl;
			bitmap = null;
		}
		load();
	}

	/**
	 * Load bitmap from url.
	 */
	private void load() {
		if (bitmap == null) {
			if (url != null) {
				ImageManager.getInstance().setImage(url, this);
			}
		} else {
			setBitmap(bitmap);
		}
	}
	
	public void dispose() {
		imageView.setImageBitmap(null);
		
		imageView.setTag(R.id.tagWebImage, null);
		imageView.setTag(R.id.tagImageURL, null);
		imageView.setTag(R.id.tagScaleType, null);
		imageView.setTag(R.id.tagBackground, null);
		imageView.setTag(R.id.tagPromoURL, null);
		
		imageView = null;
		if (bitmap!=null) {
			bitmap = null;
		}
	}
	
	public void cancel() {
		ImageManager.getInstance().cancel();
	}

}
