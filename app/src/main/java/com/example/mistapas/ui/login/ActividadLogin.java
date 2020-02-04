package com.example.mistapas.ui.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
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
import com.example.mistapas.ui.registro.RegistroActivity;
import com.example.mistapas.ui.rest.ApiUtils;
import com.example.mistapas.ui.rest.MisTapasRest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
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
            }
        });

        btnLoginRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(getApplicationContext(), RegistroActivity.class);
                startActivity(i);
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

                         String android_id = Settings.Secure.getString(getContentResolver(),
                                Settings.Secure.ANDROID_ID);
                        Date currentTime = Calendar.getInstance().getTime();
                        String token=currentTime.toString();
                         token= md5(token);
                        user.setToken(token);

                        actualizarToken(user.getId(),user);
                        BdController.insertarData(getApplicationContext(),android_id,token,user.getId());



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

    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));

            return hexString.toString();
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void actualizarToken(int id, Usuario u) {
// Llamamos al método actualizar
        Call<Usuario> call = misTapasRest.update(id, u);
        call.enqueue(new Callback<Usuario>() {
            @Override
// Si todo ok
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if(response.isSuccessful()){
                    Toast.makeText(ActividadLogin.this, "Producto actualizado", Toast.LENGTH_SHORT).show();
                }
            }
            // Si error
            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e("ERROR: ", t.getMessage());
            }
        });
    }

}
