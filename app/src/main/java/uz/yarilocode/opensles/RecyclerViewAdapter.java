package uz.yarilocode.opensles;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yaroslav on 10.01.16.
 * Copyright 2016 iYaroslav LLC.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

	private List<Song> songs;
	private final Context context;
	private int selectedItem = -1;

	private View.OnClickListener onClickListener;

	public RecyclerViewAdapter(Context context) {
		this.context = context;
		songs = new ArrayList<>();
		setHasStableIds(true);
	}

	public void setSongs(List<Song> songs) {
		this.songs = songs;
		notifyDataSetChanged();
	}

	public void selectNext() {
		int lastItem = selectedItem;

		selectedItem++;
		if (selectedItem > songs.size() - 1)
			selectedItem = 0;

		notifyItemChanged(selectedItem);
		notifyItemChanged(lastItem);

		if (onClickListener != null)
			onClickListener.onClick(null);
	}

	public void setOnItemClickListener(View.OnClickListener listener) {
		onClickListener = listener;
	}

	public Song getSelectedSong() {
		return songs.get(selectedItem);
	}

	@Override
	public long getItemId(int position) {
		return songs.get(position).id;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.song_item, viewGroup, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int position) {
		viewHolder.update(songs.get(position), position);
	}

	@Override
	public int getItemCount() {
		return songs.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		private final TextView title;
		private final TextView artist;
		private final View container;

		public ViewHolder(View itemView) {
			super(itemView);
			container = itemView.findViewById(R.id.container);
			title = (TextView) itemView.findViewById(R.id.title);
			artist = (TextView) itemView.findViewById(R.id.artist);
		}

		public void update(Song song, final int position) {
			title.setText(song.title);
			artist.setText(song.artist);

			container.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {
					int lastItem = selectedItem;
					selectedItem = position;
					notifyItemChanged(lastItem);

					container.setBackgroundColor(
							ContextCompat.getColor(context, R.color.colorHighlight));

					if (onClickListener != null)
						onClickListener.onClick(view);
				}
			});

			if (selectedItem == position) {
				container.setBackgroundColor(
						ContextCompat.getColor(context, R.color.colorHighlight));
			} else {
				container.setBackgroundColor(
						ContextCompat.getColor(context, android.R.color.transparent));
			}
		}
	}
}