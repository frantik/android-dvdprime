package com.dvdprime.android.app.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore.Images;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import com.dvdprime.android.app.ContextHolder;
import com.dvdprime.android.app.constants.Const;
import com.dvdprime.android.app.constants.MessageHelper;
import com.dvdprime.android.app.listener.UploaderListener;
import com.dvdprime.android.app.model.UploadImage;
import com.dvdprime.android.app.util.ImageUtil;

/**
 * 이미지를 서버에 업로드 하기 위한 공통 Task
 */
public class UploadTask extends AsyncTask<Uri, Integer, Object> {

	private static final String TAG = "UploadTask";

	private HttpURLConnection conn;

	private int type;

	/** upload url. */
	private Uri url;

	/** upload params. */
	private Map<String, String> params;

	private volatile boolean isPaused;
	private volatile boolean isUploading;
	private final Object mutex = new Object();

	private static final String lineEnd = "\r\n";
	private static final String twoHyphens = "--";
	private static final String boundary = "------------------219731164328969462735";

	private static final int CONNECTION_TIMEOUT = 30000;

	private UploaderListener listener;

	public UploadTask(final Context context, final Map<String, String> params,
			final int type) {
		super();

		this.params = params;
		this.type = type;
		this.setPaused(false);
	}

	public void setListener(UploaderListener listener) {
		this.listener = listener;
	}

	@Override
	protected void onCancelled() {
		stop();
	}

	@Override
	protected final void onPostExecute(Object result) {
		if (MessageHelper.DEBUG)
			Log.d(TAG, "onPostExecute");
		if (listener != null) {
			listener.uploadingComplete(result);
		}
	}

	/**
	 * Performs download on background thread. Encodes received stream of bytes
	 * depending on type of consumer.
	 * 
	 * @param urls
	 *            download urls
	 * @return download result
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected final Object doInBackground(final Uri... urls) {
		url = urls[0];

		DataEvent result = null;

		try {
			result = upload(url);
		} catch (Exception e) {
			result = null;
			e.printStackTrace();
		}

		if (result == null)
			result = new DataEvent(null, type);

		return result;
	}

	private void progress(int total, int current) {
		int percent = (int) (100 * ((float) current / (float) total));
		publishProgress(percent, total);
	}

	private DataEvent upload(final Uri path) throws Exception {

		String fileName = "";
		int maxBufferSize = 1024;// 8192;
		DataOutputStream dos;
		File mFile = new File(Const.SDCARD_DIRECTORY, "dp_upload.tmp");
		int selectedFileSize = 1024;
		
		URL url = new URL(Const.IMAGE_UPLOAD_URL);

		if (path == null) {
			return null;
		}

		if (!mFile.exists()) {
			try {
				mFile.createNewFile();
			} catch (IOException e) {
				throw new IllegalStateException("Cannot create file : "
						+ mFile.toString());
			}
		}

		// Read bitmap image
		Bitmap bitmap = Images.Media.getBitmap(ContextHolder.getInstance()
				.getContext().getContentResolver(), path);
		// If bitmap width longer than preference photo width,
		// execute resize bitmap image
		ImageUtil.WriteFileFromBitmap(ImageUtil.resizeBitmap(bitmap, selectedFileSize), mFile);
		FileInputStream fileInputStream = new FileInputStream(mFile);

		// Open a HTTP connection to the URL
		conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(CONNECTION_TIMEOUT);
		conn.setReadTimeout(CONNECTION_TIMEOUT);
		conn.setDoInput(true); // Allow Inputs
		conn.setDoOutput(true); // Allow Outputs
		conn.setUseCaches(false); // Don't use a cached copy.
		conn.setRequestMethod("POST"); // Use a post method.
		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="
				+ boundary);

		dos = new DataOutputStream(conn.getOutputStream());

		String openingPart = writeContent(params, fileName + ".jpg",
				"image/jpeg");
		String closingPart = lineEnd + "--" + boundary + "--" + lineEnd;

		// write the image data to the server
		int bytesAvailable = fileInputStream.available();
		int fileSize = bytesAvailable;
		dos.writeBytes(openingPart);

		int bufferSize = Math.min(bytesAvailable, maxBufferSize);
		byte[] buffer = new byte[bufferSize];

		int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
		int totalBytesRead = bytesRead;

		while (bytesRead > 0 && !isCancelled()) {
			synchronized (dos) {
				dos.write(buffer, 0, bufferSize);
				dos.flush();
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
				totalBytesRead = totalBytesRead + bytesRead;

				progress(fileSize, totalBytesRead);
			}
		}

		dos.writeBytes(closingPart);
		fileInputStream.close();
		dos.flush();

		// read the server response
		BufferedReader rd = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		String response = "";

		String line;

		while ((line = rd.readLine()) != null) {
			response += line;
		}

		if (MessageHelper.DEBUG)
			Log.d(TAG, "Retrieved :" + response);

		if (mFile.exists())
			mFile.delete();
		if (dos != null)
			dos.close();
		if (rd != null)
			rd.close();
		return new DataEvent(new UploadImage(response), type);
	}

	private static String writeContent(Map<String, String> params,
			String fileName, String contentType) {

		StringBuffer buf = new StringBuffer();

		Set<String> keyset = params.keySet();
		Object[] keys = keyset.toArray();

		for (int i = 0; i < keys.length; i++) {
			String key = (String) keys[i];

			String val = (String) params.get(key);
			buf.append(twoHyphens).append(boundary).append(lineEnd);
			buf.append("Content-Disposition: form-data; name=\"").append(key)
					.append("\"").append(lineEnd).append(lineEnd).append(val)
					.append(lineEnd);
		}

		buf.append(twoHyphens).append(boundary).append(lineEnd);
		buf.append("Content-Disposition: form-data; name=\"Filedata\"; filename=\"")
				.append(fileName).append("\"").append(lineEnd).append(lineEnd);

		return buf.toString();
	}

	@Override
	protected final void onProgressUpdate(Integer... progress) {
		super.onProgressUpdate(progress);
		if (listener != null) {
			listener.progressUpdate(progress[0], progress[1]);
		}
	}
	
	/**
	 * @param isUploading
	 * the isUploading to set
	 */
	public void setUploading(boolean isUploading) {
		synchronized (mutex) {
			this.isUploading = isUploading;
			
			if (this.isUploading) {
				mutex.notify();
			}
		}
	}

	/**
	 * @return the isUploading
	 */
	public boolean isUploading() {
        synchronized(mutex) {
            return isUploading;
        }
    }


	/**
	 * @param isPaused
	 * the isPaused to set
	 */
	public void setPaused(boolean isPaused) {
		synchronized (mutex) {
			this.isPaused = isPaused;
		}
	}

	/**
	 * @return the isPaused
	 */
	public boolean isPaused() {
        synchronized(mutex) {
            return isPaused;
        }
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
}