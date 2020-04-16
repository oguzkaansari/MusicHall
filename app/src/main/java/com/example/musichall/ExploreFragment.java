package com.example.musichall;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ExploreFragment extends Fragment {
    private ExploreFragmentListener listener;
    String musicType;

    public interface ExploreFragmentListener{

            void getTypeFromExplore(String type);

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore,container,false);
        Button popButton = view.findViewById(R.id.pop);
        Button metalButton = view.findViewById(R.id.metal);
        Button rockButton = view.findViewById(R.id.rock);
        Button jazzButton = view.findViewById(R.id.jazz);
        Button rap_hiphopButton = view.findViewById(R.id.rap_hiphop);
        Button classicButton = view.findViewById(R.id.classic);

        popButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.getTypeFromExplore("pop");
            }
        });
        metalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.getTypeFromExplore("metal");
            }
        });
        rockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.getTypeFromExplore("rock");
            }
        });
        jazzButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.getTypeFromExplore("jazz");
            }
        });
        rap_hiphopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.getTypeFromExplore("rap/hiphop");
            }
        });
        classicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.getTypeFromExplore("classic");
            }
        });

        return view;
    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof ExploreFragmentListener){
            listener = (ExploreFragmentListener) context;
        }else{
            throw new RuntimeException(context.toString() + "ExploreFragmentListener must be implemented.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
