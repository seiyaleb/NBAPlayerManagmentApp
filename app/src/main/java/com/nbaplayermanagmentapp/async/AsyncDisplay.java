package com.nbaplayermanagmentapp.async;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
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

public class AsyncDisplay {

    private AppDatabase _db;
    private ListView _lv_player;
    private Activity _activity;
    private String _message;

    public AsyncDisplay(AppDatabase db,ListView lv_player,Activity activity,String message) {
        _db = db;
        _lv_player = lv_player;
        _activity = activity;
        _message = message;
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

            //渡された文字列を元に、最新順か名前順で表示するリスト生成
            List<Player> playerList;
            if(_message.equals("default")) {

                playerList = playerDao.getSortLatest();

            } else {

                playerList = playerDao.getSortName();
            }

            //非同期で処理を戻すことを可能に
            PostExecutor postExecutor = new PostExecutor(playerList);
            _handler.post(postExecutor);
        }
    }

    //UIスレッドで行う処理を定義するメンバクラス
    private class PostExecutor implements Runnable {

        private List<Player> _playerList;

        public PostExecutor(List<Player> playerList) {

            _playerList = playerList;
        }

        @UiThread
        @Override
        public void run() {

            List<Map<String, String>> list_player_data = new ArrayList<>();

            for (int i = 0; i < _playerList.size(); i++) {
                Map<String, String> map_player = new HashMap<>();
                map_player.put("id", String.valueOf(_playerList.get(i).getId()));
                map_player.put("name", _playerList.get(i).getName());
                list_player_data.add(map_player);
            }

            //SimpleAdapter生成
            String[] from = {"name"};
            int[] to = {android.R.id.text1};
            SimpleAdapter adapter = new SimpleAdapter(_activity, list_player_data,
                    android.R.layout.simple_list_item_1, from, to);

            //リストビューにAdapter設定
            _lv_player.setAdapter(adapter);
        }
    }
}
