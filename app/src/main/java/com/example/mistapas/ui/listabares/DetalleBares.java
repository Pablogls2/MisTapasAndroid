package com.example.mistapas.ui.listabares;

import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.mistapas.R;

public class DetalleBares extends Fragment {

    public static DetalleBares newInstance() {
        return new DetalleBares();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.detalle_bares_fragment, container, false);

        return root;
    }

}
