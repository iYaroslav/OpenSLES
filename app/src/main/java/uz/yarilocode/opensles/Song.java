package uz.yarilocode.opensles;

import uz.yarilocode.opensles.utils.Strings;

/**
 * Created by Yaroslav on 10.01.16.
 * Copyright 2016 iYaroslav LLC.
 */
public class Song {

	public final int id;
	public final String title;
	public final String artist;
	public final String path;

	public Song(String title, String artist, String path) {
		this.title = title;
		this.artist = artist;
		this.path = path;

		id = Strings.hash(path);
	}
}
