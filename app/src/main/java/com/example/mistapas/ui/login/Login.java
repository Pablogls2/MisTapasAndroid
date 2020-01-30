package com.example.mistapas.ui.login;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.util.Log;
import android.widget.*;
import androidx.fragment.app.Fragment;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mistapas.R;
import com.example.mistapas.ui.modelos.Usuario;
import com.example.mistapas.ui.rest.ApiUtils;
import com.example.mistapas.ui.rest.MisTapasRest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Login extends Fragment {

    EditText etLoginUser, etLoginPass;
    Button btnLoginMostrarPass, btnLoginRegistro, btnLoginEntrar;
    RelativeLayout relativeLayout;
    private boolean esVisible;
    ImageView imagen;
    private MisTapasRest misTapasRest;



    public Login() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_login, container, false);
        iniciarVista(root);
        return root;
    }

    public void iniciarVista(View vista){

        etLoginUser =(EditText)vista.findViewById(R.id.etLoginUsu);
        etLoginPass =(EditText)vista.findViewById(R.id.etpLoginContra);
        btnLoginMostrarPass =(Button) vista.findViewById(R.id.btnLoginMostOcult);
        btnLoginRegistro =(Button)vista.findViewById(R.id.btnLoginRegistrar);
        btnLoginEntrar =(Button)vista.findViewById(R.id.btnLoginLogin);
        imagen=(ImageView)vista.findViewById(R.id.imgLogin);

        // Iniciamos la API REST
        if(isNetworkAvailable()) {
            misTapasRest = ApiUtils.getService();
        }else{
            Toast.makeText(getContext(), "Es necesaria una conexión a internet", Toast.LENGTH_SHORT).show();
        }

        btnLoginMostrarPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!esVisible) {
                    etLoginPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    esVisible = true;
                    btnLoginMostrarPass.setBackgroundResource(R.drawable.ic_visibility_24px);
                    ///aqui puedes cambiar el texto del boton, o textview, o cambiar la imagen de un imageView.
                }
                else {
                    etLoginPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    esVisible = false;
                    btnLoginMostrarPass.setBackgroundResource(R.drawable.ic_visibility_off_24px);
                    ///aqui puedes cambiar el texto del boton, o textview, o cambiar la imagen de un imageView.
                }


            }
        });

        btnLoginEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscarUsuario();
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService
                (Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void buscarUsuario(){
        Call<Usuario> call = misTapasRest.findUser("pablogls2","superman1");
        Log.e("Error","a"+call.toString());
        call.enqueue(new Callback<Usuario>() {
                @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if(response.isSuccessful()){
                    Toast.makeText(getContext(),"Correcto",Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(getContext(),"inCorrecto",Toast.LENGTH_SHORT);
            }
        });
    }
}
