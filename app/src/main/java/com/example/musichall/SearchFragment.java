package com.example.musichall;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private SearchFragmentListener listener;

    public interface SearchFragmentListener{

        void getIdFromSearchFragment(boolean state,String id);
        void getMusicNameFromSearchFragment(boolean state, String url, String name, String artist, String album, ArrayList<String> urlList);
        void getAlbumNameFromSearchFragment(boolean state,String name);
        void getArtistNameFromSearchFragment(boolean state,String name);
        void getDataFromSearchFragmentForAddToPlaylist(boolean state, String key, String name, String url);

    }
    private FirebaseAuth auth;
    DatabaseReference dbRef;
    private FirebaseStorage storage;
    private ArrayList<String> searchList,titleList,subTitleList,idList,musicList,albumList,artistList,urlList,albumListPlay,artistListPlay;
    private ArrayList<Uri> imgUriList;
    private ArrayList<String>keyList;
    String key,url,name;
    private TextView usersText,musicsText,artistsText,albumsText;
    private ArrayAdapter<String> arrayAdapterSearchList,arrayAdapterMusics,arrayAdapterAlbums,arrayAdapterArtists;
    private ListView listViewSearch,listViewUsers,listViewMusics,listViewAlbums,listViewArtists;
    private LinearLayout searchHistoryLayout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search,container,false);
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        searchHistoryLayout = view.findViewById(R.id.searchHistoryLayout);
        searchList = new ArrayList<String>();
        titleList = new ArrayList<String>();
        subTitleList = new ArrayList<String>();
        imgUriList = new ArrayList<Uri>();
        idList = new ArrayList<String>();
        musicList = new ArrayList<String>();
        artistList = new ArrayList<String>();
        albumList = new ArrayList<String>();
        urlList = new ArrayList<>();
        albumListPlay = new ArrayList<>();
        artistListPlay = new ArrayList<>();
        keyList = new ArrayList<>();

        usersText = view.findViewById(R.id.usersTextView);
        musicsText = view.findViewById(R.id.musicsTextView);
        artistsText = view.findViewById(R.id.artistTextView);
        albumsText = view.findViewById(R.id.albumsTextView);
        usersText.setVisibility(View.GONE);
        musicsText.setVisibility(View.GONE);
        artistsText.setVisibility(View.GONE);
        albumsText.setVisibility(View.GONE);


        listViewSearch = (ListView) view.findViewById(R.id.searchHistoryListView);
        listViewUsers = (ListView) view.findViewById(R.id.usersListView);
        listViewMusics = (ListView) view.findViewById(R.id.musicsSearch);
        listViewAlbums = (ListView) view.findViewById(R.id.albumsSearch);
        listViewArtists = (ListView) view.findViewById(R.id.artistsSearch);


        dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid()).child("searchHistory");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    searchList.clear();
                    for(DataSnapshot ds:dataSnapshot.getChildren() ){

                      String searched = ds.getKey();
                      searchList.add(searched);

                    }
                    arrayAdapterSearchList = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, searchList);
                    listViewSearch.setAdapter(arrayAdapterSearchList);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final ImageRowAdapter imageRowAdapter = new ImageRowAdapter(getActivity().getApplicationContext(),titleList,subTitleList,idList);
        arrayAdapterMusics = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, musicList);
        arrayAdapterAlbums = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, albumList);
        arrayAdapterArtists = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, artistList);

        TextInputLayout textInputSearch = view.findViewById(R.id.searchInputLayout);
        textInputSearch.getEditText().addTextChangedListener(new TextWatcher() {

            @Override

            public void afterTextChanged(Editable s) {

                searchHistoryLayout.setVisibility(View.GONE);
                titleList.clear();
                subTitleList.clear();
                imgUriList.clear();
                idList.clear();
                urlList.clear();
                albumList.clear();
                artistList.clear();
                albumListPlay.clear();
                artistListPlay.clear();
                musicList.clear();
                imageRowAdapter.clear();
                arrayAdapterMusics.clear();
                arrayAdapterAlbums.clear();
                arrayAdapterArtists.clear();

                String searchKey = s.toString();
                if(!searchKey.equals("")){
                    //User arama
                    dbRef = FirebaseDatabase.getInstance().getReference().child("Users");
                    dbRef.orderByChild("name").startAt(searchKey).endAt(searchKey + "\uf8ff").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                usersText.setVisibility(View.VISIBLE);

                                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                    String name = ds.child("name").getValue().toString();
                                    titleList.add(name);
                                    subTitleList.add("");
                                    String Id = ds.getKey();
                                    idList.add(Id);

                                }
                                listViewUsers.setAdapter(imageRowAdapter);
                                Helper.getListViewSize(listViewUsers);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    imageRowAdapter.updateAdapter(titleList,subTitleList,idList);

                    //Music arama
                    dbRef = FirebaseDatabase.getInstance().getReference().child("Musics");
                    dbRef.orderByChild("name").startAt(searchKey).endAt(searchKey + "\uf8ff").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists()) {
                                musicsText.setVisibility(View.VISIBLE);

                                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                    musicList.add(ds.child("name").getValue().toString());
                                    urlList.add(ds.child("url").getValue().toString());
                                    albumListPlay.add(ds.child("album").getValue().toString());
                                    artistListPlay.add(ds.child("artist").getValue().toString());

                                    keyList.add(ds.getKey());
                                }
                                listViewMusics.setAdapter(arrayAdapterMusics);
                                Helper.getListViewSize(listViewMusics);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    //Album arama
                    dbRef = FirebaseDatabase.getInstance().getReference().child("Musics");
                    dbRef.orderByChild("album").startAt(searchKey).endAt(searchKey + "\uf8ff").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists()) {
                                albumsText.setVisibility(View.VISIBLE);

                                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                    String album = ds.child("album").getValue().toString();
                                    if(!albumList.contains(album)){ albumList.add(album);}
                                }
                                listViewAlbums.setAdapter(arrayAdapterAlbums);
                                Helper.getListViewSize(listViewAlbums);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    //Artist arama

                    dbRef = FirebaseDatabase.getInstance().getReference().child("Musics");
                    dbRef.orderByChild("artist").startAt(searchKey).endAt(searchKey + "\uf8ff").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists()) {
                                artistsText.setVisibility(View.VISIBLE);
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                    String album = ds.child("artist").getValue().toString();
                                    if(!artistList.contains(album)){ artistList.add(album);}
                                }
                                listViewArtists.setAdapter(arrayAdapterArtists);
                                Helper.getListViewSize(listViewArtists);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });




                }else{
                    usersText.setVisibility(View.GONE);
                    musicsText.setVisibility(View.GONE);
                    albumsText.setVisibility(View.GONE);
                    artistsText.setVisibility(View.GONE);
                    searchHistoryLayout.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }
        });

        listViewUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String userId = idList.get(position);
                listener.getIdFromSearchFragment(true,userId);

            }
        });
        listViewMusics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<String> uList = new ArrayList<>();
                String url = urlList.get(position);
                uList.add(url);
                listener.getMusicNameFromSearchFragment(true,url, musicList.get(position),artistListPlay.get(position), albumListPlay.get(position), uList );

            }
        });
        listViewAlbums.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                listener.getAlbumNameFromSearchFragment(true,albumList.get(position));

            }
        });
        listViewArtists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                listener.getArtistNameFromSearchFragment(true,artistList.get(position));

            }
        });
        listViewMusics.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                key = (String) keyList.get(position);
                url = (String) urlList.get(position);
                name = (String) musicList.get(position);

                return false;
            }
        });
        registerForContextMenu(listViewMusics);
        listViewMusics.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                if (v.getId() == R.id.musicsSearch)
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
                listener.getDataFromSearchFragmentForAddToPlaylist(true,key,name,url);
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
        if(context instanceof SearchFragmentListener){
            listener = (SearchFragment.SearchFragmentListener) context;
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
