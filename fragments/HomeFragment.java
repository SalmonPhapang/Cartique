package com.car.cartique.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.car.cartique.R;
import com.car.cartique.custom.CustomMenuAdapter;
import com.car.cartique.model.GridMenu;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private GridView gridView;
    private CustomMenuAdapter menuAppAdapter;
    private ArrayList<GridMenu> gridMenuArrayList;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        gridMenuArrayList = new ArrayList<>();
        gridMenuArrayList.add(new GridMenu("Records", R.drawable.ic_menu_folder));
        gridMenuArrayList.add(new GridMenu("Quotes", R.drawable.ic_note));
        gridMenuArrayList.add(new GridMenu("Settings", R.drawable.ic_launcher));
        gridMenuArrayList.add(new GridMenu("Profile", R.drawable.ic_menu_user));
        gridMenuArrayList.add(new GridMenu("Help", R.drawable.ic_menu_help));
        gridMenuArrayList.add(new GridMenu("Search", R.drawable.ic_menu_search));
        gridMenuArrayList.add(new GridMenu("Calender", R.drawable.ic_menu_calender));

        View v = inflater.inflate(R.layout.fragment_home, container, false);
        gridView = v.findViewById(R.id.grid_view_image_text);
        menuAppAdapter = new CustomMenuAdapter(gridMenuArrayList, getActivity());
        gridView.setAdapter(menuAppAdapter);

        return v;
    }

}
