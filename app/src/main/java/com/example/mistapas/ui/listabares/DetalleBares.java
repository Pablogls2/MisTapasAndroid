package com.example.mistapas.ui.listabares;

import android.view.WindowManager;
import android.widget.*;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.mistapas.R;
import com.example.mistapas.ui.modelos.Bar;

public class DetalleBares extends Fragment {

    private ImageView ivDetalleImagen;
    private EditText etDetalleTitulo, etDetalleTapas;
    private Spinner spDetalleSpinner;
    private Button btnMapaAdd;

    public static DetalleBares newInstance(Bar bar) {
        Bundle b = new Bundle();
        b.putSerializable("bar",bar);
        DetalleBares db = new DetalleBares();
        db.setArguments(b);
        return db;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.detalle_bares_fragment, container, false);
        Bundle b = getArguments();
        Bar bar = (Bar) b.getSerializable("bar");

        return root;
    }

    /*public void iniciarVista(){
        ivDetalleImagen = (ImageView) root.findViewById(R.id.ivDetalleImagen);
        etDetalleTitulo = root.findViewById(R.id.etDetalleNombre);
        etDetalleTapas =root.findViewById(R.id.etDetalleTapas);
        spDetalleSpinner = root.findViewById(R.id.spDetalleEstrellas);
        btnMapaAdd=root.findViewById(R.id.btnDetalleBorrar);
        String[] estrellas = {"*","**", "***", "****", "*****"};
        spDetalleSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, estrellas));
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


    }*/

}
