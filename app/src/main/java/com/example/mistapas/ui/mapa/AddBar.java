package com.example.mistapas.ui.mapa;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.example.mistapas.R;
import com.example.mistapas.ui.login.BdController;
import com.example.mistapas.ui.modelos.Bar;
import com.example.mistapas.ui.rest.ApiUtils;
import com.example.mistapas.ui.rest.MisTapasRest;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBar extends Fragment {

    private ImageView ivAddImagen;
    private EditText etAddTitulo,etAddTapas;
    private Spinner  spAddSpinner;
    private static final String IMAGE_DIRECTORY = "/misTapas";
    private Button btnMapaAdd;
    private String ruta;
    private String img;
    private Bitmap imagenTransformada;
    MisTapasRest misTapasRest;

    private  Uri photoURI;
    View root;

    private static final int GALERIA = 1 ;
    private static final int CAMARA = 2 ;

   public AddBar(){

   }
    public static AddBar newInstance(Double lat, Double longi){
        Bundle b = new Bundle();
        b.putDouble("latitud",lat);
        b.putDouble("longitud",longi);
        AddBar f = new AddBar();
        f.setArguments(b);
        return f;




    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
         root = inflater.inflate(R.layout.fragment_add_bar, container, false);

        misTapasRest = ApiUtils.getService();
        iniciarVista();
        Bundle b=getArguments();
        Double latitud=b.getDouble("latitud");
        Double longitud=b.getDouble("longitud");


        ivAddImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoFoto();
            }
        });


        setHasOptionsMenu(true);
        pedirMultiplesPermisos();


        btnMapaAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etAddTitulo.getText().toString().isEmpty()|| etAddTapas.getText().toString().isEmpty()  || imagenTransformada == null) {
                    Toast.makeText(getActivity(), "¡Por favor rellena todos los campos!", Toast.LENGTH_SHORT).show();
                }else {
                   int id= BdController.selectIdUser(getContext());
                      Bar b = new Bar(etAddTitulo.getText().toString(), latitud, longitud,spAddSpinner.getSelectedItem().toString().length(),etAddTapas.getText().toString(),bitmapToBase64(imagenTransformada),id);
                      salvarBar(b);

                    }
                }

        });

        return root;
    }


    public void iniciarVista(){
        ivAddImagen= (ImageView) root.findViewById(R.id.ivAddImagen);
        etAddTitulo = root.findViewById(R.id.etAddNombre);
        etAddTapas=root.findViewById(R.id.etAddTapas);
        spAddSpinner= root.findViewById(R.id.spAddEstrellas);
        btnMapaAdd=root.findViewById(R.id.btnMapaAdd);
        String[] estrellas = {"*","**", "***", "****", "*****"};
        spAddSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, estrellas));
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


    }

    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        if (bitmap!=null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
            return stream.toByteArray();
        }
        return null;
    }





    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("FOTO", "Opción::--->" + requestCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_CANCELED) {
            return;
        }

        if (requestCode == GALERIA) {
            Log.d("FOTO", "Entramos en Galería");
            if (data != null) {
                // Obtenemos su URI con su dirección temporal
                Uri contentURI = data.getData();
                try {
                    // Obtenemos el bitmap de su almacenamiento externo
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), contentURI);
                    imagenTransformada =bitmap;
                    //ruta = salvarImagen(bitmap);
                    Toast.makeText(getActivity(), "¡Foto salvada!", Toast.LENGTH_SHORT).show();
                    this.ivAddImagen.setImageBitmap(bitmap);
                    img =bitmapToBase64(bitmap);


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "¡Fallo Galeria!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMARA) {
            Log.d("FOTO", "Entramos en Camara");
            // Cogemos la imagen, pero podemos coger la imagen o su modo en baja calidad (thumbnail
            Bitmap thumbnail = null;
            try {
                // Esta línea para baja
                //thumbnail = (Bitmap) data.getExtras().get("data");
                // Esto para alta
                thumbnail = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), photoURI);
                imagenTransformada =thumbnail;
                // salvamos
                //ruta = salvarImagen(thumbnail); //  photoURI.getPath(); Podríamos poner esto, pero vamos a salvarla comprimida y borramos la no comprimida (por gusto)

                this.ivAddImagen.setImageBitmap(thumbnail);
                img =bitmapToBase64(thumbnail);


                // Borramos el fichero de la URI
               //borrarFichero(photoURI.getPath());

                Toast.makeText(getActivity(), "¡Foto Salvada!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "¡Fallo Camara!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private ByteArrayOutputStream comprimirImagen(Bitmap myBitmap) {
        // Stream de binario
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        // Seleccionamos la calidad y la trasformamos y comprimimos
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 75, bytes);
        return bytes;
    }



    private void mostrarDialogoFoto(){
        AlertDialog.Builder fotoDialogo= new AlertDialog.Builder(getContext());
        fotoDialogo.setTitle("Seleccionar Acción");
        String[] fotoDialogoItems = {
                "Seleccionar fotografía de galería",
                "Capturar fotografía desde la cámara" };
        fotoDialogo.setItems(fotoDialogoItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                elegirFotoGaleria();
                                break;
                            case 1:
                                tomarFotoCamara();
                                break;
                        }
                    }
                });
        fotoDialogo.show();
    }

    public void elegirFotoGaleria() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALERIA);
    }

    public File crearFichero(){
        // Nombre del fichero
        String nombre = crearNombreFichero();
        return salvarFicheroPublico(nombre);
    }

    private String crearNombreFichero() {
        return Calendar.getInstance().getTimeInMillis() + ".jpg";
    }

    private File salvarFicheroPublico(String nombre) {
        // Vamos a obtener los datos de almacenamiento externo
        File dirFotos = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // Si no existe el directorio, lo creamos solo si es publico
        if (!dirFotos.exists()) {
            dirFotos.mkdirs();
        }

        // Vamos a crear el fichero con la fecha
        try {
            File f = new File(dirFotos, nombre);
            f.createNewFile();
            return f;
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return null;
    }


    private void tomarFotoCamara() {
        // Si queremos hacer uso de fotos en aklta calidad
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        // Eso para alta o baja
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Esto para alta calidad
       photoURI = Uri.fromFile(this.crearFichero());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

        // Esto para alta y baja
        startActivityForResult(intent, CAMARA);
    }
    private void pedirMultiplesPermisos(){
        // Indicamos el permisos y el manejador de eventos de los mismos
        Dexter.withActivity(this.getActivity())
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // ccomprbamos si tenemos los permisos de todos ellos
                        if (report.areAllPermissionsGranted()) {
                            //Toast.makeText(getContext(), "¡Todos los permisos concedidos!", Toast.LENGTH_SHORT).show();
                        }

                        // comprobamos si hay un permiso que no tenemos concedido ya sea temporal o permanentemente
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // abrimos un diálogo a los permisos
                            //openSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getContext(), "Existe errores! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20 , byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_flecha_back, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_volver:
                getActivity().onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void salvarBar(Bar b) {
        // Llamamos al metodo de crear

        Call<Bar> call = misTapasRest.create(b);
        call.enqueue(new Callback<Bar>() {

            // Si todo ok
            @Override
            public void onResponse(Call<Bar> call, Response<Bar> response) {
                if(response.isSuccessful()){
                    Toast.makeText(getContext(), "Bar creado", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(), response.toString() , Toast.LENGTH_LONG).show();
                    Log.e("404","dd"+response.toString());
                }
            }

            // Si error
            @Override
            public void onFailure(Call<Bar> call, Throwable t) {
                Log.e("ERROR: ", t.getMessage());
            }
        });
    }

}
