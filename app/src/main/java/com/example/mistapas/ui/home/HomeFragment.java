package com.example.mistapas.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.mistapas.R;
import com.example.mistapas.SplashScreen;
import com.example.mistapas.ui.login.ActividadLogin;
import com.example.mistapas.ui.login.BdController;

public class HomeFragment extends Fragment {

    private ImageView ivHomeLogOut;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        ivHomeLogOut= root.findViewById(R.id.ivHomeLogOut);

        ivHomeLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String android_id = Settings.Secure.getString(getActivity().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                BdController.insertarData(getContext(),android_id,"",999);

                Intent intent = new Intent( getActivity(), ActividadLogin.class);
                startActivity(intent);
            }
        });

        return root;
    }
}