package com.example.musichall;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class showPlayListFragment extends Fragment {
    private showPlayListFragmentListener listener;
    public interface showPlayListFragmentListener{

        void getDataFromshowPlayListFragment(boolean state,String url,String name,String artist,String album,ArrayList<String> urlList ) throws IOException;
    }
    private FirebaseAuth auth;
    DatabaseReference dbRef;
    ListView listViewPlaylist;
    TextView playListTitle;
    ArrayList<String> musicList,urlList;
    ArrayAdapter<String> arrayAdapterMusicList;
    String name,url,artist,album,playList,Key;
    Button sharePlaylist;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_playlist,container,false);
        listViewPlaylist = (ListView) view.findViewById(R.id.playlist);
        playListTitle = view.findViewById(R.id.playlistName);
        musicList = new ArrayList<String>();
        urlList = new ArrayList<String>();
        sharePlaylist = view.findViewById(R.id.sharePlayList) ;
        auth = FirebaseAuth.getInstance();
        playListTitle.setText(playList);
        dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid()).child("sharedPlaylists").child(playList);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    sharePlaylist.setText("Sharing");

                }else{

                    sharePlaylist.setText("Share");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid()).child("Library").child("Playlists").child(playList);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    musicList.clear();
                    for(DataSnapshot ds:dataSnapshot.getChildren() ){

                        String key = ds.getKey();
                        if(!key.equals("-default")){
                            Key = key;
                            String[] parts = key.split("-");
                            String url = ds.getValue().toString();

                            musicList.add(parts[1]);
                            urlList.add(url);
                        }





                    }

                    arrayAdapterMusicList = new ArrayAdapter<String>(Objects.requireNonNull(getActivity()), android.R.layout.simple_list_item_1, musicList);
                    listViewPlaylist.setAdapter(arrayAdapterMusicList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        listViewPlaylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                name = musicList.get(position);
                url = urlList.get(position);

                    dbRef = FirebaseDatabase.getInstance().getReference().child("Musics");
                    dbRef.orderByChild("name").equalTo(name).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                String artist = ds.child("artist").getValue().toString();
                                String album = ds.child("album").getValue().toString();
                                try {
                                    listener.getDataFromshowPlayListFragment(true, url, name, artist, album, urlList);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                            }


                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });




            }
        });


        sharePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid());

                if(sharePlaylist.getText() == "Sharing"){

                    dbRef.child("sharedPlaylists").child(playList).setValue(null);
                    sharePlaylist.setText("Share");

                }else{

                    int musicCount = musicList.size();
                    dbRef.child("sharedPlaylists").child(playList).setValue(musicCount);
                    sharePlaylist.setText("Sharing");

                }

            }
        });

        listViewPlaylist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                name = (String) musicList.get(position);

                return false;
            }
        });
        registerForContextMenu(listViewPlaylist);
        listViewPlaylist.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                if (v.getId() == R.id.playlist)
                {
                    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
                    //menu.setHeaderTitle(listViewMusic.getItemAtPosition(info.position).toString());

                    menu.add(0,0,0,"Delete from playlist");

                }
            }
        });


        return view;
    }
    public boolean onContextItemSelected(MenuItem item)
    {
        boolean donus;
        switch (item.getItemId())
        {

            case 0:
                //Kitaplığa eklenecek.

                dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid()).child("Library");
                dbRef.child("Playlists").child(playList).child(Key).removeValue();
                showPlayListFragment showPlayListFragment = new showPlayListFragment();
                showPlayListFragment.playList = playList;
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new showPlayListFragment())
                        .commit();

                donus = true;
                break;

            default:
                donus = false;
                break;
        }
        return donus;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof showPlayListFragmentListener){
            listener = (showPlayListFragmentListener) context;
        }else{
            throw new RuntimeException(context.toString() + "ListFragmentListener must be implemented.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
