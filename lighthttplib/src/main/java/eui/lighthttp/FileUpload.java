package eui.lighthttp;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FileUpload extends AbstractRequest {
    /**
     * Upload file to the specified url.
     *
     * @param url
     * @param paramsMap
     * @param callback
     */
    public void upload(Client client, String url, Map<String, Object> paramsMap, final FileUpload.Callback callback) {
        if(null == client || null == client.getOkHttpClient()){
            callback.onFailure(new Exception("Client is null. Maybe has occur error."));
            return;
        }
        try {
            RequestBody body = buildBody(paramsMap);
            Request.Builder requestBuilder = new Request.Builder();
            buildHeaders(client,requestBuilder);
            final Request request = requestBuilder.url(url).post(body).build();
            final Call call = client.getOkHttpClient().newCall(request);
            call.enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onFailure(e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String string = response.body().string();
                        callback.onSuccess(string);
                    } else {
                        callback.onFailure(new Exception("Response fail: " + response.code()));
                    }
                }
            });
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    private RequestBody buildBody(Map<String, Object> paramsMap) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for (String key : paramsMap.keySet()) {
            Object object = paramsMap.get(key);
            if (!(object instanceof File)) {
                builder.addFormDataPart(key, object.toString());
            } else {
                File file = (File) object;
                builder.addFormDataPart(key, file.getName(), RequestBody.create(null, file));
            }
        }
        return builder.build();
    }

    /**
     * The callback of upload file.
     */
    public interface Callback {
        /**
         * Invoke when upload task success.
         *
         * @param jsonString Reponse info.
         */
        void onSuccess(String jsonString);

        /**
         * Invoke when upload task fail.
         *
         * @param e Exception info.
         */
        void onFailure(Exception e);
    }
}
