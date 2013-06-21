package com.dvdprime.android.app.http;

import java.io.IOException;

public class ServerException extends IOException {

	/** The serialVersionUID. */
	private static final long serialVersionUID = 4511298840397477628L;

	public ServerException() {
		super();
	}

	public ServerException(String detailMessage) {
		super(detailMessage);
	}
}
