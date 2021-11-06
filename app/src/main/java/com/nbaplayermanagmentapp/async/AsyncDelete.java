package com.nbaplayermanagmentapp.async;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.core.os.HandlerCompat;

import com.nbaplayermanagmentapp.database.AppDatabase;
import com.nbaplayermanagmentapp.database.Player;
import com.nbaplayermanagmentapp.database.PlayerDao;

import java.sql.Timestamp;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncDelete {

    private AppDatabase _db;
    private int _id;

    public AsyncDelete(AppDatabase db, int id) {
        _db = db;
        _id = id;
    }

    //非同期処理の開始メソッド
    @UiThread
    public void asyncExecute() {
        Looper mainLooper = Looper.getMainLooper();
        Handler handler = HandlerCompat.createAsync(mainLooper);
        BackgroundTask backgroundTask = new BackgroundTask(handler);
        ExecutorService executorService  = Executors.newSingleThreadExecutor();
        executorService.submit(backgroundTask);
    }

    //バックグラウンドスレッドで行う処理を定義するメンバクラス
    private class BackgroundTask implements Runnable {
        private final Handler _handler;

        public BackgroundTask(Handler handler) {
            _handler = handler;
        }

        @WorkerThread
        @Override
        public void run() {

            PlayerDao playerDao = _db.playerDao();

            //idを元に1件検索した選手データを削除
            Player player = playerDao.findById(_id);
            playerDao.delete(player);

            //非同期で処理を戻すことを可能に
            PostExecutor postExecutor = new PostExecutor();
            _handler.post(postExecutor);
        }
    }

    //UIスレッドで行う処理を定義するメンバクラス
    private class PostExecutor implements Runnable {

        @UiThread
        @Override
        public void run() {
            Log.i("ログ確認","削除処理が成功しました。");
        }
    }
}
