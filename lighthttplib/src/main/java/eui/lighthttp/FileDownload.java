
package eui.lighthttp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class FileDownload extends AbstractRequest {
    private long mDownloadSpeed = -1L;
    private int CODE_REQUEST_SUCCESS = 200;

    /**
     * Set speed of download.
     * 
     * @param downloadSpeed dest speed of download.
     * @return this.
     */
    public FileDownload setDownloadSpeed(long downloadSpeed) {
        mDownloadSpeed = downloadSpeed;
        return this;
    }

    /**
     * Download file from url.
     *
     * @param url String url.
     * @param dest String path of local file.
     * @param callback Callback
     */
    public void download(Client client, String url, final String dest, final FileDownload.Callback callback) {
        if (null == client || null == client.getOkHttpClient()) {
            callback.onFailure(new Exception("Client is null. Maybe has occur error."));
            return;
        }
        Request.Builder requestBuilder = new Request.Builder();
        buildHeaders(client, requestBuilder);
        Request request = requestBuilder.url(url).build();
        client.getOkHttpClient().newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                readResponse(response, dest, callback);
            }
        });
    }

    /**
     * Synchronize download file from url.
     *
     * @param url String url.
     * @param dest String path of local file.
     * @param callback Callback
     */
    public void syncDownload(Client client, String url, final String dest, final FileDownload.Callback callback) {
        if (null == client || null == client.getOkHttpClient()) {
            return;
        }
        try {
            Request.Builder requestBuilder = new Request.Builder();
            buildHeaders(client, requestBuilder);
            Request request = requestBuilder.url(url).build();
            Response response = client.getOkHttpClient().newCall(request).execute();
            int code = response.code();
            if (code == CODE_REQUEST_SUCCESS) {
                readResponse(response, dest, callback);
            } else {
                callback.onFailure(new IOException("request net error!"));
            }
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    private void readResponse(Response response, String dest, Callback callback) {
        InputStream inputStream = response.body().byteStream();
        FileOutputStream fos = null;
        try {
            File destFile = new File(dest);
            if (destFile.exists()) {
                destFile.delete();
            }
            fos = new FileOutputStream(dest);
            byte[] buffer = new byte[2048];
            int len = 0;
            int progress = 0;
            long cursum = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                callback.onProgressUpdate(response.body().contentLength(), progress += len);
                if (0 < mDownloadSpeed) {
                    cursum += len;
                    if (cursum >= mDownloadSpeed) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                        cursum = 0;
                    }
                }
            }

            fos.flush();
            callback.onSuccess();
        } catch (IOException e) {
            callback.onFailure(e);
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public interface Callback {
        /**
         * Invoke when the download success.
         */
        void onSuccess();

        /**
         * Invoke when the download fail.
         *
         * @param exception String exception info.
         */
        void onFailure(Exception exception);

        /**
         * Invoke when the download doing.
         *
         * @param value Downloaded data byte. The byte will increase step by 2KB.
         */
        void onProgressUpdate(long contentLength, long value);
    }
}
