package com.example.musichall;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class SelfProfileFragment extends Fragment{

    private ImageView imageView;
    private TextView nameTextView;
    private ListView sharedPlayListsView;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    private String name;
    ArrayList<String> sharedPlayListsList;
    ArrayAdapter<String> arrayAdapterPlaylists;

    private FirebaseAuth auth;
    DatabaseReference dbRef;
    FirebaseStorage storage;
    private SelfProfileFragmentListener listener;
    public interface SelfProfileFragmentListener{

        void getDataFromSelfProfileFragment(boolean state,String name) throws IOException;

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_self_profile,container,false);
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        //storageReference = storage.getReference();
        imageView = view.findViewById(R.id.self_profile_pic);
        nameTextView = view.findViewById(R.id.selfNameText);
        sharedPlayListsView = view.findViewById(R.id.selfSharedPlaylists);
        sharedPlayListsList = new ArrayList<String>();

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);



        dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid()).child("haveProfilePic");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String haveProfilePic = dataSnapshot.getValue().toString();

                    if(haveProfilePic.equals("yes")) {
                        StorageReference storageReference = storage.getReference().child("profile_images").child(auth.getUid());
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
        dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid()).child("name");
        dbRef.addValueEventListener(new ValueEventListener() {
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

        dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid()).child("sharedPlaylists");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);



            }
        });

        sharedPlayListsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                    listener.getDataFromSelfProfileFragment(true,sharedPlayListsList.get(position));
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePath = data.getData();
            try{

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),filePath);
                imageView.setImageBitmap(bitmap);
                if(filePath != null){

                    final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();

                    StorageReference storageReference = storage.getReference().child("profile_images/" + auth.getUid());
                    storageReference.putFile(filePath)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(),"Profil Picture Uploaded", Toast.LENGTH_SHORT).show();
                                    dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid());
                                    dbRef.child("haveProfilePic").setValue("yes");

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(),"Failed", Toast.LENGTH_SHORT).show();

                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage("Uploaded " + (int)progress+"%");


                                }
                            });


                }

            }catch (IOException e){

                e.printStackTrace();
            }


        }


    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof SelfProfileFragmentListener){
            listener = (SelfProfileFragmentListener) context;
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
