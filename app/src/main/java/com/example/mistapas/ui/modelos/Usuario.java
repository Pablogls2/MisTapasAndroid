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

    //token
    @SerializedName("token")
    @Expose
    private String token;

    public Usuario() {
    }

    public Usuario(String nickname, String nombre, String email, String psw) {

        this.nickname = nickname;
        this.nombre = nombre;
        this.email = email;
        this.psw = psw;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPsw() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
