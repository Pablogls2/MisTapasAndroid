package com.example.mistapas.ui.listabares;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.mistapas.R;
import com.example.mistapas.ui.modelos.Bar;
import com.example.mistapas.ui.rest.ApiUtils;
import com.example.mistapas.ui.rest.MisTapasRest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleBares extends Fragment {

    private ImageView ivDetalleImagen;
    private TextView tvDetalleNombre, tvDetalleTapas, tvDetalleEstrellas;
    private Button btnDetalleBorrar;
    private FragmentManager fm;
    MisTapasRest misTapasRest;
    private Bar bar;

    public static DetalleBares newInstance(Bar bar) {
        Bundle b = new Bundle();
        b.putSerializable("bar", bar);
        DetalleBares db = new DetalleBares();
        db.setArguments(b);
        return db;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.detalle_bares_fragment, container, false);
        Bundle b = getArguments();
        bar = (Bar) b.getSerializable("bar");
        iniciarVista(root);
        return root;
    }

    private void iniciarVista(View root) {
        ivDetalleImagen = (ImageView) root.findViewById(R.id.ivDetalleImagen);
        tvDetalleNombre = root.findViewById(R.id.tvDetalleNombre);
        tvDetalleTapas = root.findViewById(R.id.tvDetalleTapas);
        tvDetalleEstrellas = root.findViewById(R.id.tvDetalleEstrellas);
        btnDetalleBorrar = root.findViewById(R.id.btnDetalleBorrar);

        tvDetalleNombre.setText(String.format(" %s %s", tvDetalleNombre.getText(), bar.getNombre()));
        tvDetalleTapas.setText(String.format(" %s %s", tvDetalleTapas.getText(), bar.getTapas()));
        String stars = "";
        for (int i = 0; i < bar.getEstrellas(); i++) {
            stars = stars + '*';
        }
        tvDetalleEstrellas.setText(String.format(" %s %s", tvDetalleEstrellas.getText(), stars));
        ivDetalleImagen.setImageBitmap(base64ToBitmap(bar.getImagen()));

        if(isNetworkAvailable()) {
            misTapasRest = ApiUtils.getService();
        }else{
            Toast.makeText(getContext(), "Es necesaria una conexión a internet", Toast.LENGTH_SHORT).show();
        }

        btnDetalleBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogBorrar(bar.getId());
            }
        });
    }

    private void showDialogBorrar(final int id){
        AlertDialog.Builder borrarDialog= new AlertDialog.Builder(getContext());
        borrarDialog.setTitle("ATENCIÓN! ¿QUIERES BORRAR EL BAR?");
        String[] borrarDialogItems = {
                "Confirmar ",
                "Cancelar" };
        borrarDialog.setItems(borrarDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                borrarBar(bar.getId());
                                getActivity().onBackPressed();
                                break;
                            case 1:
                                break;
                        }
                    }
                });
        borrarDialog.show();
    }

    private void borrarBar(int id) {
        // Llamamos al servicio a eliminar
        Call<Bar> call = misTapasRest.deleteBar(id);
        call.enqueue(new Callback<Bar>() {
            @Override
            public void onResponse(Call<Bar> call, Response<Bar> response) {
                if (response.isSuccessful()) {

                }
            }
            //Si error
            @Override
            public void onFailure(Call<Bar> call, Throwable t) {
                Log.e("ERROR: ", t.getMessage());
            }
        });
    }

    private Bitmap base64ToBitmap(String b64) {
        byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_flecha_back, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_volver:
                getActivity().onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService
                (Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
