package com.example.musichall;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
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

import java.util.ArrayList;

public class FriendsFragment extends Fragment {

    ArrayList<String> titleList,subTitleList,idList;
    private FirebaseAuth auth;
    DatabaseReference dbRef;
    private ListView friendList;
    private FriendsFragmentListener listener;
    public interface FriendsFragmentListener{

        void getIdFromFriendsFragment(boolean state,String id);

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends,container,false);
        titleList = new ArrayList<>();
        subTitleList = new ArrayList<>();
        idList = new ArrayList<>();
        friendList = view.findViewById(R.id.friendsListView);
        final ImageRowAdapter imageRowAdapter = new ImageRowAdapter(getActivity().getApplicationContext(),titleList,subTitleList,idList);
        auth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid()).child("friends");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    for(DataSnapshot ds:dataSnapshot.getChildren() ){

                        titleList.add(ds.getValue().toString());
                        subTitleList.add("");
                        idList.add(ds.getKey());

                    }
                    friendList.setAdapter(imageRowAdapter);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        friendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String userId = idList.get(position);
                listener.getIdFromFriendsFragment(true,userId);

            }
        });
        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof FriendsFragmentListener){
            listener = (FriendsFragmentListener) context;
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
