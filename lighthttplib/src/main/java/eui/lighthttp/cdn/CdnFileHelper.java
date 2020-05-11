
package eui.lighthttp.cdn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import android.text.TextUtils;

import eui.lighthttp.Headers;
import eui.lighthttp.Helper;
import eui.lighthttp.util.LogUtils;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Get info of video from cdn download url.
 */
public class CdnFileHelper {
    private static LogUtils sLogUtils = LogUtils.getInstance("CdnFileHelper");

    /**
     * Get cdn file info by cdn g3 download url.
     *
     * @param downloadUrl g3 download url.
     * @return CdnFileHelper.CdnFileResponse Object.
     */
    public CdnFileResponse getCdnFileInfo(String downloadUrl) {
        CdnFileResponse cdnResponse = new CdnFileResponse();
        cdnResponse.url = downloadUrl;

        if (TextUtils.isEmpty(downloadUrl)) {
            return cdnResponse;
        }

        Helper helper = new Helper();
        try {
            eui.lighthttp.Response response = helper.requestByGet(downloadUrl);
            if (null != response) {
                Headers hearders = response.getHeaders();
                cdnResponse.type = hearders.get("Content-Type");
                cdnResponse.size = Integer.valueOf(hearders.get("Content-Length"));
                cdnResponse.realUrl = getRealUrl(downloadUrl);
                if (cdnResponse.realUrl != null && !"".equals(cdnResponse.realUrl) && !downloadUrl.equals(cdnResponse.realUrl)) {
                    CdnFileInfo cdnFileInfo = new CdnFileInfo(cdnResponse.realUrl);
                    cdnResponse.netMd5 = cdnFileInfo.getMd5();
                }
                if (!TextUtils.isEmpty(cdnResponse.netMd5) && cdnResponse.netMd5.length() == 32) {
                    cdnResponse.success = true;
                }
            }
        } catch (Exception e) {
            sLogUtils.e(e);
        }
        return cdnResponse;
    }

    private String getRealUrl(String downloadUrl) {
        if (TextUtils.isEmpty(downloadUrl)) {
            return null;
        }

        String realUrl = null;

        final OkHttpClient client = new OkHttpClient().newBuilder()
                .followRedirects(false) // 禁制OkHttp的重定向操作，我们自己处理重定向
                .followSslRedirects(false)
                .cookieJar(new LocalCookieJar()) // 为OkHttp设置自动携带Cookie的功能
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .build();

        Request request = new Request.Builder().get().url(downloadUrl).build();
        try {
            Response response = client.newCall(request).execute();
            if (null != response) {
                realUrl = response.header("Location");
            }
        } catch (IOException e) {
            sLogUtils.e(e);
        }

        return realUrl;
    }

    public class CdnFileResponse {
        private boolean success;
        private long size;
        private String type;
        private String netMd5;
        private String url;
        private String realUrl;

        public CdnFileResponse() {
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getNetMd5() {
            return netMd5;
        }

        public void setNetMd5(String netMd5) {
            this.netMd5 = netMd5;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getRealUrl() {
            return realUrl;
        }

        public void setRealUrl(String realUrl) {
            this.realUrl = realUrl;
        }

        @Override
        public String toString() {
            return "CdnFileResponse{" +
                    "success=" + success +
                    ", size=" + size +
                    ", type='" + type + '\'' +
                    ", netMd5='" + netMd5 + '\'' +
                    ", url='" + url + '\'' +
                    ", realUrl='" + realUrl + '\'' +
                    '}';
        }
    }

    // CookieJar是用于保存Cookie的
    class LocalCookieJar implements CookieJar {
        List<Cookie> cookies;

        @Override
        public List<Cookie> loadForRequest(HttpUrl arg0) {
            if (cookies != null)
                return cookies;
            return new ArrayList<Cookie>();
        }

        @Override
        public void saveFromResponse(HttpUrl arg0, List<Cookie> cookies) {
            this.cookies = cookies;
        }

    }

    public class CdnFileInfo {
        private String trdID;
        private String videoCode;
        private String videoRate;
        private String audioCode;
        private String audioRate;
        private String duration;
        private String size;
        private String md5;
        private String timeStamp;

        public CdnFileInfo(String realUrl) {
            parser(realUrl);
        }

        private boolean parser(String realUrl) {
            boolean success = false;

            if (TextUtils.isEmpty(realUrl)) {
                return success;
            }

            if (!realUrl.contains("/")) {
                return success;
            }

            int start = realUrl.lastIndexOf("/") + 1;
            if (start <= 1) {
                return success;
            }

            int end = -1;
            if (realUrl.contains("?")) {
                end = realUrl.indexOf("?");
            } else {
                end = realUrl.length();
            }
            if (end <= 0) {
                return success;
            }

            if (start >= end || start > realUrl.length() || end > realUrl.length()) {
                return success;
            }
            String name = realUrl.substring(start, end);

            if (TextUtils.isEmpty(name)) {
                return success;
            }

            if (!name.contains("-")) {
                return success;
            }

            String[] arr = name.split("-");
            if (arr == null) {
                return success;
            }

            if (name.endsWith(".ts") && arr.length == 9) {
                setTrdID(arr[0]);
                setVideoCode(arr[1]);
                setVideoRate(arr[2]);
                setAudioCode(arr[3]);
                setAudioRate(arr[4]);
                setDuration(arr[5]);
                setSize(arr[6]);
                setMd5(arr[7]);
                setTimeStamp(arr[8]);
                success = true;
            } else if (name.endsWith(".mp4") && arr.length == 10) {
                setTrdID(arr[1]);
                setVideoCode(arr[2]);
                setVideoRate(arr[3]);
                setAudioCode(arr[4]);
                setAudioRate(arr[5]);
                setDuration(arr[6]);
                setSize(arr[7]);
                setMd5(arr[8]);
                setTimeStamp(arr[9]);
                success = true;
            }

            return success;
        }

        public String getTrdID() {
            return trdID;
        }

        public void setTrdID(String trdID) {
            this.trdID = trdID;
        }

        public String getVideoCode() {
            return videoCode;
        }

        public void setVideoCode(String videoCode) {
            this.videoCode = videoCode;
        }

        public String getVideoRate() {
            return videoRate;
        }

        public void setVideoRate(String videoRate) {
            this.videoRate = videoRate;
        }

        public String getAudioCode() {
            return audioCode;
        }

        public void setAudioCode(String audioCode) {
            this.audioCode = audioCode;
        }

        public String getAudioRate() {
            return audioRate;
        }

        public void setAudioRate(String audioRate) {
            this.audioRate = audioRate;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public String getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(String timestamp) {
            this.timeStamp = timestamp;
        }

        @Override
        public String toString() {
            return "CdnFileInfo{" +
                    "trdID='" + trdID + '\'' +
                    ", videoCode='" + videoCode + '\'' +
                    ", videoRate='" + videoRate + '\'' +
                    ", audioCode='" + audioCode + '\'' +
                    ", audioRate='" + audioRate + '\'' +
                    ", duration='" + duration + '\'' +
                    ", size='" + size + '\'' +
                    ", md5='" + md5 + '\'' +
                    ", timeStamp='" + timeStamp + '\'' +
                    '}';
        }
    }

}
