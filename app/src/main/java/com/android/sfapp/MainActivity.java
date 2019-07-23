package com.android.sfapp;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.sfapp.dialogs.AddMaterialDialog;
import com.android.sfapp.model.MaterialCV;
import com.android.sfapp.utils.HomeViewPagerAdapter;
import com.android.sfapp.utils.MaterialsRVAdapter;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AddMaterialDialog.AddMaterialListener {

    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;

    private TextView tvDrawerTitle;
    private ImageButton btnShowDrawer;

    private ViewPager vpHome;
    private LayoutInflater inflater;

    private FloatingActionsMenu floatingButtonObra;
    private FloatingActionButton btnAddItem;
    private FloatingActionButton btnAddObra;
    private FloatingActionButton btnAddProveedor;

    private RecyclerView rvObra;
    private MaterialsRVAdapter maquinariaAdapter;

    private Spinner spObras;

    private BottomNavigationView bottomNavigationView;

    private int[] layouts;
    private ArrayList<MaterialCV> materials;

    public static final String HOST = "https://cs-f.herokuapp.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initHomeActivity();
    }

    private void initHomeActivity() {
        dl = findViewById(R.id.dl);
        tvDrawerTitle = findViewById(R.id.tv_drawer_title);
        btnShowDrawer = findViewById(R.id.btn_drawer);

        vpHome = findViewById(R.id.vp_home);

        materials = new ArrayList<>();
        layouts = new int[]{
                //Ventanas del DrawerTitle, se empieza a contar en 0
                R.layout.home_frag_obras,
                R.layout.home_frag_add_colaboradores,
                R.layout.home_frag_maq_nom,
                R.layout.home_frag_reportes,
                //Obras
                //Agregar obra
                R.layout.home_nv_add_obra,
                //Agregar nomina
                R.layout.home_add_nomina
        };

        HomeViewPagerAdapter homeViewPagerAdapter = new HomeViewPagerAdapter(layouts, getBaseContext());
        vpHome.setAdapter(homeViewPagerAdapter);

        abdt = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);
        abdt.setDrawerIndicatorEnabled(true);

        dl.addDrawerListener(abdt);
        abdt.syncState();

        final NavigationView nav_view = findViewById(R.id.nav_view);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Acciones que se realizan en cada accion del drawer, como cambiar pagina
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id){
                    case R.id.menu_obras:
                        tvDrawerTitle.setText("Obras");
                        changeViewPage(0);
                        initObras();
                        break;

                    case R.id.menu_encargados:
                        tvDrawerTitle.setText("Encargados");
                        changeViewPage(1);
                        initObras();
                        break;

                    case R.id.menu_maquinas_nomina:
                        tvDrawerTitle.setText("Maquinaria y Nomina");
                        Toast.makeText(getBaseContext(), "Citas", Toast.LENGTH_LONG).show();
                        break;

                    case R.id.menu_reportes:
                        tvDrawerTitle.setText("Reportes");
                        Toast.makeText(getBaseContext(), "Reportes", Toast.LENGTH_LONG).show();
                        break;
                }
                return true;
            }
        });
        btnShowDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dl.openDrawer(Gravity.START);
            }
        });
    }

    private void initObras() {
        rvObra = findViewById(R.id.rv_frag_materials);
        bottomNavigationView = findViewById(R.id.bnv_obras);
        floatingButtonObra = findViewById(R.id.btn_add_item);
        btnAddItem = findViewById(R.id.add_item_obra);
        btnAddObra = findViewById(R.id.add_obra);
        btnAddProveedor = findViewById(R.id.add_proveedor);
        spObras = findViewById(R.id.sp_obras);
        loadSpinner(spObras);

        btnAddObra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Agregar Obra", Toast.LENGTH_LONG).show();
                initAddObra();
                changeViewPage(4);
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.menu_maquinaria:
                        floatingButtonObra.collapse();
                        loadObrasMaquinaria();
                        break;

                    case R.id.menu_materiales:
                        floatingButtonObra.collapse();
                        loadObrasMateriales();
                        break;

                    case R.id.menu_nomina:
                        floatingButtonObra.collapse();
                        loadObrasNomina();
                        break;
                }
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_maquinaria);
        loadObrasMaquinaria();
    }

    private void initAddObra() {
        //TEST
    }

    private void loadObrasNomina() {
        btnAddProveedor.setVisibility(View.GONE);
        rvObra.setAdapter(null);
        btnAddItem.setTitle("Agregar Nomina");
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Agregar Nomina", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadObrasMateriales() {
        btnAddProveedor.setVisibility(View.VISIBLE);
        rvObra.setAdapter(null);
        btnAddItem.setTitle("Agregar Materiales");
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Agregar nuevo material
                Toast.makeText(getBaseContext(), "Agregar Materiales", Toast.LENGTH_LONG).show();
                openAddMaterialDialog();
            }
        });

        RecyclerView.LayoutManager lmMaterials = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        for (int i = 0; i < 10; i++){
            materials.add(new MaterialCV(i, i++, "Tipo de material: " + i, "unidad generica",
                    "Cantidad: " + i, "Proveedor : " + i, "Fecha: " + i, "$" + i));
        }

        maquinariaAdapter = new MaterialsRVAdapter(materials);
        rvObra.setLayoutManager(lmMaterials);
        rvObra.setAdapter(maquinariaAdapter);
    }

    private void openAddMaterialDialog() {
        AddMaterialDialog materialDialog = new AddMaterialDialog();
        materialDialog.show(
                getSupportFragmentManager(), "Agregar nuevo material"
        );
    }

    private void loadObrasMaquinaria() {
        btnAddProveedor.setVisibility(View.GONE);
        rvObra.setAdapter(null);
        btnAddItem.setTitle("Agregar Maquinaria");
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Agregar Maquinaria", Toast.LENGTH_LONG).show();
            }
        });
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

    private void loadSpinner(Spinner spObras) {
        String[] o = new String[]{
                "Obra 1",
                "Obra 2",
                "Obra 3"
        };

        List<String> obras = new ArrayList<>(Arrays.asList(o));

        ArrayAdapter<String> sAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item, obras);
        sAdapter.setDropDownViewResource(R.layout.spinner_item);
        spObras.setAdapter(sAdapter);
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

    @Override
    public void addMaterials(int materialObraId, String materialType, String materialUnit, String materialQuantity, String materialProveedor, String materialDate, String materialPrice) {
        Log.d("MATERIAL: ", materialType + "-" + materialUnit + "-" + materialObraId);
    }
}