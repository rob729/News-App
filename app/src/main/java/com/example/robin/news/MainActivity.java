package com.example.robin.news;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    RecyclerView rv;
    ArrayList<News> newsArrayList;
    SwipeRefreshLayout pullToRefresh;
    ShimmerRecyclerView shimmerRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        shimmerRecycler = findViewById(R.id.shimmer_recycler_view);
        shimmerRecycler.showShimmerAdapter();
        rv = findViewById(R.id.rv);
        pullToRefresh = findViewById(R.id.pullToRefresh);
        rv.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        if(isNetworkConnected()){
            NewsApplication.getDb().clearAllTables();
            MyNetworkTask myNetworkTask = new MyNetworkTask();
            myNetworkTask.execute("https://newsapi.org/v2/top-headlines?sources=techcrunch&apiKey=4663b6001744472eaac1f5aa16076a7a");



            pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    NewsApplication.getDb().clearAllTables();
                    MyNetworkTask myNetworkTask = new MyNetworkTask();
                    myNetworkTask.execute("https://newsapi.org/v2/top-headlines?sources=techcrunch&apiKey=4663b6001744472eaac1f5aa16076a7a");
                    pullToRefresh.setRefreshing(false);
                }
            });

        }
        else{
            shimmerRecycler.hideShimmerAdapter();
            newsArrayList = (ArrayList<News>) NewsApplication.getDb().getNewsDao().getAllNews();
            NewsAdapter newsAdapter = new NewsAdapter(MainActivity.this, newsArrayList);
            rv.setAdapter(newsAdapter);

        }



    }

    public Result convertJsonToResponse(String json) {

        try {
            JSONObject jsonObject = new JSONObject(json);
            String status = jsonObject.getString("status");
            Integer totalResults = jsonObject.getInt("totalResults");

            JSONArray jsonArray = jsonObject.getJSONArray("articles");


            newsArrayList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject articleObject = jsonArray.getJSONObject(i);
                //String author = articleObject.getString("author");
                String title = articleObject.getString("title");
                String desc = articleObject.getString("description");
                String url = articleObject.getString("url");
                String imageUrl = articleObject.getString("urlToImage");
                //String published = articleObject.getString("publishedAt");

//                JSONObject source = articleObject.getJSONObject("source");
//                String id = source.getString("id");
//                String name = source.getString("name");
//                Source sourceJava = new Source(id, name);

                News news = new News(title, desc, url, imageUrl);

                newsArrayList.add(news);
                NewsApplication.getDb().getNewsDao().insertNews(news);

            }

            Result result = new Result(status, totalResults, newsArrayList);

            return result;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }


    class MyNetworkTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String string = strings[0];

            try {
                URL url = new URL(string);

                //Open a new Connection using the URL
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                //Store the contents of the web-page as a Stream
                InputStream inputStream = httpURLConnection.getInputStream();

                //Create a Scanner from the Stream to get data in a human readable form
                Scanner scanner = new Scanner(inputStream);

                //Tells the scanner to read the file from the very start to the very end of file
                scanner.useDelimiter("\\A");

                String result = "";

                if (scanner.hasNext()) {
                    //Read the entire content of scanner in a go, otherwise scanner reads individual bytes one by one
                    result = scanner.next();
                }
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // textView.setText(s);
            Result result = convertJsonToResponse(s);

            ArrayList<News> newsArrayList1 = result.getNews();
            NewsAdapter newsAdapter = new NewsAdapter(MainActivity.this, newsArrayList1);
            rv.setAdapter(newsAdapter);
            shimmerRecycler.hideShimmerAdapter();

//
//            result.getStatus();
//            result.getTotalResults();
        }

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm != null && cm.getActiveNetworkInfo() != null;
    }
}
