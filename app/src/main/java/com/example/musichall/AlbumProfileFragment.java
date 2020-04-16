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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;

public class AlbumProfileFragment extends Fragment {

    String albumName,musicName,key,url;
    DatabaseReference dbRef;
    private FirebaseAuth auth;
    ArrayList<String> urlList,nameList,artistList,keyList;
    ListView listViewMusics;
    ArrayAdapter<String> arrayAdapterMusics;
    private AlbumProfileFragmentListener listener;
    public interface AlbumProfileFragmentListener{

        void getDataFromAlbumProfileFragment(boolean state,String url,String name,String artist,String album,ArrayList<String> urlList) throws IOException;
        void getDataFromAlbumProfileFragForAddToPlayList(boolean state,String key,String url,String name) throws IOException;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_profile,container,false);

        urlList = new ArrayList<>();
        nameList = new ArrayList<>();
        artistList = new ArrayList<>();
        keyList = new ArrayList<>();
        listViewMusics = view.findViewById(R.id.albumProfileMusicListview);
        auth = FirebaseAuth.getInstance();

        dbRef = FirebaseDatabase.getInstance().getReference().child("Musics");
        dbRef.orderByChild("album").equalTo(albumName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds:dataSnapshot.getChildren() ) {

                    keyList.add(ds.getKey());
                    urlList.add(ds.child("url").getValue().toString());
                    nameList.add(ds.child("name").getValue().toString());
                    artistList.add(ds.child("artist").getValue().toString());

                }
                arrayAdapterMusics = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, nameList);
                listViewMusics.setAdapter(arrayAdapterMusics);
                Helper.getListViewSize(listViewMusics);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        listViewMusics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                    listener.getDataFromAlbumProfileFragment(true,urlList.get(position),nameList.get(position),artistList.get(position),albumName,urlList);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });




        listViewMusics.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                musicName = (String) nameList.get(position);
                key = (String) keyList.get(position);
                url = (String) urlList.get(position);

                return false;
            }
        });
        registerForContextMenu(listViewMusics);
        listViewMusics.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                if (v.getId() == R.id.albumProfileMusicListview)
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
                    listener.getDataFromAlbumProfileFragForAddToPlayList(true,key,musicName,url);
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
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof AlbumProfileFragmentListener){
            listener = (AlbumProfileFragmentListener) context;
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
