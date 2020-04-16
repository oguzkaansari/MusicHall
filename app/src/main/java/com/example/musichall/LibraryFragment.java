package com.example.musichall;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LibraryFragment extends Fragment {

   private LinearLayout musicsLayout,artistLayout,albumsLayout,playListLayout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_library,container,false);

        playListLayout = (LinearLayout) view.findViewById(R.id.playListLayout);
        musicsLayout = (LinearLayout) view.findViewById(R.id.musicsLayout);
        artistLayout = (LinearLayout) view.findViewById(R.id.artistsLayout);
        albumsLayout = (LinearLayout) view.findViewById(R.id.albumsLayout);

        playListLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new LibPlaylistsFragment())
                        .commit();
            }
        });
        musicsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new LibMusicsFragment())
                        .commit();
            }
        });
        artistLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new LibArtistsFragment())
                        .commit();
            }
        });
        albumsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new LibAlbumsFragment())
                        .commit();
            }
        });



        return view;
    }


}
