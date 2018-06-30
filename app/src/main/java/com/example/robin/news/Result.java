package com.example.robin.news;

import java.util.ArrayList;

public class Result {

    private String status;
    private Integer totalResults;
    private ArrayList<News> news;

    public Result(String status, Integer totalResults, ArrayList<News> news) {
        this.status = status;
        this.totalResults = totalResults;
        this.news = news;
    }

    public String getStatus() {
        return status;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public ArrayList<News> getNews() {
        return news;
    }
}
