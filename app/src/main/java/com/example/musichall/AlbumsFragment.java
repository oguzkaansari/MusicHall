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

public class AlbumsFragment extends Fragment {

    private FirebaseAuth auth;
    String type,album;
    DatabaseReference dbRef;
    ListView listViewAlbums;
    ArrayList<String> albumsList;
    ArrayAdapter<String> arrayAdapterAlbums;
    private AlbumsFragmentListener listener;
    public interface AlbumsFragmentListener{
        void getDataFromAlbumsFragment(boolean state,String name) throws IOException;

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_albums,container,false);
        auth = FirebaseAuth.getInstance();
        albumsList = new ArrayList<String>();
        listViewAlbums = (ListView) view.findViewById(R.id.albumsSearch);
        dbRef = FirebaseDatabase.getInstance().getReference().child("Musics");

        dbRef.orderByChild("type").equalTo(type).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren() ){
                    String album = ds.child("album").getValue().toString();

                    if(!albumsList.contains(album)){  albumsList.add(album);   }




                }
                arrayAdapterAlbums = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, albumsList);
                listViewAlbums.setAdapter(arrayAdapterAlbums);

            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        listViewAlbums.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                    listener.getDataFromAlbumsFragment(true,albumsList.get(position));
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
        listViewAlbums.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                album = (String) albumsList.get(position);

                return false;
            }
        });
        registerForContextMenu(listViewAlbums);
        listViewAlbums.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                if (v.getId() == R.id.albumsSearch)
                {
                    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
                    //menu.setHeaderTitle(listViewMusic.getItemAtPosition(info.position).toString());

                    menu.add(0,0,0,"Add to library");

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
                dbRef.child("Albums").child(album).setValue(album);


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
        if(context instanceof AlbumsFragmentListener){
            listener = (AlbumsFragmentListener) context;
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
