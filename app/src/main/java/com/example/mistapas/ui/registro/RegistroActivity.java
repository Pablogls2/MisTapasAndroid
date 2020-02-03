package com.example.mistapas.ui.registro;

import android.content.Intent;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.mistapas.R;
import com.example.mistapas.ui.login.ActividadLogin;
import com.example.mistapas.ui.modelos.Usuario;
import com.example.mistapas.ui.rest.ApiUtils;
import com.example.mistapas.ui.rest.MisTapasRest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroActivity extends AppCompatActivity {

    View v;
    private boolean esVisible;
    Button btnRegistroMostrarPass,btnRegistroRegistrar,btnRegistroVolver;
    MisTapasRest misTapasRest;
    EditText etRegistroEmail,etRegistroNombre,etRegistroUsuario,etRegistroPass,etRegistroConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_activity);
        iniciarVista();
        misTapasRest = ApiUtils.getService();
    }


    public void iniciarVista() {

        etRegistroEmail = (EditText) findViewById(R.id.etRegistroEmail);
        etRegistroUsuario = (EditText) findViewById(R.id.etRegistroUsuario);
        btnRegistroMostrarPass = (Button) findViewById(R.id.btnRegistroMostOcult);
        btnRegistroVolver = (Button) findViewById(R.id.btnRegistroVolver);
        btnRegistroRegistrar = (Button) findViewById(R.id.btnRegistroRegistrar);
        etRegistroNombre = (EditText) findViewById(R.id.etRegistroNombre);
        etRegistroPass = (EditText) findViewById(R.id.etRegistroPass);
        etRegistroConfirm = (EditText) findViewById(R.id.etRegistroConfirm);


        btnRegistroMostrarPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!esVisible) {
                    etRegistroPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    etRegistroConfirm.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    esVisible = true;
                    btnRegistroMostrarPass.setBackgroundResource(R.drawable.ic_visibility_24px);
                    ///aqui puedes cambiar el texto del boton, o textview, o cambiar la imagen de un imageView.
                } else {
                    etRegistroPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    etRegistroConfirm.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    esVisible = false;
                    btnRegistroMostrarPass.setBackgroundResource(R.drawable.ic_visibility_off_24px);
                    ///aqui puedes cambiar el texto del boton, o textview, o cambiar la imagen de un imageView.
                }


            }
        });

        btnRegistroVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (getApplicationContext(), ActividadLogin.class);
                startActivity(intent);

            }
        });


    }

    /**
     * Salva un producto mediante RESR
     * @param p Producto a salvar
     */
    private void salvarProducto(Usuario p) {
        // Llamamos al metodo de crear
        Call<Usuario> call = misTapasRest.create(p);
        call.enqueue(new Callback<Usuario>() {
            // Si todo ok
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if(response.isSuccessful()){
                    Toast.makeText(RegistroActivity.this, "Usuario creado", Toast.LENGTH_SHORT).show();
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
