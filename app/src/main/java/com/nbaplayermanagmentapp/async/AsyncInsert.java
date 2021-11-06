package com.nbaplayermanagmentapp.async;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.core.os.HandlerCompat;

import com.nbaplayermanagmentapp.database.AppDatabase;
import com.nbaplayermanagmentapp.database.Player;
import com.nbaplayermanagmentapp.database.PlayerDao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncInsert {

    private AppDatabase _db;
    private String _player;
    private String _position;
    private String _memo;

    public AsyncInsert(AppDatabase db,String player,String position,String memo) {
        _db = db;
        _player = player;
        _position = position;
        _memo = memo;
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
            //選手登録
            Player player = new Player(_player,_position,_memo,new Timestamp(System.currentTimeMillis()).toString());
            playerDao.insert(player);

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
            Log.i("ログ確認","追加処理が成功しました。");
        }
    }
}
