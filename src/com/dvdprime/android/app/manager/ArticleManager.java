package com.dvdprime.android.app.manager;

import android.net.Uri;

import com.dvdprime.android.app.task.DataEvent;

public class ArticleManager extends ConsumerManager {
	
	public static final String TAG = "ArticleManager";

	private static ArticleManager instance;
	
	private boolean loaded;
	
	private ArticleManager() {
		super();
		loaded = false;
	}
	
	public static synchronized ArticleManager getInstance() {
		if (instance == null) {
			instance = new ArticleManager();
		}
		return instance;
	}
	
	public void list(String articleUrl) {
		DataManager.getInstance().articleList(this, DataEvent.ARTICLE_LIST, articleUrl);
	}
	
	public void listMore(String articleUrl) {
		DataManager.getInstance().articleList(this, DataEvent.ARTICLE_LIST_MORE, articleUrl);
	}
	
	public void get(String url) {
		DataManager.getInstance().articleGet(this, DataEvent.ARTICLE_CONTENT, url);
	}
	
	public void recommend(String bbsId) {
		DataManager.getInstance().articleRecommend(this, DataEvent.ARTICLE_RECOMMEND, bbsId);
	}
	
	public void write(String subject, String content, String major, String minor, String masterId, String attachImage) {
		DataManager.getInstance().articleWrite(this, DataEvent.ARTICLE_WRITE, subject, content, major, minor, masterId, attachImage);
	}

	public void modify(String subject, String content, String major, String minor, 
			String masterId, String bbsId, String regDate, String attachImage) {
		DataManager.getInstance().articleModify(this, DataEvent.ARTICLE_MODIFY, subject, content, major, minor, 
										masterId, bbsId, regDate, attachImage);
	}

	public void delete(String major, String minor, String masterId, String bbsId) {
		DataManager.getInstance().articleDelete(this, DataEvent.ARTICLE_DELETE, major, minor, masterId, bbsId);
	}

	public void upload(Uri uri) {
		DataManager.getInstance().articleUpload(this, DataEvent.ARTICLE_ADD_IMAGE, uri);
	}

	public void saveMyDp(String bbsId) {
		DataManager.getInstance().articleSaveMyDp(this, DataEvent.ARTICLE_SAVE_MYDP, bbsId);
	}
	
	public void shortlyUrl(String longUrl) {
		DataManager.getInstance().shortlyUrl(this, DataEvent.SHORTLY_URL, longUrl);
	}

	@Override
	public void handleEvent(final DataEvent event) {
		
		if (event.getData() != null) {
			
			switch (event.getType()) {
			case DataEvent.ARTICLE_LIST:
			case DataEvent.ARTICLE_LIST_MORE:
			case DataEvent.ARTICLE_CONTENT:
			case DataEvent.ARTICLE_RECOMMEND:
			case DataEvent.ARTICLE_WRITE:
			case DataEvent.ARTICLE_MODIFY:
			case DataEvent.ARTICLE_DELETE:
			case DataEvent.ARTICLE_ADD_IMAGE:
			case DataEvent.ARTICLE_SAVE_MYDP:
			case DataEvent.SHORTLY_URL:
				dispatch(event);
				break;
			default:
				break;
			}
			
		} else
		{
			dispatch(event);
		}
	}
	
	/**
	 * 서버 응답에서 데이터 가져오기.
	 */
	public void fetch() {
		loaded = true;
	}

	/**
	 * Checks if profile is loaded.
	 * @return  true, if is loaded
	 */
	public boolean isLoaded() {
		return loaded;
	}

	/**
	 * Sets profile loaded flag.
	 * @param flag  value to set
	 */
	public void setLoaded(final boolean flag) {
		loaded = flag;
	}
}
