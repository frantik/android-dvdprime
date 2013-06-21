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


import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * The Class CustomX509TrustManager.
 * 
 * Class for support own trust manager
 */
public class CustomX509TrustManager implements X509TrustManager {

    /** The trust manager. */
    private X509TrustManager trustManager = null;

    /**
	 * Instantiates a new custom x509 trust manager.
	 * 
	 * @param keyStore
	 *            the key store
	 * @throws NoSuchAlgorithmException
	 *             the no such algorithm exception
	 * @throws KeyStoreException
	 *             the key store exception
	 */
    public CustomX509TrustManager(KeyStore keyStore) throws 
    				NoSuchAlgorithmException, KeyStoreException    {
        super();
        TrustManagerFactory factory = TrustManagerFactory
        			.getInstance( TrustManagerFactory.getDefaultAlgorithm() );
        factory.init(keyStore);
        TrustManager[] trustManagers = factory.getTrustManagers();
        if ( trustManagers.length == 0 )
        {
            throw new NoSuchAlgorithmException("no managers found");
        }
        this.trustManager = (X509TrustManager) trustManagers[0];
    }

   
    /* (non-Javadoc)
     * @see javax.net.ssl.X509TrustManager#
     * checkClientTrusted(java.security.cert.X509Certificate[], 
     * java.lang.String)
     */
    public void checkClientTrusted( X509Certificate[] certificates, 
    								String authType )
        throws CertificateException
    {
        trustManager.checkClientTrusted( certificates, authType );
    }

   
    /* (non-Javadoc)
     * @see javax.net.ssl.X509TrustManager#
     * checkServerTrusted(java.security.cert.X509Certificate[], 
     * java.lang.String)
     */
    public void checkServerTrusted( X509Certificate[] certificates, 
    								String authType )
        throws CertificateException
    {
        if ( ( certificates != null ) && ( certificates.length == 1 ) )
        {
            certificates[0].checkValidity();
        }
        else
        {
            trustManager.checkServerTrusted( certificates, authType );
        }
    }

    /* (non-Javadoc)
     * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
     */
    public X509Certificate[] getAcceptedIssuers()
    {
        return this.trustManager.getAcceptedIssuers();
    }

}
