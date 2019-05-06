package com.xinbo.httptask.builder;


import com.xinbo.httptask.OkHttpUtils;
import com.xinbo.httptask.request.OtherRequest;
import com.xinbo.httptask.request.RequestCall;

/**
 * Created by zhy on 16/3/2.
 */
public class HeadBuilder extends GetBuilder
{
    @Override
    public RequestCall build()
    {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, params, headers,id).build();
    }
}
