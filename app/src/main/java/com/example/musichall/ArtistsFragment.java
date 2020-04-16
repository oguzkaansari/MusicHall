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

public class ArtistsFragment extends Fragment {

    private FirebaseAuth auth;
    String type,artist;
    DatabaseReference dbRef;
    ListView listViewArtists;
    ArrayList<String> artistList;
    ArrayAdapter<String> arrayAdapterArtists;
    private ArtistsFragmentListener listener;
    public interface ArtistsFragmentListener{

        void getDataFromArtistsFragment(boolean state,String name) throws IOException;

    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artists,container,false);
        auth = FirebaseAuth.getInstance();
        artistList = new ArrayList<>();
        listViewArtists = (ListView) view.findViewById(R.id.artistListView);
        dbRef = FirebaseDatabase.getInstance().getReference().child("Musics");

        dbRef.orderByChild("type").equalTo(type).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren() ){
                    String artist = ds.child("artist").getValue().toString();

                    if(!artistList.contains(artist)){ artistList.add(artist);   }




                }
                arrayAdapterArtists = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, artistList);
                listViewArtists.setAdapter(arrayAdapterArtists);

            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        listViewArtists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                try {
                    listener.getDataFromArtistsFragment(true,artistList.get(position));
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
        listViewArtists.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                artist = (String) artistList.get(position);

                return false;
            }
        });
        registerForContextMenu(listViewArtists);
        listViewArtists.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                if (v.getId() == R.id.artistListView)
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
                dbRef.child("Artists").child(artist).setValue(artist);


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
        if(context instanceof ArtistsFragmentListener){
            listener = (ArtistsFragmentListener) context;
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
