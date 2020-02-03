package com.example.mistapas.ui.registro;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.mistapas.R;
import com.example.mistapas.ui.modelos.Usuario;
import com.example.mistapas.ui.rest.ApiUtils;
import com.example.mistapas.ui.rest.MisTapasRest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroActivity extends AppCompatActivity {

    MisTapasRest misTapasRest;
    private Button btnRegistroAceptar;
    private EditText etRegistroNombre;
    private EditText etRegistroUsuario;
    private EditText etRegistroEmail;
    private EditText etRegistroPsw;
    private EditText etRegistroConfirPsw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_activity);
        misTapasRest = ApiUtils.getService();

        iniciarVista();

        this.btnRegistroAceptar.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                Usuario u = new Usuario(etRegistroUsuario.getText().toString(),etRegistroNombre.getText().toString(),etRegistroEmail.getText().toString(),etRegistroPsw.getText().toString());

                salvarUsuario(u);
            }
        });





    }

    public  void iniciarVista(){
        this.btnRegistroAceptar= findViewById(R.id.btnRegistroAceptar);
        this.etRegistroUsuario= findViewById(R.id.etRegistroUsuario);
        this.etRegistroEmail= findViewById(R.id.etRegistroEmail);
        this.etRegistroConfirPsw= findViewById(R.id.etRegistroConfirm);
        this.etRegistroNombre= findViewById(R.id.etRegistroNombre);
        this.etRegistroPsw= findViewById(R.id.etRegistroPass);
    }

    /**
     * Salva un producto mediante RESR
     * @param p Producto a salvar
     */
    private void salvarUsuario(Usuario p) {
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
