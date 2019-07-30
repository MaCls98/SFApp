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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.sfapp.model.Encargado;
import com.android.sfapp.model.Machine;
import com.android.sfapp.model.Material;
import com.android.sfapp.model.MaterialCV;
import com.android.sfapp.model.Materials;
import com.android.sfapp.model.Nomina;
import com.android.sfapp.model.Obra;
import com.android.sfapp.utils.DatePickerFragment;
import com.android.sfapp.utils.EncargadosRVAdapter;
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
    private ImageView ivTop;

    private ViewPager vpHome;

    private ArrayList<Encargado> encargadosActivos = new ArrayList<>();
    private ArrayList<Encargado> encargadosInactivos = new ArrayList<>();

    private ArrayList<Machine> machines = new ArrayList<>();
    private ArrayList<Nomina> nominas = new ArrayList<>();
    private ArrayList<Material> materialList = new ArrayList<>();
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
        ivTop = findViewById(R.id.iv_top);

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
        };

        HomeViewPagerAdapter homeViewPagerAdapter = new HomeViewPagerAdapter(layouts,
                getBaseContext());
        vpHome.setAdapter(homeViewPagerAdapter);

        abdt = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);
        abdt.setDrawerIndicatorEnabled(true);

        dl.addDrawerListener(abdt);
        abdt.syncState();

        final NavigationView nav_view = findViewById(R.id.nav_view);

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id){
                    case R.id.menu_obras:
                        tvDrawerTitle.setText("Obras - Seleccion una opcion");
                        changeViewPage(0);
                        dl.closeDrawer(Gravity.START);
                        break;

                    case R.id.menu_encargados:
                        tvDrawerTitle.setText("Encargados - Seleccion una opcion");
                        changeViewPage(1);
                        dl.closeDrawer(Gravity.START);
                        break;

                    case R.id.menu_maquinas_nomina:
                        tvDrawerTitle.setText("Maquinaria y Nomina - Seleccion una opcion");
                        changeViewPage(2);
                        dl.closeDrawer(Gravity.START);
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
            }
        });

        Button btnAgregar = v.findViewById(R.id.btn_agregar_nomina);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateEmptyFields(etNombre, etApellido) && validateEmptyFields(etNumDoc,
                        etTelefono) && validateEmptyFields(etSalario, null)){

                    uploadNomina(etNombre, etApellido, etNumDoc, etTelefono, etSalario);
                }
            }
        });
    }

    private void initAddMaterial() {
        changeViewPage(5);
        View v = getLayoutInflater().inflate(R.layout.home_nv_add_material, vpHome);
        final Spinner spMaterial = v.findViewById(R.id.sp_add_mat_type);
        spMaterial.setAdapter(new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_item, Materials.values()));
        final EditText tvPrecioUnitario = v.findViewById(R.id.et_add_mat_precio);
        final EditText tvCantidad = v.findViewById(R.id.et_add_mat_cantidad);
        final EditText tvProveedor = v.findViewById(R.id.et_add_mat_proveedor);
        Button btnFecha = v.findViewById(R.id.btn_add_mat_fecha);
        final TextView tvFecha = v.findViewById(R.id.tv_add_mat_fecha);
        final Spinner spNombreObra = v.findViewById(R.id.sp_add_mat_obra);
        ArrayAdapter<Obra> sAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item, obras);
        sAdapter.setDropDownViewResource(R.layout.spinner_item);
        spNombreObra.setAdapter(sAdapter);
        Button btnCancelar = v.findViewById(R.id.btn_add_mat_cancel);
        Button btnAgregar = v.findViewById(R.id.btn_add_mat_confirm);
        btnFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(tvFecha);
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
                if (validateEmptyFields(tvPrecioUnitario, tvCantidad)
                        && validateEmptyFields(tvProveedor, null) && selectedDate != null){

                    String [] values = spMaterial.getSelectedItem().toString().split(",");
                    addMaterial(tvPrecioUnitario.getText().toString(), tvCantidad.getText().toString()
                            , tvProveedor.getText().toString(), tvFecha.getText().toString(),
                            values[1], getObraId(spNombreObra.getSelectedItem().toString()));
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
                showDatePickerDialog(tvDateObra);
            }
        });

        Button cancelar = v.findViewById(R.id.btn_cancelar_maquinaria);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvDrawerTitle.setText("Maquinaria y Nomina");
                changeViewPage(2);
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

    private void initAddObra() {
        changeViewPage(4);
        View v = getLayoutInflater().inflate(R.layout.home_nv_add_obra, vpHome);
        final EditText etNameObra = findViewById(R.id.et_obra_name);
        final TextView tvDateObra = findViewById(R.id.tv_fecha);

        Button btnDateObra = findViewById(R.id.btn_fecha_obra);
        btnDateObra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(tvDateObra);
            }
        });
        final EditText etDireccionObra = findViewById(R.id.et_direccion);

        Button btnCancelarObra = findViewById(R.id.btn_cancelar_obra);
        btnCancelarObra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvDrawerTitle.setText("Obras");
                changeViewPage(0);
            }
        });

        Button btnAgregarObra = findViewById(R.id.btn_agregar_obra);
        btnAgregarObra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateEmptyFields(etNameObra, etDireccionObra) && selectedDate != null){
                    addObra(etNameObra.getText().toString(), etDireccionObra.getText().toString(), selectedDate);
                    selectedDate = null;
                }
            }
        });
    }

    //OBRAS
    public void loadObrasNomina(View view) {
        tvDrawerTitle.setText("Obras - Nomina");
        ivTop.setImageResource(R.drawable.nomina);
        final RecyclerView rvObra = findViewById(R.id.rv_frag_materials);
        final FloatingActionButton btnAddItem = findViewById(R.id.add_item_obra);
        Spinner spObras = findViewById(R.id.sp_obras);
        spObras.setVisibility(View.VISIBLE);
        getObras(spObras);
        FloatingActionButton btnAddObra = findViewById(R.id.add_obra);
        btnAddObra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initAddObra();
            }
        });
        btnAddItem.setTitle("Asignar Nomina");

        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViewPage(2);
            }
        });

        btnAddObra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initAddObra();
            }
        });

        RecyclerView.LayoutManager lmNomina = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        NominaRVAdapter nominaRVAdapter = new NominaRVAdapter(nominas);
        rvObra.setLayoutManager(lmNomina);
        rvObra.setAdapter(nominaRVAdapter);
        getNomina(nominaRVAdapter);
    }

    public void loadObrasMaquinaria(View view) {
        tvDrawerTitle.setText("Obras - Maquinaria");
        ivTop.setImageResource(R.drawable.maquinaria);
        final RecyclerView rvObra = findViewById(R.id.rv_frag_materials);
        final FloatingActionButton btnAddItem = findViewById(R.id.add_item_obra);
        Spinner spObras = findViewById(R.id.sp_obras);
        spObras.setVisibility(View.VISIBLE);
        getObras(spObras);
        FloatingActionButton btnAddObra = findViewById(R.id.add_obra);
        btnAddObra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initAddObra();
            }
        });
        btnAddItem.setTitle("Asignar Maquinaria");
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViewPage(2);
            }
        });

        RecyclerView.LayoutManager lmMaquinaria = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        MaquinariaRVAdapter maquinariaRVAdapter = new MaquinariaRVAdapter(machines);
        rvObra.setLayoutManager(lmMaquinaria);
        rvObra.setAdapter(maquinariaRVAdapter);

        getMachines(maquinariaRVAdapter);
    }

    public void loadObrasMateriales(View view) {
        tvDrawerTitle.setText("Obras - Materiales");
        ivTop.setImageResource(R.drawable.materiales);
        final RecyclerView rvObra = findViewById(R.id.rv_frag_materials);
        final FloatingActionButton btnAddItem = findViewById(R.id.add_item_obra);
        Spinner spObras = findViewById(R.id.sp_obras);
        spObras.setVisibility(View.VISIBLE);
        getObras(spObras);
        FloatingActionButton btnAddObra = findViewById(R.id.add_obra);
        btnAddObra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initAddObra();
            }
        });
        btnAddItem.setTitle("Agregar Materiales");
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Agregar nuevo material
                Toast.makeText(getBaseContext(), "Agregar Materiales", Toast.LENGTH_LONG).show();
                initAddMaterial();
            }
        });

        RecyclerView.LayoutManager lmMaterials = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        for (Material m:
                materialList) {
            materials.add(new MaterialCV(m.getIdOrder(), m.getIdOeuvre(), m.getTypeMaterial(),
                    "n", m.getQuantity(), m.getNameProvider(), m.getDateOrder(), m.getPriceUni()));
        }

        MaterialsRVAdapter maquinariaAdapter = new MaterialsRVAdapter(materials);
        rvObra.setLayoutManager(lmMaterials);
        rvObra.setAdapter(maquinariaAdapter);
        getMateriales(maquinariaAdapter);
    }

    //ENCARGADOS
    public void loadEncargadosActivos(View view){
        tvDrawerTitle.setText("Encargados Activos");
        ivTop.setImageResource(R.drawable.active_encargado);
        RecyclerView rvEncargados = findViewById(R.id.rv_frag_encargados);
        FloatingActionButton btnAddEncargado = findViewById(R.id.btn_add_encargado);
        btnAddEncargado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEncargado();
            }
        });

        RecyclerView.LayoutManager lmEncargados = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        EncargadosRVAdapter encargadosRVAdapter = new EncargadosRVAdapter(encargadosActivos, 0);

        encargadosRVAdapter.setOnItemClickListener(new EncargadosRVAdapter.OnItemClickListener() {
            @Override
            public void uploadStatus(int position) {
                encargadosActivos.get(position).changeStatus();
            }
        });

        encargadosRVAdapter.setOnItemLongClickListener(new EncargadosRVAdapter.OnItemLongClickListener() {
            @Override
            public void changeStatus(int position) {
                
            }
        });

        encargadosRVAdapter.setOnItemClickListener(new EncargadosRVAdapter.OnItemClickListener() {
            @Override
            public void uploadStatus(int position) {
                Log.d("ENC", encargadosActivos.get(position).toString());
            }
        });

        rvEncargados.setLayoutManager(lmEncargados);
        rvEncargados.setAdapter(encargadosRVAdapter);
        getEncargados(encargadosRVAdapter);
    }

    public void loadEncargadosInactivos(View view){
        tvDrawerTitle.setText("Encargados Inactivos");
        ivTop.setImageResource(R.drawable.block_encargado);
        RecyclerView rvEncargados = findViewById(R.id.rv_frag_encargados);
        FloatingActionButton btnAddEncargado = findViewById(R.id.btn_add_encargado);
        btnAddEncargado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEncargado();
            }
        });

        RecyclerView.LayoutManager lmEncargados = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        EncargadosRVAdapter encargadosRVAdapter = new EncargadosRVAdapter(encargadosInactivos, 0);

        encargadosRVAdapter.setOnItemClickListener(new EncargadosRVAdapter.OnItemClickListener() {
            @Override
            public void uploadStatus(int position) {
                encargadosActivos.get(position).changeStatus();
            }
        });

        encargadosRVAdapter.setOnItemLongClickListener(new EncargadosRVAdapter.OnItemLongClickListener() {
            @Override
            public void changeStatus(int position) {

            }
        });

        encargadosRVAdapter.setOnItemClickListener(new EncargadosRVAdapter.OnItemClickListener() {
            @Override
            public void uploadStatus(int position) {
                Log.d("ENC", encargadosInactivos.get(position).toString());
            }
        });

        rvEncargados.setLayoutManager(lmEncargados);
        rvEncargados.setAdapter(encargadosRVAdapter);
        getEncargados(encargadosRVAdapter);
    }

    //MAQUINARIA Y NOMINA
    public void loadAllMaquinaria(View view){
        tvDrawerTitle.setText("M y N - Total de maquinaria");
        ivTop.setImageResource(R.drawable.block_encargado);
        FloatingActionButton btnAddMaquinaria = findViewById(R.id.btn_add_maq_nom);
        btnAddMaquinaria.setTitle("Agregar maquinaria");
        btnAddMaquinaria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initAddMaquinaria();
            }
        });

        RecyclerView rvMaqNom = findViewById(R.id.rv_maq_nom);

        RecyclerView.LayoutManager lmMaqNom = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        MaquinariaRVAdapter maquinariaRVAdapter = new MaquinariaRVAdapter(machines);
        rvMaqNom.setLayoutManager(lmMaqNom);
        rvMaqNom.setAdapter(maquinariaRVAdapter);
        getMachines(maquinariaRVAdapter);
    }

    public void loadAllNomina(View view){
        tvDrawerTitle.setText("M y N - Total de nomina");
        ivTop.setImageResource(R.drawable.block_encargado);

        FloatingActionButton btnAddMaquinaria = findViewById(R.id.btn_add_maq_nom);
        btnAddMaquinaria.setTitle("Agregar maquinaria");
        btnAddMaquinaria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initAddNomina();
            }
        });

        RecyclerView rvMaqNom = findViewById(R.id.rv_maq_nom);
        RecyclerView.LayoutManager lmMaqNom = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        NominaRVAdapter nominaRVAdapter = new NominaRVAdapter(nominas);
        rvMaqNom.setLayoutManager(lmMaqNom);
        rvMaqNom.setAdapter(nominaRVAdapter);
        getNomina(nominaRVAdapter);
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

    private void uploadEncargado(EditText etNombre, EditText etApellido, EditText etNumDoc,
                                 EditText etSalario, EditText etTelefono, EditText etPassword) {
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        changeViewPage(1);
                    }
                });
            }
        });
    }

    private void uploadNomina(EditText etNombre, EditText etApellido, EditText etNumDoc,
                              EditText etTelefono, EditText etSalario) {
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
        ArrayAdapter sAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_dropdown_item_1line, obras);
        sAdapter.notifyDataSetChanged();
        spObras.setAdapter(sAdapter);
        spObras.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) spObras.getSelectedView())
                        .setTextColor(getResources().getColor(R.color.colorBack));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void addObra(String etNameObra, String etDireccionObra, String selectedDate) {
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,
                                "Verifica tu conexion a internet y vuelve a intentarlo", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this,
                                    "Ocurrio un error, por favor verifica los campos y vuelve a intentarlo", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvDrawerTitle.setText("Obras");
                        changeViewPage(0);
                    }
                });
            }
        });
    }

    private void addMaterial(String precioUnitario, String cantidad, String proveedor,
                             String fecha, String tipoMaterial, String idObra) {
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
        vpHome.setCurrentItem(current, false);
    }

    private void showDatePickerDialog(final TextView date) {

        DatePickerFragment dateFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                selectedDate = year + "-" + (month+1) + "-" + day;
                date.setText(selectedDate);
            }
        });
        dateFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public boolean validateEmptyFields(EditText etOne, EditText etTwo){
        if(etOne.getText().toString().isEmpty() && etTwo != null && etTwo.getText().toString().isEmpty()){
            Toast.makeText(getBaseContext(), "Todos los campos deben estar completos",
                    Toast.LENGTH_SHORT).show();
            return false;
        }else if(etOne.getText().toString().isEmpty()){
            Toast.makeText(getBaseContext(), "Todos los campos deben estar completos",
                    Toast.LENGTH_SHORT).show();
            return false;
        }else if(etTwo != null && etTwo.getText().toString().isEmpty() ){
            Toast.makeText(getBaseContext(), "Todos los campos deben estar completos",
                    Toast.LENGTH_SHORT).show();
            return false;
        }else if(etOne.getText().toString().isEmpty() && etTwo == null){
            Toast.makeText(getBaseContext(), "Todos los campos deben estar completos",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void getObras(final Spinner spObras){
        OkHttpClient client = new OkHttpClient();
        obras.clear();

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
                if(response.isSuccessful()){
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadSpinner(spObras);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void getMateriales(final MaterialsRVAdapter maquinariaAdapter){
        OkHttpClient client = new OkHttpClient();
        materialList.clear();
        materials.clear();
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
                    Log.d("MATERIALES", array.toString());
                    for (int i = 0; i < array.length(); i++){
                        JSONObject row = array.getJSONObject(i);
                        materialList.add(new Material(row.getInt("id_order"),
                                row.getString("price_unit"),
                                row.getString("quantity"),
                                row.getString("name_provider"),
                                row.getString("date_order"),
                                row.getString("type_material"),
                                row.getInt("id_oeuvre")));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            maquinariaAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getMachines(final MaquinariaRVAdapter maquinariaRVAdapter){
        OkHttpClient client = new OkHttpClient();
        machines.clear();
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
                    Log.d("MAQUINARIA", array.toString());
                    for (int i = 0; i < array.length(); i++){
                        JSONObject row = array.getJSONObject(i);
                        machines.add(new Machine(row.getInt("id_machine"),
                                row.getString("name_machine"),
                                row.getString("date_purchase"),
                                row.getString("status_machine")));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            maquinariaRVAdapter.notifyDataSetChanged();
                        }
                    });
                    Log.d("MAQUINAS", machines.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getNomina(final NominaRVAdapter maquinariaRVAdapter){
        OkHttpClient client = new OkHttpClient();
        nominas.clear();
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
                                "no",
                                row.getString("salary")));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            maquinariaRVAdapter.notifyDataSetChanged();
                        }
                    });
                    Log.d("NOMINA", nominas.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getEncargados(final EncargadosRVAdapter encargadosRVAdapter) {
        OkHttpClient client = new OkHttpClient();
        encargadosInactivos.clear();
        encargadosActivos.clear();
        Request request = new Request.Builder()
                .url(HOST + "/persons/users")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try{
                    JSONArray array = new JSONArray(response.body().string());
                    Log.d("ENC", array.toString());
                    for (int i = 0; i < array.length(); i++){
                        JSONObject object = (JSONObject) array.get(i);
                        Encargado encargado = new Encargado(object.getInt("number_doc"),
                                object.getString("last_name"),
                                object.getString("first_name"),
                                object.getString("phone"),
                                object.getString("status_user"));


                        if (encargado.getStatus().equals("H")){
                            encargadosActivos.add(encargado);
                        }else {
                            encargadosInactivos.add(encargado);
                        }
                    }
                    encargadosInactivos.add(new Encargado(
                            1049655163,
                            "Celis",
                            "Manuel",
                            "3212142066",
                            "D"
                    ));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            encargadosRVAdapter.notifyDataSetChanged();
                        }
                    });
                }catch (JSONException e){

                }
            }
        });
    }

}

