package com.example.musichall;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private ArrayList<String> mMainTexts;
    private ArrayList<String> mSubTexts;
    private ArrayList<String> mArtists;
    private ArrayList<String> mUrls;
    private Context mContext;
    private CardAdapterListener listener;
    public CardAdapter(Context context,ArrayList<String> mMainTexts, ArrayList<String> mSubTexts,ArrayList<String> mArtists, ArrayList<String> mUrls, CardAdapterListener listener) {
        this.mMainTexts = mMainTexts;//müzik adları
        this.mSubTexts = mSubTexts;//albüm adları
        this.mContext = context;
        this.mUrls = mUrls;
        this.mArtists = mArtists;
        this.listener = listener;
    }
    public interface CardAdapterListener{
        void getDataFromCardAdapter(boolean state, String url, String name, String artist, String album,ArrayList<String> urlList) throws IOException;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_item, parent, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder,final int position) {
        FirebaseStorage storage= FirebaseStorage.getInstance();

        holder.mainText.setText(mMainTexts.get(position));
        holder.subText.setText(mSubTexts.get(position));

        StorageReference storageReference = storage.getReference().child("album_images").child(mSubTexts.get(position) + ".jpg");
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try{
                    Picasso.with(mContext).load(uri).into(holder.image);

                }catch (IndexOutOfBoundsException i){}


            }
        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {

                holder.image.setImageResource(R.mipmap.default_profile_pic_man_foreground);
            }
        });

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    listener.getDataFromCardAdapter(true,mUrls.get(position), mMainTexts.get(position), mArtists.get(position), mSubTexts.get(position), mUrls);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMainTexts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

            RelativeLayout relativeLayout;
            ImageView image;
            TextView mainText,subText;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.card_image);
                mainText = itemView.findViewById(R.id.songName);
                subText = itemView.findViewById(R.id.albumName);
                relativeLayout = itemView.findViewById(R.id.cardRelativeLayout);


            }
        }


}
