package com.example.mistapas.ui.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mistapas.MainActivity;
import com.example.mistapas.R;
import com.example.mistapas.ui.modelos.Usuario;
import com.example.mistapas.ui.rest.ApiUtils;
import com.example.mistapas.ui.rest.MisTapasRest;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ActividadLogin extends AppCompatActivity {

    EditText etLoginUser, etLoginPass;
    Button btnLoginMostrarPass, btnLoginRegistro, btnLoginEntrar;
    RelativeLayout relativeLayout;
    private boolean esVisible;
    ImageView imagen;
    private MisTapasRest misTapasRest;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        iniciarVista();

    }

    public void iniciarVista(){

        etLoginUser =(EditText)findViewById(R.id.etLoginUsu);
        etLoginPass =(EditText)findViewById(R.id.etpLoginContra);
        btnLoginMostrarPass =(Button) findViewById(R.id.btnLoginMostOcult);
        btnLoginRegistro =(Button)findViewById(R.id.btnLoginRegistrar);
        btnLoginEntrar =(Button)findViewById(R.id.btnLoginLogin);
        imagen=(ImageView)findViewById(R.id.imgLogin);


        // Iniciamos la API REST
        if(isNetworkAvailable()) {
            misTapasRest = ApiUtils.getService();
        }else{
            Toast.makeText(getApplicationContext(), "Es necesaria una conexión a internet", Toast.LENGTH_SHORT).show();
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
                //listarProductos();
               /* for (int i=0;i<lista.size();i++){
                    Log.e("HOLA","a"+lista.get(i));
                }*/
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService
                (Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    private void buscarUsuario(){
        Call<Usuario> call = misTapasRest.findUser(etLoginUser.getText().toString(),etLoginPass.getText().toString());
        Toast toast = Toast.makeText(getApplicationContext(), "hola", Toast.LENGTH_LONG);
        //toast.show();
        Log.e("cosa","a");
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if(response.isSuccessful()){
                    Usuario user = response.body();
                    if(user != null){
                        Intent intent = new Intent(ActividadLogin.this, MainActivity.class);
                        startActivity(intent);
                    }else {
                        Toast.makeText(getApplicationContext(), "No existe el usuario, registre por favor", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Por favor introduce datos ",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e("ERROR: ", t.getMessage());
            }
        });

    }



}
