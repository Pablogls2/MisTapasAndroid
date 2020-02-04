package com.example.mistapas.ui.mapa;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mistapas.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class MapaFragment extends Fragment implements  OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private View root;
    private GoogleMap mMap;

    private Bundle mBundle;


    private static final int LOCATION_REQUEST_CODE = 1; // Para los permisos
    private boolean permisos = false;

    // Para obtener el punto actual (no es necesario para el mapa)
    // Pero si para obtener las latitud y la longitud
    private FusedLocationProviderClient mPosicion;

    private Location miUltimaLocalizacion;
    private LatLng posDefecto = new LatLng(38.6901212, -4.1086075);
    private LatLng posActual = posDefecto;

    // Marcador actual
    private Marker marcadorActual = null;


    // Posición actual con eventos y no hilos
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;


    private Button btnMapaAdd;

    private ArrayList<LatLng> recorrido;

    private Timer timer = null;








    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_mapa, container, false);

        //se inicializan los componentes
        mPosicion = LocationServices.getFusedLocationProviderClient(getActivity());


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

       btnMapaAdd= root.findViewById(R.id.btnMapaAdd);
        btnMapaAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddBar de = AddBar.newInstance(posActual.latitude, posActual.longitude);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, de);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });








        return root;


    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = savedInstanceState;


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Solicitamos prmisos de Localización
        solicitarPermisos();


        // Configurar IU Mapa
        configurarIUMapa();


        // Obtenemos la posición GPS

        obtenerPosicion();

        // Situar la camara inicialmente a una posición determinada
        situarCamaraMapa();


        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Crear el LocationRequest

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 segundos en milisegundos
                .setFastestInterval(1 * 1000); // 1 segundo en milisegundos


    }

    /**
     * Metodo para guardar la ruta del usuario
     */





    private void situarCamaraMapa() {
        // movemos la camara al usuario
        mMap.moveCamera(CameraUpdateFactory.newLatLng(posActual));
    }

    private void configurarIUMapa() {
        // activamos los eventos de marcador
        mMap.setOnMarkerClickListener(this);

        // Activar Boton de Posición actual
        if (permisos) {
            // Si tenemos permisos pintamos el botón de la localización actual
            mMap.setMyLocationEnabled(true);
        }

        //ponemos el tipo de mapa en mi caso hibrido
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        UiSettings uiSettings = mMap.getUiSettings();
        // Activamos los gestos
        uiSettings.setScrollGesturesEnabled(true);
        uiSettings.setTiltGesturesEnabled(true);
        // Activamos la brújula
        uiSettings.setCompassEnabled(true);
        // Activamos los controles de zoom
        uiSettings.setZoomControlsEnabled(true);
        // Activamos la brújula
        uiSettings.setCompassEnabled(true);
        // Actiovamos la barra de herramientas
        uiSettings.setMapToolbarEnabled(true);

        // Hacemos el zoom por defecto mínimo
        mMap.setMinZoomPreference(13.0f);
        // Señalamos el tráfico
        mMap.setTrafficEnabled(true);
    }




    // Obtenermos y leemos directamente el GPS
    private void obtenerPosicion() {
        try {
            if (permisos) {
                // Lo lanzamos como tarea concurrente
                Task<Location> local = mPosicion.getLastLocation();
                local.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Actualizamos la última posición conocida
                            miUltimaLocalizacion = task.getResult();
                            posActual = new LatLng(miUltimaLocalizacion.getLatitude(),
                                    miUltimaLocalizacion.getLongitude());

                        } else {
                            Log.d("GPS", "No se encuetra la última posición.");
                            Log.e("GPS", "Exception: %s", task.getException());
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        } catch (Exception e) {

        }
    }

    // Para dibujar el marcador actual
    private void marcadorPosicionActual() {



        // Borramos el arcador actual si está puesto
        if (marcadorActual != null) {
            marcadorActual.remove();
        }
        // añadimos el marcador actual en violeta
        marcadorActual = mMap.addMarker(new MarkerOptions()
                // Posición
                .position(posActual)
                // Título
                .title("Mi Localización")
                // Subtitulo
                .snippet("Localización actual")
                // Color o tipo d icono
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
        );

    }


    // solicitamos los permisos de ubicacion
    public void solicitarPermisos() {

        // Si tenemos los permisos
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Activamos el botón de lalocalización
            permisos = true;
        } else {
            // Si no
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Mostrar diálogo explicativo
            } else {
                // Solicitar permiso
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST_CODE);
            }
        }
    }

    // Para los permisoso
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        permisos = false;
        if (requestCode == LOCATION_REQUEST_CODE) {

            if (permissions.length > 0 &&
                    permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permisos = true;
            } else {
                Toast.makeText(getContext(), "Error de permisos", Toast.LENGTH_LONG).show();
            }
            if (permissions.length > 0 &&
                    permissions[0].equals(Manifest.permission.ACCESS_COARSE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permisos = true;
            } else {
                Toast.makeText(getContext(), "Error de permisos", Toast.LENGTH_LONG).show();
            }

        }
    }




    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    private void handleNewLocation(Location location) {
        Log.d("Mapa", location.toString());
        miUltimaLocalizacion = location;
        posActual = new LatLng(miUltimaLocalizacion.getLatitude(),
                miUltimaLocalizacion.getLongitude());
        // Añadimos un marcador especial para poder operar con esto
        marcadorPosicionActual();

        situarCamaraMapa();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {

                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);

            } catch (IntentSender.SendIntentException e) {

                e.printStackTrace();
            }
        } else {

            Log.i("Mapa", "Location services connection failed with code " + connectionResult.getErrorCode());
        }

    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}
