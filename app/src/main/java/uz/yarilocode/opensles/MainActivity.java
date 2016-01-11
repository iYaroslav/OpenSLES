package uz.yarilocode.opensles;

import android.Manifest;
import android.content.ContentResolver;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.h6ah4i.android.media.IBasicMediaPlayer;
import com.h6ah4i.android.media.IMediaPlayerFactory;
import com.h6ah4i.android.media.opensl.OpenSLMediaPlayerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yaroslav on 10.01.16.
 * Copyright 2016 iYaroslav LLC.
 */
public class MainActivity extends AppCompatActivity {

	private static final int PERMISSIONS_READ_EXTERNAL_STORAGE = 0x00F;

	private static String TAG = "OpenSL";

	private SwipeRefreshLayout swipeRefreshLayout;
	private RecyclerViewAdapter adapter;

	private IMediaPlayerFactory mFactory;
	private IBasicMediaPlayer[] players = new IBasicMediaPlayer[2];
	private int currentPlayer = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		setContentView(R.layout.activity_main);

		initList();
		initPlayers();
		viewById(R.id.coordinator_layout).setKeepScreenOn(true);

		checkExternalStoragePermissions();
	}

	private void initPlayers() {
		mFactory = new OpenSLMediaPlayerFactory(this);

		for (int i = 0; i < players.length; i++) {
			IBasicMediaPlayer player = mFactory.createMediaPlayer();

//			player.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
//			player.setOnCompletionListener(mOnCompletionListener);
//			player.setOnErrorListener(mOnErrorListener);
//			player.setOnInfoListener(mOnInfoListener);
//			player.setOnPreparedListener(mOnPreparedListener);
//			player.setOnSeekCompleteListener(mOnSeekCompleteListener);

			players[i] = player;
		}
	}

	private void initList() {
		swipeRefreshLayout = viewById(R.id.refresh);
		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

			@Override
			public void onRefresh() {
				findMusic();
			}

		});
		swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorAccent));
		swipeRefreshLayout.setRefreshing(true);

		adapter = new RecyclerViewAdapter(this);
		adapter.setOnItemClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Song song = adapter.getSelectedSong();

				if (currentPlayer >= 0) {
					players[currentPlayer].stop();
					players[currentPlayer].release();
					currentPlayer++;
					if (currentPlayer > players.length - 1)
						currentPlayer = 0;
				} else {
					currentPlayer = 0;
				}

				IBasicMediaPlayer player = players[currentPlayer];
				try {
					player.setDataSource(song.path);
					player.prepare();
					player.setVolume(1f, 1f);
					player.start();
				} catch (IOException e) {
					e.printStackTrace();
				}

//				try {
//					player.setDataSource(song.path);
//					player.prepare();
//					player.start();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
			}
		});

		RecyclerView recyclerView = viewById(R.id.recycler_view);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

//		int spanCount = getResources().getInteger(R.integer.card_span_count);
//		layoutManager = new RecyclerView.Adapter<>(
//				spanCount,
//				StaggeredGridLayoutManager.VERTICAL);

//		recyclerView.setLayoutManager(layoutManager);

//		itemDecoration = new ItemDecoration(getContext(), spanCount);
//		recyclerView.addItemDecoration(itemDecoration);
	}

	public void findMusic() {
		ContentResolver contentResolver = getContentResolver();

		Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
		String sortOrder = MediaStore.Audio.Media.ARTIST + " ASC, " + MediaStore.Audio.Media.TITLE + " ASC";
		Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);
		int count;

		if(cursor != null) {
			count = cursor.getCount();
			swipeRefreshLayout.setRefreshing(false);

			List<Song> songs = new ArrayList<>();
			if(count > 0) {

				while(cursor.moveToNext()) {
					Song song = new Song(
							cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
							cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
							cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
					);

					songs.add(song);
				}
			}

			Log.e(TAG, "Songs count: " + songs.size());
			adapter.setSongs(songs);
			swipeRefreshLayout.setRefreshing(false);
			cursor.close();
		}
	}

	public <T extends View> T viewById(int id) {
		//noinspection unchecked
		return (T) super.findViewById(id);
	}

	@Override
	protected void onPause() {
		for (int i = 0; i < players.length; i++) {
			IBasicMediaPlayer player = players[i];

			if (player != null) {
				try {
					player.release();
				} catch (Exception ignored) {}

				players[i] = null;
			}
		}

		super.onPause();
	}

	private void readExternalStorageGranted() {
		new Thread() {

			@Override
			public void run() {
				super.run();
				findMusic();
			}

		}.run();
	}
	private void checkExternalStoragePermissions() {
		switch (PermissionsService.check(this, Manifest.permission.READ_EXTERNAL_STORAGE, PERMISSIONS_READ_EXTERNAL_STORAGE)) {
			case TRUE:
				readExternalStorageGranted();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
		switch (requestCode) {
			case PERMISSIONS_READ_EXTERNAL_STORAGE: {
				if (PermissionsService.checkResults(grantResults)) {
					readExternalStorageGranted();
				}
			}
		}
	}

//	private void createPlayerThread(){
//		Runnable player = new Runnable() {
//
//			@Override
//			public void run() {
//				boolean running = true;
//				while(running){
//					if (playing) {
//						synchronized(MainActivity.this){
//							playRandomSound();
//						}
//					}
//
//					try {
//						Thread.sleep((int)(Math.random() * MAX_PLAYER_GAP));
//					} catch (InterruptedException e) {
//						running = false;
//					}
//				}
//			}
//		};
//
//		Thread t = new Thread(player);
//		t.start();
//	}
}
