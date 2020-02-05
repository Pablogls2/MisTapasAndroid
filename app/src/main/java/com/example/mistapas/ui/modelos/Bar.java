package com.example.mistapas.ui.modelos;

import android.graphics.Bitmap;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.sql.Blob;

public class Bar {

    //Campos
    //id
    @SerializedName("id")
    @Expose
    private int id;

    //nombre
    @SerializedName("nombre")
    @Expose
    private String nombre;

    //latitud
    @SerializedName("latitud")
    @Expose
    private Double latitud;

    //longitud
    @SerializedName("longitud")
    @Expose
    private Double longitud;

    //estrellas
    @SerializedName("estrellas")
    @Expose
    private int estrellas;

    //idUser
    @SerializedName("idUsuario")
    @Expose
    private int idUsuario;

    //imagen
    @SerializedName("imagen")
    @Expose
    private String imagen;

    @SerializedName("tapas")
    @Expose
    private String tapas;

    public Bar(String nombre, Double latitud, Double longitud, int estrellas,String tapas,String imagen ,int idUsuario ) {
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.estrellas = estrellas;
        this.idUsuario = idUsuario;
        this.imagen = imagen;
        this.tapas=tapas;
    }

    public Bar(int id, String nombre, Double latitud, Double longitud, int estrellas, int idUsuario, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.estrellas = estrellas;
        this.idUsuario = idUsuario;
        this.imagen = imagen;
    }

    public String getTapas() {
        return tapas;
    }

    public void setTapas(String tapas) {
        this.tapas = tapas;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public int getEstrellas() {
        return estrellas;
    }

    public void setEstrellas(int estrellas) {
        this.estrellas = estrellas;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }




}
