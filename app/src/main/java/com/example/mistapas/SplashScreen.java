package com.example.mistapas;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mistapas.ui.login.ActividadLogin;
import com.example.mistapas.ui.login.BdController;
import com.example.mistapas.ui.modelos.Usuario;
import com.example.mistapas.ui.rest.ApiUtils;
import com.example.mistapas.ui.rest.MisTapasRest;

import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreen extends AppCompatActivity {
    MisTapasRest misTapasRest= ApiUtils.getService();
    String token_rest="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                String token_sqLite= BdController.selectToken(getBaseContext());
                if(token_sqLite.equals("")){
                    Intent intent = new Intent(SplashScreen.this, ActividadLogin.class);
                    startActivity(intent);
                }else{
                   buscarToken(token_sqLite);
                }




            }
        },4000);
    }


    private void buscarToken(String tok){
        Call<Usuario> call = misTapasRest.comproToken(tok);

        Log.e("cosa","a");
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if(response.isSuccessful()){
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);

                }else{
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
