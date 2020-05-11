
package eui.lighthttp.scloud;

import android.text.TextUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import eui.lighthttp.Helper;
import eui.lighthttp.Response;
import eui.lighthttp.util.LogUtils;

public class Signature1 {
    private static LogUtils sLogUtils = LogUtils.getInstance("Signature1");

    private static final String KEY_TIME = "_time";
    private static final String KEY_AK = "_ak";
    private static final String PARAMS_SEP = "&";
    private static final String KEY_SIGN = "_sign";
    private static final String REQUEST_CHARSET = "UTF-8";
    private static final char HEX_DIGITS[] = "0123456789abcdef".toCharArray();

    static Response request(Helper helper, String apiPath, String ak, String sk, Map<String, String> params) throws IOException {
        if (TextUtils.isEmpty(apiPath)) {
            throw new IllegalArgumentException("You MUST set url for the request!");
        }
        long time = System.currentTimeMillis();
        String paramString = buildParamString(ak, params, time);
        String signature = getSignature(ak, sk, params, time);
        if (null != paramString) {
            String url = apiPath + "?" + paramString + PARAMS_SEP + KEY_SIGN + "=" + signature;
            if (null != helper) {
                return helper.requestByGet(url);
            }
        }
        return null;
    }

    private static String buildParamString(String ak, Map<String, String> params, long time) throws UnsupportedEncodingException {
        SortedSet<String> set = new TreeSet<String>();
        set.add(KEY_TIME + "=" + URLEncoder.encode(String.valueOf(time), REQUEST_CHARSET));
        set.add(KEY_AK + "=" + URLEncoder.encode(ak, REQUEST_CHARSET));
        if (params != null && params.size() > 0) {
            for (String key : params.keySet()) {
                String value = params.get(key);
                if (!TextUtils.isEmpty(value)) {
                    set.add(key + "=" + URLEncoder.encode(value, REQUEST_CHARSET));
                }
            }
        }
        return join(set, PARAMS_SEP);
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
    static String getSignature(String ak, String sk, Map<String, String> params, long time) {
        if (TextUtils.isEmpty(ak)) {
            throw new IllegalArgumentException("You MUST set ak and sk for the request!");
        }
        try {
            String paramsString = buildParamString(ak, params, time);
            String skParams = paramsString + sk;
            return decode(skParams);
        } catch (Exception e) {
            sLogUtils.w(e);
        }
        return null;
    }

    private static String decode(String str2Sign) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = null;
        digest = MessageDigest.getInstance("MD5");
        digest.update(str2Sign.getBytes(REQUEST_CHARSET));
        return toHexString(digest.digest());
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
