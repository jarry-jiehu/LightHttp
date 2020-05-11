
package eui.lighthttp;

import android.util.Log;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class Client {
    private Headers mHeaders;
    private OkHttpClient mOkHttpClient;
    private OkHttpClient.Builder mClientBuilder;

    Client() {
        mOkHttpClient = new OkHttpClient();
        mClientBuilder = mOkHttpClient.newBuilder();
    }

    /**
     * Set http head for the request.
     *
     * @param headers header of lighthttp.
     */
    public void setHeaders(Headers headers) {
        mHeaders = headers;
    }

    /**
     * Get http head for the request.
     * @return Headers Header of lighthttp.
     */
    public Headers getHeaders() {
        return mHeaders;
    }

    /**
     * Sets the default connect timeout for new connections. A value of 0 means no timeout, otherwise values must be between 1 and {@link Integer#MAX_VALUE} when converted to milliseconds.
     *
     * @param timeout The time out.
     * @param unit The time unit of timeout.
     */
    public void setConnectTimeout(long timeout, TimeUnit unit) {
        mClientBuilder.connectTimeout(timeout, unit);
    }

    /**
     * Sets the default read timeout for new connections. A value of 0 means no timeout, otherwise values must be between 1 and {@link Integer#MAX_VALUE} when converted to milliseconds.
     *
     * @param timeout The time out. TimeUnit is millisecond.
     * @param unit The time unit of timeout.
     */
    public void setReadTimeout(long timeout, TimeUnit unit) {
        mClientBuilder.readTimeout(timeout, unit);
    }

    /**
     * Set the write time out.
     *
     * @param timeout The time out. TimeUnit is millisecond.
     * @param unit The time unit of timeout.
     */
    public void setWriteTimeout(long timeout, TimeUnit unit) {
        mClientBuilder.writeTimeout(timeout, unit).build();
    }

    public void trustAllSSL() {
        mClientBuilder.sslSocketFactory(createSSLSocketFactory()).build();
    }

    public void setSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
        mClientBuilder.sslSocketFactory(sslSocketFactory).build();
    }

    public void hostnameVerifier() {
        mClientBuilder.hostnameVerifier(new HostnameVerifier() {

            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;

            }
        }).build();
    }

    OkHttpClient getOkHttpClient() {
        return mClientBuilder.build();
    }

    private SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {


            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
              ssfFactory = sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ssfFactory;
    }

    public void setCertificates(InputStream... certificates) throws Exception {

        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null);
        int index = 0;
        for (InputStream certificate : certificates) {
            String certificateAlias = Integer.toString(index++);
            keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));

            if (certificate != null) {
                certificate.close();
            }
        }

        SSLContext sslContext = SSLContext.getInstance("TLS");

        TrustManagerFactory trustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

        trustManagerFactory.init(keyStore);
        sslContext.init(
                null, trustManagerFactory.getTrustManagers(),
                new SecureRandom());
        setSSLSocketFactory(sslContext.getSocketFactory());
    }

}
