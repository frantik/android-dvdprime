package com.dvdprime.android.app.http;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import com.dvdprime.android.app.ContextHolder;
import com.dvdprime.android.app.constants.Const;
import com.dvdprime.android.app.constants.MessageHelper;
import com.dvdprime.android.app.constants.PreferenceKeys;
import com.dvdprime.android.app.util.ImageUtil;
import com.dvdprime.android.app.util.PrefUtil;
import com.dvdprime.android.app.util.StringUtil;
import com.dvdprime.android.app.util.SystemUtil;

public class HttpClient {

	public static final String TAG = "HttpClient";

	/** line separator */
	public static final String LINE_SEPARATOR = System
			.getProperty("line.separator");

	/** socket timeout */
	public static final Integer SO_TIMEOUT = 20000;

	/** default http port */
	public static final int HTTP_PORT = 80;

	/** default https port */
	public static final int HTTPS_PORT = 443;
	/**
	 * Default buffer size.
	 */
	public static final int DEFAULT_BUFFER_SIZE = 8192 * 2;

	final String CONTENT_ENCODING_UTF_8 = "UTF-8";
	final String CONTENT_ENCODING_EUC_KR = "EUC-KR";

	private static final String USER_AGENT;

	private static HttpClient instance;

	/** Apache client instance. */
	private DefaultHttpClient client;

	/** Pending requests. */
	private List<HttpUriRequest> requests;

	static {
		USER_AGENT = MessageFormat
				.format("DP/{0} (Linux; U; Android {1}; {2}; {3}/{4}) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1",
						SystemUtil.getVersionName(ContextHolder.getInstance().getContext()),
						android.os.Build.VERSION.RELEASE,
						Locale.getDefault().getLanguage(),
						android.os.Build.MODEL, android.os.Build.DISPLAY);
	}

	private HttpClient() {
		HttpParams params = new BasicHttpParams();

		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setUseExpectContinue(params, true);
//		HttpConnectionParams.setConnectionTimeout(params, SO_TIMEOUT);
//		HttpConnectionParams.setSoTimeout(params, SO_TIMEOUT);
		HttpConnectionParams.setSocketBufferSize(params, DEFAULT_BUFFER_SIZE);

		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), HTTP_PORT));
		schReg.register(new Scheme("https", new EasySSLSocketFactory(), HTTPS_PORT));

//		SSLSocketFactory sslSocketFactory = SSLSocketFactory.getSocketFactory(); 
//		sslSocketFactory.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER); 
//		schReg.register(new Scheme("https", sslSocketFactory, HTTPS_PORT)); 
		
		
		// Set the timeout in milliseconds until a connection is established.
//		int timeoutConnection = 3000;
		HttpConnectionParams.setConnectionTimeout(params, SO_TIMEOUT);
		// Set the default socket timeout (SO_TIMEOUT) 
		// in milliseconds which is the timeout for waiting for data.
		//int timeoutSocket = 5000;
		HttpConnectionParams.setSoTimeout(params, SO_TIMEOUT);
		
		SingleClientConnManager connMgr = new SingleClientConnManager(params, schReg);
