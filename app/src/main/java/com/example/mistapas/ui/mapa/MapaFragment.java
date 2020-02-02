package com.example.mistapas.ui.mapa;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mistapas.R;
import com.google.android.gms.location.FusedLocationProviderClient;
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

import java.util.List;


public class MapaFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

    private SupportMapFragment supportMapFragment;
    private GoogleMap mapa;
    private LatLng posicionActual;
    private Marker marcadorActual = null;

    private boolean permisos = false;

    private FusedLocationProviderClient Posicion;
    private Location ultimaLocalizacion;

    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,
                              Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mapa, container, false);
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        iniciarMapa();

    }

    /**
     * Con este método iniciamos el mapa
     */
    private void iniciarMapa() {
        Posicion = LocationServices.getFusedLocationProviderClient(getActivity());
        FragmentManager fm = getChildFragmentManager();
        supportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.contenedorTapasMapa);
        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.contenedorTapasMapa, supportMapFragment).commit();
        }
        supportMapFragment.getMapAsync(this);
    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
        pedirMultiplesPermisos();
        //Así se va a ver el mapa
        MostrarMapa();
        //Obtenemos la posición
        obtenerPosicionActual();
    }


    /**
     * Con este método comprobamos q los permisos están otorgados, si no es asi se los pedirá al usuario
     */
    private void pedirMultiplesPermisos(){
        // Indicamos el permisos y el manejador de eventos de los mismos
        Dexter.withActivity(getActivity())
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {


                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // ccomprbamos si tenemos los permisos de todos ellos
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getContext().getApplicationContext(), "¡Todos los permisos concedidos!", Toast.LENGTH_SHORT).show();
                            permisos=true;
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
                        Toast.makeText(getContext().getApplicationContext(), "Existe errores! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void MostrarMapa() {
        mapa.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mapa.setMinZoomPreference(15.0f);
        mapa.setOnMarkerClickListener(this);

        if(permisos){
            mapa.setMyLocationEnabled(true);
        }

        UiSettings uiSettings = mapa.getUiSettings();
        uiSettings.setScrollGesturesEnabled(true);
        uiSettings.setTiltGesturesEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setCompassEnabled(false);
    }


    /**
     * Obtenemos la posición acutal
     */
    private void obtenerPosicionActual() {
        try {

            Task<Location> local = Posicion.getLastLocation();
            local.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {

                    if (task.isSuccessful()) {
                        ultimaLocalizacion = task.getResult();
                        if (ultimaLocalizacion != null) {



                            ultimaLocalizacion.setLatitude(ultimaLocalizacion.getLatitude());
                            ultimaLocalizacion.setLongitude(ultimaLocalizacion.getLongitude());
                            posicionActual = new LatLng(ultimaLocalizacion.getLatitude(),ultimaLocalizacion.getLongitude());


                            situarCamara();
                            marcadorPosicionActual();







                        } else {
                            Snackbar.make(getView(), "No se puede establecer la posición actual",
                                    Snackbar.LENGTH_LONG).show();
                        }

                    } else {
                        Log.d("GPS", "No se ha podido encuentrar la última posición.");
                        Log.e("GPS", "Exception: %s", task.getException());
                    }
                }
            });

        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }


    }

    
    private void situarCamara() {
        mapa.moveCamera(CameraUpdateFactory.newLatLng(posicionActual));
    }

    // Para dibujar el marcador actual
    private void marcadorPosicionActual() {
        /*
        Circle circle = mMap.addCircle(new CircleOptions()
                .center(this.posActual)
                .radius(50)
                .strokeColor(Color.MAGENTA)
                .fillColor(Color.BLUE));

        */

        // Borramos el arcador actual si está puesto
        if(marcadorActual!=null){
            marcadorActual.remove();
        }
        // añadimos el marcador actual

        marcadorActual= mapa.addMarker(new MarkerOptions()

                .position(posicionActual)

                .title("Mi Localización")

                .snippet("Localización actual")

                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))


        );


    }

}
