package com.dvdprime.android.app.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.RejectedExecutionException;

import android.net.Uri;
import android.util.Log;

import com.dvdprime.android.app.constants.Client;
import com.dvdprime.android.app.task.DataConsumer;
import com.dvdprime.android.app.task.DataEvent;
import com.dvdprime.android.app.task.DataOperation;
import com.dvdprime.android.app.task.DataTask;

/**
 * DataManager �����Ϳ� ��׶��� �۾� ����.
 */
public final class DataManager {

	public static final String TAG = "DataManager";

	/** The singleton instance. */
	private static DataManager instance;

	/** The active tasks. */
	private ArrayList<DataTask> tasks;

	/**
	 * Instantiates a new data manager.
	 */
	private DataManager() {
		tasks = new ArrayList<DataTask>();
	}

	protected static synchronized DataManager getInstance() {
		if (instance == null) {
			instance = new DataManager();
		}
		return instance;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Singleton");
	}

	/**
	 * �۾� ��⿭�� �߰�.
	 */
	private synchronized void addTask(final DataConsumer consumer,
			final DataOperation operation) {
		if (operation != null) {
			try {
				DataTask task = new DataTask(this, consumer);
				task.execute(operation);
				tasks.add(task);
			} catch (RejectedExecutionException e) {
				Log.i("DP", "Caught RejectedExecutionException Exception - Adding task to Queue");
			}
		}
	}

	/**
	 * ��� Ȱ�� �۾� ���.
	 */
	public synchronized void cancel() {
		int size = tasks.size();
		while (size > 0) {
			DataTask task = tasks.remove(0);
			task.cancel(true);
			size--;
		}
		Client.getInstance().cancel();
	}

	/**
	 * �۾���⿭���� �Ϸ�� �۾� ����.
	 */
	public synchronized void complete(final DataTask task) {
		tasks.remove(task);
	}

	/**
	 * �Խù� ����Ʈ ��û.
	 * 
	 * @param consumer
	 * @param eventType
	 */
	public void articleList(final DataConsumer consumer, final int eventType,
						final String articleUrl) {

		DataOperation operation = new DataOperation() {
			@Override
			public Object launch() throws IOException {
				if (eventType == DataEvent.ARTICLE_LIST)
					return Client.getInstance().articleList(articleUrl);
				else 
					return Client.getInstance().articleListMore(articleUrl);
			}

			@Override
			public int getEventType() {
				return eventType;
			}
		};

		addTask(consumer, operation);
	}

	/**
	 * �Խù� ���� ��û.
	 * 
	 * @param consumer
	 * @param eventType
	 */
	public void articleGet(final DataConsumer consumer, final int eventType,
						final String articleUrl) {

		DataOperation operation = new DataOperation() {
			@Override
			public Object launch() throws IOException {
				return Client.getInstance().articleGet(articleUrl);
			}

			@Override
			public int getEventType() {
				return eventType;
			}
		};

		addTask(consumer, operation);
	}

	/**
	 * �Խù� ��õ ��û.
	 * 
	 * @param consumer
	 * @param eventType
	 */
	public void articleRecommend(final DataConsumer consumer, final int eventType,
						final String bbsId) {

		DataOperation operation = new DataOperation() {
			@Override
			public Object launch() throws IOException {
				return Client.getInstance().articleRecommend(bbsId);
			}

			@Override
			public int getEventType() {
				return eventType;
			}
		};

		addTask(consumer, operation);
	}

	/**
	 * �Խù� �ۼ� ��û.
	 * 
	 * @param consumer
	 * @param eventType
	 */
	public void articleWrite(final DataConsumer consumer, final int eventType,
						final String subject, final String content,
						final String major, final String minor, final String masterId,
						final String attachImage) {

		DataOperation operation = new DataOperation() {
			@Override
			public Object launch() throws IOException {
				return Client.getInstance().articleWrite(subject, content, major, minor, masterId, attachImage);
			}

			@Override
			public int getEventType() {
				return eventType;
			}
		};

		addTask(consumer, operation);
	}

	/**
	 * �Խù� ���� ��û.
	 * 
	 * @param consumer
	 * @param eventType
	 */
	public void articleModify(final DataConsumer consumer, final int eventType,
						final String subject, final String content,
						final String major, final String minor, final String masterId,
						final String bbsId, final String regDate, final String attachImage) {

		DataOperation operation = new DataOperation() {
			@Override
			public Object launch() throws IOException {
				return Client.getInstance().articleModify(subject, content, major, minor, masterId, bbsId, regDate, attachImage);
			}

			@Override
			public int getEventType() {
				return eventType;
			}
		};

		addTask(consumer, operation);
	}

