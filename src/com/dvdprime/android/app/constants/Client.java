package com.dvdprime.android.app.constants;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import com.dvdprime.android.app.ContextHolder;
import com.dvdprime.android.app.R;
import com.dvdprime.android.app.http.CommentRetrieveThread;
import com.dvdprime.android.app.http.DocumentRetrieveThread;
import com.dvdprime.android.app.http.HttpClient;
import com.dvdprime.android.app.http.MemoCheckThread;
import com.dvdprime.android.app.http.MemoRetrieveThread;
import com.dvdprime.android.app.http.MessageGetThread;
import com.dvdprime.android.app.http.MessageRetrieveMoreThread;
import com.dvdprime.android.app.http.MessageRetrieveThread;
import com.dvdprime.android.app.http.ScrapRetrieveThread;
import com.dvdprime.android.app.util.DpUtil;
import com.dvdprime.android.app.util.PrefUtil;
import com.dvdprime.android.app.util.StringUtil;

/**
 * 백엔드 클라이언트 구현.
 */
public final class Client {

	public static final String TAG = "Client";

	private static Client instance;

	public static final String QUESTION = "?";

	public static final String AMPERSAND = "&";

	public static final String EQUAL = "=";

	/** Default buffer size. */
	public static final int DEFAULT_BUFFER_SIZE = 8192 * 2;
	/** line separator */
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private HttpClient client;

	private Client() {
		client = HttpClient.getInstance();
	}

	public static synchronized Client getInstance() {
		if (instance == null) {
			instance = new Client();
		}
		return instance;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Singleton");
	}

	/**
	 * Makes actual HTTP request.
	 * 
	 * @param url
	 *            the request url
	 * @param param
	 *            the request parameter
	 * @return server response as JSON object
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws JSONException
	 *             If there is a syntax error in the response source string.
	 * 
	 * 
	 *             private JSONObject request(final String url, final JSONObject
	 *             param) throws IOException, JSONException { JSONObject result
	 *             = null; String str; if (param == null || param.length() == 0)
	 *             { str = client.get(url); } else { str = client.post(url,
	 *             param); } result = new JSONObject(str); try { Log.d(TAG,
	 *             "Retrieved :" + new
	 *             JSONObject(str).toString(HttpClient.JSON_OUTPUT_INDENT)); }
	 *             catch (JSONException e) { Log.d(TAG, e.getMessage()); }
	 *             return result; }
	 */

	/**
	 * Makes actual HTTP request.
	 * 
	 * @param url
	 *            the request url
	 * @param param
	 *            the request parameter
	 * @return server response as JSON object
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws JSONException
	 *             If there is a syntax error in the response source string.
	 */
	private InputStream request(final String url, final Map<String, Object> param) throws IOException {

		InputStream result;
		if (param == null || param.size() == 0) {
			result = client.get(url);
		} else {
			if (param.containsKey("multipart"))
				result = client.multipart(url, param);
			else
				result = client.post(url, param);
		}
		// 로그인 상태 일 경우에는 액션 후 시간 값을 업데이트 한다.
		if (result != null && DpUtil.isLogined())
			PrefUtil.getInstance().setLong(PreferenceKeys.ACCOUNT_TIME, System.currentTimeMillis());

		return result;
	}

