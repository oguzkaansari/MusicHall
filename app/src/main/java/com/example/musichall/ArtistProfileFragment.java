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

public class ArtistProfileFragment extends Fragment {

    String artistName,albumName;
    ArrayList<String> albumList;
    ListView listViewAlbums;
    ArrayAdapter<String> arrayAdapterAlbums;
    DatabaseReference dbRef;
    private FirebaseAuth auth;
    private ArtistProfileFragmentListener listener;
    public interface ArtistProfileFragmentListener{

        void getDataFromArtistProfileFragment(boolean state, String album) throws IOException;


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist_profile,container,false);
        albumList = new ArrayList<>();
        listViewAlbums = view.findViewById(R.id.artistProfileAlbumListview);
        auth = FirebaseAuth.getInstance();

        dbRef = FirebaseDatabase.getInstance().getReference().child("Musics");
        dbRef.orderByChild("artist").equalTo(artistName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds:dataSnapshot.getChildren() ) {

                    String album = ds.child("album").getValue().toString();
                    if(!albumList.contains(album)){albumList.add(album);}

                }
                arrayAdapterAlbums = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, albumList);
                listViewAlbums.setAdapter(arrayAdapterAlbums);
                Helper.getListViewSize(listViewAlbums);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listViewAlbums.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                    listener.getDataFromArtistProfileFragment(true,albumList.get(position));
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });

        listViewAlbums.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                albumName = (String) albumList.get(position);

                return false;
            }
        });
        registerForContextMenu(listViewAlbums);
        listViewAlbums.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                if (v.getId() == R.id.artistProfileAlbumListview)
                {
                    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
                    //menu.setHeaderTitle(listViewMusic.getItemAtPosition(info.position).toString());

                    menu.add(0,0,0,"Follow");

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
                dbRef.child("Albums").child(albumName).setValue(albumName);


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
        if(context instanceof ArtistProfileFragmentListener){
            listener = (ArtistProfileFragmentListener) context;
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
