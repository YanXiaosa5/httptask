package com.xinbo.httptask;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.xinbo.httptask.callback.Callback;
import com.xinbo.httptask.encry.aes.AesException;
import com.xinbo.httptask.encry.aes.EncryAndroid;
import com.xinbo.httptask.utils.JSONUtils;
import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yxs on 2018/10/5.
 */

public abstract class ResultCallback<T> extends Callback<T> {
    private Type mType;
    private boolean showLoading = false;
    private boolean customJson = false;

    /**
     * Loading Dialog
     **/
    @SuppressWarnings("deprecation")
    private ProgressDialog mProgressDialog;
    private String showText = "Load...";
    public Context mContext;

    public ResultCallback(Context aContext) {
        this(aContext, false);
    }

    public ResultCallback(Context aContext, boolean customJson) {
        this(aContext, false, "", customJson);
    }

    public ResultCallback(Context aContext, boolean showLoading, boolean customJson) {
        this(aContext, showLoading, "", customJson);
    }

    public ResultCallback(Context aContext, boolean showLoading, String showText, boolean customJson) {
        mType = getSuperclassTypeParameter(getClass());
        this.mContext = aContext;
        this.showLoading = showLoading;
        this.showText = showText;
        this.customJson = customJson;
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        onFailure(e.getMessage());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBefore(Request request, int id) {
        super.onBefore(request, id);
        if (showLoading) {
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(mContext);
            } else {
                mProgressDialog.cancel();
            }
            mProgressDialog.setMessage(showText);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }
    }

    @Override
    public void inProgress(int progress, long total, int id) {
        super.inProgress(progress, total, id);
        onLoading(progress, total);
    }

    public void onLoading(float progress, long total) {
    }

    /**
     * 关闭ProgressDialog
     **/
    private void initCloseProgressDialog() {
        if (showLoading && mProgressDialog != null
                && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    private static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            return null;
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
    }

    @Override
    public T parseNetworkResponse(Response response, int id) throws Exception {
        final String string = response.body().string();
        OkHttpUtils.getInstance().getDelivery().execute(new Runnable() {
            @Override
            public void run() {
                onResponseString(string);
            }
        });
        if (string != null && string.length() > 0) {
            try {
                if (customJson) {
                    return null;
                } else {
                    JSONObject obj = new JSONObject(string);
                    String data = "";
                    if (obj.has("data")) {
                        data = obj.getString("data");
                    }
                    final int status = obj.getInt("code");
                    final String message = obj.getString("message");
                    final String finalData = getResult(data);
//                    OkHttpUtils.getInstance().getDelivery().execute(() -> onSuccessString(finalData, message, status));
                    OkHttpUtils.getInstance().getDelivery().execute(new Runnable() {
                        @Override
                        public void run() {
                            onSuccessString(finalData, message, status);
                        }
                    });
                    switch (status) {
                        case 200:
                            if (mType != null && !finalData.equals("[]")) {
                                return new Gson().fromJson(finalData, mType);
                            } else {
                                return null;
                            }
                        case 201:
                            onNoLogin();
                            return null;
                        default:
                            onSpecialStatus(status, message);
                            throw new Exception(message);
                    }
                }
            } catch (JSONException e) {
                throw new Exception("unknown json type");
            }
        } else {
            throw new Exception("none data");
        }
    }

    private String getResult(String data) {
        String result = "";
        if (data != null && data.length() > 0 && !data.equals("null")) {
            ResponseBean responseBean = JSONUtils.fromJson(data, ResponseBean.class);
            try {
                result = EncryAndroid.decrypt(responseBean.getEncryptstr());
            } catch (AesException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public void onResponse(T response, int id) {
        onSuccess();
        onSuccess(response);
    }

    public void onSuccess(T response) {
        initCloseProgressDialog();
    }

    public void onSuccessString(String data, String message, int code) {
        initCloseProgressDialog();
    }

    public void onSuccess() {
        initCloseProgressDialog();
    }

    public void onResponseString(String response) {
        initCloseProgressDialog();
    }

    public void onSpecialStatus(int status, String message) {//特殊状态码处理

    }

    public void onNoLogin() {
//        OkHttpUtils.getInstance().getDelivery().execute(() -> {
//            initCloseProgressDialog();
//            doLoginOut();
//        });
        OkHttpUtils.getInstance().getDelivery().execute(new Runnable() {
            @Override
            public void run() {
                initCloseProgressDialog();
                doLoginOut();
            }
        });
    }

    public void doLoginOut() {

    }

    public void onResultError(String message) {
        initCloseProgressDialog();
        if (message != null && message.length() > 0) {
            Toast.makeText(mContext, message+"", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "server not enable", Toast.LENGTH_SHORT).show();
        }
    }

    public void onFailure(String strMsg) {
        if (strMsg != null && strMsg.length() > 0) {
            if(strMsg.contains("Failed to connect to") || strMsg.contains("failed to connect to")){
                strMsg = "服务器开小差了!";
            }

            Toast.makeText(mContext, strMsg+"", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "server not enable", Toast.LENGTH_SHORT).show();
        }
        initCloseProgressDialog();
    }
}

