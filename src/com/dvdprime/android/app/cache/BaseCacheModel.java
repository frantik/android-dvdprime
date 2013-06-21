package com.dvdprime.android.app.cache;

import java.util.Date;

/**
 *	The Class BaseCacheModel for saving date during creating constructor.
 */
public class BaseCacheModel {

	private static final int SIZE_IN_BYTE_FOR_DATE = 6;
	/**
	 * The save date in milliseconds.
	 */
	private Long saveDate;
	
	/**
	 * Instantiates a new base cache model.
	 */
	public BaseCacheModel() {
		this.saveDate = (new Date()).getTime(); 
	}
	
	/**
	 * Getting the saveDate.
	 * @return  the saveDate
	 */
	public Long getSaveDate() {
		return saveDate;
	}
	
	
	/**
	 * Getters size in byte for model.
	 * @return size
	 */
	public Integer getSize() {
		return SIZE_IN_BYTE_FOR_DATE;
	}

	public void dispose() {
	}
}
