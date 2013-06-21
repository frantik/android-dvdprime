package com.dvdprime.android.app.listener;

/**
 * 업로더와 통신을 위한 리스너 인터페이스
 *
 */
public interface UploaderListener {
	void uploadingComplete(Object result);
    void progressUpdate(int progress, int total);
}