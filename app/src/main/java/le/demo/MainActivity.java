
package le.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import eui.lighthttp.FileDownload;
import eui.lighthttp.FileUpload;
import eui.lighthttp.Headers;
import eui.lighthttp.Helper;
import eui.lighthttp.Response;
import eui.lighthttp.scloud.ScloudHelper;

public class MainActivity extends Activity {
    private static final String TAG = "LightHttp";
    private Helper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHelper = new Helper();
    }

    public void click(View v) {
        switch (v.getId()) {
            case R.id.get:
                requestGet();
                break;
            case R.id.post:
                requestPost();
                break;
            case R.id.download:
                download();
                break;
            case R.id.request_with_secret1:
                requestWithSecret1();
                break;
            case R.id.request_with_secret2:
                requestWithSecret2();
                break;
            case R.id.delete:
                requestDelete();
                break;
            case R.id.https_trust_all:
                getHttpsByTrustAll();
                break;
            case R.id.https_set_cert:
                getHttpsByCerts();
                break;
        }
    }

    private void requestWithSecret2() {
        Log.i(TAG, "requestWithSecret2");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String accessKey = "lekeyAK";
                String secretKey = "lekeySKuhgfderfcsajs";
                String requestUrl = "http://test.tvapi.letv.com/api/v1/lekey/getchannel";
                String paramsUrl = "version=V2401RCN02C060240D04184T&versionName=6.0.240T_0418&model=X443N&ui=6.0&hwVersion=H2000&mac=c80b77f9aa08&region=CN&user-prefer-language=zh-cn";
                String letvUA = "TV/X443N (V2401RCN02C060240D04184T; com.stv.lekey)";

                Headers mHeader = new Headers();
                mHeader.addHeader("os", "android");
                mHeader.addHeader("user-agent", letvUA);
                ScloudHelper helper = new ScloudHelper();
                helper.getClient().setHeaders(mHeader);
                try {
                    Response response = helper.requestWithSecurity2(requestUrl, accessKey, secretKey, paramsUrl);
                    Log.i(TAG, "code: " + response.getStatusCode());
                    Log.i(TAG, "content: " + response.getContent());
                } catch (IOException e) {
                    Log.i(TAG, "", e);
                }
            }
        }).start();
    }

    private void requestWithSecret1() {
        Log.i(TAG, "requestWithSecret1");
        new Thread(new Runnable() {
            @Override
            public void run() {
                ScloudHelper helper = new ScloudHelper();
                String ak = "ak_lMHsCi32Wgyaqg23g9YL";
                String sk = "sk_xVU5x7uvHXse38f8axKN";

                Map<String, String> params = new HashMap<String, String>();
                params.put("packageName", "com.stv.systemupgrade");
                params.put("apkVersion", "1");
                params.put("deviceType", "TV");
                params.put("deviceId", "c80e777e348a");
                params.put("model", "S240F");
                params.put("region", "CN");
                params.put("user-prefer-language", "zh-cn");
                params.put("ui", "5.8");
                try {
                    Response response = helper.requestWithSecurity1("http://ota.scloud.letv.com/apk/api/v1/getUpgradeInfo", ak, sk, params);
                    Log.i(TAG, "code: " + response.getStatusCode());
                    Log.i(TAG, "content: " + response.getContent());
                } catch (IOException e) {
                    Log.i(TAG, "", e);
                }
            }
        }).start();
    }

    private void upload() {
        mHelper.upload("xxx", new HashMap<String, Object>(), new FileUpload.Callback() {
            @Override
            public void onSuccess(String jsonString) {

            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    private void download() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "http://lemall.com/app/shop_letv_android.apk";
                String dest = getExternalFilesDir(null).getAbsolutePath() + "/shop.apk";
                mHelper.download(url, dest, new FileDownload.Callback() {
                    @Override
                    public void onSuccess() {
                        Log.i(TAG, "Download Success");
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Log.i(TAG, exception.getMessage());
                    }

                    @Override
                    public void onProgressUpdate(long contentLength, long value) {
                        Log.i(TAG, "Downloaded: " + value + " / " + contentLength);
                    }
                });
            }
        }).start();
    }

    private void requestPost() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> data = new HashMap<String, String>();
                data.put("key", "value");
                try {
                    Response response = mHelper.requestByPost("http://www.le.com", data);
                    Log.i(TAG, "code: " + response.getStatusCode());
                    Log.i(TAG, "content: " + response.getContent());
                } catch (IOException e) {
                    Log.i(TAG, "Request post fail." + e.getMessage());
                }
            }
        }).start();
    }

    private void requestGet() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = mHelper.requestByGet("http://www.le.com/");

                    Log.i(TAG, "code: " + response.getStatusCode());
                    Log.i(TAG, "content: " + response.getContent());
                } catch (IOException e) {
                    Log.i(TAG, "Request get fail." + e.getMessage());
                }
            }
        }).start();
    }

    private void getHttpsByTrustAll() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // trust all certs 信任所有的证书
                    mHelper.trustAllSSLRepuest();
                    Response response = mHelper.requestByGet(
                            "https://test.tvapi.letv.com/iptv/api/sms/getSMSProfile.json?isCIBN=1&lang=zh-cn&version=V2401RCN02C060243D05095T&versionName=6.0.243T_0509&model=X440&ui=6.0&hwVersion=H2000&mac=b01bd205a51d&region=CN&user-prefer-language=zh-cn");
                    Log.i(TAG, "code: " + response.getStatusCode());
                    Log.i(TAG, "content: " + response.getContent());

                } catch (IOException e) {
                    Log.i(TAG, "Request get fail." + e.getMessage());
                }
            }
        }).start();
    }

    private void getHttpsByCerts() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // trust all certs 信任特定的证书
                    InputStream is = null;
                    try {
                        is = getAssets().open("srca.cer");
                        mHelper.setCertification(is);
                        Response response = mHelper.requestByGet("https://kyfw.12306.cn/otn/");
                        is.close();
                        Log.i(TAG, "code: " + response.getStatusCode());
                        Log.i(TAG, "content: " + response.getContent());
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (is != null) {
                            is.close();
                        }
                    }

                } catch (IOException e) {
                    Log.i(TAG, "Request get fail." + e.getMessage());
                }
            }
        }).start();
    }

    private void requestDelete() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // this url is used to delete bind info with device and user, X440-b01bd205a51d- is deviceid and sso_tk is current token
                    // response code maybe error, this just used to show the method
                    Response response = mHelper.requestByDelete(
                            "https://device-scloud.cp21.ott.cibntv.net/api/v1/device/bind/X440-b01bd205a51d-?sso_tk=103XXXQvbgzuQRspC9goMjgWfOT7cyAm5UZe8nf7g9zbLdhS4fB3FZ8m5L30eovV3t4U8yW0o51m2NjoOYhMHBTiPnTpuB66m2rcNK2JURT2Cm53VYgIm1gm4");
                    Log.i(TAG, "code: " + response.getStatusCode());
                    Log.i(TAG, "content: " + response.getContent());
                } catch (IOException e) {
                    Log.i(TAG, "Request post fail." + e.getMessage());
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
