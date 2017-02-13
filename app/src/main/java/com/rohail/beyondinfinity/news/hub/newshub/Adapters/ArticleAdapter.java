package com.rohail.beyondinfinity.news.hub.newshub.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rohail.beyondinfinity.news.hub.newshub.R;
import com.rohail.beyondinfinity.news.hub.newshub.models.Articles;
import com.squareup.picasso.Picasso;

/**
 * Created by rohail on 2/10/2017.
 */

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.MyViewHolder> {

    private Context context;
    private Articles[] sourcesArrayList;
    private IOnItemClickCustomListner iOnItemClickCustomListner;

    public ArticleAdapter(Context context, Articles[] sources, IOnItemClickCustomListner listner) {
        this.context = context;
        this.sourcesArrayList = sources;
        iOnItemClickCustomListner = listner;
    }

    @Override
    public int getItemCount() {
        return sourcesArrayList.length;
    }

    @Override
    public void onBindViewHolder(ArticleAdapter.MyViewHolder holder, final int position) {

        holder.tvTitle.setText(sourcesArrayList[position].getTitle());

        Picasso.with(context).load(sourcesArrayList[position].getUrlToImage())
                .placeholder(R.mipmap.stub).into(holder.icon);

        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iOnItemClickCustomListner.onItemClicked(view, position);
            }
        });

    }


    @Override
    public ArticleAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_item, parent, false);
        return new ArticleAdapter.MyViewHolder(itemView);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView icon;
        public TextView tvTitle;
        public RelativeLayout llMain;

        public MyViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.imageView);
            tvTitle = (TextView) itemView.findViewById(R.id.title);
            llMain = (RelativeLayout) itemView.findViewById(R.id.rl_parent);
        }
    }

}
