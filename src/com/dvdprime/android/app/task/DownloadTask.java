package com.dvdprime.android.app.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.dvdprime.android.app.constants.MessageHelper;
import com.dvdprime.android.app.http.HttpClient;
import com.dvdprime.android.app.manager.ImageManager;

public class DownloadTask extends AsyncTask<String, Void, Object> {

	private static final String TAG = "DownloadTask";

	private static final String KEEP_ALIVE = "Keep-Alive";

	private static final String CONNECTION = "Connection";
	
	private HttpURLConnection conn;

	/** download consumers wating for data. */
	private List<DownloadConsumer> consumers;

	/** task life-cycle  manager.*/
	private ImageManager manager;

	/** download url. */
	private String url;

	private boolean cache;
	
	private int filesize = 0;
	
	private static final int SOCKET_CONNECTION_TIMEOUT = 5000;
    
	public DownloadTask(final ImageManager pManager, final DownloadConsumer pConsumer, boolean cache) {
		super();
		this.consumers = new ArrayList<DownloadConsumer>();
		this.cache = cache;
		this.consumers.add(pConsumer);
		this.manager = pManager;
	}

	/**
	 * Notifies consumer and manager about task completion.  
	 * @param result download result
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected final void onPostExecute(final Object result) {
		if (/*result != null &&*/ !isCancelled()) {		
			for (DownloadConsumer consumer : consumers) {
				consumer.consume(result);
			}
		}
		manager.complete(result, url, cache);
	}

	/**
	 * Performs download on background thread. 
	 * Encodes received stream of bytes depending on type of consumer. 
	 * 
	 * @param urls download urls
	 * @return download result
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected final Object doInBackground(final String... urls) {
		url = urls[0];
		Object result = null;

		switch (consumers.get(0).getType()) {
		case DownloadConsumer.BITMAP:
			result = downloadBitmap(url);
			break;
		default:
			break;
		}
		
		return result;
	}
	
	/**
	 * Returns last consumer
	 * @return
	 */
	public DownloadConsumer getDownloadConsumer() {
		return consumers.get(consumers.size()-1);
	}
	
	/**
	 * Download bitmap from network.
	 * for normal image
	 * 
	 * @param pUrl
	 *            bitmap URL
	 * @return downloaded bitmap
	 */
	private Bitmap downloadBitmap(final String pUrl) {
		Bitmap bitmap = null;
		InputStream stream = null;

		try
		{
			stream = download(pUrl);
            
			// if image size is 1MB over, do resize.
            if (filesize > 1000000)
        	{
            	BitmapFactory.Options opts = new BitmapFactory.Options();

            	if (filesize > 1000000)
            		opts.inSampleSize = 2;
            	else if (filesize > 2000000)
            		opts.inSampleSize = 4;
            	else if (filesize > 3000000)
            		opts.inSampleSize = 6;
            	
            	opts.inJustDecodeBounds = false;
            	opts.inDither = true; // we're using RGB_565, dithering improves this a bit. 
            	opts.inPreferredConfig = Bitmap.Config.RGB_565; 
            	
        		if (stream != null)
        			bitmap = BitmapFactory.decodeStream(stream, null, opts);
        	} else
        	{
        		if (stream != null)
        			bitmap = BitmapFactory.decodeStream(stream);
        	}
		} catch (OutOfMemoryError e) {
			if (MessageHelper.DEBUG)
				Log.d(TAG, Log.getStackTraceString(e));
		} catch (Exception e) {
			if (MessageHelper.DEBUG)
				Log.d(TAG, Log.getStackTraceString(e));
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException ex) {
				}
				stream = null;
			}
			stop();
		}
		return bitmap;
	}
	
	/**
	 * Establishes connection with given url and retrieves bytes as stream.
	 * 
	 * @param spec
	 *            download url
	 * @return stream of bytes
	 */
	private BufferedInputStream download(final String spec) {
		BufferedInputStream stream = null;

		try {
			if (MessageHelper.DEBUG)
				Log.d(TAG,"download: " + spec);
			URL specUrl = new URL(spec);
			conn = (HttpURLConnection) specUrl.openConnection();
			conn.setDoInput(true);
			conn.setConnectTimeout(SOCKET_CONNECTION_TIMEOUT);
			//conn.setReadTimeout(SOCKET_CONNECTION_TIMEOUT);
			conn.setRequestProperty(CONNECTION, KEEP_ALIVE);
			conn.connect();
			filesize = (int) conn.getHeaderFieldInt("Content-Length", 0);
			stream = new BufferedInputStream(new FlushedInputStream(conn.getInputStream()), HttpClient.DEFAULT_BUFFER_SIZE);
		} catch (MalformedURLException e) {
			if (MessageHelper.DEBUG)
				Log.d(TAG, Log.getStackTraceString(e));
		} catch (IOException e) {
			if (MessageHelper.DEBUG)
				Log.d(TAG, Log.getStackTraceString(e));
		} catch (OutOfMemoryError e) {
			if (MessageHelper.DEBUG)
				Log.d(TAG, Log.getStackTraceString(e));
		} catch (IllegalStateException e) {
			if (MessageHelper.DEBUG)
				Log.d(TAG, Log.getStackTraceString(e));
		} catch (NullPointerException e) {
			if (MessageHelper.DEBUG)
				Log.d(TAG, Log.getStackTraceString(e));
		} catch (Exception e) {
			if (MessageHelper.DEBUG)
				Log.d(TAG, Log.getStackTraceString(e));
		} finally {
			//System.gc();
		}

		return stream;
	}

	/**
	 * Stops execution of this task.
	 */
	public final void stop() {
		if (conn != null) {
			conn.disconnect();
			conn = null;
		}
	}

	/**
	 * Add consumer.
	 * @param consumer the consumer
	 */
	public final void addConsumer(DownloadConsumer consumer) {
		if (!consumers.contains(consumer) ) {
			consumers.add(consumer);
		}
	}
	
	/*
     * An InputStream that skips the exact number of bytes provided, unless it reaches EOF.
     * 스트림이 끝나지 않은 한, skip() 메서드가 실제로 전달받은 바이트 수 만큼을 건너띄도록 구현
     */
    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break;  // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }
}