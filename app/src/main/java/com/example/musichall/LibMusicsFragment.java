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

public class LibMusicsFragment extends Fragment {
    private LibMusicsFragmentListener listener;

    public interface LibMusicsFragmentListener{

        void getDataFromLibMusicsFrag(boolean state,String url,String name,String artist,String album,ArrayList<String> urlList) throws IOException;
        void getDataFromLibMusicsFragForAddToPlayList(boolean state, String key, String name, String url) throws IOException;
    }

    private FirebaseAuth auth;
    DatabaseReference dbRef;
    ListView listViewMusic;
    ArrayList<String> musicList,urlList,keyList;
    ArrayAdapter<String> arrayAdapterMusics;
    String name,url,key;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lib_musics,container,false);

        auth = FirebaseAuth.getInstance();
        musicList = new ArrayList<String>();
        urlList = new ArrayList<String>();
        keyList = new ArrayList<>();

        listViewMusic = (ListView) view.findViewById(R.id.musicListView);

        dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid()).child("Library").child("Musics");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){//Kitaplıkta müzik varsa
                    musicList.clear();
                    urlList.clear();
                    for(DataSnapshot ds:dataSnapshot.getChildren() ){

                        String key = ds.getKey();
                        keyList.add(key);
                        String[] parts = key.split("-");
                        String url = ds.getValue().toString();
                        musicList.add(parts[1]);
                        urlList.add(url);



                    }


                }
                arrayAdapterMusics = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, musicList);
                listViewMusic.setAdapter(arrayAdapterMusics);
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
                 dbRef = FirebaseDatabase.getInstance().getReference().child("Musics");
                 dbRef.orderByChild("url").equalTo(url).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for(DataSnapshot ds:dataSnapshot.getChildren() ){

                            String artist = ds.child("artist").getValue().toString();
                            String album = ds.child("album").getValue().toString();

                            try {
                                listener.getDataFromLibMusicsFrag(true,url,name,artist,album,urlList);
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
        listViewMusic.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                name = (String) musicList.get(position);
                key = (String) keyList.get(position);
                url = (String) urlList.get(position);

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
                    menu.add(0,1,1,"Delete from library");

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

                try {
                    listener.getDataFromLibMusicsFragForAddToPlayList(true,key,name,url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                donus = true;
                break;
            case 1:

                dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid()).child("Library");
                dbRef.child("Musics").child(key).removeValue();
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new LibMusicsFragment())
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
        if(context instanceof MusicsFragment.MusicsFragmentListener){
            listener = (LibMusicsFragment.LibMusicsFragmentListener) context;
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
