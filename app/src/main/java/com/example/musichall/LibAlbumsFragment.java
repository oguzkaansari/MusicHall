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

public class LibAlbumsFragment extends Fragment {
    ListView listViewAlbum;
    ArrayList<String> albumList;
    ArrayAdapter<String> arrayAdapterAlbums;
    private FirebaseAuth auth;
    DatabaseReference dbRef;
    String album;
    private LibAlbumsFragmentListener listener;
    public interface LibAlbumsFragmentListener{

        void getDataFromLibAlbumsFragment(boolean state,String name) throws IOException;

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lib_albums,container,false);

        albumList = new ArrayList<String>();
        listViewAlbum = (ListView) view.findViewById(R.id.albumListView);
        auth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid()).child("Library").child("Albums");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    for(DataSnapshot ds:dataSnapshot.getChildren() ) {

                        albumList.add(ds.getValue().toString());

                    }


                }
                arrayAdapterAlbums = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, albumList);
                listViewAlbum.setAdapter(arrayAdapterAlbums);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        listViewAlbum.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                    listener.getDataFromLibAlbumsFragment(true,albumList.get(position));
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
        listViewAlbum.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                album = (String) albumList.get(position);

                return false;
            }
        });
        registerForContextMenu(listViewAlbum);
        listViewAlbum.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                if (v.getId() == R.id.albumListView)
                {
                    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
                    //menu.setHeaderTitle(listViewMusic.getItemAtPosition(info.position).toString());

                    menu.add(0,0,0,"Delete from library");

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
                dbRef.child("Albums").child(album).removeValue();
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new LibAlbumsFragment())
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
        if(context instanceof LibAlbumsFragmentListener){
            listener = (LibAlbumsFragmentListener) context;
        }else{
            throw new RuntimeException(context.toString() + "LibAlbumsFragmentListener must be implemented.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
