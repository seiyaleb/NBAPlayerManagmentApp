package com.nbaplayermanagmentapp.async;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.core.os.HandlerCompat;

import com.nbaplayermanagmentapp.database.AppDatabase;
import com.nbaplayermanagmentapp.database.Player;
import com.nbaplayermanagmentapp.database.PlayerDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncDetail {

    private AppDatabase _db;
    private int _id;
    private EditText _et_player;
    private Spinner _spinner;
    private EditText _et_memo;


    public AsyncDetail(AppDatabase db,int id,EditText et_player,Spinner spinner,EditText et_memo) {
        _db = db;
        _id = id;
        _et_player = et_player;
        _spinner = spinner;
        _et_memo = et_memo;
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

            //idを元にデータ１件取得
            PlayerDao playerDao = _db.playerDao();
            Player player = playerDao.findById(_id);

            //非同期で処理を戻すことを可能に
            PostExecutor postExecutor = new PostExecutor(player);
            _handler.post(postExecutor);
        }
    }

    //UIスレッドで行う処理を定義するメンバクラス
    private class PostExecutor implements Runnable {

        private Player _player;

        public PostExecutor(Player player) {

            _player = player;
        }

        @UiThread
        @Override
        public void run() {

            //選手名、ポジション、メモを取得
            String name = _player.getName();
            String memo = _player.getMemo();

            String position = _player.getPosition();
            int position_index = 0;
            switch (position) {
                case "ガード":
                    position_index = 0;
                    break;
                case "フォワード":
                    position_index = 1;
                    break;
                case "センター":
                    position_index = 2;
                    break;

            }

            //Viewに反映
            _et_player.setText(name);
            _spinner.setSelection(position_index);
            _et_memo.setText(memo);
        }
    }
}
