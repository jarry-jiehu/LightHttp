
package eui.lighthttp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.text.TextUtils;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Delete extends AbstractRequest {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * Request by http delete.
     *
     * @param url The url of request.
     * @return Response data.
     * @throws IOException
     */
    public Response request(Client client, String url) throws IOException {
        if (null == client || null == client.getOkHttpClient()) {
            return null;
        }
        return request(client, url, (RequestBody) null);
    }

    /**
     * Request by http delete.
     *
     * @param url The url of request.
     * @param params Map data, Will convert to json.
     * @return Response data.
     * @throws IOException
     */
    public Response request(Client client, String url, Map<String, String> params) throws IOException {
        if (null == client || null == client.getOkHttpClient()) {
            return null;
        }
        if (params == null) {
            return request(client, url, (RequestBody) null);
        }
        RequestBody requestBody = buildBody(params);
        return request(client, url, requestBody);
    }

    /**
     * Request HTTP by delete.
     *
     * @param url The url of request.
     * @param jsonParams Json type body.
     * @return Response data.
     * @throws IOException
     */
    public Response request(Client client, String url, String jsonParams) throws IOException {
        if (null == client || null == client.getOkHttpClient()) {
            return null;
        }
        if (TextUtils.isEmpty(jsonParams)) {
            return request(client, url, (RequestBody) null);
        }
        RequestBody requestBody = RequestBody.create(JSON, jsonParams);
        return request(client, url, requestBody);
    }

    /**
     * Request http delete with RequestBody.
     *
     * @param client
     * @param url
     * @param requestBody
     * @return
     * @throws IOException
     */
    private Response request(Client client, String url, RequestBody requestBody) throws IOException {
        Request.Builder requestBuilder = new Request.Builder();
        if (requestBody != null) {
            requestBuilder.delete(requestBody);
        } else {
            requestBuilder.delete();
        }
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