	/**
	 * �Խù� ���� ��û.
	 * 
	 * @param consumer
	 * @param eventType
	 */
	public void articleDelete(final DataConsumer consumer, final int eventType,
						final String major, final String minor, final String masterId, final String bbsId) {

		DataOperation operation = new DataOperation() {
			@Override
			public Object launch() throws IOException {
				return Client.getInstance().articleDelete(major, minor, masterId, bbsId);
			}

			@Override
			public int getEventType() {
				return eventType;
			}
		};

		addTask(consumer, operation);
	}

	/**
	 * ÷�� ���� ���ε� ��û.
	 * 
	 * @param consumer
	 * @param eventType
	 */
	public void articleUpload(final DataConsumer consumer, final int eventType,
						final Uri uri) {

		DataOperation operation = new DataOperation() {
			@Override
			public Object launch() throws IOException {
				return Client.getInstance().articleUpload(uri);
			}

			@Override
			public int getEventType() {
				return eventType;
			}
		};

		addTask(consumer, operation);
	}

	/**
	 * �Խù� MY ���ǿ� ���� ��û
	 * 
	 * @param consumer
	 * @param eventType
	 */
	public void articleSaveMyDp(final DataConsumer consumer, final int eventType, final String bbsId) {

		DataOperation operation = new DataOperation() {
			@Override
			public Object launch() throws IOException {
				return Client.getInstance().articleSaveMyDp(bbsId);
			}

			@Override
			public int getEventType() {
				return eventType;
			}
		};

		addTask(consumer, operation);
	}
	
	/**
	 * ���� URL ���ϱ�
	 * 
	 * @param consumer
	 * @param eventType
	 * @param longUrl
	 */
	public void shortlyUrl(final DataConsumer consumer, final int eventType, final String longUrl) {
		
		DataOperation operation = new DataOperation() {
			@Override
			public Object launch() throws IOException {
				return Client.getInstance().shortlyUrl(longUrl);
			}

			@Override
			public int getEventType() {
				return eventType;
			}
		};

		addTask(consumer, operation);
	}

	/**
	 * �α��� ��û.
	 * 
	 * @param consumer
	 * @param eventType
	 */
	public void login(final DataConsumer consumer, final int eventType,
						final String userId, final String userPw) {

		DataOperation operation = new DataOperation() {
			@Override
			public Object launch() throws IOException {
				return Client.getInstance().login(userId, userPw);
			}

			@Override
			public int getEventType() {
				return eventType;
			}
		};

		addTask(consumer, operation);
	}

	/**
	 * �α��� ���� üũ
	 * 
	 * @param consumer
	 * @param eventType
	 */
	public void loginCheck(final DataConsumer consumer, final int eventType) {

		DataOperation operation = new DataOperation() {
			@Override
			public Object launch() throws IOException {
				return Client.getInstance().loginCheck();
			}

			@Override
			public int getEventType() {
				return eventType;
			}
		};

		addTask(consumer, operation);
	}

	/**
	 * ��� ����
	 * 
	 * @param consumer
	 * @param eventType
	 */
	public void commentWrite(final DataConsumer consumer, final int eventType,
						final String bbsId, final String text) {

		DataOperation operation = new DataOperation() {
			@Override
			public Object launch() throws IOException {
				return Client.getInstance().commentWrite(bbsId, text);
			}

			@Override
			public int getEventType() {
				return eventType;
			}
		};

		addTask(consumer, operation);
	}

	/**
	 * ���� ����
	 * 
	 * @param consumer
	 * @param eventType
	 */
	public void commentChildWrite(final DataConsumer consumer, final int eventType,
						final String bbsId, final String cmtId, final String text) {

		DataOperation operation = new DataOperation() {
			@Override
			public Object launch() throws IOException {
				return Client.getInstance().commentChildWrite(bbsId, cmtId, text);
			}

			@Override
			public int getEventType() {
				return eventType;
			}
		};

		addTask(consumer, operation);
	}

	/**
	 * ��� ��õ
	 * 
	 * @param consumer
	 * @param eventType
	 */
	public void commentRecommend(final DataConsumer consumer, final int eventType,
						final String cmtId) {

		DataOperation operation = new DataOperation() {
			@Override
			public Object launch() throws IOException {
				return Client.getInstance().commentRecommend(cmtId);
			}

			@Override
			public int getEventType() {
				return eventType;
			}
		};

		addTask(consumer, operation);
	}

