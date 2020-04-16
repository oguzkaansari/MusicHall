package com.example.musichall;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

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
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ProfileFragment extends Fragment {

    String userId;
    String selfName;
    private ImageView imageView;
    private TextView nameTextView;
    private ListView sharedPlayListsView;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    private String name;
    private String friendState = "request";
    private Button addFriendButton;
    ArrayList<String> sharedPlayListsList;
    ArrayAdapter<String> arrayAdapterPlaylists;
    private FirebaseAuth auth;
    DatabaseReference dbRef,notificationRef;
    FirebaseStorage storage;
    private ProfileFragmentListener listener;
    public interface ProfileFragmentListener{

        void getDataFromProfileFragment(boolean state,String name) throws IOException;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        //storageReference = storage.getReference();
        imageView = view.findViewById(R.id.profile_pic);
        nameTextView = view.findViewById(R.id.nameText);
        sharedPlayListsView = view.findViewById(R.id.sharedPlaylists);
        sharedPlayListsList = new ArrayList<>();
        addFriendButton = view.findViewById(R.id.addFriendButton);
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);


        dbRef = FirebaseDatabase.getInstance().getReference().child("Users");

        dbRef.child(userId).child("haveProfilePic").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String haveProfilePic = dataSnapshot.getValue().toString();

                    if(haveProfilePic.equals("yes")) {
                        StorageReference storageReference = storage.getReference().child("profile_images").child(userId);
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.with(getContext()).load(uri).into(imageView);
                                progressDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "Image retrieving failed", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        dbRef.child(userId).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){


                    name = dataSnapshot.getValue().toString();
                    nameTextView.setText(name);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dbRef.child(userId).child("sharedPlaylists").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    sharedPlayListsList.clear();
                    for(DataSnapshot ds:dataSnapshot.getChildren() ){
                        String playlist = ds.getKey();
                        sharedPlayListsList.add(playlist);


                    }
                    arrayAdapterPlaylists = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, sharedPlayListsList);
                    sharedPlayListsView.setAdapter(arrayAdapterPlaylists);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dbRef.child(auth.getUid()).child("friends").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists()){

                        for(DataSnapshot ds:dataSnapshot.getChildren() ) {

                            if(ds.getKey().equals(userId)){
                                addFriendButton.setText("Remove Friend");
                                friendState = "friend";
                                break;
                            }
                        }

                        if(!friendState.equals("friend")) {
                            dbRef.child(auth.getUid()).child("sendedRequests").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.exists()) {

                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                            if (ds.getKey().equals(userId)) {
                                                addFriendButton.setText("Requested");
                                                friendState = "request";
                                                break;
                                            }
                                        }

                                        if(!friendState.equals("request")){
                                            addFriendButton.setText("Add Friend");
                                            friendState = "default";
                                        }
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

        dbRef.child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        selfName = dataSnapshot.child("name").getValue().toString();

                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                if(friendState.equals("default")){
                    addFriendButton.setText("Requested");
                    dbRef.child(userId).child("friendRequests").child(auth.getUid()).setValue(selfName);
                    dbRef.child(auth.getUid()).child("sendedRequests").child(userId).setValue(name);
                    friendState = "request";

                    notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
                    HashMap<String,String> notificationMap = new HashMap<>();
                    notificationMap.put("from", userId);
                    notificationMap.put("type", "request");
                    notificationRef.child(userId).push().setValue(notificationMap);




                }else if(friendState.equals("request")){
                    addFriendButton.setText("Add Friend");
                    dbRef.child(userId).child("friendRequests").child(auth.getUid()).setValue(null);
                    dbRef.child(auth.getUid()).child("sendedRequests").child(userId).setValue(null);
                    friendState = "default";


                }else{

                    new AlertDialog.Builder(getActivity())
                            .setTitle("Remove Friend")
                            .setMessage("Are you sure to remove this friend?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {


                                    addFriendButton.setText("Add Friend");
                                    dbRef.child(auth.getUid()).child("friends").child(userId).setValue(null);
                                    dbRef.child(userId).child("friends").child(auth.getUid()).setValue(null);
                                    friendState = "default";
                                }
                            })

                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();





                }

            }
        });

        sharedPlayListsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                    listener.getDataFromProfileFragment(true,sharedPlayListsList.get(position));
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });


        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof ProfileFragmentListener){
            listener = (ProfileFragmentListener) context;
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
