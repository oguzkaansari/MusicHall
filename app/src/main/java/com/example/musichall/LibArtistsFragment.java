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

public class LibArtistsFragment extends Fragment {
    ListView listViewArtist;
    ArrayList<String> artistList;
    String artist;
    ArrayAdapter<String> arrayAdapterArtists;
    private FirebaseAuth auth;
    DatabaseReference dbRef;
    private LibArtistsFragmentListener listener;
    public interface LibArtistsFragmentListener{

        void getDataFromLibArtistsFragment(boolean state,String name) throws IOException;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lib_artists,container,false);

        artistList = new ArrayList<String>();
        listViewArtist = (ListView) view.findViewById(R.id.artistListView);
        auth = FirebaseAuth.getInstance();

        dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid()).child("Library").child("Artists");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    for(DataSnapshot ds:dataSnapshot.getChildren() ) {

                        artistList.add(ds.getValue().toString());

                    }

                }
                arrayAdapterArtists = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, artistList);
                listViewArtist.setAdapter(arrayAdapterArtists);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        listViewArtist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                    listener.getDataFromLibArtistsFragment(true,artistList.get(position));
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });

        listViewArtist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                artist = (String) artistList.get(position);

                return false;
            }
        });
        registerForContextMenu(listViewArtist);
        listViewArtist.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                if (v.getId() == R.id.artistListView)
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
                dbRef.child("Artists").child(artist).removeValue();
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new LibArtistsFragment())
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
        if(context instanceof LibArtistsFragmentListener){
            listener = (LibArtistsFragmentListener) context;
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
