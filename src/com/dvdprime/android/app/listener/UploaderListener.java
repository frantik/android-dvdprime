package com.dvdprime.android.app.listener;

/**
 * ���δ��� ����� ���� ������ �������̽�
 *
 */
public interface UploaderListener {
	void uploadingComplete(Object result);
    void progressUpdate(int progress, int total);
}