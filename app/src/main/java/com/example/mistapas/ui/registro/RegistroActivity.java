package com.example.mistapas.ui.registro;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.mistapas.R;
import com.example.mistapas.ui.login.ActividadLogin;
import com.example.mistapas.ui.modelos.Usuario;
import com.example.mistapas.ui.rest.ApiUtils;
import com.example.mistapas.ui.rest.MisTapasRest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroActivity extends AppCompatActivity {

    MisTapasRest misTapasRest;
    private Button btnRegistroAceptar;
    private Button btnRegistroVolver;
    private EditText etRegistroNombre;
    private EditText etRegistroUsuario;
    private EditText etRegistroEmail;
    private EditText etRegistroPsw;
    private EditText etRegistroConfirPsw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_activity);

        if(isNetworkAvailable()) {
            misTapasRest = ApiUtils.getService();
        }else{
            Toast.makeText(getApplicationContext(), "Es necesaria una conexión a internet", Toast.LENGTH_SHORT).show();
        }
        iniciarVista();
//Funcion del boton registrar
        this.btnRegistroAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Usuario u = null;
                if (comprobarEmail(etRegistroEmail.getText().toString())) {
                    etRegistroEmail.setBackgroundResource(R.drawable.normal_et);
                    etRegistroPsw.setBackgroundResource(R.drawable.normal_et);
                    if (etRegistroConfirPsw.getText().toString().equals(etRegistroPsw.getText().toString())) {
                        u = new Usuario(etRegistroUsuario.getText().toString(), etRegistroNombre.getText().toString(), etRegistroEmail.getText().toString(), etRegistroPsw.getText().toString());
                        if(isNetworkAvailable()) {
                            salvarUsuario(u);
                        }else{
                            Toast.makeText(getApplicationContext(), "Es necesaria una conexión a internet", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        etRegistroConfirPsw.setError("No coinciden");
                        etRegistroConfirPsw.setText("");
                        etRegistroConfirPsw.setBackgroundResource(R.drawable.error_et);
                        etRegistroPsw.setBackgroundResource(R.drawable.normal_et);

                    }
                }else{
                    etRegistroEmail.setError("Email erroneo");
                    etRegistroEmail.setBackgroundResource(R.drawable.error_et);
                }
            }
        });
//Funcion del boton volver
        this.btnRegistroVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistroActivity.this, ActividadLogin.class);
                startActivity(intent);
            }
        });


    }
//Comprobamos la conexion
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService
                (Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
//Iniciamos la vista
    public void iniciarVista() {
        this.btnRegistroAceptar = findViewById(R.id.btnRegistroRegistrar);
        this.btnRegistroVolver = findViewById(R.id.btnRegistroVolver);
        this.etRegistroUsuario = findViewById(R.id.etRegistroUsuario);
        this.etRegistroEmail = findViewById(R.id.etRegistroEmail);
        this.etRegistroConfirPsw = findViewById(R.id.etRegistroConfirm);
        this.etRegistroNombre = findViewById(R.id.etRegistroNombre);
        this.etRegistroPsw = findViewById(R.id.etRegistroPass);
    }

    //Guardamos el usuario en el servicio rest, le pasaremos el usuario
    private void salvarUsuario(Usuario user) {
        // Llamamos al metodo de crear

        Call<Usuario> call = misTapasRest.create(user);
        call.enqueue(new Callback<Usuario>() {
            // Si todo ok
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegistroActivity.this, "Usuario creado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegistroActivity.this, "Usuario repetido", Toast.LENGTH_LONG).show();
                }
            }

            // Si error
            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e("cositas", "Error: " + t.getMessage());
            }
        });
    }
//Metodo que nos comprueba el email con un pattern
    private boolean comprobarEmail(String email) {

        // Patrón para validar el email
        Pattern pattern = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

        Matcher mather = pattern.matcher(email);

        if (mather.find() == true) {
            return true;
        } else {
            return false;
        }

    }
}
