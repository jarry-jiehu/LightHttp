
package eui.lighthttp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Post extends AbstractRequest {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * Request by http post.
     *
     * @param client client of lighthttp.
     * @param url The url of request.
     * @param params Map data, Will convert to json.
     * @return Response data.
     * @throws IOException if occur fail.
     */
    public Response request(Client client, String url, Map<String, String> params) throws IOException {
        if (null == client || null == client.getOkHttpClient()) {
            return null;
        }
        RequestBody requestBody = buildBody(params);
        return request(client, url, requestBody);
    }

    /**
     * Request HTTP by post.
     *
     * @param client client of lighthttp.
     * @param url The url of request.
     * @param jsonParams Json type body.
     * @return Response data.
     * @throws IOException if occur fail.
     */
    public Response request(Client client, String url, String jsonParams) throws IOException {
        if (null == client || null == client.getOkHttpClient()) {
            return null;
        }
        RequestBody requestBody = RequestBody.create(JSON, jsonParams);
        return request(client, url, requestBody);
    }

    /**
     * Request http post with RequestBody.
     * 
     * @param client
     * @param url
     * @param requestBody
     * @return
     * @throws IOException
     */
    private Response request(Client client, String url, RequestBody requestBody) throws IOException {
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.post(requestBody);
        buildHeaders(client, requestBuilder);
        Request request = requestBuilder.url(url).build();
        okhttp3.Response response = client.getOkHttpClient().newCall(request).execute();
        return new Response(response);
    }

    private RequestBody buildBody(Map<String, String> params) {
        if (params == null) {
            params = new HashMap<String, String>();
        }
        FormBody.Builder formBuilder = new FormBody.Builder();
        Set<Map.Entry<String, String>> entrySet = params.entrySet();
        if (null != entrySet) {
            for (Map.Entry<String, String> entry : entrySet) {
                formBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        RequestBody requestBody = formBuilder.build();
        return requestBody;
    }
}
