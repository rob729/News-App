package com.example.robin.news;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private Context ctx;
    private ArrayList<News> newsArrayList ;

    public NewsAdapter(Context ctx, ArrayList<News> newsArrayList) {
        this.ctx = ctx;
        this.newsArrayList = newsArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(ctx);
        View inflatedView = li.inflate(R.layout.item_row,parent,false);
        ViewHolder vh= new ViewHolder(inflatedView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        News n = newsArrayList.get(position);
        holder.title.setText(n.getTitle());
        holder.detail.setText(n.getDesc());
        Picasso.get().load(n.getImageUrl()).into(holder.img);
    }

    @Override
    public int getItemCount() {
        return newsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title,detail;
        ImageView img;
        public ViewHolder(View inflatedView) {
            super(inflatedView);
            title = inflatedView.findViewById(R.id.title);
            detail = inflatedView.findViewById(R.id.detail);
            img = inflatedView.findViewById(R.id.img);
            inflatedView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(newsArrayList.get(getAdapterPosition()).getUrl()));
                    ctx.startActivity(i);
                }
            });

        }
    }
}
