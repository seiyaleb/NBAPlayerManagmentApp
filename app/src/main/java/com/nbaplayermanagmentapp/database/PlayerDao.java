package com.nbaplayermanagmentapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Ignore;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PlayerDao {

    //最新順でのデータ取得
    @Query("select * from Player order by created desc")
    @Ignore
    List<Player> getSortLatest();

    //アイウエオ順でのデータ取得
    @Query("select * from Player order by name")
    @Ignore
    List<Player> getSortName();

    //IDを元にしたデータ取得
    @Query("select * from Player where id = :playerid")
    Player findById(int playerid);

    //データ挿入
    @Insert
    void insert(Player player);

    //データ更新
    @Update
    void update(Player player);

    //データ削除
    @Delete
    void delete(Player player);
}
