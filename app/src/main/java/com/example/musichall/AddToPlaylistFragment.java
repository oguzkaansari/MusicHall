package com.example.musichall;

import android.os.Bundle;
import android.view.LayoutInflater;
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

import java.util.ArrayList;

public class AddToPlaylistFragment extends Fragment {

    private FirebaseAuth auth;
    DatabaseReference dbRef;
    ListView listViewPlaylist;
    ArrayList<String> playlistList;
    ArrayAdapter<String> arrayAdapterPlaylists;
    String key,name,url,playList;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_to_playlist,container,false);
        listViewPlaylist = (ListView) view.findViewById(R.id.listViewPlaylist);
        playlistList = new ArrayList<String>();
        auth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid()).child("Library").child("Playlists");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){//Kitaplıkta playlist varsa
                    playlistList.clear();
                    for(DataSnapshot ds:dataSnapshot.getChildren() ){

                        String playlist = ds.getKey();
                        playlistList.add(playlist);
                    }

                    arrayAdapterPlaylists = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, playlistList);
                    listViewPlaylist.setAdapter(arrayAdapterPlaylists);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        listViewPlaylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                playList = playlistList.get(position);
                dbRef.child(playList).child(key).setValue(url);
                dbRef.child(playList).child("default").setValue(null);

                Toast.makeText(getActivity(), "Added to the" + playList, Toast.LENGTH_SHORT).show();

                //listeye eklendikten sonra fragment kapatılacak.

            }
        });






        return view;
    }


}
