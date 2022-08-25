package com.example.android.bakingapp.ui;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Class replaces the deprecated AsyncTask
 */
public class TaskRunner {
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    public interface Callback<R> {
        void onComplete(R result);
    }

    public <R> void executeAsync(Callable<R> callable, Callback<R> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                R result = null;
                try {
                    result = callable.call();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    final R finalResult = result;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onComplete(finalResult);
                        }
                    });
                }
            }
        });
    }
}