package com.dvdprime.android.app.task;

import android.os.Handler;

/**
 * Classes implementing this interface are meant to consume downloaded from network on background 
 * thread data. To do so they implement <code>consume(Object result)</code> method, where <code>result</code> 
 * parameter is download result.
 * DownloadConsumer should return its download type to show the form received data bytes should encoded to. 
 *  
 */
public interface DownloadConsumer {

	/** The constant bitmap download type. */
	int BITMAP = 0;
	
	/**
	 * Download performer notifies consumer by invoking this method, that download task completed
	 * passing download result as method parameter.
	 * 
	 * @param result
	 *            download result
	 */
	void consume(Object result);

	/**
	 * Gets the download consumer type. 
	 * 
	 * @return download type
	 */
	int getType();
	
	/**
	 * 다운로드를 호출한 핸들러
	 * @return
	 */
	Handler getHandler();

}
