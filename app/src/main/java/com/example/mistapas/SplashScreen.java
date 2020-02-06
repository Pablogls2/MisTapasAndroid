package com.example.mistapas;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mistapas.ui.login.ActividadLogin;
import com.example.mistapas.ui.login.BdController;
import com.example.mistapas.ui.modelos.Usuario;
import com.example.mistapas.ui.rest.ApiUtils;
import com.example.mistapas.ui.rest.MisTapasRest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreen extends AppCompatActivity {
    private MisTapasRest misTapasRest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        //comprobamos conexion
        if(isNetworkAvailable()) {
            //obtenemos servicio
            misTapasRest = ApiUtils.getService();
        }else{
            Toast.makeText(getApplicationContext(), "Es necesaria una conexión a internet", Toast.LENGTH_SHORT).show();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Recogemos el token de sqlite
                String token_sqLite = BdController.selectToken(getBaseContext());
                //Si no existe token entrara al login
                if (token_sqLite.equals("")) {
                    Intent intent = new Intent(SplashScreen.this, ActividadLogin.class);
                    startActivity(intent);
                    //Si existe lo buscamos
                } else {
                    if(isNetworkAvailable()) {
                        buscarToken(token_sqLite);
                    }else{
                        Toast.makeText(getApplicationContext(), "Es necesaria una conexión a internet", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        }, 4000);
    }
//metodo que comprueba la conexion a internet
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService
                (Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

//metodo que busca token mediante la conexion rest
    private void buscarToken(String tok) {
        Call<Usuario> call = misTapasRest.comproToken(tok);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                //Si encuentra el token accede a la main activity directamente
                if (response.isSuccessful()) {
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                //Si encuentra un token distinto accede al login
                } else {
                    Intent intent = new Intent(SplashScreen.this, ActividadLogin.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e("ERROR: ", t.getMessage());
            }
        });

    }

}

