package com.example.cronogramacapilar.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cronogramacapilar.R;
import com.example.cronogramacapilar.TreatmentsAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

import java.util.Date;

public class MarkAsCompleteBottomFragments extends BottomSheetDialogFragment {

    private Context context;
    private TreatmentsAdapter adapter;

    public MarkAsCompleteBottomFragments(TreatmentsAdapter treatmentsAdapter) {
        adapter = treatmentsAdapter;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.bottom_nav_layout, container, false);

        NavigationView navigationView = rootView.findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(adapter);
        navigationView.getMenu().getItem(0).setTitle("No dia correto (" + getArguments().getString("correctDate") + ")");
        navigationView.getMenu().getItem(1).setTitle("Hoje (" + DateFormat.format("dd/MM/yyyy", new Date()) + ")");

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