	/**
	 * ��� ����
	 * 
	 * @param consumer
	 * @param eventType
	 */
	public void commentDelete(final DataConsumer consumer, final int eventType,
						final String bbsId, final String cmtId) {

		DataOperation operation = new DataOperation() {
			@Override
			public Object launch() throws IOException {
				return Client.getInstance().commentDelete(bbsId, cmtId);
			}

			@Override
			public int getEventType() {
				return eventType;
			}
		};

		addTask(consumer, operation);
	}

	/**
	 * ���� ���
	 * 
	 * @param consumer
	 * @param eventType
	 */
	public void memoList(final DataConsumer consumer, final int eventType,
						final String url) {

		DataOperation operation = new DataOperation() {
			@Override
			public Object launch() throws IOException {
				return Client.getInstance().memoList(url);
			}

			@Override
			public int getEventType() {
				return eventType;
			}
		};

		addTask(consumer, operation);
	}

	/**
	 * ���� ����
	 * 
	 * @param consumer
	 * @param eventType
	 */
	public void memoDelete(final DataConsumer consumer, final int eventType,
						final String memoId, final String pageFlag) {

		DataOperation operation = new DataOperation() {
			@Override
			public Object launch() throws IOException {
				return Client.getInstance().memoDelete(memoId, pageFlag);
			}

			@Override
			public int getEventType() {
				return eventType;
			}
		};

		addTask(consumer, operation);
	}

	/**
	 * ���� ����
	 * 
	 * @param consumer
	 * @param eventType
	 */
	public void memoWrite(final DataConsumer consumer, final int eventType,
						final String receiver, final String content,
						final String sendCheck) {

		DataOperation operation = new DataOperation() {
			@Override
			public Object launch() throws IOException {
				return Client.getInstance().memoWrite(receiver, content, sendCheck);
			}

			@Override
			public int getEventType() {
				return eventType;
			}
		};

		addTask(consumer, operation);
	}

	/**
	 * ���� ���������� �̵�
	 * 
	 * @param consumer
	 * @param eventType
	 */
	public void memoMoveStorage(final DataConsumer consumer, final int eventType,
						final String memoId, final String pageFlag) {

		DataOperation operation = new DataOperation() {
			@Override
			public Object launch() throws IOException {
				return Client.getInstance().memoMoveStorage(memoId, pageFlag);
			}

			@Override
			public int getEventType() {
				return eventType;
			}
		};

		addTask(consumer, operation);
	}

	/**
	 * ���� üũ
	 * 
	 * @param consumer
	 * @param eventType
	 */
	public void memoCheck(final DataConsumer consumer, final int eventType) {

		DataOperation operation = new DataOperation() {
			@Override
			public Object launch() throws IOException {
				return Client.getInstance().memoCheck();
			}

			@Override
			public int getEventType() {
				return eventType;
			}
		};

		addTask(consumer, operation);
	}

	/**
	 * ��ũ�� ����Ʈ ��û.
	 * 
	 * @param consumer
	 * @param eventType
	 */
	public void scrapList(final DataConsumer consumer, final int eventType,
						final String scrapUrl) {

		DataOperation operation = new DataOperation() {
			@Override
			public Object launch() throws IOException {
				if (eventType == DataEvent.SCRAP_LIST)
					return Client.getInstance().scrapList(scrapUrl, false);
				else 
					return Client.getInstance().scrapList(scrapUrl, true);
			}

			@Override
			public int getEventType() {
				return eventType;
			}
		};

		addTask(consumer, operation);
	}

	/**
	 * ��â�� ����Ʈ ��û.
	 * 
	 * @param consumer
	 * @param eventType
	 */
	public void documentList(final DataConsumer consumer, final int eventType,
						final String documentUrl) {

		DataOperation operation = new DataOperation() {
			@Override
			public Object launch() throws IOException {
				if (eventType == DataEvent.DOCUMENT_LIST)
					return Client.getInstance().documentList(documentUrl, false);
				else 
					return Client.getInstance().documentList(documentUrl, true);
			}

			@Override
			public int getEventType() {
				return eventType;
			}
		};

		addTask(consumer, operation);
	}

	/**
	 * ���â�� ����Ʈ ��û.
	 * 
	 * @param consumer
	 * @param eventType
	 */
	public void commentList(final DataConsumer consumer, final int eventType,
						final String commentUrl) {

		DataOperation operation = new DataOperation() {
			@Override
			public Object launch() throws IOException {
				if (eventType == DataEvent.COMMENT_LIST)
					return Client.getInstance().commentList(commentUrl, false);
				else 
					return Client.getInstance().commentList(commentUrl, true);
			}

			@Override
			public int getEventType() {
				return eventType;
			}
		};

		addTask(consumer, operation);
	}

}