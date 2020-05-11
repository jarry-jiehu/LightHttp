
package eui.lighthttp.scloud;

import android.net.Uri;
import android.text.TextUtils;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import eui.lighthttp.Headers;
import eui.lighthttp.Helper;
import eui.lighthttp.Response;
import eui.lighthttp.util.LogUtils;

public class Signature2 {
    private static LogUtils sLogUtils = LogUtils.getInstance("Signature2");
    private static final char HEX_DIGITS[] = "0123456789abcdef".toCharArray();

    static Response request(Helper helper, String url, String ak, String sk, String generalParam) throws IOException {
        if (TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException("You MUST set url for the request!");
        }

        String fullUrl = buildParamString(helper, url, ak, sk, generalParam);
        if (null != helper) {
            return helper.requestByGet(fullUrl);
        }
        return null;
    }

    private static String buildParamString(Helper helper, String apiPath, String ak, String sk, String generalParam) {
        Map<String, String[]> paramsMap = getParams(generalParam);
        String strDate = getCurrentFormatDate();
        Uri uri = Uri.parse(apiPath);
        String signature = getSignature(sk, "GET", uri.getPath(), null, strDate, paramsMap);
        String authorization = "LETV " + ak + " " + signature;

        Headers mHeader;
        if (helper.getClient().getHeaders() == null) {
            mHeader = new Headers();
        } else {
            mHeader = helper.getClient().getHeaders();
        }
        mHeader.addHeader("Date", strDate);
        mHeader.addHeader("Authorization", authorization);

        return apiPath + "?" + generalParam;
    }

    private static Map<String, String[]> getParams(String paramsUrl) {
        Map<String, String[]> paramMap = null;
        if (!TextUtils.isEmpty(paramsUrl)) {
            paramMap = new HashMap<String, String[]>();
            String[] keyAndValueStr = paramsUrl.split("&");
            for (int i = 0; i < keyAndValueStr.length; i++) {
                String keyAndValue = keyAndValueStr[i];
                if (keyAndValue.contains("=")) {
                    String[] keyValue = keyAndValue.split("=");
                    if (keyValue.length != 1) {
                        String key = keyValue[0];
                        String value = keyValue[1];
                        paramMap.put(key, new String[] {
                                value
                        });

                    }
                }
            }
        }
        return paramMap;
    }

    private static String getCurrentFormatDate() {
        Date date = new Date();
        SimpleDateFormat fm = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss'GMT'", Locale.US);
        String strDate = fm.format(date);
        return strDate;
    }

    /**
     * Get the params sign by the signature2.
     *
     * @see <code>http://wiki.letv.cn/pages/viewpage.action?pageId=37323874</code>
     * @param secretKey
     * @param method HTTP Method, must be upcase.
     * @param path Path of url, e.g: "/api/xxx/".
     * @param body Body of request.
     * @param date The time of request, follow rfc822, e.g: Mon, 24 Nov 2014 12:11:17 CST
     * @param params The params format such as "key=value", contain url and post form except if the params is json. Params must URL Encodeing.
     * @return String signature, null if generate fail.
     */
    static String getSignature(String secretKey, String method, String path, byte[] body,
            String date, Map<String, String[]> params) {
        try {
            String bodyMD5 = "";
            if (body != null && body.length != 0) {
                MessageDigest digest;
                digest = MessageDigest.getInstance("MD5");
                digest.update(body);
                bodyMD5 = toHexString(digest.digest());
            }
            String paramString = "";
            if (params != null) {
                SortedSet<String> set = new TreeSet<String>();
                for (String param : params.keySet()) {
                    String[] values = params.get(param);
                    for (String value : values) {
                        set.add(param + "=" + value);
                    }
                }
                paramString = join(set, "&");
            }
            String stringToSign = method + "\n" + path + "\n" + bodyMD5 + "\n" + date + "\n"
                    + paramString;
            sLogUtils.i(stringToSign);
            SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes(),
                    "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(stringToSign.getBytes());
            return toHexString(rawHmac);
        } catch (NoSuchAlgorithmException e) {
            sLogUtils.w("NoSuchAlgorithm!");
        } catch (InvalidKeyException e) {
            sLogUtils.w("InvalidKey!");
        }
        return null;
    }

    private static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(HEX_DIGITS[(b & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b & 0x0f]);
        }
        return sb.toString();
    }

    private static String join(Iterable<String> strings, String sep) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String item : strings) {
            if (first) {
                first = false;
            } else {
                sb.append(sep);
            }
            sb.append(item);
        }
        return sb.toString();
    }
}
