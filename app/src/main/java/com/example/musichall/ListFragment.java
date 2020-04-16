package com.example.musichall;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;

public class ListFragment extends Fragment {
    private ListFragmentListener listener;

    public interface ListFragmentListener{

        void getDataFromListFrag(String type,String selected) throws IOException;
    }
    String musicType;
    LinearLayout musicsLayout,artistLayout,albumsLayout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list,container,false);

        musicsLayout = (LinearLayout) view.findViewById(R.id.musicsLayout);
        artistLayout = (LinearLayout) view.findViewById(R.id.artistLayout);
        albumsLayout = (LinearLayout) view.findViewById(R.id.albumsLayout);

        musicsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    listener.getDataFromListFrag(musicType,"musics");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        artistLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    listener.getDataFromListFrag(musicType,"artists");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        albumsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    listener.getDataFromListFrag(musicType,"albums");
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
        if(context instanceof ListFragmentListener){
            listener = (ListFragmentListener) context;
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
