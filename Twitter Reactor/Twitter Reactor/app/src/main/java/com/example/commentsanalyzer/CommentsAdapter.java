package com.example.commentsanalyzer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    private ArrayList<Comment> data;
    private ArrayList<Comment> allData;
    private Context context;

    public CommentsAdapter(Context context, ArrayList<Comment> data, ArrayList<Comment> allData) {
        this.context = context;
        this.data = data;
        this.allData = allData;
    }

    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.comment_list_item, parent, false);
        return new CommentsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.ViewHolder holder, int position) {
        final Comment comment = data.get(position);
        Picasso.get().load(comment.getProfilePicUrl()).fit().centerCrop().into(holder.profilePic);
        holder.name.setText(comment.getName());
        holder.fullText.setText(comment.getFullText());
        holder.container.setCardBackgroundColor(comment.getPrediction()==1 ? Color.parseColor("#24fc03") : Color.parseColor("#fc2c03"));
        holder.showMore.setVisibility(View.GONE);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long id = comment.getId();
                context.startActivity(new Intent(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/twitter/statuses/" + id))));
            }
        });
        if(position==data.size()-1 && allData.size() != data.size())
            holder.showMore.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() { return data.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        CardView container;
        ImageView profilePic;
        TextView name;
        TextView fullText;
        Button showMore;

        ViewHolder(View itemView) {
            super(itemView);
            this.profilePic = itemView.findViewById(R.id.item_profile_pic);
            this.name = itemView.findViewById(R.id.item_name);
            this.fullText = itemView.findViewById(R.id.item_full_text);
            this.showMore = itemView.findViewById(R.id.show_more);
            this.container = itemView.findViewById(R.id.listitem_container);

            showMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RVAllItemsFragment fragment = new RVAllItemsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("all_comments_list", allData);
                    fragment.setArguments(bundle);
                    ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                }
            });

        }
    }
}
