package com.nbaplayermanagmentapp;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nbaplayermanagmentapp.async.AsyncDisplay;
import com.nbaplayermanagmentapp.database.AppDatabase;
import com.nbaplayermanagmentapp.database.AppDatabaseSingleton;
import com.nbaplayermanagmentapp.database.Player;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private AppDatabase db;
    private ListView lv_player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //データベースを呼び出し
        db = AppDatabaseSingleton.getInstance(MainActivity.this);

        //ViewModelインスタンス作成
        //IdViewModel viewModel = new ViewModelProvider(this).get(IdViewModel.class);

        lv_player = findViewById(R.id.lv_player);

        //フローティングアクションボタンを押した時
        //選手追加画面へ遷移
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, InsertActivity.class);
                startActivity(intent);
            }
        });

        //リストの一行分がタップされた時
        //データのidを取得し、ViewModelに格納
        //選手詳細画面へ遷移
        lv_player.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Map<String,String> item = (Map<String,String>) parent.getItemAtPosition(position);
                String data_id = (String)item.get("id");

                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("id",Integer.valueOf(data_id));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //非同期処理（リスト表示）
        AsyncDisplay asyncDisplay = new AsyncDisplay(db,lv_player,this,"default");
        asyncDisplay.asyncExecute();
    }

    //オプションメニュー表示
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //オプションメニューが選択された時
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {

            //最新順のメニューが選択された時
            case R.id.menu_sort_insert:

                //非同期処理（デフォルトの並び替え）
                AsyncDisplay asyncDisplay = new AsyncDisplay(db,lv_player,this,"default");
                asyncDisplay.asyncExecute();

                //アイウエオ順のメニューが選択された時
            case R.id.menu_sort_abc:

                //非同期処理（アイウエオ順に並び替え）
                asyncDisplay = new AsyncDisplay(db,lv_player,this,"abc");
                asyncDisplay.asyncExecute();
        }

        return super.onOptionsItemSelected(item);
    }
}