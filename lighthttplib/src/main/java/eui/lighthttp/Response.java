package eui.lighthttp;

import java.io.IOException;
import java.io.InputStream;

/**
 * HTTP response
 */
public class Response {

    private final okhttp3.Response mResponse;

    Response(okhttp3.Response response) {
        mResponse = response;
    }

    /**
     * Get content.
     * @return String content of body.
     * @throws IOException if occur fail.
     */
    public String getContent() throws IOException {
        return mResponse.body().string();
    }

    /**
     * Get inputStream.
     * @return InputStream of body.
     * @throws IOException if occur fail.
     */
    public InputStream getInputStream() throws IOException {
        return mResponse.body().byteStream();
    }

    /**
     * HTTP status code
     * @return Code
     */
    public int getStatusCode() {
        return mResponse.code();
    }

    /**
     * Get the http header of response.
     * @return Header of lighthttp.
     */
    public Headers getHeaders() {
        return new Headers(mResponse.headers());
    }

    @Override
    public String toString() {
            return super.toString() + ": " + mResponse.toString() + mResponse.getClass().getName() + '@' + Integer.toHexString(mResponse.hashCode());
    }
}
