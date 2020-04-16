package com.example.musichall;

import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MusicsFragment extends Fragment implements CardAdapter.CardAdapterListener  {

    private MusicsFragmentListener listener;
    private FirebaseAuth auth;




    public interface MusicsFragmentListener{

        void getDataFromMusicsFrag(boolean state,String url,String name,String artist,String album,ArrayList<String> urlList) throws IOException;
        void getDataFromMusicsFragForAddToPlayList(boolean state,String key,String url,String name) throws IOException;

    }

    DatabaseReference dbRef;
    ListView listViewMusic;
    ArrayList<String> keyList,musicList,urlList,artistList,albumList,keyListTop,musicListTop,urlListTop,artistListTop,albumListTop;
    String type,url,name,artist,album,key;
    ArrayAdapter<String> arrayAdapterMusics;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_musics,container,false);
        auth = FirebaseAuth.getInstance();
        keyList = new ArrayList<>();
        musicList = new ArrayList<>();
        artistList = new ArrayList<>();
        albumList = new ArrayList<>();
        urlList = new ArrayList<>();
        keyListTop = new ArrayList<>();
        musicListTop = new ArrayList<>();
        artistListTop = new ArrayList<>();
        albumListTop = new ArrayList<>();
        urlListTop = new ArrayList<>();
        listViewMusic = (ListView) view.findViewById(R.id.musicListView);

        dbRef = FirebaseDatabase.getInstance().getReference().child("Musics");
        dbRef.orderByChild("libCount").limitToLast(50).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if(ds.child("type").getValue().toString().equals(type)) {
                            String key = ds.getKey();
                            String name = ds.child("name").getValue().toString();
                            String url = ds.child("url").getValue().toString();
                            String artist = ds.child("artist").getValue().toString();
                            String album = ds.child("album").getValue().toString();

                            artistListTop.add(artist);
                            albumListTop.add(album);
                            keyListTop.add(key);
                            musicListTop.add(name);
                            urlListTop.add(url);
                        }

                    }
                    initRecyclerViewExplore();

                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dbRef.orderByChild("type").equalTo(type).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String key = ds.getKey();
                        String name = ds.child("name").getValue().toString();
                        String url = ds.child("url").getValue().toString();
                        String artist = ds.child("artist").getValue().toString();
                        String album = ds.child("album").getValue().toString();

                        artistList.add(artist);
                        albumList.add(album);
                        keyList.add(key);
                        musicList.add(name);
                        urlList.add(url);


                    }
                    arrayAdapterMusics = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, musicList);
                    listViewMusic.setAdapter(arrayAdapterMusics);
                    Helper.getListViewSize(listViewMusic);

                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listViewMusic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                url = (String) urlList.get(position);
                name = (String) musicList.get(position);
                artist = (String) artistList.get(position);
                album = (String) albumList.get(position);

                try {
                    listener.getDataFromMusicsFrag(true,url,name,artist,album,urlList);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
        listViewMusic.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                key = (String) keyList.get(position);
                url = (String) urlList.get(position);
                name = (String) musicList.get(position);

                return false;
            }
        });
        registerForContextMenu(listViewMusic);
        listViewMusic.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                if (v.getId() == R.id.musicListView)
                {
                    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
                    //menu.setHeaderTitle(listViewMusic.getItemAtPosition(info.position).toString());
                    menu.add(0,0,0,"Add to playlist");
                    menu.add(0,1,1,"Add to library");
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
                //Playlist seçme sayfası açılacak.
                try {
                    listener.getDataFromMusicsFragForAddToPlayList(true,key,name,url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                donus = true;
                break;
            case 1:
                //Kitaplığa eklenecek.

                dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid()).child("Library");
                dbRef.child("Musics").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Toast.makeText(getContext(), "The song has already been added", Toast.LENGTH_LONG).show();
                        }else{
                            dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid()).child("Library");
                            dbRef.child("Musics").child(key).setValue(url);
                            dbRef = FirebaseDatabase.getInstance().getReference().child("Musics");
                            dbRef.child(key).child("libCount").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){


                                        int count = dataSnapshot.getValue(Integer.class);
                                        count = count + 1;
                                        dbRef.child(key).child("libCount").setValue(count);


                                    }else{
                                        dbRef.child(key).child("libCount").setValue(1);

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



                donus = true;
                break;

            default:
                donus = false;
                break;
        }
        return donus;
    }
    private void initRecyclerViewExplore(){

        Collections.reverse(urlListTop);
        Collections.reverse(musicListTop);
        Collections.reverse(artistListTop);
        Collections.reverse(albumListTop);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = getActivity().findViewById(R.id.cardRcyViewExplore);
        recyclerView.setLayoutManager(layoutManager);
        CardAdapter adapter = new CardAdapter(getContext(),musicListTop, albumListTop, artistListTop, urlListTop,this);
        recyclerView.setAdapter(adapter);
    }
    @Override
    public void getDataFromCardAdapter(boolean state, String url, String name, String artist, String album, ArrayList<String> urlList) throws IOException {

        listener.getDataFromMusicsFrag(state,url,name,artist,album,urlList);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof MusicsFragmentListener){
            listener = (MusicsFragmentListener) context;
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