//		ClientConnectionManager connMgr = new ThreadSafeClientConnManager(params, schReg);
		
		client = new DefaultHttpClient(connMgr, params);
		requests = new ArrayList<HttpUriRequest>();
	}

	/**
	 * Clears all client cookies
	 */
	public void clearCookies() {
		client.getCookieStore().clear();
	}

	/**
	 * Gets the singleton instance of backend client.
	 * 
	 * @return singleton instance of Client
	 */
	public static synchronized HttpClient getInstance() {
		if (instance == null) {
			instance = new HttpClient();
		}
		return instance;
	}

	/**
	 * Throws CloneNotSupportedException, as this class is a singleton.
	 * 
	 * @see java.lang.Object#clone()
	 * @throws CloneNotSupportedException
	 *             always when invoked
	 * @return Object
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Singleton");
	}

	/**
	 * Cancels all active http requests.
	 */
	public final synchronized void cancel() {
		if (MessageHelper.DEBUG)
			Log.d(TAG, "Cancel all");
		int size = requests.size();
		while (size > 0) {
			HttpUriRequest request = requests.remove(0);
			if (request != null) request.abort();
			size--;
		}
		ClientConnectionManager connMgr = client.getConnectionManager();
		connMgr.closeExpiredConnections();
		connMgr.closeIdleConnections(0, TimeUnit.MILLISECONDS);
	}

	/**
	 * Shuts down the client.
	 */
	public final void shutdown() {
		client.getConnectionManager().shutdown();
		if (MessageHelper.DEBUG)
			Log.d(TAG, "Http client have been shutdown");
	}

	/**
	 * Makes request via GET http method.
	 * 
	 * @param url
	 *            request url
	 * @return server response string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public final InputStream get(final String url) throws IOException {
		if (MessageHelper.DEBUG)
			Log.d(TAG, "GET " + url);
		HttpGet request = new HttpGet(url);
		request.setHeader("Accept", "*/*");
		request.setHeader("User-Agent", USER_AGENT);

		return getResponse(request);
	}

	/**
	 * Makes request via POST http method.
	 * 
	 * @param url
	 *            request url
	 * @param jo
	 *            parameters in form of JSON object
	 * @return server response string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
//	public final InputStream post(final String url, final JSONObject jo)
//			throws IOException {
//		HttpPost request = new HttpPost(url);
//		request.setHeader("Accept", "*/*");
//		request.setHeader("Content-type", "multipart/form-data");
//		request.setHeader("User-Agent", USER_AGENT);
//
//		StringEntity se = new StringEntity(jo.toString());
//		request.setEntity(se);
//		if (MessageHelper.DEBUG)
//			Log.d(TAG, "POST to " + url);
//		try {
//			if (MessageHelper.DEBUG)
//				Log.d(TAG, jo.toString(JSON_OUTPUT_INDENT));
//		} catch (JSONException e) {
//			if (MessageHelper.DEBUG)
//				Log.d(TAG, e.getMessage());
//		}
//
//		return getResponse(request);
//	}

	private static List<NameValuePair> getPostParams(Map<String, Object> params) {
		if (params == null || params.size() == 0)
			return null;

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			if (MessageHelper.DEBUG)
				Log.d(TAG, "entry.getKey(): " + entry.getKey()
						+ " entry.getValue(): " + entry.getValue());
			nameValuePairs.add(new BasicNameValuePair(entry.getKey(), String
					.valueOf(entry.getValue())));
		}
		return nameValuePairs;
	}

	public final InputStream post(final String url, final Map<String, Object> param)
			throws IOException {
		HttpPost request = new HttpPost(url);
		request.setHeader("Accept", "*/*");
		request.addHeader("Content-Type", "application/x-www-form-urlencoded");
		request.setHeader("User-Agent", USER_AGENT);

		List<NameValuePair> nameValuePairs = getPostParams(param);

		if (nameValuePairs != null && !nameValuePairs.isEmpty())
			request.setEntity(new UrlEncodedFormEntity(nameValuePairs, CONTENT_ENCODING_EUC_KR));

		if (MessageHelper.DEBUG)
			Log.d(TAG, "POST to " + url);
		try {
			if (MessageHelper.DEBUG)
				Log.d(TAG, nameValuePairs.toString());
		} catch (Exception e) {
			if (MessageHelper.DEBUG)
				Log.d(TAG, e.getMessage());
		}

		return getResponse(request);
	}

	public final InputStream multipart(final String url, final Map<String, Object> params)
			throws IOException {
		Uri uri = null;
		HttpPost request = new HttpPost(url);
		request.setHeader("Accept", "*/*");
		request.setHeader("Connection", "Keep-Alive");
		request.setHeader("User-Agent", USER_AGENT);
		
		MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		
		Set<String> keyset = params.keySet();
		Object[] keys = keyset.toArray();

		for (int i = 0; i < keys.length; i++) {
			String key = (String) keys[i];

			if (StringUtil.equals(key, "photo")) { // 첨부 사진 업로드 일 경우
				uri = (Uri) params.get(key);
			} else if (StringUtil.equals(key, "multipart")) { // multipart는 제외한다.
			} else {
				String val = (String) params.get(key);

// 사진은 1장만 올리게 수정되었으므로 주석처리
//				if (StringUtil.equals(key, "attachImage") && StringUtil.isNotBlank(val)) { // 사진이 여러장 있을 수 있기 때문에 별도 처리한다.
//					String[] aImages = StringUtil.split(val, '|');
//					for (String image : aImages) {
//						if (StringUtil.isNotEmpty(image))
//							reqEntity.addPart(key, new StringBody(image, Charset.forName(CONTENT_ENCODING_EUC_KR)));
//					}
//				} else {
					reqEntity.addPart(key, new StringBody(val, Charset.forName(CONTENT_ENCODING_EUC_KR)));
//				}
				
				if (MessageHelper.DEBUG)
					Log.d(TAG, "key: " + key + ", value: " + val);
			}
		}

		if (uri != null) {
			File mFile = new File(Const.SDCARD_DIRECTORY, "dp_upload.jpg");

			if (!mFile.exists()) {
				try {
					mFile.createNewFile();
				} catch (IOException e) {
					throw new IllegalStateException("Cannot create file : " + mFile.toString());
				}
			}
			// Read bitmap image
			Bitmap bitmap = Images.Media.getBitmap(ContextHolder.getInstance()
					.getContext().getContentResolver(), uri);
			int selectedFileSize = StringUtil.toNumber(PrefUtil.getInstance()
								.getString(PreferenceKeys.FILE_WIDTHSIZE, "800"));
			// If bitmap width longer than preference photo width,
			// execute resize bitmap image
			ImageUtil.WriteFileFromBitmap(ImageUtil.resizeBitmap(bitmap, selectedFileSize), mFile);

			ContentBody bin = new FileBody(mFile);
			reqEntity.addPart("photo", bin);
		}
		
		request.setEntity(reqEntity);

		if (MessageHelper.DEBUG)
			Log.d(TAG, "MULTIPART to " + url);
		
		return getResponse(request);
	}

	/**
	 * Retrieves the response.
	 * 
	 * @param request
	 *            URI request
	 * @return server response string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private InputStream getResponse(final HttpUriRequest request) throws IOException {
		synchronized (requests) {
			requests.add(request);
		}

		InputStream is = null;

		try {
			HttpResponse response = client.execute(request);

			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				log(response.getEntity().getContent());
				throw new ServerException(response.getStatusLine().getReasonPhrase());
			}
			is = response.getEntity().getContent();
		} catch (IOException ex) {
			request.abort();
		} catch (RuntimeException ex) {
			// In case of an unexpected exception you may want to abort
            // the HTTP request in order to shut down the underlying
            // connection immediately.
			request.abort();
		} finally {
			synchronized (requests) {
				File mFile = new File(Const.SDCARD_DIRECTORY, "dp_upload.jpg");
				if (mFile.exists())
					mFile.delete();

				requests.remove(request);
			}
		}

		return is;
	}

	/**
	 * Retrieves the response.
	 * 
	 * @param request
	 *            URI request
	 * @return server response string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	/*
	private InputStream getResponse(final HttpPost request) throws IOException {
		synchronized (requests) {
			requests.add(request);
		}

		InputStream is = null;

		try {
			HttpResponse response = client.execute(request);

			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				log(response.getEntity().getContent());
				throw new ServerException(response.getStatusLine().getReasonPhrase());
			}
			is = response.getEntity().getContent();
		} finally {
			synchronized (requests) {
				requests.remove(request);
			}
		}

		return is;
	}
	*/

	/**
	 * Reads stream to String.
	 * 
	 * @param in
	 *            input stream
	 * @return read string
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private String read(final InputStream in) throws IOException {
		String text = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(in),
				DEFAULT_BUFFER_SIZE);
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line).append(LINE_SEPARATOR);
			}
			text = sb.toString();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				if (MessageHelper.DEBUG)
					Log.d(TAG, "IOException caught: " + e.getMessage());
			}
		}
		return text;
	}

	/**
	 * Logs content of input stream. Used for debugging purpose.
	 * 
	 * @param in
	 *            input stream
	 * @throws IOException
	 */
	private void log(final InputStream in) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				if (MessageHelper.DEBUG)
					Log.d(TAG, "Received:"+line);
			}
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				if (MessageHelper.DEBUG)
					Log.d(TAG, "IOException caught: " + e.getMessage());
			}
		}
	}
}
