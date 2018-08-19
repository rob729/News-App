package com.example.robin.news;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class MainActivity extends AppCompatActivity {

    RecyclerView rv;
    ArrayList<News> newsArrayList;
    SwipeRefreshLayout pullToRefresh;
    ShimmerLayout shimmerText;
    LinearLayout ll2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        shimmerText = findViewById(R.id.shimmer_recycler_view);
        shimmerText.startShimmerAnimation();
        rv = findViewById(R.id.rv);
        ll2 = findViewById(R.id.ll2);
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
            shimmerText.stopShimmerAnimation();
            ll2.setVisibility(View.GONE);
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
            shimmerText.stopShimmerAnimation();
            ll2.setVisibility(View.GONE);

//
//            result.getStatus();
//            result.getTotalResults();
        }

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm != null && cm.getActiveNetworkInfo() != null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()){
            case R.id.about:
                Intent nextpage = new Intent(this,About.class);
                startActivity(nextpage);

        }
        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

}
