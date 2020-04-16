package com.example.musichall;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class FriendRequestsFragment extends Fragment implements FriendRequestsAdapter.FriendRequestsAdapterListener{


    private ArrayList<String> idList = new ArrayList<>();
    private ArrayList<String> nameList = new ArrayList<>();
    private ArrayList<Uri> imgUriList = new ArrayList<>();
    private FirebaseAuth auth;
    DatabaseReference dbRef;
    private FirebaseStorage storage;
    private FriendRequestFragmentListener listener;

    public interface FriendRequestFragmentListener{

        void getDataFromFriendRequestFragment(boolean state, String id) throws IOException;


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_requests,container,false);
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Users");
        dbRef.child(auth.getUid()).child("friendRequests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String name = ds.getValue().toString();
                        String Id = ds.getKey();
                        if(!nameList.contains(name)){nameList.add(name);}
                        if(!idList.contains(Id)){idList.add(Id);}
                        StorageReference storageReference = storage.getReference().child("profile_images").child(Id);
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                if(!imgUriList.contains(uri)){imgUriList.add(uri);}

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });


                    }
                    initRecyclerView();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        return view;
    }

    private void initRecyclerView(){

        RecyclerView recyclerView = getView().findViewById(R.id.requestsRcyView);
        FriendRequestsAdapter adapter = new FriendRequestsAdapter(getContext(),nameList,imgUriList,idList,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void getIdFromFriendRequestsAdapter(boolean state, String id) throws IOException {

        listener.getDataFromFriendRequestFragment(state,id);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof FriendRequestFragmentListener){
            listener = (FriendRequestFragmentListener) context;
        }else{
            throw new RuntimeException(context.toString() + "HomeFragmentListener must be implemented.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
