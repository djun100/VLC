/*****************************************************************************
 * AudioListActivity.java
 *****************************************************************************
 * Copyright © 2011-2012 VLC authors and VideoLAN
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/

package org.videolan.vlc.gui.audio;

import java.util.ArrayList;
import java.util.Collections;
import org.videolan.libvlc.Media;
import org.videolan.vlc.AudioServiceController;
import org.videolan.vlc.R;
import org.videolan.vlc.Util;
import org.videolan.vlc.gui.MainActivity;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import com.actionbarsherlock.app.SherlockFragment;

public class AudioAlbumsSongsFragment extends SherlockFragment {

    public final static String TAG = "VLC/AudioAlbumsSongsFragment";

    AudioServiceController mAudioController;

    private AudioBrowserListAdapter mSongsAdapter;
    private AudioBrowserListAdapter mAlbumsAdapter;

    public final static String EXTRA_NAME = "name";
    public final static String EXTRA_NAME2 = "name2";
    public final static String EXTRA_MODE = "mode";

    private ArrayList<Media> mediaList;

    private static final MediaComparators mComparators = new MediaComparators();

    /* All subclasses of Fragment must include a public empty constructor. */
    public AudioAlbumsSongsFragment() { }

    public AudioAlbumsSongsFragment(ArrayList<Media> mediaList) {
        this.mediaList = mediaList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAlbumsAdapter = new AudioBrowserListAdapter(getActivity(), AudioBrowserListAdapter.ITEM_WITH_COVER);
        mSongsAdapter = new AudioBrowserListAdapter(getActivity(), AudioBrowserListAdapter.ITEM_WITH_COVER);

        mAudioController = AudioServiceController.getInstance();
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.vlc_audio_albums_songs, container, false);

        TabHost tabHost = (TabHost) v.findViewById(android.R.id.tabhost);
        ListView albumsList = (ListView) v.findViewById(R.id.albums);
        ListView songsList = (ListView) v.findViewById(R.id.songs);

        songsList.setAdapter(mSongsAdapter);
        albumsList.setAdapter(mAlbumsAdapter);

        songsList.setOnItemClickListener(songsListener);
        albumsList.setOnItemClickListener(albumsListener);

        tabHost.setup();

        tabHost.addTab(tabHost.newTabSpec("albums").setIndicator("Albums").setContent(R.id.albums));
        tabHost.addTab(tabHost.newTabSpec("songs").setIndicator("Songs").setContent(R.id.songs));

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.vlc_audio_list_browser, menu);
        if (!Util.isPhone())
            menu.setGroupVisible(R.id.phone_only, false);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    private void updateList() {
        if (mediaList == null)
            return;

        Collections.sort(mediaList, mComparators.byAlbum);
        for (int i = 0; i < mediaList.size(); ++i) {
            Media media = mediaList.get(i);
            mAlbumsAdapter.add(media.getAlbum(), null, media);
        }

        Collections.sort(mediaList, mComparators.byName);
        for (int i = 0; i < mediaList.size(); ++i) {
            Media media = mediaList.get(i);
            mSongsAdapter.add(media.getTitle(), null, media);
        }
    }

    OnItemClickListener albumsListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> av, View v, int p, long id) {
            String name = mAlbumsAdapter.getMedia(p).get(0).getAlbum();

            AudioListFragment audioList = new AudioListFragment();
            Bundle b = new Bundle();
            b.putString(AudioListFragment.EXTRA_NAME, name);
            b.putString(AudioListFragment.EXTRA_NAME2, null);
            b.putInt(AudioListFragment.EXTRA_MODE, AudioBrowserFragment.MODE_ALBUM);
            audioList.setArguments(b);

            MainActivity.ShowFragment(getActivity(), "tracks", audioList);
        }
    };

    OnItemClickListener songsListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> av, View v, int p, long id) {
            ArrayList<String> mediaLocation = new ArrayList<String>();
            mediaLocation.add(mSongsAdapter.getMedia(p).get(0).getLocation());
            mAudioController.load(mediaLocation, p);
            AudioPlayerFragment.start(getActivity());
        }
    };
}
