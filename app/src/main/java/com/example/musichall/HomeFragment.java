package com.example.musichall;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment implements CardAdapter.CardAdapterListener {

    private ArrayList<String>  nameList,albumList,artistList,urlList,nameListHist,albumListHist,artistListHist,urlListHist,
        nameListLib,albumListLib, artistListLib,urlListLib, nameListPop,artistListPop,albumListPop,urlListPop;


    ArrayAdapter<String> arrayAdapterHistory;
    ListView listViewHistory;
    private FirebaseAuth auth;
    DatabaseReference dbRef;
    private HomeFragmentListener listener;
    Button createWeeklyMix;
    private FirebaseFunctions mFunctions;
    boolean refresh = false;
    HomeFragment homeFragment;
    public interface HomeFragmentListener{

        void getDataFromHomeFragment(boolean state,String url,String name,String artist,String album,ArrayList<String> urlList) throws IOException;


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        auth = FirebaseAuth.getInstance();
        nameList = new ArrayList<>();
        albumList = new ArrayList<>();
        artistList = new ArrayList<>();
        urlList = new ArrayList<>();
        nameListHist = new ArrayList<>();
        albumListHist = new ArrayList<>();
        artistListHist = new ArrayList<>();
        urlListHist = new ArrayList<>();
        nameListLib = new ArrayList<>();
        albumListLib = new ArrayList<>();
        artistListLib = new ArrayList<>();
        urlListLib = new ArrayList<>();
        nameListPop = new ArrayList<>();
        artistListPop = new ArrayList<>();
        albumListPop = new ArrayList<>();
        urlListPop = new ArrayList<>();
        homeFragment = new HomeFragment();
        mFunctions = FirebaseFunctions.getInstance();
        createWeeklyMix = view.findViewById(R.id.createMix);
        createWeeklyMix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createWeeklyMix();

            }
        });

        listViewHistory = (ListView) view.findViewById(R.id.historyListview);

        final ProgressDialog progressdialog = new ProgressDialog(getActivity());
        progressdialog.setMessage("Please Wait....");

        final int interval = 3000; // 2 Second
        Handler handler = new Handler();
        Runnable runnable = new Runnable(){
            public void run() {

                homeFragment.refresh = true;
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, homeFragment)
                        .commit();
                progressdialog.dismiss();

            }

        };
        if(!refresh) {
            progressdialog.show();
            handler.postAtTime(runnable, System.currentTimeMillis() + interval);
            handler.postDelayed(runnable, interval);
        }else{

            dbRef = FirebaseDatabase.getInstance().getReference().child("Musics");
            dbRef.orderByChild("libCount").limitToLast(50).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            urlListPop.add(ds.child("url").getValue().toString());
                            nameListPop.add(ds.child("name").getValue().toString());
                            artistListPop.add(ds.child("artist").getValue().toString());
                            albumListPop.add(ds.child("album").getValue().toString());

                        }
                        initRecyclerViewMostPopular();


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid());
            dbRef.child("WeeklyMix").child("WeeklyMix").addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            String url = ds.getValue().toString();
                            urlList.add(url);
                            DatabaseReference ddRef = FirebaseDatabase.getInstance().getReference();
                            ddRef.child("Musics").orderByChild("url").equalTo(url).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                        nameList.add(ds.child("name").getValue().toString());
                                        albumList.add(ds.child("album").getValue().toString());
                                        artistList.add(ds.child("artist").getValue().toString());


                                    }
                                    initRecyclerViewMix();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        }


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });


            dbRef.child("Library").child("Musics").addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            String url = ds.getValue().toString();
                            //urlListLib.add(url);
                            DatabaseReference ddRef = FirebaseDatabase.getInstance().getReference();
                            ddRef.child("Musics").orderByChild("url").equalTo(url).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                        urlListLib.add(ds.child("url").getValue().toString());
                                        nameListLib.add(ds.child("name").getValue().toString());
                                        albumListLib.add(ds.child("album").getValue().toString());
                                        artistListLib.add(ds.child("artist").getValue().toString());
                                        initRecyclerViewLibrary();


                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });

            dbRef.child("History").addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            String url = ds.getValue().toString();
                            //urlListHist.add(url);
                            DatabaseReference ddRef = FirebaseDatabase.getInstance().getReference();
                            ddRef.child("Musics").orderByChild("url").equalTo(url).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                        urlListHist.add(ds.child("url").getValue().toString());
                                        nameListHist.add(ds.child("name").getValue().toString());
                                        albumListHist.add(ds.child("album").getValue().toString());
                                        artistListHist.add(ds.child("artist").getValue().toString());


                                    }
                                    arrayAdapterHistory = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, nameListHist);
                                    listViewHistory.setAdapter(arrayAdapterHistory);
                                    Helper.getListViewSize(listViewHistory);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });

            listViewHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String url = urlListHist.get(position);
                    String name = nameListHist.get(position);
                    String artist = artistListHist.get(position);
                    String album = albumListHist.get(position);

                    try {
                        listener.getDataFromHomeFragment(true, url, name, artist, album, urlList);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            });

        }
        return view;
    }

    private void initRecyclerViewMix(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = getActivity().findViewById(R.id.cardRcyViewMix);
        recyclerView.setLayoutManager(layoutManager);
        CardAdapter adapter = new CardAdapter(getContext(),nameList, albumList, artistList, urlList,this);
        recyclerView.setAdapter(adapter);
    }
    private void initRecyclerViewLibrary(){

        Collections.reverse(urlListPop);
        Collections.reverse(nameListPop);
        Collections.reverse(artistListPop);
        Collections.reverse(albumListPop);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = getActivity().findViewById(R.id.cardRcyViewHistory);
        recyclerView.setLayoutManager(layoutManager);
        CardAdapter adapter = new CardAdapter(getContext(),nameListLib, albumListLib, artistListLib, urlListLib,this);
        recyclerView.setAdapter(adapter);
    }
    private void initRecyclerViewMostPopular(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = getActivity().findViewById(R.id.cardRcyViewPopular);
        recyclerView.setLayoutManager(layoutManager);
        CardAdapter adapter = new CardAdapter(getContext(),nameListPop, albumListPop, artistListPop, urlListPop,this);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void getDataFromCardAdapter(boolean state, String url, String name, String artist, String album, ArrayList<String> urlList) throws IOException {

        listener.getDataFromHomeFragment(state,url,name,artist,album,urlList);

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof HomeFragmentListener){
            listener = (HomeFragmentListener) context;
        }else{
            throw new RuntimeException(context.toString() + "HomeFragmentListener must be implemented.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private Task<String> createWeeklyMix() {

        Map<String, Object> data = new HashMap<>();
        data.put("text", "");
        data.put("push", true);

        return mFunctions
                .getHttpsCallable("createWeeklyMix")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {

                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
    }

}

