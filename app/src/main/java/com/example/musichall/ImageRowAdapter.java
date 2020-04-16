package com.example.musichall;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageRowAdapter extends ArrayAdapter<String> {

    Context context;
    ArrayList<String> rTitles;
    ArrayList<String> rSubTitles;
    ArrayList<String> Ids;
    FirebaseStorage storage;


    ImageRowAdapter(Context c,ArrayList<String> titles,ArrayList<String> subTitles,ArrayList<String> ids){
        super(c,R.layout.row_with_image,R.id.rowText, titles);
        rTitles = new ArrayList<>();
        rSubTitles = new ArrayList<>();
        Ids = new ArrayList<>();
        this.context = c;
        this.rTitles = titles;
        this.rSubTitles = subTitles;
        this.Ids = ids;

    }
    public void updateAdapter(ArrayList<String> titles,ArrayList<String> subTitles,ArrayList<String> Ids){
        rTitles.clear();
        rSubTitles.clear();
        this.rTitles = titles;
        this.rSubTitles = subTitles;
        this.Ids = Ids;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("ViewHolder") View row = layoutInflater.inflate(R.layout.row_with_image,parent,false);
        final ImageView image = row.findViewById(R.id.rowImage);
        TextView title = row.findViewById(R.id.rowText);
        TextView subTitle = row.findViewById(R.id.rowSubText);
        storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child("profile_images").child(Ids.get(position));
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try{
                    Picasso.with(getContext()).load(uri).into(image);

                }catch (IndexOutOfBoundsException i){}


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        /*try{
        Picasso.with(getContext()).load((rImgs.get(position))).into(image);

        }catch (IndexOutOfBoundsException i){}*/
        title.setText(rTitles.get(position));
        subTitle.setText(rSubTitles.get(position));

        return row ;

    }
}
