package com.dvdprime.android.app.task;

import android.os.AsyncTask;

import java.io.IOException;

import com.dvdprime.android.app.manager.DataManager;

/**
 * The class implementing AsyncTask. It's used for long-term data operations running 
 * on background thread asynchronously. It takes DataOperation for execution as parameter and
 * produces DataEvent notification message.
 */
public class DataTask extends AsyncTask<DataOperation, Exception, DataEvent> {

	/**
	 * The consumer waiting for the result of this task execution.
	 */
	private DataConsumer consumer;

	/**
	 * The task life-cycle  manager.
	 */
	private DataManager manager;

	/**
	 * Instantiates a new data task. 
	 * 
	 * @param pManager
	 *            DataTask life-cycle  manager
	 * @param pConsumer
	 *            consumer waiting for the result of this task execution
	 */
	public DataTask(final DataManager pManager, final DataConsumer pConsumer) {
		super();
		this.consumer = pConsumer;
		this.manager = pManager;
	}
	
	/**
	 * Notifies consumer and manager about task completion.  
	 * @param event notification message
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected final void onPostExecute(final DataEvent event) {
		if (event != null) {
			consumer.handleEvent(event);
		}
		
		manager.complete(this);
	}

	/**
	 * Performs computation on background thread by launching given data operations. 
	 * Creates new data event containing execution result when execution finished.
	 * @param operations data operations to be launched
	 * @return data event containing execution result
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected final DataEvent doInBackground(final DataOperation... operations) {
		DataEvent result = null;

		if (operations != null) {
			try {
				result = new DataEvent(operations[0].launch(), operations[0].getEventType());
			} catch (IOException e) {
				publishProgress(e);
			} catch (IllegalStateException e) {
				publishProgress(e);
			} catch (Exception e) {
				publishProgress(e);
			}
			// 결과값이 없을 경우 event type 만 설정후 결과값 보냄.
			if(result == null)
				result = new DataEvent(null, operations[0].getEventType());
		}
		return result;
	}

	/**
	 * Shows toast message in case when exceptions caught during operations execution.
	 * @param exceptions exceptions caught
	 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
	 */
	@Override
	protected final void onProgressUpdate(final Exception... exceptions) {
		super.onProgressUpdate(exceptions);
		ExceptionHandler.getInstance().handle(isCancelled(), false, exceptions);
	}

	
}
