package com.example.mistapas.ui.rest;

import com.example.mistapas.ui.modelos.Bar;
import com.example.mistapas.ui.modelos.Usuario;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface MisTapasRest {
    //Obtenemos todos los resultados
    // GET: http://80.102.108.57:5555/app/usuarios
    @GET("usuarios")
    Call<List<Usuario>> findALlUser();

    //Obtener usuario
    // GET: por definir direccion
    @GET("login/{nick}/{psw}")
    Call<Usuario> findUser(@Path("nick") String nick,@Path("psw") String psw);

    //Obtener usuario
    // GET: por definir direccion
    @GET("comproRegistro/{nick}")
    Call<Usuario>  comproUser(@Path("nick") String nick);

    // Crear un usuario
    //POST:
    @POST("registro")
    Call<Usuario> create(@Body Usuario user);

    //Añadir un bar
    @POST("insertBar")
    Call<Bar> create(@Body Bar bar);

    //ListarBares
    @GET("listBar/{idUsuario}")
    Call<ArrayList<Bar>> findAllBares(@Path("idUsuario") String idUsuario);

    //recoger bares con filtro
    @GET("listBar/{idUsuario}/{nombre}")
    Call<List<Bar>> findFiltroBares();

    //update del token
    @PUT("updateTok/{id}")
    Call<Usuario> update(@Path ("id") int id, @Body Usuario user);

    //borrar bares
    @DELETE("deleteBar/{id}")
    Call<Bar> deleteBar(@Path("id") int id);

    //comprobamos el token
    @GET("comproToken/{token}")
    Call<Usuario>  comproToken(@Path("token") String token);

    @GET("bares")
    Call<ArrayList<Bar>> findALlBar();
}
