package com.xinbo.httptask;


import com.xinbo.httptask.encry.aes.AesException;
import com.xinbo.httptask.encry.aes.EncryAndroid;
import com.xinbo.httptask.encry.sgin.SignUtil;
import com.xinbo.httptask.callback.Callback;
import com.xinbo.httptask.utils.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import okhttp3.MediaType;

public class HttpTaskUtil {
    //api地址
    public final static String BASE_URL = "https://qxx.beijingui.cn";
//     public final static String BASE_URL = "https://dt.wenzhouer.cn/majia";


    private final static String JSON_TYPE = "application/json; charset=utf-8";
    private final static String TICKET = "ticket";
    private final static String ENCRYPTSTR = "encryptstr";
    private final static String TIMESTAMP = "timestamp";
    private final static String ECHOSTR = "echostr";
    private final static String SIGN = "sign";
    private final static String APP_KEY = "7908b2179af04e1099877643ad7c83a2";

    public static void doJsonTask(String url, Callback callback) {
        //doJsonTask(url, null, callback);
    }

    public static void doJsonTask(String url, Map<String, String> params, Callback callback) {
    }

    public static void doJsonTask(String url, String json, Callback callback,String tag) {
        //String json = getJsonString(params);
        try {
//            String encryptstr = CryptUtil.encrypt(json);
            String encryptstr = EncryAndroid.encrypt(json);

            String timestamp = System.currentTimeMillis() + "";
            String echostr = createRandom(false, 32);
            SortedMap<String, String> signMap = new TreeMap<>();
            signMap.put(ECHOSTR, echostr);
            signMap.put(ENCRYPTSTR, encryptstr);
            signMap.put(TIMESTAMP, timestamp);
            String sign = SignUtil.createSign(signMap, APP_KEY);
            signMap.put(SIGN, sign);

            OkHttpUtils.postString().url(BASE_URL + url).content(getJsonString(signMap)).tag(tag)
                    .mediaType(MediaType.parse(JSON_TYPE))
                    .build().execute(callback);
//            OkHttpUtils.post().url(BASE_URL + url)
//                    .addParams(ENCRYPTSTR, json)
//                    .addParams(TIMESTAMP, SharedPreference.getTicket())
//                    .addParams(ECHOSTR, json)
//                    .addParams(SIGN, json)
//                    .build().execute(callback);

//            OkHttpUtils.post().url(BASE_URL + url)
//                    .addParams(ENCRYPTSTR, encryptstr)
//                    .addParams(TIMESTAMP, timestamp)
//                    .addParams(ECHOSTR, echostr)
//                    .addParams(SIGN, SignUtil.createSign(signMap, APP_KEY))
//                    .build().execute(callback);
        } catch (AesException e) {
            e.printStackTrace();
        }
    }

    private static String getJsonString(Map<String, String> params) {
        JSONObject jsonObject = new JSONObject();
        if (params == null) {
            params = new HashMap<>();
        }
        for (Map.Entry<String, String> entry : params.entrySet()) {
            try {
                jsonObject.put(entry.getKey(), entry.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return String.valueOf(jsonObject);
    }

    public static void doFormTask(String objName, String url, String json, Callback callback) {
        doFormTask(objName, url, json, null, callback);
    }

    public static void doFormTask(String objName, String url, Map<String, String> params, Callback callback) {
        doFormTask(objName, url, null, params, callback);
    }

    public static void doFormTask(String objName, String url, String json, Map<String, String> params, Callback callback) {
        if (json == null) {
            JSONObject jsonObject = new JSONObject();
            if (params == null) {
                params = new HashMap<>();
            }

            for (Map.Entry<String, String> entry : params.entrySet()) {
                try {
                    jsonObject.put(entry.getKey(), entry.getValue());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            json = String.valueOf(jsonObject);
        }

        OkHttpUtils.post().url(BASE_URL + url).addParams(objName, json)
                .addParams(TICKET, SharedPreference.getTicket()).build().execute(callback);
    }

    /**
     * 创建指定数量的随机字符串
     *
     * @param numberFlag 是否是数字
     * @param length 长度
     * @return  返回加密内容
     */
    public static String createRandom(boolean numberFlag, int length) {
        String retStr = "";
        String strTable = numberFlag ? "1234567890" : "1234567890abcdefghijkmnpqrstuvwxyz";
        int len = strTable.length();
        boolean bDone = true;
        do {
            retStr = "";
            int count = 0;
            for (int i = 0; i < length; i++) {
                double dblR = Math.random() * len;
                int intR = (int) Math.floor(dblR);
                char c = strTable.charAt(intR);
                if (('0' <= c) && (c <= '9')) {
                    count++;
                }
                retStr += strTable.charAt(intR);
            }
            if (count >= 2) {
                bDone = false;
            }
        } while (bDone);

        return retStr;
    }
}
