
package eui.lighthttp.scloud;

import java.io.IOException;
import java.util.Map;
import eui.lighthttp.Helper;
import eui.lighthttp.Response;
import eui.lighthttp.util.LogUtils;

public class ScloudHelper extends Helper {
    private static LogUtils sLogUtils = LogUtils.getInstance("ScloudHelper");

    /**
     * Request with security1.
     *
     * @see <code>http://wiki.letv.cn/pages/viewpage.action?pageId=37325204</code>
     * @param url The url without params of request.
     * @param ak Access key of client.
     * @param sk Secret key of client.
     * @param params Params of request.
     * @return Response The Response of request.
     * @throws IOException If rquest fail, will throw exception.
     */
    public Response requestWithSecurity1(String url, String ak, String sk, Map<String, String> params) throws IOException {
        return Signature1.request(this, url, ak, sk, params);
    }

    /**
     * Request with signature2.
     *
     * @see <code>http://wiki.letv.cn/pages/viewpage.action?pageId=37323874</code>
     * @param url The url without params of request.
     * @param ak Access key of client.
     * @param sk Secret key of client.
     * @param generalParam Path of url, e.g: "version=V2401RCN02C060240D04184T".
     * @return Response The Response of request.
     * @throws IOException If rquest fail, will throw exception.
     */
    public Response requestWithSecurity2(String url, String ak, String sk, String generalParam) throws IOException {
        return Signature2.request(this, url, ak, sk, generalParam);
    }

    /**
     * Get sign string of request params.
     * <p>
     * See <code>http://wiki.letv.cn/pages/viewpage.action?pageId=37325204</code>
     *
     * @param ak Access key of client.
     * @param sk Secret key of client.
     * @param params Params of request.
     * @param time Current timestamp.
     * @return Result of sign, null if generate fail.
     */
    public static String getSignature1(String ak, String sk, Map<String, String> params, long time) {
        return Signature1.getSignature(ak,sk,params,time);
    }

    /**
     * Get the params sign by the signature2.
     *
     * @see <code>http://wiki.letv.cn/pages/viewpage.action?pageId=37323874</code>
     * @param secretKey secretKey get by scloud.
     * @param method HTTP Method, must be upcase.
     * @param path Path of url, e.g: "/api/xxx/".
     * @param body Body of request.
     * @param date The time of request, follow rfc822, e.g: Mon, 24 Nov 2014 12:11:17 CST
     * @param params The params format such as "key=value", contain url and post form except if the params is json. Params must URL Encodeing.
     * @return String signature, null if generate fail.
     */
    public static String getSignature2(String secretKey, String method, String path, byte[] body,
            String date, Map<String, String[]> params) {
        return Signature2.getSignature(secretKey,method,path,body,date,params);
    }
}
