package com.example.emil.musicplayer;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Emil on 06/09/2017.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongHolder> {
    ArrayList<SongInfo> songs;
    Context context;

    OnitemClickListener onItemClickListener;

    SongAdapter(Context context,ArrayList<SongInfo> songs) {
        this.context = context;
        this.songs = songs;
    }

    public interface OnitemClickListener {
        void onItemClick(Button b, View v, SongInfo obj, int position);
    }

    public void setOnItemClickListener(OnitemClickListener onItemClickListener)
    {
        this.onItemClickListener = onItemClickListener;
    }

    public SongHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View myview = LayoutInflater.from(context).inflate(R.layout.row_song,viewGroup, false);
        return new SongHolder(myview);
    }

    @Override
    public void onBindViewHolder(final SongHolder songHolder, final int i) {
        final SongInfo c = songs.get(i);
        songHolder.songName.setText(c.SongName);
        songHolder.artistName.setText(c.artistName);
        songHolder.btnAction.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (onItemClickListener != null)
                {
                    onItemClickListener.onItemClick(songHolder.btnAction,v,c,i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public class SongHolder extends RecyclerView.ViewHolder {

        TextView songName, artistName;
        Button btnAction;

        public SongHolder(View itemView) {
            super(itemView);
            songName = (TextView) itemView.findViewById(R.id.tvSongName);
            artistName = (TextView) itemView.findViewById(R.id.tvArtistName);
            btnAction = (Button) itemView.findViewById(R.id.btnAction);
        }
    }
}
