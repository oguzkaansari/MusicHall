package com.example.musichall;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;

public class LibPlaylistsFragment extends Fragment {
    private FirebaseAuth auth;
    DatabaseReference dbRef;
    ListView listViewPlaylist;
    ArrayList<String> playlistList;
    ArrayAdapter<String> arrayAdapterPlaylists;
    String playlist;
    private LibPlaylistsFragmentListener listener;
    public interface LibPlaylistsFragmentListener{

        void getDataFromLibPlaylistsFragment(boolean state,String playListName)throws IOException;

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lib_playlists,container,false);

        auth = FirebaseAuth.getInstance();

        playlistList = new ArrayList<String>();
        listViewPlaylist = (ListView) view.findViewById(R.id.playlistListView);

        //Kitaplıktan müzikleri oku ve listele
        dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid()).child("Library").child("Playlists");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){//Kitaplıkta playlist varsa
                    playlistList.clear();
                    for(DataSnapshot ds:dataSnapshot.getChildren() ){

                        String playlist = ds.getKey();
                        if(!playlistList.contains(playlist)){playlistList.add(playlist);}
                    }

                    arrayAdapterPlaylists = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, playlistList);
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

                playlist = playlistList.get(position);
                try {
                    listener.getDataFromLibPlaylistsFragment(true,playlist);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Create new playlist");
                final EditText input = new EditText(getActivity());
                builder.setView(input);

                builder.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       String name_input = input.getText().toString();
                       dbRef.child(name_input).child("-default").setValue("");

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });


        listViewPlaylist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                playlist = playlistList.get(position);

                return false;
            }
        });
        registerForContextMenu(listViewPlaylist);
        listViewPlaylist.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                if (v.getId() == R.id.playlistListView)
                {
                    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
                    //menu.setHeaderTitle(listViewMusic.getItemAtPosition(info.position).toString());

                    menu.add(0,0,0,"Delete playlist");

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

                new AlertDialog.Builder(getActivity())
                        .setTitle("Remove Playlist")
                        .setMessage("Are you sure to remove playlist " + playlist + "?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid()).child("Library");
                                dbRef.child("Playlists").child(playlist).removeValue();
                                getFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, new LibPlaylistsFragment())
                                        .commit();

                            }
                        })

                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();



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
        if(context instanceof LibPlaylistsFragmentListener){
            listener = (LibPlaylistsFragmentListener) context;
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
