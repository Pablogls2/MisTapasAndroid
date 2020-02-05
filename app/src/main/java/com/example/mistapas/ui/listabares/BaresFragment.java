package com.example.mistapas.ui.listabares;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mistapas.MainActivity;
import com.example.mistapas.R;
import com.example.mistapas.ui.login.ActividadLogin;
import com.example.mistapas.ui.login.BdController;
import com.example.mistapas.ui.modelos.Bar;
import com.example.mistapas.ui.modelos.Usuario;
import com.example.mistapas.ui.rest.ApiUtils;
import com.example.mistapas.ui.rest.MisTapasRest;
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
import java.util.List;

public class BaresFragment extends Fragment {

    private ConstraintLayout clJuegoConstr;
    private Spinner spnBaresFiltro;
    private RecyclerView recyclerView;
    private BaresAdapter adapter;
    private MisTapasRest misTapasRest;
    private ArrayList<Bar> listaBares;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.lista_bares, container, false);
        recyclerView = (RecyclerView) root.findViewById(R.id.rvBaresRecycler);
        spnBaresFiltro = (Spinner) root.findViewById(R.id.spinner2);
        pedirMultiplesPermisos();

        if(isNetworkAvailable()) {
            misTapasRest = ApiUtils.getService();
        }else{
            Toast.makeText(getContext(), "Es necesaria una conexión a internet", Toast.LENGTH_SHORT).show();
        }

        cargarDatos();

        /*FragmentManager fm = getFragmentManager();
        adapter = new BaresAdapter (listaJuegos, getContext(),fm);
        recyclerView.setHasFixedSize(true);
        // se presenta en formato lineal
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
        //se le aplica el adaptador al recyclerView
        recyclerView.setAdapter(adapter);

        srlJuegoRefresh.setRefreshing(false);*/

        return root;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService
                (Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void cargarDatos(){
        int id = BdController.selectIdUser(getContext());
        Call<ArrayList<Bar>> call = misTapasRest.findAllBares(String.valueOf(id));
        call.enqueue(new Callback<ArrayList<Bar>>() {
            @Override
            public void onResponse(Call<ArrayList<Bar>> call, Response<ArrayList<Bar>> response) {
                //Log.e("ERROR: ", "asda");
                if(response.isSuccessful()){
                    listaBares =  response.body();
                    FragmentManager fm = getFragmentManager();
                    adapter = new BaresAdapter (listaBares, getContext(),fm);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
                    //se le aplica el adaptador al recyclerView
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Bar>> call, Throwable t) {
                Log.e("ERROR: ", t.getMessage());
                //Toast.makeText( , "Es necesaria una conexión a internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

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