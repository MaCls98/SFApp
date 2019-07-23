package com.android.sfapp;

import android.app.DatePickerDialog;
import android.os.Handler;
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
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.sfapp.model.Documentos;
import com.android.sfapp.model.Machine;
import com.android.sfapp.model.Material;
import com.android.sfapp.model.MaterialCV;
import com.android.sfapp.model.Materials;
import com.android.sfapp.model.Nomina;
import com.android.sfapp.model.Obra;
import com.android.sfapp.utils.DatePickerFragment;
import com.android.sfapp.utils.HomeViewPagerAdapter;
import com.android.sfapp.utils.MaquinariaRVAdapter;
import com.android.sfapp.utils.MaterialsRVAdapter;
import com.android.sfapp.utils.NominaRVAdapter;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;

    private TextView tvDrawerTitle;
    private ImageButton btnShowDrawer;

    private ViewPager vpHome;

    private FloatingActionsMenu floatingButtonObra;
    private FloatingActionButton btnAddItem;
    private FloatingActionButton btnAddObra;

    private RecyclerView rvObra;
    private MaterialsRVAdapter maquinariaAdapter;

    private Spinner spObras;
    private ArrayAdapter<Obra> sAdapter;
    private ArrayList<Machine> machines = new ArrayList<>();
    private ArrayList<Nomina> nominas = new ArrayList<>();
    private ArrayList<Material> materialList = new ArrayList<>();

    private BottomNavigationView bottomNavigationView;

    private String selectedDate;

    private int[] layouts;

    private ArrayList<MaterialCV> materials;
    private ArrayList<Obra> obras;

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
        getObras();

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
                        initEncargados();
                        break;

                    case R.id.menu_maquinas_nomina:
                        tvDrawerTitle.setText("Maquinaria y Nomina");
                        changeViewPage(2);
                        initMaquiNomina();
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

    private void initMaquiNomina() {
        View v = getLayoutInflater().inflate(R.layout.home_frag_maq_nom, vpHome);

        final FloatingActionsMenu floatingButtonMaqNom = v.findViewById(R.id.fl);

        final FloatingActionButton btnAddMaqNom = v.findViewById(R.id.btn_add_maq_nom);
        BottomNavigationView bottomMaqNom = v.findViewById(R.id.bottom_maq_nom);
        bottomMaqNom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.menu_list_maquinaria:
                        floatingButtonMaqNom.collapse();
                        Toast.makeText(MainActivity.this, "Lista maquinas", Toast.LENGTH_SHORT).show();

                        btnAddMaqNom.setTitle("Agregar maquinaria");
                        btnAddMaqNom.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                initAddMaquinaria();
                            }
                        });
                        break;

                    case R.id.menu_list_nomina:
                        floatingButtonMaqNom.collapse();
                        Toast.makeText(MainActivity.this, "Lista nomina", Toast.LENGTH_SHORT).show();

                        btnAddMaqNom.setTitle("Agregar nomina");
                        btnAddMaqNom.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                initAddNomina();
                            }
                        });
                        break;
                }
                return true;
            }
        });
    }

    private void initAddNomina() {
        changeViewPage(6);
        View v = getLayoutInflater().inflate(R.layout.home_add_nomina, vpHome);
        final EditText etNombre = v.findViewById(R.id.et_nomina_nombre);
        final EditText etApellido = v.findViewById(R.id.et_nomina_apellido);

        final EditText etNumDoc = v.findViewById(R.id.et_doc_num);
        final EditText etTelefono = v.findViewById(R.id.et_nomina_telefono);
        final EditText etSalario = v.findViewById(R.id.et_nomina_salario);

        Button btnCancelar = v.findViewById(R.id.btn_cancelar_nomina);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvDrawerTitle.setText("Maquinaria y Nomina");
                changeViewPage(2);
                initMaquiNomina();
            }
        });

        Button btnAgregar = v.findViewById(R.id.btn_agregar_nomina);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateEmptyFields(etNombre, etApellido) && validateEmptyFields(etNumDoc, etTelefono) && validateEmptyFields(etSalario, null)){
                    uploadNomina(etNombre, etApellido, etNumDoc, etTelefono, etSalario);
                }
            }
        });
    }



    private void initAddMaquinaria() {
        changeViewPage(7);
        View v = getLayoutInflater().inflate(R.layout.home_add_maquinaria, vpHome);
        final EditText nombre = v.findViewById(R.id.et_maquinaria_nombre);
        Button btnFecha = findViewById(R.id.btn_fecha_maquinaria);
        final TextView tvDateObra = v.findViewById(R.id.tv_date_maquinaria);

        btnFecha.setOnClickListener(new View.OnClickListener() {
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

        Button cancelar = v.findViewById(R.id.btn_cancelar_maquinaria);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvDrawerTitle.setText("Maquinaria y Nomina");
                changeViewPage(2);
                initMaquiNomina();
            }
        });

        Button agregar = v.findViewById(R.id.btn_agregar_maquinaria);
        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateEmptyFields(nombre, null) && selectedDate.length() > 0){
                    uploadMaquinaria(nombre.getText().toString(), selectedDate);
                }
            }
        });
    }



    private void initEncargados() {
        View v = getLayoutInflater().inflate(R.layout.home_frag_add_encargados, vpHome);
        FloatingActionButton btnAddEncargado = v.findViewById(R.id.btn_add_encargado);
        btnAddEncargado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEncargado();
            }
        });
    }

    private void addEncargado() {
        changeViewPage(6);
        View v = getLayoutInflater().inflate(R.layout.home_add_nomina, vpHome);
        LinearLayout llFields = v.findViewById(R.id.ll_fields);
        final EditText etPassword = new EditText(this);
        etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        etPassword.setHint("Contraseña del encargado");
        llFields.addView(etPassword);
        TextView tvTitle = v.findViewById(R.id.tv_title);
        tvTitle.setHint("Agregar Encargado");
        final EditText etNombre = v.findViewById(R.id.et_nomina_nombre);
        etNombre.setHint("Nombre encargado");
        final EditText etApellido = v.findViewById(R.id.et_nomina_apellido);
        etApellido.setHint("Apellido encargado");

        final EditText etNumDoc = v.findViewById(R.id.et_doc_num);
        final EditText etTelefono = v.findViewById(R.id.et_nomina_telefono);
        etTelefono.setHint("Telefono encargado");
        final EditText etSalario = v.findViewById(R.id.et_nomina_salario);
        etSalario.setText("0");
        etSalario.setVisibility(View.GONE);

        Button btnCancelar = v.findViewById(R.id.btn_cancelar_nomina);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvDrawerTitle.setText("Encargados");
                changeViewPage(1);
                initEncargados();
            }
        });

        Button btnAgregar = v.findViewById(R.id.btn_agregar_nomina);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateEmptyFields(etNombre, etApellido)
                        && validateEmptyFields(etNumDoc, etSalario)
                        && validateEmptyFields(etTelefono, etPassword)){
                    uploadEncargado(etNombre, etApellido, etNumDoc, etSalario, etTelefono, etPassword);
                }

            }
        });
    }



    private void initObras() {
        View obras = getLayoutInflater().inflate(R.layout.home_frag_obras, vpHome);
        rvObra = obras.findViewById(R.id.rv_frag_materials);
        bottomNavigationView = obras.findViewById(R.id.bnv_obras);
        floatingButtonObra = obras.findViewById(R.id.btn_add_item);
        btnAddItem = obras.findViewById(R.id.add_item_obra);
        btnAddObra = obras.findViewById(R.id.add_obra);
        spObras = obras.findViewById(R.id.sp_obras);
        loadSpinner(spObras);

        btnAddObra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initAddObra();
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.menu_maquinaria:
                        rvObra.setAdapter(null);
                        floatingButtonObra.collapse();
                        getMachines();
                        Runnable rMaquinas = new Runnable() {
                            @Override
                            public void run() {
                                loadObrasMaquinaria();
                            }
                        };
                        Handler hMaquinas = new Handler();
                        hMaquinas.postDelayed(rMaquinas, 2000);
                        break;

                    case R.id.menu_materiales:
                        rvObra.setAdapter(null);
                        floatingButtonObra.collapse();
                        getMateriales();
                        Runnable rMateriales = new Runnable() {
                            @Override
                            public void run() {
                                loadObrasMateriales();
                            }
                        };
                        Handler hMateriales = new Handler();
                        hMateriales.postDelayed(rMateriales, 2000);
                        break;

                    case R.id.menu_nomina:
                        rvObra.setAdapter(null);
                        floatingButtonObra.collapse();
                        getNomina();
                        Runnable rNomina = new Runnable() {
                            @Override
                            public void run() {
                                loadObrasNomina();
                            }
                        };
                        Handler hNomina = new Handler();
                        hNomina.postDelayed(rNomina, 2000);
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

    private void loadObrasNomina() {
        btnAddItem.setTitle("Asignar Nomina");
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Asignar Nomina", Toast.LENGTH_LONG).show();
            }
        });

        RecyclerView.LayoutManager lmNomina = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        NominaRVAdapter nominaRVAdapter = new NominaRVAdapter(nominas);
        rvObra.setLayoutManager(lmNomina);
        rvObra.setAdapter(nominaRVAdapter);
    }

    private void loadObrasMaquinaria() {
        btnAddItem.setTitle("Asignar Maquinaria");
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Asignar Maquinaria", Toast.LENGTH_LONG).show();
            }
        });

        RecyclerView.LayoutManager lmMaquinaria = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        MaquinariaRVAdapter maquinariaRVAdapter = new MaquinariaRVAdapter(machines);
        rvObra.setLayoutManager(lmMaquinaria);
        rvObra.setAdapter(maquinariaRVAdapter);
    }

    private void loadObrasMateriales() {
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

        for (Material m:
                materialList) {
            materials.add(new MaterialCV(m.getIdOrder(), m.getIdOeuvre(), m.getTypeMaterial(), "n", m.getQuantity(), m.getNameProvider(), m.getDateOrder(), m.getPriceUni()));
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
                //frag_encargados
                break;

            case 2:
                //frag_maq_nom
                break;
        }
    }

    private void uploadMaquinaria(String toString, String selectedDate) {
        OkHttpClient client = new OkHttpClient();

        FormBody.Builder formBuilder = new FormBody.Builder()
                .add("name_machine", toString)
                .add("date_purchase", selectedDate)
                .add("status_machine", "DI");

        RequestBody requestBody = formBuilder.build();

        Request request = new Request.Builder()
                .url(HOST + "/machines/add")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("ERROR", e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d("ENCARGADO", response.body().string());
            }
        });
    }

    private void uploadEncargado(EditText etNombre, EditText etApellido, EditText etNumDoc, EditText etSalario, EditText etTelefono, EditText etPassword) {
        OkHttpClient client = new OkHttpClient();

        FormBody.Builder formBuilder = new FormBody.Builder()
                .add("number_doc", etNumDoc.getText().toString())
                .add("type_doc", "CC")
                .add("last_name", etApellido.getText().toString())
                .add("first_name", etNombre.getText().toString())
                .add("phone", etTelefono.getText().toString())
                .add("type_person", "U")
                .add("password", etPassword.getText().toString())
                .add("type_user", "EN")
                .add("status_user", "H")
                .add("salary", "0");

        RequestBody requestBody = formBuilder.build();

        Request request = new Request.Builder()
                .url(HOST + "/persons/add")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("ERROR", e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d("ENCARGADO", response.body().string());
            }
        });
    }

    private void uploadNomina(EditText etNombre, EditText etApellido, EditText etNumDoc, EditText etTelefono, EditText etSalario) {
        OkHttpClient client = new OkHttpClient();

        FormBody.Builder formBuilder = new FormBody.Builder()
                .add("number_doc", etNumDoc.getText().toString())
                .add("type_doc", "CC")
                .add("last_name", etApellido.getText().toString())
                .add("first_name", etNombre.getText().toString())
                .add("phone", etTelefono.getText().toString())
                .add("type_person", "E")
                .add("salary", etSalario.getText().toString());

        RequestBody requestBody = formBuilder.build();

        Request request = new Request.Builder()
                .url(HOST + "/persons/add")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("ERROR", e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d("ENCARGADO", response.body().string());
            }
        });
    }

    private void loadSpinner(final Spinner spObras) {
        sAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, obras);
        sAdapter.notifyDataSetChanged();
        spObras.setAdapter(sAdapter);
        spObras.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) spObras.getSelectedView()).setTextColor(getResources().getColor(R.color.colorBack));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
        switch (current){
            case 0:
                Toast.makeText(this, "HOLOOOOOOOOoo", Toast.LENGTH_SHORT).show();
                initObras();
                break;
        }
        vpHome.setCurrentItem(current, false);
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
                } catch (JSONException e) {
                        e.printStackTrace();
                }
            }
        });
    }

    public void getMateriales(){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(HOST + "/materials")
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
                        materialList.clear();
                        materialList.add(new Material(row.getInt("id_order"),
                                row.getString("price_unit"),
                                row.getString("quantity"),
                                row.getString("name_provider"),
                                row.getString("date_order"),
                                row.getString("type_material"),
                                row.getInt("id_oeuvre")));
                    }
                    Log.d("MATERIALES", materialList.toString());
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
                        machines.clear();
                        machines.add(new Machine(row.getInt("id_machine"),
                                row.getString("name_machine"),
                                row.getString("date_purchase"),
                                row.getString("status_machine")));
                    }
                    Log.d("MAQUINAS", machines.toString());
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
                        nominas.clear();
                        nominas.add(new Nomina(row.getInt("number_doc"),
                                row.getString("type_doc"),
                                row.getString("last_name"),
                                row.getString("first_name"),
                                row.getString("phone"),
                                "no",
                                row.getString("salary")));
                    }
                    Log.d("NOMINA", nominas.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}

