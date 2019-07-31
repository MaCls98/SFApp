package com.android.sfapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.shockwave.pdfium.PdfDocument;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener {

    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String SAMPLE_FILE = "test.pdf";
    PDFView pdfView;
    Integer pageNumber = 0;
    String pdfFileName;

    private TextView tvDrawerTitle;
    private TextView tvCerrarSesion;
    private ImageButton btnShowDrawer;
    private ImageView ivTop;
    private ImageView ivWelcome;

    private ProgressBar pbObras;
    private NominaRVAdapter nominaRVAdapter;
    private MaquinariaRVAdapter maquinariaRVAdapter;
    private MaterialsRVAdapter maquinariaAdapter;

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

    private ArrayList<Nomina> actualEmployees = new ArrayList<>();
    private ArrayList<Machine> actualMachines = new ArrayList<>();

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
        ivWelcome = findViewById(R.id.iv_wellcome);

        obras = new ArrayList<>();
        nominaRVAdapter = new NominaRVAdapter(nominas);
        maquinariaRVAdapter = new MaquinariaRVAdapter(machines);

        tvCerrarSesion = findViewById(R.id.tv_sesion);
        tvCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(home);
                finish();
            }
        });

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
                //Manual de usuario
                R.layout.home_manual,
                //Asignar maquina
                R.layout.home_assign_maquina,
                //Asignar nomina
                R.layout.home_assign_nomina

        };

        HomeViewPagerAdapter homeViewPagerAdapter = new HomeViewPagerAdapter(layouts,
                getBaseContext());
        vpHome.setAdapter(homeViewPagerAdapter);

        vpHome.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

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

                    case R.id.menu_ayuda:
                        tvDrawerTitle.setText("Manual de usuario");
                        changeViewPage(8);
                        showManual();
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
        final EditText etNombre = findViewById(R.id.et_nomina_nombre);
        final EditText etApellido = findViewById(R.id.et_nomina_apellido);

        final EditText etNumDoc = findViewById(R.id.et_doc_num);
        final EditText etTelefono = findViewById(R.id.et_nomina_telefono);
        final EditText etSalario = findViewById(R.id.et_nomina_salario);
        etSalario.addTextChangedListener(new MoneyTextWatcher(etSalario));

        Button btnCancelar = findViewById(R.id.btn_cancelar_nomina);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvDrawerTitle.setText("Maquinaria y Nomina");
                changeViewPage(2);
            }
        });

        Button btnAgregar = findViewById(R.id.btn_agregar_nomina);
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
        final Spinner spMaterial = findViewById(R.id.sp_add_mat_type);
        spMaterial.setAdapter(new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_item, Materials.values()));
        final EditText tvPrecioUnitario = findViewById(R.id.et_add_mat_precio);
        tvPrecioUnitario.addTextChangedListener(new MoneyTextWatcher(tvPrecioUnitario));

        final EditText tvCantidad = findViewById(R.id.et_add_mat_cantidad);
        final EditText tvProveedor = findViewById(R.id.et_add_mat_proveedor);
        Button btnFecha = findViewById(R.id.btn_add_mat_fecha);
        final TextView tvFecha = findViewById(R.id.tv_add_mat_fecha);
        final Spinner spNombreObra = findViewById(R.id.sp_add_mat_obra);
        ArrayAdapter<Obra> sAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item, obras);
        sAdapter.setDropDownViewResource(R.layout.spinner_item);
        spNombreObra.setAdapter(sAdapter);
        Button btnCancelar = findViewById(R.id.btn_add_mat_cancel);
        Button btnAgregar = findViewById(R.id.btn_add_mat_confirm);
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
        final EditText nombre = findViewById(R.id.et_maquinaria_nombre);
        Button btnFecha = findViewById(R.id.btn_fecha_maquinaria);
        final TextView tvDateObra = findViewById(R.id.tv_date_maquinaria);

        btnFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(tvDateObra);
            }
        });

        Button cancelar = findViewById(R.id.btn_cancelar_maquinaria);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvDrawerTitle.setText("Maquinaria y Nomina");
                changeViewPage(2);
            }
        });

        Button agregar = findViewById(R.id.btn_agregar_maquinaria);
        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateEmptyFields(nombre, null) && selectedDate.length() > 0){
                    uploadMaquinaria(nombre.getText().toString(), selectedDate);
                }else{
                    Toast.makeText(MainActivity.this, "Llena todos los campos por favor", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void addEncargado(final EncargadosRVAdapter rvEncargados) {
        changeViewPage(6);
        LinearLayout llFields = findViewById(R.id.ll_fields);
        final EditText etPassword = new EditText(this);
        etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        etPassword.setHint("Contraseña del encargado");

        llFields.addView(etPassword);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setHint("Agregar Encargado");
        final EditText etNombre = findViewById(R.id.et_nomina_nombre);
        etNombre.setHint("Nombre encargado");
        final EditText etApellido = findViewById(R.id.et_nomina_apellido);
        etApellido.setHint("Apellido encargado");

        final EditText etNumDoc = findViewById(R.id.et_doc_num);
        final EditText etTelefono = findViewById(R.id.et_nomina_telefono);
        etTelefono.setHint("Telefono encargado");
        final EditText etSalario = findViewById(R.id.et_nomina_salario);
        etSalario.addTextChangedListener(new MoneyTextWatcher(etSalario));
        etSalario.setText("0");
        etSalario.setVisibility(View.GONE);

        Button btnCancelar = findViewById(R.id.btn_cancelar_nomina);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvDrawerTitle.setText("Encargados");
                changeViewPage(1);
            }
        });

        Button btnAgregar = findViewById(R.id.btn_agregar_nomina);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateEmptyFields(etNombre, etApellido)
                        && validateEmptyFields(etNumDoc, etSalario)
                        && validateEmptyFields(etTelefono, etPassword)){
                    uploadEncargado(etNombre, etApellido, etNumDoc, etSalario, etTelefono,
                            etPassword, rvEncargados);
                }

            }
        });
    }

    private void initAddObra() {
        changeViewPage(4);
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
        pbObras = findViewById(R.id.pb_obras);
        tvDrawerTitle.setText("Obras - Nomina");
        ivWelcome.setVisibility(View.GONE);
        final RecyclerView rvObra = findViewById(R.id.rv_frag_materials);
        final FloatingActionButton btnAddItem = findViewById(R.id.add_item_obra);
        final Spinner spObras = findViewById(R.id.sp_maq_obras);
        final TextView tvObras = findViewById(R.id.tv_obra_info);
        spObras.setVisibility(View.VISIBLE);
        getObras(spObras);

        spObras.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView) parent.getChildAt(0)).setTextSize(16);
                actualemployees(getObraId2(spObras.getSelectedItem().toString()), nominaRVAdapter);
                for (Obra obra:
                     obras) {
                    if (obra.getObraId() == getObraId2(spObras.getSelectedItem().toString())){
                        tvObras.setText(obra.getObraName()
                        + " - " + obra.getObraDireccion());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView) parent.getChildAt(0)).setTextSize(16);
                actualemployees(getObraId2(spObras.getSelectedItem().toString()), nominaRVAdapter);
                for (Obra obra:
                        obras) {
                    if (obra.getObraId() == getObraId2(spObras.getSelectedItem().toString())){
                        tvObras.setText(obra.getObraName()
                                + " - " + obra.getObraDireccion());
                    }
                }
            }
        });

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

        rvObra.setLayoutManager(lmNomina);
        rvObra.setAdapter(nominaRVAdapter);
        getNomina(nominaRVAdapter, pbObras);
    }

    public void loadObrasMaquinaria(View view) {
        ProgressBar pbObras = findViewById(R.id.pb_obras);
        tvDrawerTitle.setText("Obras - Maquinaria");
        ivWelcome.setVisibility(View.GONE);
        final RecyclerView rvObra = findViewById(R.id.rv_frag_materials);
        final FloatingActionButton btnAddItem = findViewById(R.id.add_item_obra);
        final Spinner spObras = findViewById(R.id.sp_maq_obras);
        final TextView tvObras = findViewById(R.id.tv_obra_info);
        spObras.setVisibility(View.VISIBLE);
        getObras(spObras);

        spObras.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView) parent.getChildAt(0)).setTextSize(16);
                actualmachines(getObraId2(spObras.getSelectedItem().toString()));
                for (Obra obra:
                        obras) {
                    if (obra.getObraId() == getObraId2(spObras.getSelectedItem().toString())){
                        tvObras.setText(obra.getObraName()
                                + " - " + obra.getObraDireccion());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView) parent.getChildAt(0)).setTextSize(16);
                actualmachines(getObraId2(spObras.getSelectedItem().toString()));
                for (Obra obra:
                        obras) {
                    if (obra.getObraId() == getObraId2(spObras.getSelectedItem().toString())){
                        tvObras.setText(obra.getObraName()
                                + " - " + obra.getObraDireccion());
                    }
                }
            }
        });

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

        maquinariaRVAdapter = new MaquinariaRVAdapter(machines);
        rvObra.setLayoutManager(lmMaquinaria);
        rvObra.setAdapter(maquinariaRVAdapter);

        getMachines(maquinariaRVAdapter, pbObras);
    }

    public void loadObrasMateriales(View view) {
        final ProgressBar pbObras = findViewById(R.id.pb_obras);
        tvDrawerTitle.setText("Obras - Materiales");
        ivWelcome.setVisibility(View.GONE);
        final RecyclerView rvObra = findViewById(R.id.rv_frag_materials);
        final FloatingActionButton btnAddItem = findViewById(R.id.add_item_obra);
        final Spinner spObras = findViewById(R.id.sp_maq_obras);
        final TextView tvObras = findViewById(R.id.tv_obra_info);
        spObras.setVisibility(View.VISIBLE);
        getObras(spObras);

        spObras.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView) parent.getChildAt(0)).setTextSize(16);
                int acId = getObraId2(spObras.getSelectedItem().toString());
                ArrayList<MaterialCV> filterMaterials = new ArrayList<>();
                for (MaterialCV m:
                     materials) {
                    if (m.getMaterialObraId() == acId){
                        filterMaterials.add(m);
                    }
                    maquinariaAdapter.setMaterials(filterMaterials);
                    maquinariaAdapter.notifyDataSetChanged();
                }
                for (Obra obra:
                        obras) {
                    if (obra.getObraId() == getObraId2(spObras.getSelectedItem().toString())){
                        tvObras.setText(obra.getObraName()
                                + " - " + obra.getObraDireccion());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView) parent.getChildAt(0)).setTextSize(16);
                int acId = getObraId2(spObras.getSelectedItem().toString());
                ArrayList<MaterialCV> filterMaterials = new ArrayList<>();
                getMateriales(maquinariaAdapter, pbObras);
                for (MaterialCV m:
                        materials) {
                    if (m.getMaterialObraId() == acId){
                        filterMaterials.add(m);
                    }
                    maquinariaAdapter.setMaterials(filterMaterials);
                    maquinariaAdapter.notifyDataSetChanged();
                }
                for (Obra obra:
                        obras) {
                    if (obra.getObraId() == getObraId2(spObras.getSelectedItem().toString())){
                        tvObras.setText(obra.getObraName()
                                + " - " + obra.getObraDireccion());
                    }
                }
            }
        });

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

        Log.d("MAT", materials.toString());

        maquinariaAdapter = new MaterialsRVAdapter(materials);
        rvObra.setLayoutManager(lmMaterials);
        rvObra.setAdapter(maquinariaAdapter);
        getMateriales(maquinariaAdapter, pbObras);
    }

    private void matList() {
        for (Material m:
                materialList) {
            materials.add(new MaterialCV(m.getIdOrder(), m.getIdOeuvre(), m.getTypeMaterial(),
                    "n", m.getQuantity(), m.getNameProvider(), m.getDateOrder(), m.getPriceUni()));
        }
    }

    //ENCARGADOS
    public void loadEncargadosActivos(View view){
        tvDrawerTitle.setText("Encargados Activos");
        ivWelcome.setVisibility(View.GONE);
        final RecyclerView rvEncargados = findViewById(R.id.rv_frag_encargados);
        FloatingActionButton btnAddEncargado = findViewById(R.id.btn_add_encargado);

        RecyclerView.LayoutManager lmEncargados = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        final EncargadosRVAdapter encargadosRVAdapter = new EncargadosRVAdapter(encargadosActivos, 0);

        btnAddEncargado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEncargado(encargadosRVAdapter);
            }
        });

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
                disableEncargado(encargadosActivos.get(position).getNumberDoc(), encargadosRVAdapter);
            }
        });

        rvEncargados.setLayoutManager(lmEncargados);
        rvEncargados.setAdapter(encargadosRVAdapter);
        getEncargados(encargadosRVAdapter);
    }

    public void loadEncargadosInactivos(View view){
        tvDrawerTitle.setText("Encargados Inactivos");
        ivWelcome.setVisibility(View.GONE);
        RecyclerView rvEncargados = findViewById(R.id.rv_frag_encargados);
        FloatingActionButton btnAddEncargado = findViewById(R.id.btn_add_encargado);


        RecyclerView.LayoutManager lmEncargados = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        final EncargadosRVAdapter encargadosRVAdapter = new EncargadosRVAdapter(encargadosInactivos, 0);

        btnAddEncargado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEncargado(encargadosRVAdapter);
            }
        });

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
                enableEncargado(encargadosInactivos.get(position).getNumberDoc(), encargadosRVAdapter);
            }
        });

        rvEncargados.setLayoutManager(lmEncargados);
        rvEncargados.setAdapter(encargadosRVAdapter);
        getEncargados(encargadosRVAdapter);
    }

    //MAQUINARIA Y NOMINA
    public void loadAllMaquinaria(View view){
        ProgressBar pbObras = findViewById(R.id.pb_maq_nom);
        tvDrawerTitle.setText("M y N - Total de maquinaria");
        ivWelcome.setVisibility(View.GONE);
        FloatingActionButton btnAddMaquinaria = findViewById(R.id.btn_add_maq_nom);
        btnAddMaquinaria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initAddMaquinaria();
            }
        });
        btnAddMaquinaria.setTitle("Agregar maquinaria");

        RecyclerView rvMaqNom = findViewById(R.id.rv_maq_nom);

        RecyclerView.LayoutManager lmMaqNom = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        MaquinariaRVAdapter maquinariaRVAdapter = new MaquinariaRVAdapter(machines);

        maquinariaRVAdapter.setOnItemLongClickListener(new MaquinariaRVAdapter.OnItemLongClickListener() {
            @Override
            public void assing(int position) {

            }
        });

        maquinariaRVAdapter.setOnItemClickListener(new MaquinariaRVAdapter.OnItemClickListener() {
            @Override
            public void uploadAssign(int position) {
                Machine machine = machines.get(position);
                initMaqAssign(machine);
            }
        });

        rvMaqNom.setLayoutManager(lmMaqNom);
        rvMaqNom.setAdapter(maquinariaRVAdapter);
        getMachines(maquinariaRVAdapter, pbObras);
    }

    private void initMaqAssign(final Machine machine) {
        changeViewPage(9);
        TextView tvNombre = findViewById(R.id.tv_maq_nombre);
        tvNombre.setText(machine.getName());
        final Spinner spObras = findViewById(R.id.sp_maq_obras);
        getObras(spObras);

        Button btnAdd = findViewById(R.id.btn_acep_asig_maq);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asignaMaquinaria(getObraId2(spObras.getSelectedItem().toString()), machine.getId(), "2018/10/10");
            }
        });

        Button btnCancel = findViewById(R.id.btn_can_asig_maq);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViewPage(2);
            }
        });
    }

    public void loadAllNomina(View view){
        ProgressBar pbObras = findViewById(R.id.pb_maq_nom);
        tvDrawerTitle.setText("M y N - Total de nomina");
        ivWelcome.setVisibility(View.GONE);
        FloatingActionButton btnAddMaquinaria = findViewById(R.id.btn_add_maq_nom);
        btnAddMaquinaria.setTitle("Agregar nominna");
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

        nominaRVAdapter.setOnItemLongClickListener(new NominaRVAdapter.OnItemLongClickListener() {
            @Override
            public void assing(int position) {

            }
        });

        nominaRVAdapter.setOnItemClickListener(new NominaRVAdapter.OnItemClickListener() {
            @Override
            public void uploadAssign(int position) {
                Nomina nomina = nominas.get(position);
                initNomAsign(nomina);
            }
        });

        rvMaqNom.setLayoutManager(lmMaqNom);
        rvMaqNom.setAdapter(nominaRVAdapter);
        getNomina(nominaRVAdapter, pbObras);
    }

    private void initNomAsign(final Nomina nomina) {
        changeViewPage(10);
        TextView tvNombre = findViewById(R.id.tv_non_nombre);
        tvNombre.setText(nomina.getFirstName() + " " + nomina.getLastName());
        final Spinner spObras = findViewById(R.id.sp_obras_nom);
        getObras(spObras);

        Button btnAdd = findViewById(R.id.btn_acep_asig_nnom);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asignarNomina(getObraId2(spObras.getSelectedItem().toString()), String.valueOf(nomina.getNumberDoc()), "2018/10/10");
            }
        });

        Button btnCancel = findViewById(R.id.btn_can_asign_nom);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViewPage(2);
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvDrawerTitle.setText("Maquinaria y Nomina");
                        changeViewPage(2);
                    }
                });
            }
        });
    }

    private void uploadEncargado(EditText etNombre, EditText etApellido, EditText etNumDoc,
                                 EditText etSalario, EditText etTelefono, EditText etPassword,
                                 final EncargadosRVAdapter rvEncargados) {
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
                        getEncargados(rvEncargados);
                        changeViewPage(1);
                    }
                });
            }
        });
    }

    private void uploadNomina(EditText etNombre, EditText etApellido, EditText etNumDoc,
                              EditText etTelefono, EditText etSalario) {
        OkHttpClient client = new OkHttpClient();

        String r = etSalario.getText().toString()
                .substring(0, etSalario.getText().toString().length() - 2);

        FormBody.Builder formBuilder = new FormBody.Builder()
                .add("number_doc", etNumDoc.getText().toString())
                .add("type_doc", "CC")
                .add("last_name", etApellido.getText().toString())
                .add("first_name", etNombre.getText().toString())
                .add("phone", etTelefono.getText().toString())
                .add("type_person", "E")
                .add("salary", r
                        .replace("$", "").replace(",", "")
                        .replace(".", ""));

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
                        tvDrawerTitle.setText("Maquinaria y Nomina");
                        changeViewPage(2);
                    }
                });
            }
        });
    }

    private void loadSpinner(final Spinner spObras) {
        ArrayAdapter sAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_list_item_1, obras);
        sAdapter.notifyDataSetChanged();
        spObras.setAdapter(sAdapter);
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


        String r = precioUnitario.substring(0, precioUnitario.length() - 2);

        FormBody.Builder formBuilder = new FormBody.Builder()
                .add("price_unit", r
                        .replace("$", "")
                        .replace(",", "")
                        .replace(".", ""))
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

    private String getObraId(String name) {
        String id = "";
        for (int i = 0; i < obras.size(); i++){
            if (name.equalsIgnoreCase(obras.get(i).getObraName())){
                id = String.valueOf(obras.get(i).getObraId());
            }
        }
        return id;
    }

    private int getObraId2(String name) {
        int id = 0;
        for (int i = 0; i < obras.size(); i++){
            if (name.equalsIgnoreCase(obras.get(i).getObraName())){
                id = obras.get(i).getObraId();
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



        DatePickerFragment dateFragment = DatePickerFragment.newInstance(
                new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                selectedDate = year + "-" + (month+1) + "-" + day;
                Calendar a = Calendar.getInstance();
                a.set(Calendar.YEAR, year);
                a.set(Calendar.MONTH, month);
                a.set(Calendar.DAY_OF_MONTH, day + 1);

                if(a.getTime().after(new Date())){
                    date.setText(selectedDate);
                }else {
                    Toast.makeText(getApplicationContext(), "Fecha de asignacion no puede ser menor que la fecha actual", Toast.LENGTH_LONG).show();
                    showDatePickerDialog(date);
                }

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

    private void disableEncargado(int numberDoc, final EncargadosRVAdapter adapter) {
        OkHttpClient client = new OkHttpClient();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject encargado = new JSONObject();
        try {
            encargado.put("status_user", "D");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(JSON, encargado.toString());

        final Request request = new Request.Builder()
                .url(HOST + "/persons/changeStatusU/" + numberDoc)
                .method("PUT", requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Verifica tu conexion a internet y vuelve a intentarlo", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Ocurrio un error, por favor vuelve a intentarlo", Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    getEncargados(adapter);
                }
            }
        });


    }

    private void enableEncargado(int numberDoc, final EncargadosRVAdapter adapter) {
        OkHttpClient client = new OkHttpClient();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject encargado = new JSONObject();
        try {
            encargado.put("status_user", "H");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(JSON, encargado.toString());

        final Request request = new Request.Builder()
                .url(HOST + "/persons/changeStatusU/" + numberDoc)
                .method("PUT", requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Verifica tu conexion a internet y vuelve a intentarlo", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Ocurrio un error, por favor vuelve a intentarlo", Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    getEncargados(adapter);
                }
            }
        });


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

    public void getMateriales(final MaterialsRVAdapter maquinariaAdapter, final ProgressBar pb){
        pb.setVisibility(View.VISIBLE);
        OkHttpClient client = new OkHttpClient();
        materials.clear();
        materialList.clear();
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
                    Log.d("MAT", materialList.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            matList();
                            pb.setVisibility(View.GONE);
                            maquinariaAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getMachines(final MaquinariaRVAdapter maquinariaRVAdapter, final ProgressBar pb){
        pb.setVisibility(View.VISIBLE);
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
                        String status = row.getString("actual_ouevre");

                        if (status.equalsIgnoreCase("En ningua")){
                            machines.add(new Machine(row.getInt("id_machine"),
                                    row.getString("name_machine"),
                                    row.getString("date_purchase"),
                                    row.getString("status_machine")));
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pb.setVisibility(View.GONE);
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

    public void getNomina(final NominaRVAdapter maquinariaRVAdapter, final ProgressBar pb){
        pb.setVisibility(View.VISIBLE);
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
                        String status = row.getString("actual_ouevre");

                        if (status.equalsIgnoreCase("En ninguna")){
                            nominas.add(new Nomina(row.getInt("number_doc"),
                                    "CC",
                                    row.getString("last_name"),
                                    row.getString("first_name"),
                                    row.getString("phone"),
                                    "no",
                                    row.getString("salary")));
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pb.setVisibility(View.GONE);
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

    private void asignarNomina(final int id_oeuvre, String number_doc, String date_assignment){
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject asignar = new JSONObject();

        try{
            asignar.put("id_oeuvre", id_oeuvre);
            asignar.put("number_doc", number_doc);
            asignar.put("date_assignment", date_assignment);
        }catch (JSONException e){
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(JSON, asignar.toString());

        Request request = new Request.Builder()
                .url(HOST + "/persons/toOeuvre")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Revisa tu conexion a internet y vuelve a intentarlo", Toast.LENGTH_SHORT).show();

                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Log.d("ERROR", response.body().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            changeViewPage(0);
                        }
                    });
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Log.d("NOMINA", response.body().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            actualemployees(id_oeuvre, nominaRVAdapter);
                        }
                    });
                }
            }
        });
    }

    private void asignaMaquinaria(int id_oeuvre, int id_machine, String date_start){
        Log.d("ID", String.valueOf(id_oeuvre));
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject asignar = new JSONObject();

        try{
            asignar.put("id_oeuvre", id_oeuvre);
            asignar.put("id_machine", id_machine);
            asignar.put("date_start", date_start);
        }catch (JSONException e){
            e.printStackTrace();
        }


        RequestBody requestBody = RequestBody.create(JSON, asignar.toString());

        final Request request = new Request.Builder()
                .url(HOST + "/machines/toOeuvre")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Revisa tu conexion a internet y vuelve a intentarlo", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Log.d("ERROR", response.body().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            changeViewPage(0);
                        }
                    });
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Log.d("MAQUINA", response.body().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            changeViewPage(0);
                        }
                    });
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

    private void actualmachines(int id_oveuvre){
        OkHttpClient client = new OkHttpClient();
        machines.clear();
        actualMachines.clear();
        Request request = new Request.Builder()
                .url(HOST + "/oeuvres/actualMachines/" + id_oveuvre)
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
                            actualMachines.add(new Machine(row.getInt("id_machine"),
                                    row.getString("name_machine"),
                                    row.getString("date_purchase"),
                                    row.getString("status_machine")));
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                changeViewPage(0);
                                maquinariaRVAdapter.setMaquinarias(actualMachines);
                                maquinariaRVAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void actualemployees(int id_oveuvre, final NominaRVAdapter nominaRVAdapter){
        OkHttpClient client = new OkHttpClient();
        actualEmployees.clear();
        Request request = new Request.Builder()
                .url(HOST + "/oeuvres/actualEmployees/" + id_oveuvre)
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
                            actualEmployees.add(new Nomina(row.getInt("number_doc"),
                                    row.getString("type_doc"),
                                    row.getString("last_name"),
                                    row.getString("first_name"),
                                    row.getString("phone"),
                                    "",
                                    row.getString("salary")));
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                changeViewPage(0);
                                Log.d("NOM", actualEmployees.toString());
                                nominaRVAdapter.setNominas(actualEmployees);
                                nominaRVAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void showManual() {
        tvDrawerTitle.setText("Manual de usuario");
        pdfView = findViewById(R.id.pdfView);
        displayFromAsset(SAMPLE_FILE);
    }

    private void displayFromAsset(String assetFileName) {
        pdfFileName = assetFileName;

        pdfView.fromAsset(SAMPLE_FILE)
                .defaultPage(pageNumber)
                .enableSwipe(true)

                .swipeHorizontal(false)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
    }

    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        printBookmarksTree(pdfView.getTableOfContents(), "-");

    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

    public class MoneyTextWatcher implements TextWatcher {
        private final WeakReference<EditText> editTextWeakReference;

        public MoneyTextWatcher(EditText editText) {
            editTextWeakReference = new WeakReference<EditText>(editText);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            EditText editText = editTextWeakReference.get();
            if (editText == null) return;
            String s = editable.toString();
            if (s.isEmpty()) return;
            editText.removeTextChangedListener(this);
            String cleanString = s.replaceAll("[$,.]", "");
            BigDecimal parsed = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
            String formatted = NumberFormat.getCurrencyInstance().format(parsed);
            editText.setText(formatted);
            editText.setSelection(formatted.length());
            editText.addTextChangedListener(this);
        }
    }

    @Override
    public void onBackPressed() {

    }
}

