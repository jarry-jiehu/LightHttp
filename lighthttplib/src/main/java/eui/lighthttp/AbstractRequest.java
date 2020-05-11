package eui.lighthttp;

import okhttp3.Request;

public abstract class AbstractRequest {
    /**
     * Build the header.
     * @param client client of lighthttp.
     * @param requestBuilder builder
     * @return Request.Builder builder.
     */
    protected Request.Builder buildHeaders(Client client, Request.Builder requestBuilder){
        Headers headers = client.getHeaders();
        if(null != headers && null != headers.getOkHeaders()){
            return requestBuilder.headers(headers.getOkHeaders());
        }
        return requestBuilder;
    }
}