	/**
	 * Reads stream to String.
	 * 
	 * @param in
	 *            input stream
	 * @return read string
	 * @throws IOException
	 */
	private String read(final InputStream in) throws IOException {
		String text = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(in), DEFAULT_BUFFER_SIZE);
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line).append(LINE_SEPARATOR);
			}
			text = sb.toString();
		} 
		catch (SocketException e) {
			e.printStackTrace();
		}
		finally {
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
	 * 게시물 리스트 가져옴.
	 * 
	 * @param articleUrl
	 * @return
	 * @throws IOException
	 */
	public boolean articleList(final String articleUrl) throws IOException {

		Map<String, Object> param = new HashMap<String, Object>();

		
		InputStream is = request(articleUrl, param);
		
		if (is == null)
			return false;

		MessageRetrieveThread mrThread = new MessageRetrieveThread(
				"msg_retrieve_"+System.currentTimeMillis(), 
				is);
		mrThread.start();

		// wait for finishing data parsing.
		try {
			mrThread.join();
			Throwable e = mrThread.getException();
			if (e != null) {
				throw e;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * 게시물 리스트 가져옴.
	 * 
	 * @param articleUrl
	 * @return
	 * @throws IOException
	 */
	public boolean articleListMore(final String articleUrl) throws IOException {

		Map<String, Object> param = new HashMap<String, Object>();

		InputStream is = request(articleUrl, param);

		if (is == null)
			return false;

		MessageRetrieveMoreThread mrmThread = new MessageRetrieveMoreThread(
				"msg_retrieve_"+System.currentTimeMillis(), 
				is);
		mrmThread.start();

		// wait for finishing data parsing.
		try {
			mrmThread.join();
			Throwable e = mrmThread.getException();
			if (e != null) {
				throw e;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * 게시물 본문 가져옴.
	 * 
	 * @param articleUrl
	 * @return
	 * @throws IOException
	 */
	public boolean articleGet(final String articleUrl) throws IOException {

		Map<String, Object> param = new HashMap<String, Object>();

		InputStream is = request(articleUrl, param);

		if (is == null)
			return false;

		MessageGetThread mgThread = new MessageGetThread(
				"msg_get_"+System.currentTimeMillis(), 
				is);
		mgThread.start();

		// wait for finishing data parsing.
		try {
			mgThread.join();
			Throwable e = mgThread.getException();
			if (e != null) {
				throw e;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * 게시물 추천
	 * 
	 * @param bbsId
	 * @return
	 * @throws IOException
	 */
	public int articleRecommend(final String bbsId) throws IOException {

		Map<String, Object> param = new HashMap<String, Object>();

		StringBuilder url = new StringBuilder(Const.RECOMMEND_URL + "?bbslist_id")
							.append(EQUAL).append(bbsId);

		InputStream is = request(url.toString(), param);
		
		if (is == null)
			return Const.INTERNAL_SERVER_ERROR;
		
		String result = read(is);
		
		if (result != null	&& result.length() < 75) // success : 71, fail : 79
			return Const.OK;
		else if (result != null && result.length() > 75)
			return Const.ALREADY_REQUEST;
		else
			return Const.INTERNAL_SERVER_ERROR;
	}

	/**
	 * 게시물 작성
	 * 
	 * @param subject
	 * @param content
	 * @return
	 * @throws IOException
	 */
	public boolean articleWrite(final String subject, final String content,
							final String major, final String minor,
							final String masterId, final String attachImage) throws IOException {

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("returnUrl", "http://dvdprime.donga.com");
		param.put("major", major);
		param.put("minor", minor);
		param.put("bbslist_id", "0");
		param.put("writemode", "0");
		param.put("bbsReleaseWriteMode", "0");
		param.put("reg_date", "");
		param.put("editormode", "1");
		param.put("userid", PrefUtil.getInstance().getString(PreferenceKeys.ACCOUNT_ID, ""));
		param.put("master_id", masterId);
		param.put("notice_yn", "");
		param.put("hot_yn", "");
		param.put("cool_yn", "");
		param.put("custom_tag", "DPApp");
		param.put("useBR", "");
		param.put("short_content", "");
		param.put("fword", ContextHolder.getInstance().getContext().getString(R.string.enter_tag_text));
		param.put("subject", subject);
		param.put("valuation", "");
		param.put("content", StringUtil.replace(content, "\n", "<br/>"));
		param.put("scrap_yn", "");
		param.put("article_yn", "");
		param.put("multipart", "");
		param.put("attachImage", attachImage);

		InputStream is = request(Const.WRITE_URL, param);
		if (is == null)
			return false;
		
		String result = read(is);
//		Log.d("P", "received:"+result);
		if (StringUtil.contains(result, "/dp_icon-web.ico"))
			return true;
		else
			return false;
	}

	/**
	 * 게시물 수정
	 * 
	 * @param subject
	 * @param content
	 * @return
	 * @throws IOException
	 */
	public boolean articleModify(final String subject, final String content,
							final String major, final String minor,
							final String masterId, final String bbsId,
							final String regDate, final String attachImage) throws IOException {

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("returnUrl", "http://dvdprime.donga.com");
		param.put("major", major);
		param.put("minor", minor);
		param.put("bbslist_id", bbsId);
		param.put("writemode", "1");
		param.put("bbsReleaseWriteMode", "0");
		param.put("reg_date", regDate);
		param.put("editormode", "1");
		param.put("userid", PrefUtil.getInstance().getString(PreferenceKeys.ACCOUNT_ID, ""));
		param.put("master_id", masterId);
		param.put("notice_yn", "");
		param.put("hot_yn", "");
		param.put("cool_yn", "");
		param.put("custom_tag", "DPApp");
		param.put("useBR", "");
		param.put("short_content", "");
		param.put("fword", ContextHolder.getInstance().getContext().getString(R.string.enter_tag_text));
		param.put("subject", subject);
		param.put("valuation", "");
		param.put("content", StringUtil.replace(content, "\n", "<br/>"));
		param.put("scrap_yn", "");
		param.put("article_yn", "");
		param.put("multipart", "");
		param.put("attachImage", attachImage);

		InputStream is = request(Const.MODIFY_URL, param);
		if (is == null)
			return false;
		
		String result = read(is);
//		Log.d("P", "received:"+result);
		if (StringUtil.contains(result, "/dp_icon-web.ico"))
			return true;
		else
			return false;
	}

	/**
	 * 게시물 삭제
	 * 
	 * @param subject
	 * @param content
	 * @return
	 * @throws IOException
	 */
	public boolean articleDelete(final String major, final String minor,
							final String masterId, final String bbsId) throws IOException {

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("major", major);
		param.put("minor", minor);
		param.put("bbslist_id", bbsId);
		param.put("bbsfword_id", "");
		param.put("master_sel", "");
		param.put("fword_sel", "");
		param.put("SortMethod", "0");
		param.put("SearchConditionsss", "");
		param.put("SearchConditionTxt", "");
		param.put("bbslist_id", bbsId);
		param.put("returnListPageName", "http://dvdprime.donga.com");

		InputStream is = request(Const.DELETE_URL, param);
		if (is == null)
			return false;
		
		String result = read(is);
//		Log.d("P", "received:"+result);
		if (StringUtil.contains(result, "/dp_icon-web.ico"))
			return true;
		else
			return false;
	}

	/**
	 * 게시물 첨부 사진 전송
	 * 
	 * @param uri
	 * @return
	 * @throws IOException
	 */
	public String articleUpload(final Uri uri) throws IOException {

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("multipart", "");
		param.put("photo", uri);

		InputStream is = request(Const.IMAGE_UPLOAD_URL, param);
		if (is == null)
			return null;
		
		String result = read(is);
//		Log.d("P", "received:"+result);
		if (StringUtil.contains(result, "DoAttachImageFile"))
			return StringUtil.substringBeforeLast(
					StringUtil.substringAfter(result, "(\""), "\"");
		else
			return null;
	}

	/**
	 * 게시물 MY 디피에 저장
	 * 
	 * @param bbsId
	 * @return
	 * @throws IOException
	 */
	public boolean articleSaveMyDp(final String bbsId) throws IOException {

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("bbslist_id", bbsId);

		InputStream is = request(Const.MYDP_SAVE_URL, param);
		if (is == null)
			return false;
		
		String result = read(is);
//		Log.d("P", "received:"+result);
		if (StringUtil.contains(result, "pop_singo_ok"))
			return true;
		else
			return false;
	}
	
	/**
	 * 단축 URL 호출
	 * 
	 * @param longUrl
	 * @return
	 * @throws IOException
	 */
	public String shortlyUrl(final String longUrl) throws IOException {
		
		Map<String, Object> param = new HashMap<String, Object>();
		
		param.put("login", Const.BITLY_LOGIN);
		param.put("apikey", Const.BITLY_APIKEY);
		param.put("longUrl", longUrl);
		param.put("format", Const.BITLY_FORMAT);

		InputStream is = request(Const.BITLY_REQUEST_URL, param);
		if (is == null)
			return null;
		
		return read(is);
	}

	/**
	 * 로그인
	 * 
	 * @param userId
	 * @param userPw
	 * 
	 * @return
	 * @throws IOException
	 */
	public int login(final String userId, final String userPw) throws IOException {

		Map<String, Object> param = new HashMap<String, Object>();
		
		param.put("url_home", "http://frantik");
		param.put("id", userId);
		param.put("pw", userPw);
		param.put("ReturnUrl", "");

		InputStream is = request(Const.LOGIN_URL, param);
		if (is == null)
			return Const.SERVICE_UNAVAILABLE;
		
		String content = read(is);
		
		if (StringUtil.contains(content, "FAIL"))
			return Const.LOGIN_FAILED;
		else if (StringUtil.contains(content, "image_key"))
			return Const.SERVICE_UNAVAILABLE;
		else if (StringUtil.contains(content, "dvdprime.donga.com")) {
			PrefUtil.getInstance().setLong(PreferenceKeys.ACCOUNT_TIME, System.currentTimeMillis());
			return Const.OK;
		} else 
			return Const.SERVICE_UNAVAILABLE;
	}

	/**
	 * 로그인상태 체크
	 * 
	 * @param userId
	 * @param userPw
	 * 
	 * @return
	 * @throws IOException
	 */
	public boolean loginCheck() throws IOException {

		Map<String, Object> param = new HashMap<String, Object>();
		
		InputStream is = request(Const.LOGIN_CHECK_URL, param);
		if (is == null)
			return false;
		
		String content = read(is);
		
		if (StringUtil.defaultIfNull(content).length() > 200) {
			PrefUtil.getInstance().setLong(PreferenceKeys.ACCOUNT_TIME, System.currentTimeMillis());
			return true;
		} else
			return false;
	}

	/**
	 * 댓글 쓰기
	 * 
	 * @param userId
	 * @param userPw
	 * 
	 * @return
	 * @throws IOException
	 */
	public boolean commentWrite(final String bbsId, final String text) throws IOException {

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("bbslist_id", bbsId);
		param.put("bbsmemo_id", "0");
		param.put("answerContent", text);
		param.put("user_status", "2");
		param.put("YN_Check", "Y");
		
		InputStream is = request(Const.COMMENT_WRITE_URL, param);
		if (is == null)
			return false;
		
		String content = read(is);
		
		return StringUtil.contains(content, "reply-area");
	}

	/**
	 * 덧글 쓰기
	 * 
	 * @param userId
	 * @param userPw
	 * 
	 * @return
	 * @throws IOException
	 */
	public boolean commentChildWrite(final String bbsId, final String cmtId, final String text) throws IOException {

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("bbslist_id", bbsId);
		param.put("bbsmemo_id", cmtId);
		param.put("answerContent", text);
		param.put("user_status", "2");
		param.put("YN_Check", "Y");
		
		InputStream is = request(Const.COMMENT_WRITE_URL, param);
		if (is == null)
			return false;
		
		String content = read(is);
		
		return StringUtil.contains(content, "reply-area");
	}

	/**
	 * 댓글 추천
	 * 
	 * @param userId
	 * @param userPw
	 * 
	 * @return
	 * @throws IOException
	 */
	public boolean commentRecommend(final String cmtId) throws IOException {

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("bbsmemo_id", cmtId);
		param.put("regI", "0");
		
		InputStream is = request(Const.COMMENT_RCMD_URL, param);
		if (is == null)
			return false;
		
		String content = read(is);
		
		return StringUtil.contains(content, "<br>");
	}

	/**
	 * 댓글 삭제
	 * 
	 * @param userId
	 * @param userPw
	 * 
	 * @return
	 * @throws IOException
	 */
	public boolean commentDelete(final String bbsId, final String cmtId) throws IOException {

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("bbslist_id", bbsId);
		param.put("bbsmemo_id", cmtId);
		
		InputStream is = request(Const.COMMENT_DEL_URL, param);
		if (is == null)
			return false;
		
		String content = read(is);
		
		return StringUtil.contains(content, "parent.location.href");
	}

	/**
	 * 쪽지 목록
	 * 
	 * @param url
	 * 
	 * @return
	 * @throws IOException
	 */
	public boolean memoList(final String url) throws IOException {

		Map<String, Object> param = new HashMap<String, Object>();
		
		InputStream is = request(url, param);

		MemoRetrieveThread mrThread = new MemoRetrieveThread(
				"memo_retrieve_"+System.currentTimeMillis(), 
				is);
		mrThread.start();

		// wait for finishing data parsing.
		try {
			mrThread.join();
			Throwable e = mrThread.getException();
			if (e != null) {
				throw e;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * 쪽지 삭제
	 * 
	 * @param url
	 * 
	 * @return
	 * @throws IOException
	 */
	public boolean memoDelete(final String memoId, final String pageFlag) throws IOException {

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("flag", "D");
		param.put("pageflag", pageFlag);
		param.put("userid", PrefUtil.getInstance().getString(PreferenceKeys.ACCOUNT_ID, ""));
		param.put("memosn", memoId);
		param.put("page", "1");
		
		InputStream is = request(Const.MEMO_EXEC_URL, param);
		if (is == null)
			return false;
		
		String content = read(is);
//		Log.d("P", "received:"+content);
		return StringUtil.contains(content, "alert(");
	}

	/**
	 * 쪽지 전송
	 * 
	 * @param url
	 * 
	 * @return
	 * @throws IOException
	 */
	public boolean memoWrite(final String receiver, final String content, final String sendCheck) throws IOException {

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("headermsg", "");
		param.put("siteid", "");
		param.put("sendid", PrefUtil.getInstance().getString(PreferenceKeys.ACCOUNT_ID, ""));
		param.put("nexturl", "");
		param.put("userid", receiver);
		param.put("contents", content);
		if (StringUtil.isNotBlank(sendCheck))
			param.put("sendsave", sendCheck);
		
		InputStream is = request(Const.MEMO_SENDEXEC_URL, param);
		if (is == null)
			return false;
		
		String result = read(is);
//		Log.d("P", "received:"+content);
		return StringUtil.trimToEmpty(result).length() > 0;
	}

	/**
	 * 쪽지 저장
	 * 
	 * @param url
	 * 
	 * @return
	 * @throws IOException
	 */
	public boolean memoMoveStorage(final String memoId, final String pageFlag) throws IOException {

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("flag", "S");
		param.put("pageflag", pageFlag);
		param.put("userid", PrefUtil.getInstance().getString(PreferenceKeys.ACCOUNT_ID, ""));
		param.put("memosn", memoId);
		param.put("page", "1");
		
		InputStream is = request(Const.MEMO_EXEC_URL, param);
		if (is == null)
			return false;
		
		String content = read(is);
//		Log.d("P", "received:"+content);
		return StringUtil.contains(content, "confirm");
	}

	/**
	 * 쪽지 체크
	 * 
	 * @param url
	 * 
	 * @return
	 * @throws IOException
	 */
	public boolean memoCheck() throws IOException {

		Map<String, Object> param = new HashMap<String, Object>();
		
		InputStream is = request(Const.MEMO_CHECK_URL, param);
		if (is == null)
			return false;

		MemoCheckThread mcThread = new MemoCheckThread(
				"memo_check_"+System.currentTimeMillis(), 
				is);
		mcThread.start();

		// wait for finishing data parsing.
		try {
			mcThread.join();
			Throwable e = mcThread.getException();
			if (e != null) {
				throw e;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	/**
	 * 스크랩 리스트 가져옴.
	 * 
	 * @param scrapUrl
	 * @return
	 * @throws IOException
	 */
	public boolean scrapList(final String scrapUrl, final boolean isMore) throws IOException {

		Map<String, Object> param = new HashMap<String, Object>();

		InputStream is = request(scrapUrl, param);

		if (is == null)
			return false;

		ScrapRetrieveThread srThread = new ScrapRetrieveThread(
				"msg_retrieve_"+System.currentTimeMillis(), 
				is, isMore);
		srThread.start();

		// wait for finishing data parsing.
		try {
			srThread.join();
			Throwable e = srThread.getException();
			if (e != null) {
				throw e;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * 글창고 리스트 가져옴.
	 * 
	 * @param documentUrl
	 * @return
	 * @throws IOException
	 */
	public boolean documentList(final String documentUrl, final boolean isMore) throws IOException {

		Map<String, Object> param = new HashMap<String, Object>();

		
		InputStream is = request(documentUrl, param);
		
		if (is == null)
			return false;

		DocumentRetrieveThread drThread = new DocumentRetrieveThread(
				"msg_retrieve_"+System.currentTimeMillis(), 
				is, isMore);
		drThread.start();

		// wait for finishing data parsing.
		try {
			drThread.join();
			Throwable e = drThread.getException();
			if (e != null) {
				throw e;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * 댓글창고 리스트 가져옴.
	 * 
	 * @param commentUrl
	 * @return
	 * @throws IOException
	 */
	public boolean commentList(final String commentUrl, final boolean isMore) throws IOException {

		Map<String, Object> param = new HashMap<String, Object>();

		
		InputStream is = request(commentUrl, param);
		
		if (is == null)
			return false;

		CommentRetrieveThread crThread = new CommentRetrieveThread(
				"msg_retrieve_"+System.currentTimeMillis(), 
				is, isMore);
		crThread.start();

		// wait for finishing data parsing.
		try {
			crThread.join();
			Throwable e = crThread.getException();
			if (e != null) {
				throw e;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * 대기 중인 모든 요청을 취소한다.
	 */
	public void cancel() {
		client.cancel();
	}

}
