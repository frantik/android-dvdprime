package com.dvdprime.android.app.task;

import java.io.IOException;

/**
 * DataOperation �������̽��� ������ �۾��� ���� anonymous ������ ���� ���˴ϴ�. 
 * �̰��� Ư�� �̺�Ʈ ������ ��ȯ ��ü ������ ����� �Ҵ��߽��ϴ�. 
 * DataOperation ������ DataTasks���� ���۵˴ϴ�.
 */
public interface DataOperation {

	/**
	 * �� ������ �۾��� ���� ����.
	 * 
	 * @return result object
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	Object launch() throws IOException;

	/**
	 * �̺�Ʈ ������ ������ �۾��� �Ҵ�� �ɴϴ�.
	 * 
	 * @return event type
	 */
	int getEventType();
}
