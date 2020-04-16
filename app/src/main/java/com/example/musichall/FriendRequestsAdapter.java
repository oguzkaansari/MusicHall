package com.example.musichall;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.ViewHolder> {

    private ArrayList<String> mIds = new ArrayList<>();
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<Uri> mImgs = new ArrayList<>();
    private String selfName;
    private Context mContext;
    private FriendRequestsAdapterListener listener;

    public FriendRequestsAdapter(Context context, ArrayList<String> mNames, ArrayList<Uri> mImgs, ArrayList<String> mIds, FriendRequestsAdapterListener listener ) {
        this.mNames = mNames;
        this.mImgs = mImgs;
        this.mContext = context;
        this.mIds = mIds;
        this.listener = listener;
    }
    public interface FriendRequestsAdapterListener{
        void getIdFromFriendRequestsAdapter(boolean state, String id) throws IOException;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_requests_row,parent,false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {


        try{
        Picasso.with(mContext).load((mImgs.get(position))).into(holder.image);
        }catch (IndexOutOfBoundsException i){}
        holder.name.setText(mNames.get(position));
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    listener.getIdFromFriendRequestsAdapter(true, mIds.get(position));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = mIds.get(position);
                String name = mNames.get(position);
                FirebaseAuth auth = FirebaseAuth.getInstance();
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Users");

                dbRef.child(auth.getUid()).child("friends").child(id).setValue(name);
                dbRef.child(auth.getUid()).child("friendRequests").child(id).setValue(null);

                dbRef.child(id).child("friends").child(auth.getUid()).setValue(selfName);
                dbRef.child(id).child("sendedRequests").child(auth.getUid()).setValue(null);

                holder.itemView.findViewById(R.id.rcyParentLayout).setVisibility(View.GONE);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = mIds.get(position);
                FirebaseAuth auth = FirebaseAuth.getInstance();
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Users");
                dbRef.child(auth.getUid()).child("friendRequests").child(id).setValue(null);
                dbRef.child(id).child("sendedRequests").child(auth.getUid()).setValue(null);
                holder.itemView.findViewById(R.id.rcyParentLayout).setVisibility(View.GONE);


            }
        });
    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout relativeLayout;
        CircleImageView image;
        TextView name;
        Button accept,delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.rcyParentLayout);
            image = itemView.findViewById(R.id.requestImage);
            name = itemView.findViewById(R.id.requestName);
            accept = itemView.findViewById(R.id.acceptButton);
            delete = itemView.findViewById(R.id.deleteButton);
            FirebaseAuth auth = FirebaseAuth.getInstance();
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Users");
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


        }





    }


}
