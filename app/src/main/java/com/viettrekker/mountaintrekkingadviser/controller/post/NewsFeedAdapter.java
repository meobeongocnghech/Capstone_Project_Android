package com.viettrekker.mountaintrekkingadviser.controller.post;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.viettrekker.mountaintrekkingadviser.R;
import com.viettrekker.mountaintrekkingadviser.controller.notification.NotificationAdapter;

import org.w3c.dom.Text;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.ViewHolder> {

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_post_item,viewGroup,false);
        return new NewsFeedAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPostAvatar;
        TextView tvPostUserName;
        TextView tvTime;
        TextView tvPostCategory;
        ImageButton btnPostOption;
        TextView tvPostTitle;
        TextView tvPostContent;
        MaterialButton btnReadMore;
        GridLayout gridPostPicture;
        ImageView imgPreview1;
        ImageView imgPreview2;
        ImageView imgPreview3;
        ImageView imgPreview4;
        TextView tvCount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPostAvatar = (ImageView) itemView.findViewById(R.id.imgPostAvatar);
            tvPostUserName = (TextView) itemView.findViewById(R.id.tvPostUserName);
            TextView tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            TextView tvPostCategory = (TextView) itemView.findViewById(R.id.tvPostCategory);
            ImageButton btnPostOption = (ImageButton) itemView.findViewById(R.id.btnPostOption);
            TextView tvPostTitle = (TextView) itemView.findViewById(R.id.tvPostTitle);
            TextView tvPostContent = (TextView) itemView.findViewById(R.id.tvPostContent);
            MaterialButton btnReadMore = (MaterialButton) itemView.findViewById(R.id.btnReadMore);
            GridLayout gridPostPicture = (GridLayout) itemView.findViewById(R.id.gridPostPicture);
            ImageView imgPreview1 = (ImageView) itemView.findViewById(R.id.imgPreview1);
            ImageView imgPreview2 = (ImageView) itemView.findViewById(R.id.imgPreview2);
            ImageView imgPreview3 = (ImageView) itemView.findViewById(R.id.imgPreview3);
            ImageView imgPreview4 = (ImageView) itemView.findViewById(R.id.imgPreview4);
            TextView tvCount = (TextView) itemView.findViewById(R.id.tvCount);
        }
    }
}
