package com.dvdprime.android.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 모든 모델 클래스에 대한 추상 기본 클래스. 
 */
public abstract class BaseModel implements Parcelable {
	
	public static final String TAG = "BaseModel";
	
	/** 모델타입정의 */
	public static final int BASE_MODEL = 0;
	
	public static final int ARTICLE = 1;
	
	public static final int UPLOADIMAGE = 2;
	
	public static final int MEMO = 3;
	
	public static final int SCRAP = 4;

	private String str;

	public BaseModel() {
		str = null;
	}
	
	public BaseModel(final String val) {
		str = val;
	}

	/**
	 * Instantiates a new base model from Parcel sorce.
	 * 
	 * @param source
	 *            the source
	 */
	public BaseModel(final Parcel source) {
		readFromParcel(source);
	}

	/**
	 * Read from parcel.
	 * 
	 * @param in
	 *            the in
	 */
	protected void readFromParcel(final Parcel in) {
		Serializable serializable = in.readSerializable();
		try {
			str = (String) serializable;
		} catch (Exception e) {
		}
	}
	
	@Override
	public final int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel parcel, final int i) {
		parcel.writeSerializable(str);
	}
	
	/**
	 * @return the model type
	 */
	public int getModelType() {
		return BASE_MODEL;
	}
}