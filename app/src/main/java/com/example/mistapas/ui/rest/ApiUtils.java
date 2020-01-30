package com.example.mistapas.ui.rest;

import com.example.mistapas.ui.modelos.Usuario;
import retrofit2.Call;

public class ApiUtils {
    //ip del server
    private static final String server = "80.102.108.57";
    //puerto
    private static final String port = "5555";
    //ip del servicio
    private static final String API_URL = "http://"+server+":"+port+"/app/";


    private ApiUtils() {

    }

    //Constructor servicio con los elementos de la interfaz
    public static MisTapasRest getService() {
        return ClienteRetrofit.getClient(API_URL).create(MisTapasRest.class);
    }

}
