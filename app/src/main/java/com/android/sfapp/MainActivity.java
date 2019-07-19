package com.android.sfapp;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.sfapp.model.MaterialCV;
import com.android.sfapp.utils.HomeViewPagerAdapter;
import com.android.sfapp.utils.MaterialsRVAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;

    private ViewPager vpHome;
    private FloatingActionButton btnAddItem;
    private RecyclerView rvMaterials;

    private BottomNavigationView bottomNavigationView;

    private int[] layouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dl = findViewById(R.id.dl);

        vpHome = findViewById(R.id.vp_home);
        layouts = new int[]{
                R.layout.home_frag_materials
        };

        HomeViewPagerAdapter homeViewPagerAdapter = new HomeViewPagerAdapter(layouts, getBaseContext());
        vpHome.setAdapter(homeViewPagerAdapter);

        abdt = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);
        abdt.setDrawerIndicatorEnabled(true);

        dl.addDrawerListener(abdt);
        abdt.syncState();

        NavigationView nav_view = findViewById(R.id.nav_view);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Manejo del Drawer
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id){
                    case R.id.menu_obras:
                        changeFragMaterials();
                        break;

                    case R.id.menu_proveedores:
                        Toast.makeText(getBaseContext(), "Proveedores", Toast.LENGTH_LONG).show();
                        break;

                    case R.id.menu_citas:
                        Toast.makeText(getBaseContext(), "Citas", Toast.LENGTH_LONG).show();
                        break;

                    case R.id.menu_reportes:
                        Toast.makeText(getBaseContext(), "Reportes", Toast.LENGTH_LONG).show();
                        break;
                }
                return true;
            }
        });
    }

    private void changeFragMaterials() {
        bottomNavigationView = findViewById(R.id.bnv_obras);
        bottomNavigationView.setSelectedItemId(0);
        loadObrasMaquinaria();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.menu_maquinaria:
                        loadObrasMaquinaria();
                        break;

                    case R.id.menu_materiales:
                        loadObrasMateriales();
                        break;

                    case R.id.menu_nomina:
                        loadObrasNomina();
                        break;
                }
                return true;
            }
        });
        changeViewPage(0);
    }

    private void loadObrasNomina() {
    }

    private void loadObrasMateriales() {

    }

    private void loadObrasMaquinaria() {
        btnAddItem = findViewById(R.id.btn_add_item);

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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return abdt.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    //Manejo de viewPager
    private void changeViews(int position) {
        switch (position){
            case 0:
                //frag_obras

                break;

            case 1:
                //frag_proveedores
                break;

            case 2:
                //frag_citas
                break;

            case 3:
                //frag_reportes
                break;
        }
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