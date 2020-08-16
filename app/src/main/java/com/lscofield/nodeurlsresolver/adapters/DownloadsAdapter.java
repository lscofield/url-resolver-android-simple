package com.lscofield.nodeurlsresolver.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.lscofield.nodeurlsresolver.R;
import com.lscofield.nodeurlsresolver.activities.VideoPlayer;

import java.util.List;

import ir.siaray.downloadmanagerplus.classes.Downloader;
import ir.siaray.downloadmanagerplus.enums.DownloadStatus;
import ir.siaray.downloadmanagerplus.model.DownloadItem;

public class DownloadsAdapter extends RecyclerView.Adapter<DownloadsAdapter.MyViewHolder>{

    private List<DownloadItem> mDataset;
    private Context mContext;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle, mDwInfo;
        public ImageView mDelete, mPause, mPlay;
        public CardView mItem;
        public ProgressBar mDownProgress;

        public MyViewHolder(View view, Context mContext) {
            super(view);
            mTitle = (TextView) view.findViewById(R.id.textView5);
            mDwInfo = (TextView) view.findViewById(R.id.percentage);
            mDelete = (ImageView) view.findViewById(R.id.imageView10);
            mPause = (ImageView) view.findViewById(R.id.imageView11);
            mPlay = (ImageView) view.findViewById(R.id.imageView12);
            mItem = (CardView) view.findViewById(R.id.click_me);
            mDownProgress = (ProgressBar) view.findViewById(R.id.dw_progress);
        }
    }

    public DownloadsAdapter(List<DownloadItem> data, Context context) {
        mDataset = data;
        mContext = context;
        setHasStableIds(true);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DownloadsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        return new MyViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.download_item,
                                parent,
                                false), mContext);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DownloadItem item = mDataset.get(position);
        holder.mTitle.setText(item.getTitle());
        holder.mDownProgress.setProgress(item.getPercent());
        holder.mDwInfo.setText(item.getPercent()
                + "% (" +  Math.round((item.getDownloadedBytes()/1e+6))
                + "MB/" +  Math.round((item.getTotalBytes()/1e+6)) + "MB)");

        if (Downloader.getInstance(mContext).getStatus(item.getToken())
                == DownloadStatus.PAUSED){
            notifyPausedResumed(true, holder);
        }

        if (Downloader.getInstance(mContext).getStatus(item.getToken())
                == DownloadStatus.RUNNING) {
            notifyPausedResumed(false, holder);
        }

        holder.mPause.setOnClickListener(v -> Downloader.pause(mContext, item.getToken()));
        holder.mPlay.setOnClickListener(v -> Downloader.resume(mContext, item.getToken()));

        holder.mItem.setOnClickListener(v -> mContext.startActivity(
                new Intent(mContext, VideoPlayer.class)
                        .putExtra("title", item.getTitle())
                        .putExtra("url", item.getFilePath())
                        .putExtra("referer", "")
                        .putExtra("stream", false)));
        holder.mDelete.setOnClickListener(v -> {
            Downloader.getInstance(mContext).cancel(item.getToken());
            mDataset.remove(item);
            notifyDataSetChanged();
        });
    }

    private void notifyPausedResumed(boolean paused, MyViewHolder h){
        if (paused){
            h.mPause.setVisibility(View.GONE);
            h.mPlay.setVisibility(View.VISIBLE);
        }else{
            h.mPlay.setVisibility(View.GONE);
            h.mPause.setVisibility(View.VISIBLE);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
