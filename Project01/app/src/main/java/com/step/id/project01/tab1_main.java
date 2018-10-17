package com.step.id.project01;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class tab1_main extends Fragment implements View.OnClickListener{

    private CardView pendingCard,projectCard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.tab1_main, container, false);


        pendingCard =(CardView)rootView.findViewById(R.id.pendingCard);
        pendingCard.setOnClickListener(this);

        projectCard = (CardView)rootView.findViewById(R.id.projectCard);
        projectCard.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
            case R.id.pendingCard:
                intent = new Intent(getActivity(), defectEditor.class);
                startActivity(intent);
                break;
            case R.id.projectCard:
                intent = new Intent(getActivity(),projectEditor.class);
                startActivity(intent);
                break;
                default:
                    break;
        }

    }
}
