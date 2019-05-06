package com.xinbo.httptask.callback;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public abstract class Callback<T> {
    /**
     * UI Thread
     *
     * @param request 请求体
     *                @param id  id标志
     */
    public void onBefore(Request request, int id) {
    }

    /**
     * UI Thread
     * @param id id标志
     */
    public void onAfter(int id) {
    }

    /**
     * UI Thread
     *
     * @param progress 进度
     *                 @param total 总长度
     *                              @param id id标志
     */
    public void inProgress(int progress, long total, int id) {

    }

    /**
     * if you parse reponse code in parseNetworkResponse, you should make this method return true.
     *
     * @param response 响应体
     *                 @param id id标志
     * @return 返回是否验证
     */
    public boolean validateReponse(Response response, int id) {
        return response.isSuccessful();
    }

    /**
     * Thread Pool Thread
     * @param response 响应体
     *                 @param id id标志
     *                           @throws Exception 抛出异常
     *                           @return 相应内容
     */
    public abstract T parseNetworkResponse(Response response, int id) throws Exception;

    public abstract void onError(Call call, Exception e, int id);

    public abstract void onResponse(T response, int id);


    public static Callback CALLBACK_DEFAULT = new Callback() {

        @Override
        public Object parseNetworkResponse(Response response, int id) throws Exception {
            return null;
        }

        @Override
        public void onError(Call call, Exception e, int id) {

        }

        @Override
        public void onResponse(Object response, int id) {

        }
    };

}