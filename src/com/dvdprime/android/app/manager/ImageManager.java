package com.dvdprime.android.app.manager;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.AsyncTask.Status;
import android.view.View;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.dvdprime.android.app.adapter.BaseModelAdapter;
import com.dvdprime.android.app.cache.CacheImage;
import com.dvdprime.android.app.cache.ImageCacheMap;
import com.dvdprime.android.app.constants.Const;
import com.dvdprime.android.app.task.DownloadConsumer;
import com.dvdprime.android.app.task.DownloadTask;
import com.dvdprime.android.app.view.WebImageView;

/**
 * This class manages network image download requests. It's also responsible for 
 * showing and hiding image loading indicator. 
 * 
 * {@link:http://sunpeaksky.blog.163.com/blog/static/119613420108292442127/}
 */
public final class ImageManager extends ConsumerManager {

	public static final String TAG = "ImageManager";

	private static ImageManager instance;

	/**
	 * Active download tasks .
	 */
	private HashMap<String, DownloadTask> tasks;

	/**
	 * Image cache implementation.
	 */
	private ImageCacheMap cache;
	
	private DownloadTask runingTask;
	/**
	 * Instantiates a new image manager.
	 */
	private ImageManager() {
		tasks = new HashMap<String, DownloadTask>();
		
//		Context context 		= ContextHolder.getInstance().getContext();
//		Resources resources 	= context.getResources();
//		String expiration 		= resources.getString(R.string.cacheImageExpirationTime);
//		String sizeImage 		= resources.getString(R.string.cacheImageSize);
//		Integer cacheImageSize 	= Integer.valueOf(sizeImage);
		cache 					= new ImageCacheMap(Const.CACHE_IMAGE_SIZE);
	}

	/**
	 * Gets the single instance of ImageManager.
	 * 
	 * @return single instance of ImageManager
	 */
	public static synchronized ImageManager getInstance() {
		if (instance == null) {
			instance = new ImageManager();
		}
		return instance;
	}

	/**
	 * Adds the task to queue.
	 * 
	 * @param consumer
	 *            image receiver
	 * @param url
	 *            image url
	 */
	private synchronized void addTask(final DownloadConsumer consumer, final String url, final boolean doCache) {
		if (!tasks.containsKey(url)) {
			DownloadTask task = new DownloadTask(this, consumer, doCache);
			//task.execute(url);
			tasks.put(url, task);
			runFirst();
		} else {
			tasks.get(url).addConsumer(consumer);
		}
	}
	
	private synchronized void runFirst() {
		//ArrayList<String> mQueue = new ArrayList<String>();
		//mQueue.addAll( tasks.keySet() );
		
		Set<String> keySet = tasks.keySet(); 
		Iterator<String> itor = keySet.iterator(); 
			
		if (itor.hasNext() && runingTask == null) {
			//String url = (String) mQueue.get(0);
			String url = (String) itor.next(); 
			
			runingTask = (DownloadTask) tasks.get(url);
			if (runingTask.getStatus() == Status.PENDING) {
				runingTask.execute(url);
			}
		}
	}
	
	/**
	 * Cancels all download tasks.
	 */
	@Override
	public synchronized void cancel() {
		Iterator<String> it = tasks.keySet().iterator();
		while (it.hasNext()) {
			DownloadTask task = tasks.get(it.next());
			task.stop();
			task.cancel(true);
		}
		runingTask = null;
		tasks.clear();
	}

	/**
	 * Completes given task and caches result.
	 * 
	 * @param url
	 *            image url
	 * @param result
	 *            downloaded image
	 */
	public synchronized void complete(final Object result, final String url, final boolean doCache) {
		if (result != null && doCache) {
			cache.putFile(url, new CacheImage((Bitmap) result));
		}
		runingTask = null;
		tasks.remove(url);
		runFirst();
	}
	
	/**
	 * Returns ImageCacheMap
	 * 
	 * @return
	 */
	public ImageCacheMap getCache() {
		return cache;
	}
	
	/**
	 * Returns cash file exist
	 * @param url
	 * @return
	 */
	public boolean existFile(final String url) {
		CacheImage cacheBitmap = cache.getFile(url);
		
		if (cacheBitmap != null) {
			cacheBitmap.dispose();
			return true;
		} else
			return false;
	}

	/**
	 * Retrieves image from cache (if cached already) or  
	 * starts download task for image with specified URL and starts loading indication as well. 
	 * As a result view gets image set. 
	 * 
	 * @param url
	 *            image url
	 * @param view
	 *            image receiver
	 * @param adapter
	 *            view's parent adapter
	 */
	public void setImage( final String url, 
						  final ImageView view,
						  final BaseModelAdapter adapter,
						  final int type) {
		
		CacheImage cacheBitmap = cache.getFile(url);
		
		if (cacheBitmap != null) {
			adapter.setBitmap(url, cacheBitmap.getBitmap(), view);
		} else 
		{
			//showLoading(view);
			addTask(new DownloadConsumer() {

				@Override
				public void consume(final Object object) {
					Bitmap bmp = (Bitmap) object;
					adapter.setBitmap(url, bmp, view);
				}

				@Override
				public int getType() {
					return type;
				}
				
				@Override
				public Handler getHandler() {
					// TODO Auto-generated method stub
					return null;
				}
				
			}, url, true);
		}
	}
	
	public void setImage( final String url, 
			  final ImageView view,
			  final BaseModelAdapter adapter) {
		
		 setImage(url, view, adapter, DownloadConsumer.BITMAP);
	}
	
	/**
	 * Retrieves image from cache (if cached already) or  
	 * starts download task for image with specified URL. 
	 * WebImageView starts and stops loading indication by its own means. 
	 * As a result view gets image set.
	 * 
	 * @param url
	 *            image url
	 * @param view
	 *            image receiver
	 */
	public void setImage( final String url, 
						  final WebImageView view,
						  final int type,
						  final Handler mHandler) {
		
		CacheImage cacheBitmap = cache.getFile(url);
		
		if (cacheBitmap != null) {
			view.setBitmap(cacheBitmap.getBitmap());
		} else 
		{
			addTask(new DownloadConsumer() {

				@Override
				public void consume(final Object object) {
					Bitmap bmp = (Bitmap) object;
					view.setBitmap(bmp);
				}

				@Override
				public int getType() {
					return type;
				}
				
				@Override
				public Handler getHandler() {
					return mHandler;
				}

			}, url, true);
		}
	}
	
	public void setImage( final String url, 
			              final WebImageView view) {
		
		setImage( url, view, DownloadConsumer.BITMAP, null);
	}
	
	/**
	 * Hides loading indication in image waiting view.
	 * This method is called from activities to hide loading indication in window header. 
	 * 
	 * @param view
	 *            image waiting view
	 * @param activity
	 * 			caller activity
	 */
	public static void hideLoading(final Activity activity, final ImageView view) {
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				view.setVisibility(View.INVISIBLE);
				view.setAnimation(null);
			}
		});

	}

	/**
	 * Shows loading indication in image waiting view. 
	 * This method is called from activities to show loading indication. 
	 * 
	 * @param view
	 *            image waiting view
	 * @param activity
	 * 			caller activity
	 */
//	public static void showLoading(final Activity activity,
//			final ImageView view) {
//		activity.runOnUiThread(new Runnable() {
//
//			@Override
//			public void run() {
//				Animation rotation = AnimationUtils.loadAnimation(activity, R.anim.rotate);
//				view.startAnimation(rotation);
//				view.setVisibility(View.VISIBLE);
//			}
//
//		});
//	}
	
	@Override
	public void invalidate() {
		super.invalidate();
		cancel();
	}
}
