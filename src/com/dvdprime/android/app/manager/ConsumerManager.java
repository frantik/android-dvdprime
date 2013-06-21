package com.dvdprime.android.app.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.dvdprime.android.app.constants.MessageHelper;
import com.dvdprime.android.app.task.DataConsumer;
import com.dvdprime.android.app.task.DataEvent;

import android.util.Log;

public class ConsumerManager implements DataConsumer {
	
	public static final String TAG = "ConsumerManager";
	
	/** ������ �Һ��� ��� ���� */
	private HashMap<Integer, List<DataConsumer>> consumers;

	protected ConsumerManager() {
		consumers = new HashMap<Integer, List<DataConsumer>>();
	}
	
	@Override
	protected final Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Singleton");
	}
	
	@Override
	public void handleEvent(final DataEvent event) {
		dispatch(event);
	}

	/**
	 * ��� ��û ��� �� ��ϵ� û���� ��� ����.
	 * 
	 */
	@Override
	public void invalidate() {
		DataManager.getInstance().cancel();
		consumers.clear();
	}

	/**
	 * ���� Ȱ������ ��� ��û�� �����.
	 */
	public void cancel() {
		DataManager.getInstance().cancel();
	}
	
	/**
	 * ������ ��ü ����
	 */
	public final void removeConsumer() {
		DataManager.getInstance().cancel();

		List<DataConsumer> list = null;
		Iterator<Integer> it = consumers.keySet().iterator();
		while (it.hasNext()) {
			list = consumers.get(it.next());
			list.clear();
		}
	}

	/**
	 * ������ ����.
	 * 
	 * @param listener
	 *            the listener
	 */
	@Deprecated
	public final void removeConsumer(final DataConsumer listener) {
		DataManager.getInstance().cancel();

		List<DataConsumer> list = null;
		Iterator<Integer> it = consumers.keySet().iterator();
		while (it.hasNext()) {
			list = consumers.get(it.next());
			if (list.contains(listener)) {
				list.remove(listener);
			}
		}
	}

	/**
	 *  eventtype ���� ��ϵ�  �������� �ش� �����ʸ� ������.
	 * 
	 * @param listener
	 *            consumer
	 * @param eventType
	 *            the event type
	 */
	public final void removeConsumer(final DataConsumer listener, final int eventType) {
		DataManager.getInstance().cancel();

		List<DataConsumer> list = null;
		list = consumers.get(eventType);
		if (list != null && list.contains(listener)) {
			list.remove(listener);
		}
	}
	
	/**
	 * event type���� ��ϵ� �������� ������ �����´�.
	 */
	public int getConsumerCount(int eventType) {
		int count = 0;
		
		if (consumers != null && consumers.get(eventType) != null) {
			return consumers.get(eventType).size();
		}
		
		return count;
	}

	/**
	 * eventtype �� ���� ������ ���.
	 * 
	 * @param listener
	 *            consumer
	 * @param eventType
	 *            the event type
	 */
	public final void addConsumer(final DataConsumer listener, final int eventType) {
		// ����ϱ� ���� ��ü ����
		removeConsumer();
		
		List<DataConsumer> list = null;
		if (consumers.containsKey(eventType)) {
			list = consumers.get(eventType);
		} else {
			list = new ArrayList<DataConsumer>();
			consumers.put(eventType, list);
		}
		if (!list.contains(listener)) {
			list.add(listener);
		}
	}

	/**
	 * ��ϵ� �����ʿ� �̺�Ʈ�� �����Ѵ�.
	 * 
	 * @param event
	 *            event to be dispatched
	 */
	protected final void dispatch(final DataEvent event) {
		//event.setError(ExceptionHandler.getInstance().handle(this, event));
		if (MessageHelper.DEBUG)
			Log.d(TAG,"dispatch() ");
		continueDispatch(event);
	}
	
	public void continueDispatch(final DataEvent event) {
		List<DataConsumer> list = null;
		int type = event.getType();
		if (consumers.containsKey(type)) {
			list = consumers.get(type);
			Iterator<DataConsumer> it = list.iterator();
			while (it.hasNext()) {
				DataConsumer dataConsumer = it.next();
				dataConsumer.handleEvent(event);
			}
		}
	}
}