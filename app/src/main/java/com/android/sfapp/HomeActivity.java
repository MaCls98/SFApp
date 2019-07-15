package com.android.sfapp;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.sfapp.R;
import com.android.sfapp.model.MaterialCV;
import com.android.sfapp.utils.HomeViewPagerAdapter;
import com.android.sfapp.utils.MaterialsRVAdapter;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private ViewPager vpHome;
    private RecyclerView rvMaterials;
    private FloatingActionButton btnAddItem;

    private int[] layouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initHome();
    }

    private void initHome() {
        vpHome = findViewById(R.id.vp_home);
        btnAddItem = findViewById(R.id.btn_add_item);


        //Ventanas de viewPager
        layouts = new int[]{
                R.layout.home_frag_materials
        };

        HomeViewPagerAdapter homeViewPagerAdapter = new HomeViewPagerAdapter(layouts, getBaseContext());
        vpHome.setAdapter(homeViewPagerAdapter);
    }

    private void changeViews(int position) {
        switch (position){
            case 0:
                //frag_materials

                break;

            case 1:
                //frag_maquinaria
                break;

            case 2:
                //frag_nomina
                break;
        }
    }

    public void changeFragMaterials(View view){
        rvMaterials = findViewById(R.id.rv_frag_materials);
        rvMaterials.setHasFixedSize(true);
        rvMaterials.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (!recyclerView.canScrollVertically(1)) {
                    btnAddItem.hide();
                }else {
                    btnAddItem.show();
                }
            }
        });

        RecyclerView.LayoutManager lmMaterials = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        ArrayList<MaterialCV> materials = new ArrayList<>();

        for (int i = 0; i < 10; i++){
            materials.add(new MaterialCV(i, "Material tipo " + i, i + "", "Hace " + i + " dias", i + ""));
        }

        MaterialsRVAdapter materialsAdapter = new MaterialsRVAdapter(materials);
        rvMaterials.setLayoutManager(lmMaterials);
        rvMaterials.setAdapter(materialsAdapter);

        changeViewPage(0);
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int position) {
            changeViews(position);
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    private void changeViewPage(int current) {
        vpHome.setCurrentItem(current, false);
    }
}