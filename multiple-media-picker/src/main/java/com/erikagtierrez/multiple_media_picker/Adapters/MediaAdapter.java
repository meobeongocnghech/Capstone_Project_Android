package com.erikagtierrez.multiple_media_picker.Adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.erikagtierrez.multiple_media_picker.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MyViewHolder>{
    private List<String> bitmapList;
    private List<Boolean> selected;
    private Context context;
    private int width;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail,check;

        public MyViewHolder(View view) {
            super(view);
            thumbnail=(ImageView) view.findViewById(R.id.image);
            check=(ImageView) view.findViewById(R.id.image2);
        }
    }

    public MediaAdapter(List<String> bitmapList,List<Boolean> selected, Context context) {
        this.bitmapList = bitmapList;
        this.context=context;
        this.selected=selected;
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Picasso.get().load("file://"+bitmapList.get(position)).resize(width/3,width/3).centerCrop().memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.thumbnail);

        if(selected.get(position).equals(true)){
            holder.check.setVisibility(View.VISIBLE);
            holder.check.setAlpha(150);
        }else{
            holder.check.setVisibility(View.GONE);
        }

    }

    @Override
   public int getItemCount() {
        return bitmapList.size();
    }
}

