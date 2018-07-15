package com.example.robin.news;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.example.robin.news.db.NewsDatabase;

public class NewsApplication extends Application {

    static NewsDatabase newsDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        newsDatabase = Room.databaseBuilder(getApplicationContext(),
                NewsDatabase.class,
                "news.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
    }
    public  static  NewsDatabase getDb(){
        return newsDatabase;
    }
}
