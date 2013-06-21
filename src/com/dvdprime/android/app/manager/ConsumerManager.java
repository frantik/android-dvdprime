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
	
	/** 데이터 소비자 등록 변수 */
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
	 * 모든 요청 취소 및 등록된 청취자 모두 삭제.
	 * 
	 */
	@Override
	public void invalidate() {
		DataManager.getInstance().cancel();
		consumers.clear();
	}

	/**
	 * 현재 활동동인 모든 요청을 취소함.
	 */
	public void cancel() {
		DataManager.getInstance().cancel();
	}
	
	/**
	 * 리스너 전체 삭제
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
	 * 리스너 삭제.
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
	 *  eventtype 으로 등록된  리스너중 해당 리스너를 삭제함.
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
	 * event type으로 등록된 리스너의 개수를 가져온다.
	 */
	public int getConsumerCount(int eventType) {
		int count = 0;
		
		if (consumers != null && consumers.get(eventType) != null) {
			return consumers.get(eventType).size();
		}
		
		return count;
	}

	/**
	 * eventtype 에 대한 리스너 등록.
	 * 
	 * @param listener
	 *            consumer
	 * @param eventType
	 *            the event type
	 */
	public final void addConsumer(final DataConsumer listener, final int eventType) {
		// 등록하기 전에 전체 삭제
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
	 * 등록된 리스너에 이벤트를 전달한다.
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