package com.xinbo.httptask;

import android.app.Application;

public class TaskApplication extends Application {

    private static TaskApplication taskApplication;

    public static TaskApplication getInstance(){
        return taskApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        taskApplication = this;
    }
}
