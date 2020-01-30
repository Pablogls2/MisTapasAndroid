package com.example.mistapas.ui.modelos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Usuario {

    //Campos
    //id
    @SerializedName("id")
    @Expose
    private int id;

    //nick
    @SerializedName("nickname")
    @Expose
    private String nickname;

    //nombre user
    @SerializedName("nombre")
    @Expose
    private String nombre;

    //email user
    @SerializedName("email")
    @Expose
    private String email;

    //Contraseña
    @SerializedName("psw")
    @Expose
    private String psw;
}
