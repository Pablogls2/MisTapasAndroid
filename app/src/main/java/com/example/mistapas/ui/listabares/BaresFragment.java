package com.example.mistapas.ui.listabares;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.mistapas.MainActivity;
import com.example.mistapas.R;
import com.example.mistapas.ui.login.BdController;
import com.example.mistapas.ui.modelos.Bar;
import com.example.mistapas.ui.rest.ApiUtils;
import com.example.mistapas.ui.rest.MisTapasRest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class BaresFragment extends Fragment {

    private ConstraintLayout clJuegoConstr;
    private Spinner spnBaresFiltro;
    private RecyclerView recyclerView;
    private String[] listaFiltro =
            {"Filtros:", "Ordenar por nombre ascendente", "Ordenar por nombre descendente","Ordenar por estrellas ascendente","Ordenar por estrellas descendente"};
    private SwipeRefreshLayout srlBaresRefresh;
    private BaresAdapter adapter;
    private MisTapasRest misTapasRest;
    private ArrayList<Bar> listaBares;
    private FloatingActionButton fabBaresVoz;

    private static final int VOZ = 10;
    private static final int NADA = 10;
    private static final int NOMBRE_ASC = 11;
    private static final int NOMBRE_DESC = 12;
    private static final int ESTRELLA_ASC = 13;
    private static final int ESTRELLA_DESC = 14;
    private int tipoFiltro = NADA;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.lista_bares, container, false);
        iniciarFragment(root);
        return root;
    }
//iniciamos la vista del fragment bar
    private void iniciarFragment(View root){
        recyclerView = (RecyclerView) root.findViewById(R.id.rvBaresRecycler);
        pedirMultiplesPermisos();
        fabBaresVoz= root.findViewById(R.id.fabBaresVoz);
        fabBaresVoz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlarVoz();
            }
        });
//Comprobamos la conexion
        if(isNetworkAvailable()) {
            misTapasRest = ApiUtils.getService();
            cargarDatos();
            //gestionamos el spinner
            gestionFiltrosSpinner(root);

            iniciarSwipeVertical(root);
        }else{
            Toast.makeText(getContext(), "Es necesaria una conexión a internet", Toast.LENGTH_SHORT).show();
        }
//cargamos datos

    }
//Gestionamos el spinner de filtros
    private void gestionFiltrosSpinner(View root) {
        this.spnBaresFiltro = root.findViewById(R.id.spinner2);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, listaFiltro);
        spnBaresFiltro.setAdapter(dataAdapter);
        this.spnBaresFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tipoFiltro = NADA;
                switch (spnBaresFiltro.getSelectedItemPosition()) {
                    case 0:
                        tipoFiltro = NADA;
                        break;
                    case 1:
                        tipoFiltro = NOMBRE_ASC;
                        ordenarBares();
                        adapter = new BaresAdapter (listaBares, getContext(),getFragmentManager());
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
                        //se le aplica el adaptador al recyclerView
                        recyclerView.setAdapter(adapter);
                        srlBaresRefresh.setRefreshing(false);
                        break;
                    case 2:
                        tipoFiltro = NOMBRE_DESC;
                        ordenarBares();
                        adapter = new BaresAdapter (listaBares, getContext(),getFragmentManager());
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
                        //se le aplica el adaptador al recyclerView
                        recyclerView.setAdapter(adapter);
                        srlBaresRefresh.setRefreshing(false);
                        break;
                    case 3:
                        tipoFiltro = ESTRELLA_ASC;
                        ordenarBares();
                        adapter = new BaresAdapter (listaBares, getContext(),getFragmentManager());
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
                        //se le aplica el adaptador al recyclerView
                        recyclerView.setAdapter(adapter);
                        srlBaresRefresh.setRefreshing(false);
                        break;
                    case 4:
                        tipoFiltro = ESTRELLA_DESC;
                        ordenarBares();
                        adapter = new BaresAdapter (listaBares, getContext(),getFragmentManager());
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
                        //se le aplica el adaptador al recyclerView
                        recyclerView.setAdapter(adapter);
                        srlBaresRefresh.setRefreshing(false);
                        break;
                    default:
                        tipoFiltro = NADA;
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //llamadita 2.0
            }
        });
    }
//iniciamos el swipe vertical
    public void iniciarSwipeVertical(View root){
        srlBaresRefresh = (SwipeRefreshLayout) root.findViewById(R.id.srfBaresRefresh);
        srlBaresRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srlBaresRefresh.setColorSchemeResources(R.color.colorAccent);
                srlBaresRefresh.setProgressBackgroundColorSchemeResource(R.color.backIcon);
                // Volvemos a cargar los datos
                cargarDatos();

            }
        });
    }
