package com.nbaplayermanagmentapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.nbaplayermanagmentapp.async.AsyncDelete;
import com.nbaplayermanagmentapp.database.AppDatabase;
import com.nbaplayermanagmentapp.database.AppDatabaseSingleton;
import com.nbaplayermanagmentapp.viewmodel.IdViewModel;

public class DetailActivity extends AppCompatActivity {

    private AppDatabase db;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //ViewModelインスタンス作成
        IdViewModel viewModel = new ViewModelProvider(this).get(IdViewModel.class);

        //Intent経由で取得したidをViewModelに格納
        Intent intent = getIntent();
        id = intent.getIntExtra("id",0);
        viewModel.getId().setValue(id);

        //データベースを呼び出し
        db = AppDatabaseSingleton.getInstance(DetailActivity.this);

        //戻るメニュー表示
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    //オプションメニュー表示
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    //オプションメニューが選択された時
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        switch (itemId) {

            //戻るメニューが選択された時
            //選手一覧画面へ戻る
            case android.R.id.home:

                finish();
                break;

                //削除メニューが選択された時
                //ダイアログ表示
            case R.id.menu_delete:

                new AlertDialog.Builder(DetailActivity.this)
                        .setTitle(R.string.dialog_title)
                        .setMessage(R.string.dialog_content)
                        //削除がOKな場合
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //非同期処理（削除）
                                AsyncDelete asyncDelete = new AsyncDelete(db,id);
                                asyncDelete.asyncExecute();

                                //選手一覧画面へ戻る
                                finish();
                            }
                        })
                        .show();

                break;
        }

        return super.onOptionsItemSelected(item);
    }
}