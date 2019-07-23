package com.android.sfapp;

import android.app.DatePickerDialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.sfapp.dialogs.AddMaterialDialog;
import com.android.sfapp.model.Machine;
import com.android.sfapp.model.MaterialCV;
import com.android.sfapp.model.Materials;
import com.android.sfapp.model.Nomina;
import com.android.sfapp.model.Obra;
import com.android.sfapp.utils.DatePickerFragment;
import com.android.sfapp.utils.HomeViewPagerAdapter;
import com.android.sfapp.utils.MaterialsRVAdapter;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

    private String selectedDate;

    private int[] layouts;

    private ArrayList<MaterialCV> materials;
    private ArrayList<Obra> obras;
    private ArrayList<Machine> maquinas;
    private ArrayList<Nomina> nominas;

    public static final String HOST = "https://cs-f.herokuapp.com";

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

        obras = new ArrayList<>();

        vpHome = findViewById(R.id.vp_home);

        materials = new ArrayList<>();
        layouts = new int[]{
                //Ventanas del DrawerTitle, se empieza a contar en 0
                R.layout.home_frag_obras,
                R.layout.home_frag_add_encargados,
                R.layout.home_frag_maq_nom,
                R.layout.home_frag_reportes,
                //Agregar obra
                R.layout.home_nv_add_obra,
                //Agregar material
                R.layout.home_nv_add_material,
                //Agregar nomina
                R.layout.home_add_nomina,
                //Agregar maquinaria
                R.layout.home_add_maquinaria,
                //Agregar nomina
                R.layout.home_add_nomina,
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
                        changeViews(2);
                        Toast.makeText(getBaseContext(), "Citas", Toast.LENGTH_LONG).show();
                        break;

                    case R.id.menu_reportes:
                        tvDrawerTitle.setText("Reportes");
                        changeViews(3);
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
        spObras = findViewById(R.id.sp_obras);
        spObras.setPrompt("Listado de obras");

        btnAddObra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initAddObra();
            }
        });

        loadObrasMaquinaria();

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
    }

    private void initAddObra() {
        changeViewPage(4);
        View v = getLayoutInflater().inflate(R.layout.home_nv_add_obra, vpHome);

        final EditText etNameObra = v.findViewById(R.id.et_obra_name);

        final TextView tvDateObra = v.findViewById(R.id.tv_fecha);

        Button btnDateObra = v.findViewById(R.id.btn_fecha_obra);
        btnDateObra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
                if (selectedDate != null){
                    tvDateObra.setText(selectedDate);
                }else {
                    Toast.makeText(getBaseContext(), "Por favor selecciona una fecha de agregacion", Toast.LENGTH_LONG).show();
                }
            }
        });
        final EditText etDireccionObra = v.findViewById(R.id.et_direccion);

        Button btnCancelarObra = v.findViewById(R.id.btn_cancelar_obra);
        btnCancelarObra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvDrawerTitle.setText("Obras");
                changeViewPage(0);
                initObras();
            }
        });

        Button btnAgregarObra = v.findViewById(R.id.btn_agregar_obra);
        btnAgregarObra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateEmptyFields(etNameObra, etDireccionObra) && selectedDate != null){
                    addMaquinaria(etNameObra.getText().toString(), etDireccionObra.getText().toString(), selectedDate);
                    selectedDate = null;
                }
            }
        });
    }

    private void addMaquinaria(String etNameObra, String etDireccionObra, String selectedDate) {
        getObras();
        OkHttpClient client = new OkHttpClient();

        FormBody.Builder formBuilder = new FormBody.Builder()
                .add("name_oeuvre", etNameObra)
                .add("date_start", selectedDate)
                .add("addres", etDireccionObra)
                .add("status_oeuvre", "A");

        RequestBody requestBody = formBuilder.build();

        Request request = new Request.Builder()
                .url(HOST + "/oeuvres/add")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("ERROR", e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d("OBRA", response.body().string());
            }
        });
    }

    private void loadObrasMateriales() {
        rvObra.setAdapter(null);
        obras.clear();
        loadSpinner(spObras);
        btnAddItem.setTitle("Agregar Materiales");
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Agregar nuevo material
                Toast.makeText(getBaseContext(), "Agregar Materiales", Toast.LENGTH_LONG).show();
                initAddMaterial();
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

    private void initAddMaterial() {
        changeViewPage(5);
        View v = getLayoutInflater().inflate(R.layout.home_nv_add_material, vpHome);
        final Spinner spMaterial = v.findViewById(R.id.sp_add_mat_type);
        spMaterial.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, Materials.values()));
        final EditText tvPrecioUnitario = v.findViewById(R.id.et_add_mat_precio);
        final EditText tvCantidad = v.findViewById(R.id.et_add_mat_cantidad);
        final EditText tvProveedor = v.findViewById(R.id.et_add_mat_proveedor);
        Button btnFecha = v.findViewById(R.id.btn_add_mat_fecha);
        final TextView tvFecha = v.findViewById(R.id.tv_add_mat_fecha);
        final Spinner spNombreObra = v.findViewById(R.id.sp_add_mat_obra);
        ArrayAdapter<Obra> sAdapter = new ArrayAdapter<>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, obras);
        sAdapter.setDropDownViewResource(R.layout.spinner_item);
        spNombreObra.setAdapter(sAdapter);
        Button btnCancelar = v.findViewById(R.id.btn_add_mat_cancel);
        Button btnAgregar = v.findViewById(R.id.btn_add_mat_confirm);

        btnFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
                if (selectedDate != null){
                    tvFecha.setText(selectedDate);
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvDrawerTitle.setText("Obras");
                changeViewPage(0);
                initObras();
            }
        });

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateEmptyFields(tvPrecioUnitario, tvCantidad) && validateEmptyFields(tvProveedor, null) && selectedDate != null){
                    String [] values = spMaterial.getSelectedItem().toString().split(",");
                    addMaterial(tvPrecioUnitario.getText().toString(), tvCantidad.getText().toString(), tvProveedor.getText().toString(), tvFecha.getText().toString(), values[1], getObraId(spNombreObra.getSelectedItem().toString()));
                }
            }
        });
    }

    private void addMaterial(String precioUnitario, String cantidad, String proveedor, String fecha, String tipoMaterial, String idObra) {
        OkHttpClient client = new OkHttpClient();

        FormBody.Builder formBuilder = new FormBody.Builder()
                .add("price_unit", precioUnitario)
                .add("quantity", cantidad)
                .add("name_provider", proveedor)
                .add("date_order", fecha)
                .add("type_material", tipoMaterial)
                .add("id_oeuvre", idObra);

        RequestBody requestBody = formBuilder.build();

        Request request = new Request.Builder()
                .url(HOST + "/materials/add")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("ERROR", e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d("MATERIALES", response.body().string());
            }
        });
    }

    private String getObraId(String name) {
        String id = "";
        for (int i = 0; i < obras.size(); i++){
            if (name.equalsIgnoreCase(obras.get(i).getObraName())){
                id = String.valueOf(obras.get(i).getObraId());
            }
        }
        return id;
    }

    private void loadObrasNomina() {
        rvObra.setAdapter(null);
        obras.clear();
        loadSpinner(spObras);
        btnAddItem.setTitle("Asignar Nomina");
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Asignar Nomina", Toast.LENGTH_LONG).show();
                changeViewPage(6);
            }
        });
    }

    private void loadObrasMaquinaria() {
        rvObra.setAdapter(null);
        obras.clear();
        loadSpinner(spObras);
        btnAddItem.setTitle("Asignar Maquinaria");
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Asignar Maquinaria", Toast.LENGTH_LONG).show();
                changeViewPage(7);
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
        getObras();

        ArrayAdapter<Obra> sAdapter = new ArrayAdapter<>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, obras);
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

    private void showDatePickerDialog() {

        DatePickerFragment dateFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                selectedDate = year + "-" + (month+1) + "-" + day;
            }
        });
        dateFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public boolean validateEmptyFields(EditText etOne, EditText etTwo){
        if(etOne.getText().toString().isEmpty() && etTwo != null && etTwo.getText().toString().isEmpty()){
            Toast.makeText(getBaseContext(), "Todos los campos deben estar completos", Toast.LENGTH_SHORT).show();
            return false;
        }else if(etOne.getText().toString().isEmpty()){
            Toast.makeText(getBaseContext(), "Todos los campos deben estar completos", Toast.LENGTH_SHORT).show();
            return false;
        }else if(etTwo != null && etTwo.getText().toString().isEmpty() ){
            Toast.makeText(getBaseContext(), "Todos los campos deben estar completos", Toast.LENGTH_SHORT).show();
            return false;
        }else if(etOne.getText().toString().isEmpty() && etTwo == null){
            Toast.makeText(getBaseContext(), "Todos los campos deben estar completos", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void getObras(){
        OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(HOST + "/oeuvres")
                    .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("ERROR", e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    JSONArray array = new JSONArray(response.body().string());
                    for (int i = 0; i < array.length(); i++){
                        JSONObject row = array.getJSONObject(i);
                        obras.add(new Obra(row.getInt("id_oeuvre"),
                                row.getString("name_oeuvre"),
                                row.getString("date_start"),
                                "En progreso",
                                row.getString("status_oeuvre"),
                                row.getString("addres")));
                    }
                    Log.d("OBRAS", obras.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getMachines(){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(HOST + "/machines")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("ERROR", e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    JSONArray array = new JSONArray(response.body().string());
                    for (int i = 0; i < array.length(); i++){
                        JSONObject row = array.getJSONObject(i);
                        maquinas.add(new Machine(row.getInt("id_machine"),
                                row.getString("name_machine"),
                                row.getString("date_purchase"),
                                row.getString("status_machine")));
                    }
                    Log.d("MAQUINAS", obras.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getNomina(){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(HOST + "/persons/employees")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("ERROR", e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    JSONArray array = new JSONArray(response.body().string());
                    for (int i = 0; i < array.length(); i++){
                        JSONObject row = array.getJSONObject(i);
                        nominas.add(new Nomina(row.getInt("number_doc"),
                                row.getString("type_doc"),
                                row.getString("last_name"),
                                row.getString("first_name"),
                                row.getString("phone"),
                                row.getString("email"),
                                row.getString("salary")));
                    }
                    Log.d("NOMINA", obras.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
