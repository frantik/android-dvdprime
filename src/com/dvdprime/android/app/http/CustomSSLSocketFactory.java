/**
 * Copyright 2009 Art Technology Group, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file 
 * except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under 
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions 
 * and limitations under the License.
 */
package com.dvdprime.android.app.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * Factory for create ssl socket and accept self 
 * certificate
 */
public class CustomSSLSocketFactory implements SocketFactory, 
									LayeredSocketFactory {
	
	private static final String SSL_PROTOCOL_NAME = "TLS";
	private SSLContext sslContext = null;

	/**
	 * Creates a new CustomSSLSocket object.
	 * 
	 * @return the SSL context
	 * @throws NoSuchAlgorithmException
	 *             the no such algorithm exception
	 * @throws KeyManagementException
	 *             the key management exception
	 * @throws KeyStoreException
	 *             the key store exception
	 */
	private static SSLContext createSSLContext() throws 
							NoSuchAlgorithmException, 
							KeyManagementException, 
							KeyStoreException {
		SSLContext context = SSLContext.getInstance(SSL_PROTOCOL_NAME);
		context.init(null, new TrustManager[] { new CustomX509TrustManager(
				null) }, null);
		return context;
	}

	/**
	 * Gets the SSL context. Create new context and handle exceptions. 
	 * 
	 * @return the sSL context
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private SSLContext getSSLContext() throws IOException {
		if (sslContext == null) {
			try {
				sslContext = createSSLContext();
			} catch (KeyManagementException e) {
				throw new IOException(e.getLocalizedMessage());
			} catch (NoSuchAlgorithmException e) {
				throw new IOException(e.getLocalizedMessage());
			} catch (KeyStoreException e) {
				throw new IOException(e.getLocalizedMessage());
			}
		}
		return sslContext;
	}

	/* (non-Javadoc)
	 * @see org.apache.http.conn.scheme.SocketFactory#createSocket()
	 */
	public Socket createSocket() throws IOException {
		return getSSLContext().getSocketFactory().createSocket();
	}

	
	/* (non-Javadoc)
	 * @see org.apache.http.conn.scheme.SocketFactory#isSecure(java.net.Socket)
	 */
	public boolean isSecure(Socket socket) throws IllegalArgumentException {
		return true;
	}


	/* (non-Javadoc)
	 * @see org.apache.http.conn.scheme.LayeredSocketFactory#
	 * createSocket(java.net.Socket, java.lang.String, int, boolean)
	 */
	public Socket createSocket(Socket socket, String host, int port,
			boolean autoClose) throws IOException, UnknownHostException {
		return getSSLContext().getSocketFactory().createSocket();
	}

	/* (non-Javadoc)
	 * @see org.apache.http.conn.scheme.SocketFactory#
	 * connectSocket(java.net.Socket, java.lang.String, int, 
	 * java.net.InetAddress, int, org.apache.http.params.HttpParams)
	 */
	@Override
	public Socket connectSocket(Socket socket, String reqHost, int reqPort,
			InetAddress arg3, int arg4, HttpParams params) throws IOException,
			UnknownHostException, ConnectTimeoutException {
		int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
		int soTimeout = HttpConnectionParams.getSoTimeout(params);

		InetSocketAddress remoteAddress = new InetSocketAddress(reqHost, reqPort);
		SSLSocket sslSocket = null;
		if (socket != null) {
			sslSocket = (SSLSocket) socket;
		} else {
			sslSocket = (SSLSocket) createSocket();
		}
		InetSocketAddress inetSocketAddress = new InetSocketAddress(arg3, arg4);
		sslSocket.bind(inetSocketAddress);

		sslSocket.connect(remoteAddress, connTimeout);
		sslSocket.setSoTimeout(soTimeout);
		return sslSocket;
	}
}
