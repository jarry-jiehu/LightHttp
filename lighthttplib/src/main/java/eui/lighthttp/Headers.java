package eui.lighthttp;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Headers of request or response
 */
public class Headers {
    private Map<String, String> mHeadersMap;

    /*
     package accessibility
     */
    Headers(okhttp3.Headers headers) {
        mHeadersMap = new HashMap<String, String>();
        Set<String> names = headers.names();
        for (Iterator<String> it = names.iterator(); it.hasNext();) {
            String name = it.next();
            String value = headers.get(name);
            mHeadersMap.put(name, value);
        }
    }

    /**
     * Create a empty header.
     */
    public Headers() {
        mHeadersMap = new HashMap<String, String>();
    }

    /**
     * Create headers with headers.
     * @param headers
     */
    public Headers(Map<String, String> headers) {
        mHeadersMap = headers;
    }

    /**
     * Create a Headers with key and value.
     * @param key String Key of header.
     * @param value String values of header.
     */
    public Headers(String key, String value) {
        mHeadersMap = new HashMap<String, String>();
        mHeadersMap.put(key, value);
    }

    /**
     * Add a pair of string as header.
     * @param key String Key of header.
     * @param value String values of header.
     */
    public void addHeader(String key, String value) {
        if(null == mHeadersMap){
            mHeadersMap = new HashMap<String,String>();
        }
        mHeadersMap.put(key, value);
    }

    /**
     * get the value of name.
     * @param name
     * @return value
     */
    public String get(String name) {
        return getOkHeaders().get(name);
    }

    /**
     * Get date value in headers.
     * @param name
     * @return
     */
    public Date getDate(String name) {
        return getOkHeaders().getDate(name);
    }

    /**
     * Get all names of this headers.
     * @return
     */
    public Set<String> names() {
        return getOkHeaders().names();
    }

    /**
     * Get the values as a list of the parameter name.
     * @param name
     * @return
     */
    public List<String> values(String name) {
        return getOkHeaders().values(name);
    }

    @Override
    public String toString() {
        return getOkHeaders().toString();
    }

    /**
     * Remove the header whose name equals parameter.
     * @param name
     */
    public void remove(String name) {
        if(null != mHeadersMap) {
            mHeadersMap.remove(name);
        }
    }

    /**
     * Clear the headers.
     */
    public void removeAll() {
        if(null != mHeadersMap) {
            mHeadersMap.clear();
        }
    }

    okhttp3.Headers getOkHeaders() {
        return okhttp3.Headers.of(mHeadersMap);
    }
}
