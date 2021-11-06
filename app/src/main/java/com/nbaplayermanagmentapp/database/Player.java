package com.nbaplayermanagmentapp.database;

import androidx.room.*;

import java.sql.Timestamp;

@Entity
public class Player {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    private String name;

    @ColumnInfo
    private String position;

    @ColumnInfo
    private String memo;

    @ColumnInfo
    private String created;

    public Player(String name,String position,String memo,String created) {

        this.name = name;
        this.position = position;
        this.memo = memo;
        this.created = created;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPosition() {
        return position;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getMemo() {
        return memo;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getCreated() {
        return created;
    }
}
