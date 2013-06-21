package com.dvdprime.android.app.model;

/**
 * 이미지 업로드 API 대응
 */
public class UploadImage extends BaseModel {
	
	public static final String TAG = "UploadImage";
	
	public static final String STATUS = "error";
	
	public static final String RT = "rt";
	
	public static final String THUMBNAIL = "thumbnail_urls";
	
	public static final String URL = "url";
	
	public static final String SIZE = "size";
	
	public static final String ERROR_MESSAGE = "msg";

	public UploadImage() {
		super();
	}
	
	public UploadImage(final String str) {
		super(str);
	}
	
//	public final int getStatus() {
//		return getInt(STATUS);
//	}
//	
//	public final String getErrorMessage() {
//		return getString(ERROR_MESSAGE);
//	}
//	
//	public final String getUrl() {
//		return getString(URL);
//	}
//	
	@Override
	public int getModelType() {
		return BaseModel.UPLOADIMAGE;
	}
}
