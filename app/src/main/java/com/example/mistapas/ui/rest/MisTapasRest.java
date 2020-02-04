package com.example.mistapas.ui.rest;

import com.example.mistapas.ui.modelos.Bar;
import com.example.mistapas.ui.modelos.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MisTapasRest {
    //Obtenemos todos los resultados
    // GET: http://80.102.108.57:5555/app/usuarios
    @GET("usuarios")
    Call<List<Usuario>> findALlUser();

    //Obtener usuario
    // GET: por definir direccion
    @GET("login/{nick}/{psw}")
    Call<Usuario> findUser(@Path("nick") String nick,@Path("psw") String psw);

    // Crear un producto
    //POST:
    @POST("registro")
    Call<Usuario> create(@Body Usuario user);

    //Añadir un bar
    @POST("insertBar")
    Call<Bar> create(@Body Bar bar);

    //ListarBares
    @GET("listBar/{idUsuario}")
    Call<List<Bar>> findAllBares();

    //recoger bares con filtro
    @GET("listBar/{idUsuario}/{nombre}")
    Call<List<Bar>> findFiltroBares();

}
