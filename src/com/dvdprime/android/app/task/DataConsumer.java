package com.dvdprime.android.app.task;

/**
 * DataConsumer is meant to consume data. It does so by handling incoming DataEvents 
 * regarding the type of actual event sent by implementations of ConsumerManagers when
 * new data update is avaliable. To receive this update notifications DataConsumer should sign
 * for an event type in ConsumerManager.
 */
public interface DataConsumer {

	/**
	 * Handles new data event notification.
	 * 
	 * @param event
	 *            data event, containing data to be consumed 
	 */
	void handleEvent(DataEvent event);

	/**
	 * Invalidates this DataConsumer. In this method DataConsumer shold unsign from all
	 * receiving event types in ConsumerManagers.  
	 */
	void invalidate();

}
