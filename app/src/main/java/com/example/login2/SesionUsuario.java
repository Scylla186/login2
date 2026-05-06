package com.example.login2;

public class SesionUsuario {

    private static SesionUsuario instance;

    private String  uid      = "";
    private String  nombre   = "";
    private String  usuario  = "";
    private boolean activo   = false;

    private SesionUsuario() {}

    public static SesionUsuario getInstance() {
        if (instance == null) instance = new SesionUsuario();
        return instance;
    }

    public void iniciarSesion(String uid, String nombre, String usuario) {
        this.uid     = uid;
        this.nombre  = nombre;
        this.usuario = usuario;
        this.activo  = true;
    }

    public void cerrarSesion() {
        this.uid     = "";
        this.nombre  = "";
        this.usuario = "";
        this.activo  = false;
    }

    public String  getUid()     { return uid; }
    public String  getNombre()  { return nombre; }
    public String  getUsuario() { return usuario; }
    public boolean isActivo()   { return activo; }
}