//Comprueba la conexion
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService
                (Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
//Cargamos los datos del servicio rest
    private void cargarDatos(){
        int id = BdController.selectIdUser(getContext());
        Call<ArrayList<Bar>> call = misTapasRest.findAllBares(String.valueOf(id));
        call.enqueue(new Callback<ArrayList<Bar>>() {
            @Override
            public void onResponse(Call<ArrayList<Bar>> call, Response<ArrayList<Bar>> response) {
                //si encuentra bares
                if(response.isSuccessful()){
                    listaBares =  response.body();
                    FragmentManager fm = getFragmentManager();
                    //creamos el adaptador
                    adapter = new BaresAdapter (listaBares, getContext(),fm);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
                    //se le aplica el adaptador al recyclerView
                    recyclerView.setAdapter(adapter);
                    srlBaresRefresh.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Bar>> call, Throwable t) {
                Log.e("ERROR: ", t.getMessage());
                //Toast.makeText( , "Es necesaria una conexión a internet", Toast.LENGTH_SHORT).show();
            }
        });
    }
//Ordenamos los bares segun el tipo de filtro que hallamos utilizado
    private void ordenarBares() {
        switch (tipoFiltro) {
            case NADA:
                //Collections.sort(this.bares, (Bar l1, Bar l2) -> l1.getId().co);
                break;
            case NOMBRE_ASC:
                Collections.sort(this.listaBares, (Bar l1, Bar l2) -> l1.getNombre().compareTo(l2.getNombre()));
                break;
            case NOMBRE_DESC:
                Collections.sort(this.listaBares, (Bar l1, Bar l2) -> l2.getNombre().compareTo(l1.getNombre()));
                break;
            case ESTRELLA_ASC:
                Collections.sort(this.listaBares, (Bar l1, Bar l2) -> Integer.compare(l1.getEstrellas(),l2.getEstrellas()) );
                break;
            case ESTRELLA_DESC:
                Collections.sort(this.listaBares, (Bar l1, Bar l2) -> Integer.compare(l2.getEstrellas(),l1.getEstrellas()));
                break;
            default:
                break;
        }
    }
//Controlamos el filtro de voz
    private void controlarVoz() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //reconocemos el idioma del telefono
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "¿Cómo quieres ordenar los bares?");
        try {
            startActivityForResult(intent, VOZ);
        } catch (Exception e) {
        }
    }
    /**
     * Activity Result de control por voz
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == MainActivity.RESULT_CANCELED) {
            return;
        }


        if (requestCode == VOZ) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> voz = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                // Analizamos los que nos puede llegar
                String secuencia = "";
                int tipoFiltro;
                // Concatenamos todo lo que tiene la cadena encontrada para buscar palabras clave
                for (String v : voz) {
                    secuencia += " " + v;
                }

                // A partir de aquí podemos crear el if todo lo complejo que queramos o irnos a otro fichero
                // O métpdp
                if (secuencia != null) {
                    analizarFiltroVoz(secuencia);

                    ordenarBares();
                    FragmentManager fm = getFragmentManager();
                    adapter = new BaresAdapter (listaBares, getContext(),fm);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
                    //se le aplica el adaptador al recyclerView
                    recyclerView.setAdapter(adapter);

                }
            }

        }
    }
//Analizamos el filtro de voz para comprobar lo que pide el usuario
    private void analizarFiltroVoz(String secuencia) {
        // Nombre
        if ((secuencia.contains("nombre")) &&
                !((secuencia.contains("descendente") || secuencia.contains("inverso")))) {
            tipoFiltro = NOMBRE_ASC;
        } else if ((secuencia.contains("nombre")) &&
                !((secuencia.contains("ascendente") || secuencia.contains("ascendiente")))) {
            tipoFiltro = NOMBRE_DESC;
            // Fecha
        } else if ((secuencia.contains("estrellas")) &&
                !((secuencia.contains("descendentes") || secuencia.contains("inverso")))) {
            tipoFiltro = ESTRELLA_ASC;
        } else if ((secuencia.contains("estrellas")) &&
                !((secuencia.contains("ascendentes") || secuencia.contains("ascendiente")))) {
            tipoFiltro = ESTRELLA_DESC;


        } else {
            tipoFiltro = NADA;
        }
    }
//Pedimos los permisos
    private void pedirMultiplesPermisos(){
        // Indicamos el permisos y el manejador de eventos de los mismos
        Dexter.withActivity(this.getActivity())
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // ccomprbamos si tenemos los permisos de todos ellos
                        if (report.areAllPermissionsGranted()) {
                            //Toast.makeText(getContext(), "¡Todos los permisos concedidos!", Toast.LENGTH_SHORT).show();
                        }

                        // comprobamos si hay un permiso que no tenemos concedido ya sea temporal o permanentemente
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // abrimos un diálogo a los permisos
                            //openSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getContext(), "Existe errores! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }
}