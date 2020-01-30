package com.example.mistapas.ui.login;

import android.content.Context;
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
import androidx.fragment.app.Fragment;

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


public class Login extends Fragment {

    EditText etLoginUser, etLoginPass;
    Button btnLoginMostrarPass, btnLoginRegistro, btnLoginEntrar;
    RelativeLayout relativeLayout;
    private boolean esVisible;
    ImageView imagen;
    private MisTapasRest misTapasRest;

    List lista= new List() {
        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(@Nullable Object o) {
            return false;
        }

        @NonNull
        @Override
        public Iterator iterator() {
            return null;
        }

        @NonNull
        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @NonNull
        @Override
        public Object[] toArray(@NonNull Object[] a) {
            return new Object[0];
        }

        @Override
        public boolean add(Object o) {
            return false;
        }

        @Override
        public boolean remove(@Nullable Object o) {
            return false;
        }

        @Override
        public boolean containsAll(@NonNull Collection c) {
            return false;
        }

        @Override
        public boolean addAll(@NonNull Collection c) {
            return false;
        }

        @Override
        public boolean addAll(int index, @NonNull Collection c) {
            return false;
        }

        @Override
        public boolean removeAll(@NonNull Collection c) {
            return false;
        }

        @Override
        public boolean retainAll(@NonNull Collection c) {
            return false;
        }

        @Override
        public void clear() {

        }

        @Override
        public Object get(int index) {
            return null;
        }

        @Override
        public Object set(int index, Object element) {
            return null;
        }

        @Override
        public void add(int index, Object element) {

        }

        @Override
        public Object remove(int index) {
            return null;
        }

        @Override
        public int indexOf(@Nullable Object o) {
            return 0;
        }

        @Override
        public int lastIndexOf(@Nullable Object o) {
            return 0;
        }

        @NonNull
        @Override
        public ListIterator listIterator() {
            return null;
        }

        @NonNull
        @Override
        public ListIterator listIterator(int index) {
            return null;
        }

        @NonNull
        @Override
        public List subList(int fromIndex, int toIndex) {
            return null;
        }
    };

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
                //listarProductos();
               /* for (int i=0;i<lista.size();i++){
                    Log.e("HOLA","a"+lista.get(i));
                }*/
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
        Call<Usuario> call = misTapasRest.findUser("a","a");
        Toast toast = Toast.makeText(getContext(), "hola", Toast.LENGTH_LONG);
        //toast.show();
        Log.e("cosa","a");
        call.enqueue(new Callback<Usuario>() {
                @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if(response.isSuccessful()){
                    Toast.makeText(getContext(),"Correcto",Toast.LENGTH_SHORT).show();
                    Log.e("bien","aaaaaa");
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(getContext(),"inCorrecto",Toast.LENGTH_SHORT).show();
                Log.e("ERROR: ", t.getMessage());
            }
        });
    }
    /*private void listarProductos() {
// Creamos la tarea que llamará al servicio rest y la encolamos
        Call<List<Usuario>> call = misTapasRest.findALl();
        call.enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if(response.isSuccessful()){
// Si tienes exito nos quedamos con el ResponseBody, listado en JSON
// Nos hace el pasrser automáticamente
                    lista = response.body();
                    //listView.setAdapter(new ProductosAdapter(MainActivity.this, R.layout.list_productos, list));
                }
            }
            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                Log.e("ERROR: ", t.getMessage());
            }
        });
    }*/



}
