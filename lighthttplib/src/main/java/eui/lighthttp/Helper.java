
package eui.lighthttp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import okhttp3.MediaType;

public class Helper {
    private static final String TAG = "Helper";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    protected Client mClient;
    protected Headers mHeaders;

    public Helper() {
        mClient = new Client();
    }

    public Client getClient() {
        return mClient;
    }

    /**
     * Request HTTP by get.
     *
     * @param url The url of request.
     * @return Response data.
     * @throws IOException if request fail.
     */
    public Response requestByGet(String url) throws IOException {
        return new Get().request(mClient, url);
    }

    /**
     * Request HTTP by post.
     *
     * @param url The url of request.
     * @param params Map data, Will convert to json.
     * @return Response data.
     * @throws IOException if request fail.
     */
    public Response requestByPost(String url, Map<String, String> params) throws IOException {
        return new Post().request(mClient, url, params);
    }

    /**
     * Request HTTP by post.
     *
     * @param url The url of request.
     * @param jsonParams Json type body.
     * @return Response data.
     * @throws IOException if request fail.
     */
    public Response requestByPost(String url, String jsonParams) throws IOException {
        return new Post().request(mClient, url, jsonParams);
    }

    /**
     * Download file from url.
     *
     * @param url String url.
     * @param destPath String path of local file.
     * @param callback Callback
     */
    public void download(String url, final String destPath, final FileDownload.Callback callback) {
        new FileDownload().download(mClient, url, destPath, callback);
    }

    /**
     * Download file from url with custom download speed.
     *
     * @param url String url.
     * @param destPath String path of local file.
     * @param callback Callback
     * @param downloadSpeed Download speed.
     */
    public void download(String url, final String destPath, final FileDownload.Callback callback, final long downloadSpeed) {
        new FileDownload().setDownloadSpeed(downloadSpeed).download(mClient, url, destPath, callback);
    }

    /**
     * Synchronize download file from url.
     *
     * @param url String url.
     * @param destPath String path of local file.
     * @param callback Callback
     * @param downloadSpeed Download speed.
     */
    public void syncDownload(String url, final String destPath, final FileDownload.Callback callback, final long downloadSpeed) {
        new FileDownload().setDownloadSpeed(downloadSpeed).syncDownload(mClient, url, destPath, callback);
    }

    /**
     * Upload file to the specified url.
     *
     * @param url String url of request.
     * @param paramsMap Map params.
     * @param callback The callback of upload status.
     */
    public void upload(String url, Map<String, Object> paramsMap, final FileUpload.Callback callback) {
        new FileUpload().upload(mClient, url, paramsMap, callback);
    }

    /**
     * Request HTTP by delete.
     *
     * @param url The url of request.
     * @return Response data.
     * @throws IOException if request fail.
     */
    public Response requestByDelete(String url) throws IOException {
        return new Delete().request(mClient, url);
    }

    /**
     * Request HTTP by delete.
     *
     * @param url The url of request.
     * @param params Map data, Will convert to json.
     * @return Response data.
     * @throws IOException if request fail.
     */
    public Response requestByDelete(String url, Map<String, String> params) throws IOException {
        return new Delete().request(mClient, url, params);
    }

    /**
     * Request HTTP by delete.
     *
     * @param url The url of request.
     * @param jsonParams Json type body.
     * @return Response data.
     * @throws IOException if quest fail.
     */
    public Response requestByDelete(String url, String jsonParams) throws IOException {
        return new Delete().request(mClient, url, jsonParams);
    }

    /**
     * for https request, trust all SSLSocketFactory
     */
    public void trustAllSSLRepuest() {
        mClient.trustAllSSL();
        mClient.hostnameVerifier();
    }

    /**
     * for https request, trust given certs
     *
     * @param certificates The certificates of request.
     * @throws Exception if request fail.
     */
    public void setCertification(InputStream... certificates) throws Exception {
        mClient.setCertificates(certificates);
    }
}
