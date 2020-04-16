package com.example.musichall;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.Toolbar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Context;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.VISIBLE;


public class MainActivity extends AppCompatActivity implements
        ExploreFragment.ExploreFragmentListener,
        ListFragment.ListFragmentListener,
        LibPlaylistsFragment.LibPlaylistsFragmentListener,
        LibMusicsFragment.LibMusicsFragmentListener,
        MusicsFragment.MusicsFragmentListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener,
        showPlayListFragment.showPlayListFragmentListener,
        SearchFragment.SearchFragmentListener,
        FriendRequestsFragment.FriendRequestFragmentListener,
        HomeFragment.HomeFragmentListener,
        AlbumProfileFragment.AlbumProfileFragmentListener,
        AlbumsFragment.AlbumsFragmentListener,
        LibAlbumsFragment.LibAlbumsFragmentListener,
        ArtistProfileFragment.ArtistProfileFragmentListener,
        LibArtistsFragment.LibArtistsFragmentListener,
        ArtistsFragment.ArtistsFragmentListener,
        ProfileFragment.ProfileFragmentListener,
        SelfProfileFragment.SelfProfileFragmentListener,
        FriendsFragment.FriendsFragmentListener

{

    private LinearLayout playerShortcut;
    private BottomNavigationView bottomNavigationView;
    private ExploreFragment exploreFragment;
    private ListFragment listFragment;
    private MusicsFragment musicsFragment;
    private ArtistsFragment artistsFragment;
    private AlbumsFragment albumsFragment;
    private AddToPlaylistFragment addToPlaylistFragment;
    private showPlayListFragment showPlayListFragment;
    private ProfileFragment profileFragment;
    private FriendRequestsFragment friendRequestsFragment;
    private AlbumProfileFragment albumProfileFragment;
    private ArtistProfileFragment artistProfileFragment;
    private FriendsFragment friendsFragment;
    TextView artisttxt, songtxt,albumtxt,songtxtPlayer,artisttxtPlayer,timeFromStart,timeToEnd;
    Toolbar toolbar;
    ToggleButton toggle;
    boolean state;
    String url, name, artist, album;
    ArrayList<String> urlList;
    ImageView playerImage;
    View playerLayout;
    ImageButton play_pause, play_pause_player,skip_next,skip_next_player,skip_previous,skip_previous_player;
    private MediaPlayer mediaPlayer;
    private int mediaFileLength;
    private int realtimeLength;
    final Handler handler = new Handler();
    private SeekBar seekBar;
    ProgressDialog mDialog;
    FirebaseStorage storage= FirebaseStorage.getInstance();
    private FirebaseAuth auth;
    DatabaseReference dbMix,dbRequest;



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playerLayout = findViewById(R.id.playerLayout);
        playerLayout.setVisibility(View.GONE);
        playerShortcut =  findViewById(R.id.playerShortcut);
        exploreFragment = new ExploreFragment();
        listFragment = new ListFragment();
        musicsFragment = new MusicsFragment();
        artistsFragment = new ArtistsFragment();
        albumsFragment = new AlbumsFragment();
        addToPlaylistFragment = new AddToPlaylistFragment();
        showPlayListFragment = new showPlayListFragment();
        profileFragment = new ProfileFragment();
        friendRequestsFragment = new FriendRequestsFragment();
        albumProfileFragment = new AlbumProfileFragment();
        artistProfileFragment = new ArtistProfileFragment();
        friendsFragment = new FriendsFragment();
        this.bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navlistener);
        toolbar =  findViewById(R.id.toolbar);
        setActionBar(toolbar);
        toolbar.inflateMenu(R.menu.toolbar_menu);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        artisttxt =  findViewById(R.id.artistText);
        songtxt =  findViewById(R.id.songText);
        artisttxtPlayer = findViewById(R.id.artistTextPlayer);
        albumtxt = findViewById(R.id.albumTextPlayer);
        songtxtPlayer = findViewById(R.id.songTextPlayer);
        play_pause = findViewById(R.id.play_pause);
        play_pause_player = playerLayout.findViewById(R.id.play_pause_player);
        skip_next = findViewById(R.id.skipNext);
        skip_next_player = playerLayout.findViewById(R.id.skipNext_player);
        skip_previous = findViewById(R.id.skipPrevious);
        skip_previous_player = playerLayout.findViewById(R.id.skipPrevious_player);
        seekBar = playerLayout.findViewById(R.id.seekBar);
        timeFromStart = playerLayout.findViewById(R.id.timeFromStart);
        timeToEnd = playerLayout.findViewById(R.id.timeToEnd);
        playerImage = findViewById(R.id.playerImageView);
        urlList = new ArrayList<>();
        mDialog = new ProgressDialog(MainActivity.this);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
        auth = FirebaseAuth.getInstance();

        init();





        playerShortcut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomNavigationView.setVisibility(View.GONE);
                playerLayout.bringToFront();
                playerLayout.setVisibility(VISIBLE);


            }
        });


        playerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        play_pause_player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mediaPlayer.isPlaying()) {
                   play();

                } else {
                    mediaPlayer.pause();
                    play_pause.setBackgroundResource(R.drawable.ic_play);
                    play_pause_player.setBackgroundResource(R.drawable.ic_play);

                }

            }
        });
        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mediaPlayer.isPlaying()) {
                    play();

                } else {
                    mediaPlayer.pause();
                    play_pause.setBackgroundResource(R.drawable.ic_play);
                }

            }
        });
        seekBar.setMax(99); // 100% (0~99)
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mediaPlayer.isPlaying()) {
                    SeekBar seekBar = (SeekBar) v;
                    int playPosition = (mediaFileLength / 100) * seekBar.getProgress();
                    mediaPlayer.seekTo(playPosition);
                }
                return false;
            }
        });



    }






    public void previousTrack(View view){
        int index = urlList.indexOf(url);
        if(index != 0) {
            DatabaseReference dbRef;
            mediaPlayer.stop();
            mediaPlayer.reset();
            url = urlList.get(index - 1);
            dbRef = FirebaseDatabase.getInstance().getReference().child("Musics");
            dbRef.orderByChild("url").equalTo(url).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot ds:dataSnapshot.getChildren() ) {
                            artist = ds.child("artist").getValue().toString();
                            album = ds.child("album").getValue().toString();
                            name = ds.child("name").getValue().toString();

                            StorageReference storageReference = storage.getReference().child("album_images").child(album + ".jpg");
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    try{
                                        Picasso.with(getBaseContext()).load(uri).into(playerImage);

                                    }catch (IndexOutOfBoundsException i){}


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    playerImage.setImageDrawable(null);

                                }
                            });
                        }
                        songtxt.setText(name);
                        artisttxt.setText(artist);
                        albumtxt.setText(album);
                        songtxtPlayer.setText(name);
                        artisttxtPlayer.setText(artist);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            play();
        }



    }
    public void nextTrack(View view){

        int index = urlList.indexOf(url);
        if(index + 1 != urlList.size() ) {
            DatabaseReference dbRef;
            mediaPlayer.stop();
            mediaPlayer.reset();
            url = urlList.get(index + 1);
            dbRef = FirebaseDatabase.getInstance().getReference().child("Musics");
            dbRef.orderByChild("url").equalTo(url).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds:dataSnapshot.getChildren() ) {

                             artist = ds.child("artist").getValue().toString();
                             album = ds.child("album").getValue().toString();
                             name = ds.child("name").getValue().toString();

                            StorageReference storageReference = storage.getReference().child("album_images").child(album + ".jpg");
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    try{
                                        Picasso.with(getBaseContext()).load(uri).into(playerImage);

                                    }catch (IndexOutOfBoundsException i){}


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    playerImage.setImageDrawable(null);

                                }
                            });

                        }
                        songtxt.setText(name);
                        artisttxt.setText(artist);
                        albumtxt.setText(album);
                        songtxtPlayer.setText(name);
                        artisttxtPlayer.setText(artist);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            play();
        }

    }


    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        seekBar.setSecondaryProgress(percent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        play_pause.setImageResource(R.drawable.ic_play);
        mp.stop();
        mp.release();
    }


    @Override
    public void getTypeFromExplore(String type) {
        listFragment.musicType = type;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, listFragment).commit();
    }

    @Override
    public void getDataFromMusicsFragForAddToPlayList(boolean state,String key ,String name, String url) {

        addToPlaylistFragment.key = key;
        addToPlaylistFragment.name = name;
        addToPlaylistFragment.url = url;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, addToPlaylistFragment).commit();


    }

    @Override
    public void getDataFromAlbumProfileFragForAddToPlayList(boolean state, String key, String url, String name) throws IOException {
        toolbar.setVisibility(VISIBLE);
        addToPlaylistFragment.key = key;
        addToPlaylistFragment.name = name;
        addToPlaylistFragment.url = url;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, addToPlaylistFragment).commit();
    }
    @Override
    public void getDataFromLibMusicsFragForAddToPlayList(boolean state, String key, String name, String url) throws IOException {
        addToPlaylistFragment.key = key;
        addToPlaylistFragment.name = name;
        addToPlaylistFragment.url = url;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, addToPlaylistFragment).commit();
    }
    @Override
    public void getDataFromSearchFragmentForAddToPlaylist(boolean state, String key, String name, String url) {
        addToPlaylistFragment.key = key;
        addToPlaylistFragment.name = name;
        addToPlaylistFragment.url = url;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, addToPlaylistFragment).commit();
    }
    @Override
    public void getDataFromMusicsFrag(boolean state, String url, String name, String artist, String album,ArrayList<String> urlList) throws IOException {
        //Bu fonksiyon musicsfragment dan veri alacak.
        this.urlList = urlList;
        this.url = url;
        this.state = state;
        this.name = name;
        this.artist = artist;
        this.album = album;
        mediaPlayer.stop();
        mediaPlayer.reset();
        artisttxt.setText(artist);
        songtxt.setText(name);
        artisttxtPlayer.setText(artist);
        songtxtPlayer.setText(name);
        albumtxt.setText(album);
        StorageReference storageReference = storage.getReference().child("album_images").child(album + ".jpg");
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try{
                    Picasso.with(getBaseContext()).load(uri).into(playerImage);

                }catch (IndexOutOfBoundsException i){}


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                playerImage.setImageDrawable(null);

            }
        });
        play();




    }

    @Override
    public void getDataFromLibMusicsFrag(boolean state, String url, String name, String artist, String album,ArrayList<String> urlList) throws IOException {

        this.urlList = urlList;
        this.url = url;
        this.state = state;
        this.name = name;
        this.artist = artist;
        this.album = album;
        mediaPlayer.stop();
        mediaPlayer.reset();
        artisttxt.setText(artist);
        songtxt.setText(name);
        artisttxtPlayer.setText(artist);
        songtxtPlayer.setText(name);
        albumtxt.setText(album);
        StorageReference storageReference = storage.getReference().child("album_images").child(album + ".jpg");
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try{
                    Picasso.with(getBaseContext()).load(uri).into(playerImage);

                }catch (IndexOutOfBoundsException i){}


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                playerImage.setImageDrawable(null);

            }
        });
        play();


    }



    @Override
    public void getDataFromHomeFragment(boolean state, String url, String name, String artist, String album, ArrayList<String> urlList) throws IOException {

        this.urlList = urlList;
        this.url = url;
        this.state = state;
        this.name = name;
        this.artist = artist;
        this.album = album;
        mediaPlayer.stop();
        mediaPlayer.reset();
        artisttxt.setText(artist);
        songtxt.setText(name);
        artisttxtPlayer.setText(artist);
        songtxtPlayer.setText(name);
        albumtxt.setText(album);
        StorageReference storageReference = storage.getReference().child("album_images").child(album + ".jpg");
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try{
                    Picasso.with(getBaseContext()).load(uri).into(playerImage);

                }catch (IndexOutOfBoundsException i){}


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                playerImage.setImageDrawable(null);

            }
        });
        play();

    }
    @Override
    public void getDataFromAlbumProfileFragment(boolean state, String url, String name, String artist, String album, ArrayList<String> urlList) throws IOException {
        this.urlList = urlList;
        this.url = url;
        this.state = state;
        this.name = name;
        this.artist = artist;
        this.album = album;
        mediaPlayer.stop();
        mediaPlayer.reset();
        artisttxt.setText(artist);
        songtxt.setText(name);
        artisttxtPlayer.setText(artist);
        songtxtPlayer.setText(name);
        albumtxt.setText(album);
        StorageReference storageReference = storage.getReference().child("album_images").child(album + ".jpg");
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try{
                    Picasso.with(getBaseContext()).load(uri).into(playerImage);

                }catch (IndexOutOfBoundsException i){}


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                playerImage.setImageDrawable(null);

            }
        });
        play();

    }



    @Override
    public void getDataFromshowPlayListFragment(boolean state, String url, String name, String artist, String album,ArrayList<String> urlList) throws IOException {

        this.urlList = urlList;
        this.url = url;
        this.state = state;
        this.name = name;
        this.artist = artist;
        this.album = album;
        mediaPlayer.stop();
        mediaPlayer.reset();
        artisttxt.setText(artist);
        songtxt.setText(name);
        artisttxtPlayer.setText(artist);
        songtxtPlayer.setText(name);
        albumtxt.setText(album);
        StorageReference storageReference = storage.getReference().child("album_images").child(album + ".jpg");
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try{
                    Picasso.with(getBaseContext()).load(uri).into(playerImage);

                }catch (IndexOutOfBoundsException i){}


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                playerImage.setImageDrawable(null);

            }
        });
        play();

    }



    @Override
    public void getDataFromLibPlaylistsFragment(boolean state, String playListName) throws IOException {
        showPlayListFragment.playList = playListName;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, showPlayListFragment).commit();

    }
    @Override
    public void getDataFromAlbumsFragment(boolean state, String name) throws IOException {
        toolbar.setVisibility(View.GONE);
        albumProfileFragment.albumName = name;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, albumProfileFragment).commit();
    }
    @Override
    public void getDataFromLibAlbumsFragment(boolean state, String name) throws IOException {
        toolbar.setVisibility(View.GONE);
        albumProfileFragment.albumName = name;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, albumProfileFragment).commit();
    }


    @Override
    public void getDataFromArtistProfileFragment(boolean state, String name) throws IOException {
        toolbar.setVisibility(View.GONE);
        albumProfileFragment.albumName = name;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, albumProfileFragment).commit();
    }
    @Override
    public void getDataFromLibArtistsFragment(boolean state, String name) throws IOException {
        toolbar.setVisibility(View.GONE);
        artistProfileFragment.artistName = name;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, artistProfileFragment).commit();
    }
    @Override
    public void getDataFromArtistsFragment(boolean state, String name) throws IOException {
        toolbar.setVisibility(View.GONE);
        artistProfileFragment.artistName = name;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, artistProfileFragment).commit();
    }
    @Override
    public void getDataFromProfileFragment(boolean state, String name) throws IOException {
        showPlayListFragment.playList = name;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, showPlayListFragment).commit();
    }

    @Override
    public void getDataFromSelfProfileFragment(boolean state, String name) throws IOException {
        showPlayListFragment.playList = name;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, showPlayListFragment).commit();
    }


    @Override
    public void getDataFromListFrag(String type, String selected) throws IOException {
        String str = type.substring(0,1).toUpperCase() + type.substring(1);
        switch (selected) {

            case "musics":
                toolbar.setTitle("Explore " + str);
                musicsFragment.type = type;
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, musicsFragment).commit();
                break;
            case "artists":
                toolbar.setTitle("Explore " + str);
                artistsFragment.type = type;
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, artistsFragment).commit();
                break;
            case "albums":
                toolbar.setTitle("Explore " + str);
                albumsFragment.type = type;
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, albumsFragment).commit();
                break;


        }


    }
    @Override
    public void getIdFromSearchFragment(boolean state, String id) {
        toolbar.setVisibility(View.GONE);
        profileFragment.userId = id;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, profileFragment).commit();

    }
    @Override
    public void getIdFromFriendsFragment(boolean state, String id) {
        toolbar.setVisibility(View.GONE);
        profileFragment.userId = id;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, profileFragment).commit();
    }

    @Override
    public void getMusicNameFromSearchFragment(boolean state,String url, String name, String artist, String album, ArrayList<String> urlList) {

        this.urlList = urlList;
        this.url = url;
        this.state = state;
        this.name = name;
        this.artist = artist;
        this.album = album;
        mediaPlayer.stop();
        mediaPlayer.reset();
        artisttxt.setText(artist);
        songtxt.setText(name);
        artisttxtPlayer.setText(artist);
        songtxtPlayer.setText(name);
        albumtxt.setText(album);
        StorageReference storageReference = storage.getReference().child("album_images").child(album + ".jpg");
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try{
                    Picasso.with(getBaseContext()).load(uri).into(playerImage);

                }catch (IndexOutOfBoundsException i){}


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                playerImage.setImageDrawable(null);

            }
        });
        play();
    }

    @Override
    public void getAlbumNameFromSearchFragment(boolean state, String name) {

        toolbar.setVisibility(View.GONE);
        albumProfileFragment.albumName = name;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, albumProfileFragment).commit();
    }

    @Override
    public void getArtistNameFromSearchFragment(boolean state, String name) {
        toolbar.setVisibility(View.GONE);
        artistProfileFragment.artistName = name;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, artistProfileFragment).commit();
    }




    @Override
    public void getDataFromFriendRequestFragment(boolean state, String id) throws IOException {
        toolbar.setVisibility(View.GONE);
        profileFragment.userId = id;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, profileFragment).commit();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.requests:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, friendRequestsFragment).commit();
                break;
            case R.id.friends:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, friendsFragment).commit();
                break;

            case R.id.sign_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginScreen.class));

                break;
        }
        return true;

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navlistener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;
                    switch (menuItem.getItemId()) {
                        case R.id.nav_home:
                            toolbar.setVisibility(VISIBLE);
                            toolbar.setTitle("Home");
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_explore:
                            toolbar.setVisibility(VISIBLE);
                            toolbar.setTitle("Explore");
                            selectedFragment = new ExploreFragment();
                            break;
                        case R.id.nav_library:
                            toolbar.setVisibility(VISIBLE);
                            toolbar.setTitle("Library");
                            selectedFragment = new LibraryFragment();
                            break;
                        case R.id.nav_profile:
                            toolbar.setVisibility(View.GONE);
                            selectedFragment = new SelfProfileFragment();
                            break;
                        case R.id.nav_search:
                            toolbar.setVisibility(View.GONE);
                            selectedFragment = new SearchFragment();
                            break;


                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    return true;
                }
            };


    public void play(){

        @SuppressLint("StaticFieldLeak") AsyncTask<String, String, String> mp3Play = new AsyncTask<String, String, String>() {

            @Override
            protected void onPreExecute() {
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Musics");
                dbRef.orderByChild("url").equalTo(url).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds:dataSnapshot.getChildren()){

                            String key = ds.getKey();
                            FirebaseAuth auth = FirebaseAuth.getInstance();
                            DatabaseReference ddRef = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid());
                            assert key != null;
                            ddRef.child("History").child(key).setValue(url);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {


                    }
                });
                FirebaseAuth auth =  FirebaseAuth.getInstance();
                DatabaseReference ddRef = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid());
                ddRef.child("lastListened").child("url").setValue(url);
                ddRef.child("lastListened").child("name").setValue(name);
                ddRef.child("lastListened").child("artist").setValue(artist);
                ddRef.child("lastListened").child("album").setValue(album);
                ddRef.child("lastUrlList").setValue(urlList);
            }

            @Override
            protected String doInBackground(String... params) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
                try {
                    mediaPlayer.setDataSource(url);
                    mediaPlayer.prepare();
                } catch (Exception ex) {

                }
                return "";
            }

            @SuppressLint("DefaultLocale")
            @Override
            protected void onPostExecute(String s) {

                mediaFileLength = mediaPlayer.getDuration();
                realtimeLength = mediaFileLength;
                mediaPlayer.start();
                play_pause.setBackgroundResource(R.drawable.ic_pause);
                play_pause_player.setBackgroundResource(R.drawable.ic_pause);
                timeToEnd.setText(String.format("%d", mediaFileLength));
                updateSeekBar();


            }

        };
        mp3Play.execute();
    }
    public void init(){

        DatabaseReference dbRef;
        FirebaseAuth auth = FirebaseAuth.getInstance();

        dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid());
        dbRef.child("lastListened").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {

                    url = dataSnapshot.child("url").getValue().toString();
                    name = dataSnapshot.child("name").getValue().toString();
                    artist = dataSnapshot.child("artist").getValue().toString();
                    album = dataSnapshot.child("album").getValue().toString();
                    songtxt.setText(name);
                    artisttxt.setText(artist);
                    albumtxt.setText(album);

                    StorageReference storageReference = storage.getReference().child("album_images").child(album + ".jpg");
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            try{
                                Picasso.with(getBaseContext()).load(uri).into(playerImage);

                            }catch (IndexOutOfBoundsException i){}


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        dbRef.child("lastUrlList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    for(DataSnapshot ds:dataSnapshot.getChildren() ){

                        urlList.add(ds.getValue().toString());

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void updateSeekBar() {
        seekBar.setProgress((int)(((float)mediaPlayer.getCurrentPosition() / mediaFileLength)*100));
        if(mediaPlayer.isPlaying())
        {
            Runnable updater = new Runnable() {
                @SuppressLint({"DefaultLocale", "SetTextI18n"})
                @Override
                public void run() {
                    updateSeekBar();
                    String elapsedTime = createTimeLabel(mediaPlayer.getCurrentPosition());
                    timeFromStart.setText(elapsedTime);

                    String remainingTime = createTimeLabel(mediaFileLength-mediaPlayer.getCurrentPosition());
                    timeToEnd.setText("- " + remainingTime);



                }

            };
            handler.postDelayed(updater,1000); // 1 second
        }
    }

    public String createTimeLabel(int time) {
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }




    @Override
    public void onBackPressed() {


        if (playerLayout.getVisibility() == VISIBLE) {
            playerLayout.setVisibility(View.GONE);
            bottomNavigationView.setVisibility(VISIBLE);


        }

    }



}