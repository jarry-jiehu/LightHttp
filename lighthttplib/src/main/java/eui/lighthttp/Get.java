package eui.lighthttp;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Request;

public class Get extends AbstractRequest {
    /**
     * Request by http get.
     * @param client
     * @param url
     * @return
     * @throws IOException
     */
    public Response request(Client client, String url) throws IOException {
        if(null == client || null == client.getOkHttpClient()){
            return null;
        }
        Request.Builder requestBuilder = new Request.Builder();
        buildHeaders(client, requestBuilder);
        Request request = requestBuilder.url(url).build();
        Call call = client.getOkHttpClient().newCall(request);
        return new Response(call.execute());
    }
}
