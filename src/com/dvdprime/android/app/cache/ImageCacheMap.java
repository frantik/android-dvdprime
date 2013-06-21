package com.dvdprime.android.app.cache;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import com.dvdprime.android.app.ContextHolder;
import com.dvdprime.android.app.constants.MessageHelper;
import com.dvdprime.android.app.db.DBAdapter;
import com.dvdprime.android.app.util.SystemUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

/**
 * The Class ImageCacheMap.
 * The class is designed for organizations caching models. Extending of {@link CacheMap} for synchronized methods put and remove.
 * 
 * @see CacheMap
 */
public class ImageCacheMap extends CacheMap<CacheImage> {

	/** The constant logging tag. */
	public static final String TAG = "ImageCacheMap";
	
	/**
	 * The expiration time of storage.
	 */
//	private Long expiration;
	
	private Context mContext = ContextHolder.getInstance().getContext();
	
	private Map<String, SoftReference<CacheImage>> mCache;
	
	/**
	 * Instantiates a new image cache map.
	 * 
	 * @param pSize the size of storage
	 * @param pExpiration the expiration of storage item
	 */
	public ImageCacheMap(final int pSize) {
		super(pSize);
		this.mCache = new HashMap<String, SoftReference<CacheImage>>();
	}

	/* (non-Javadoc)
	 * @see com.ensight.android.helizet.cache.CacheMap#put(java.lang.String, com.ensight.android.helizet.cache.BaseCacheModel)
	 */
	@Override
	public final synchronized void put(final String key, final CacheImage t) {
		super.put(key, t);
	}

	/* (non-Javadoc)
	 * @see com.ensight.android.helizet.cache.CacheMap#removeInvalidate(java.lang.String, com.ensight.android.helizet.cache.BaseCacheModel)
	 */
	@Override
	protected final synchronized CacheImage remove(final String key, final CacheImage t) {
		CacheImage cacheImage = super.remove(key, t);
		return cacheImage;
	}
	
	public final synchronized void putFile(final String url, final CacheImage t) {
		if(getFile(url) != null)
		{
			return;
		}
		
		FileOutputStream fos = null;
		String hashedKey = getMd5(url);
		Bitmap bitmap = null;
		String absolutePath = SystemUtil.getCacheDir(mContext) + File.separator + hashedKey;
		
		try 
		{
			// ������ ĳ�� ���丮�� ����
			fos = new FileOutputStream(new File(SystemUtil.getCacheDir(mContext), hashedKey));
			bitmap = t.getBitmap();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

			// ĳ�� ���丮�� DB Table�� �����ϵ��� ��� �߰�
			DBAdapter.getInstance().insertReceivedPhoto(url, absolutePath);
		} catch (Exception e) {
			if (MessageHelper.DEBUG)
				Log.d(TAG, Log.getStackTraceString(e));
		}  finally {
			if (fos != null) { try { fos.close(); } catch (IOException e) {} }
			t.dispose();
		}
	}
	
	/**
	 * ���� �ý��ۿ� �̹��� ������ �����Ѵٸ� ������.
	 * @param key
	 * @return
	 */
	public final CacheImage lookupFile(final String url) {
		Uri imageFileUri = DBAdapter.getInstance().searchReceivedPhoto(url);
		ParcelFileDescriptor pfd = null;
		InputStream is = null;
		Bitmap bm = null;
		boolean exist = true;

		if (imageFileUri == null)
			return null;
		
		try
		{
			pfd = mContext.getContentResolver().openFileDescriptor(imageFileUri, "r");
			is = new ParcelFileDescriptor.AutoCloseInputStream(pfd);
			bm = BitmapFactory.decodeStream(is);
			return new CacheImage(bm);
		}
		catch (FileNotFoundException e) { 
			if (MessageHelper.DEBUG)
				Log.d(TAG, Log.getStackTraceString(e));
			exist = false;
			return null; 
		}
		catch (NullPointerException e) { 
			if (MessageHelper.DEBUG)
				Log.d(TAG, Log.getStackTraceString(e));
			exist = false;
			return null; 
		}
		catch (IllegalStateException e) { 
			if (MessageHelper.DEBUG)
				Log.d(TAG, Log.getStackTraceString(e));
			exist = false;
			return null; 
		}
		catch (OutOfMemoryError e) { 
			if (MessageHelper.DEBUG)
				Log.d(TAG, Log.getStackTraceString(e));
			exist = false;
			return null; 
		}
		finally
		{
			if(is != null) {
				try {
					is.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			// ���� ������ �������� ���� ��� DB�� ������ �����Ѵ�.
			if (!exist) {
				DBAdapter.getInstance().deleteReceivedPhoto(url);
			}
		}
	}
	
	public final CacheImage getFile(final String url) {
		CacheImage bitmap;
	    bitmap = lookupFile(url);

	    if (bitmap != null)
	    {
	    	return bitmap;
	    }
	    
		if (MessageHelper.DEBUG)
			Log.d(TAG, "Image is missing: " + url);
	    return null;
	}
	
	@Deprecated
	public final synchronized void clearFile() {
		//TODO
		//���丮 ���� ���� �ؾ���.
		String [] files = mContext.fileList();
		
	    for (String file : files) {
	      mContext.deleteFile(file);
	    }
	    
	    mCache.clear();
	}
}
