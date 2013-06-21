package com.dvdprime.android.app.task;

import java.io.IOException;

/**
 * DataOperation 인터페이스는 데이터 작업에 관한 anonymous 구현을 위해 사용됩니다. 
 * 이것은 특정 이벤트 유형을 반환 개체 실행의 결과로 할당했습니다. 
 * DataOperation 실행이 DataTasks에서 시작됩니다.
 */
public interface DataOperation {

	/**
	 * 이 데이터 작업의 개시 실행.
	 * 
	 * @return result object
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	Object launch() throws IOException;

	/**
	 * 이벤트 유형이 데이터 작업에 할당되 옵니다.
	 * 
	 * @return event type
	 */
	int getEventType();
}
