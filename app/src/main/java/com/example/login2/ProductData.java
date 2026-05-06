package com.example.login2;

public class ProductData {

    private static ProductData instance;

    private String  qrId          = "";
    private String  nombre        = "";
    private String  descripcion   = "";
    private String  detalles      = "";
    private boolean esVegano      = false;
    private String  alergenos     = "";
    private boolean tieneProducto = false;

    private ProductData() {}

    public static ProductData getInstance() {
        if (instance == null) instance = new ProductData();
        return instance;
    }

    public void setProducto(String qrId, String nombre, String descripcion,
                            String detalles, boolean esVegano, String alergenos) {
        this.qrId          = qrId;
        this.nombre        = nombre;
        this.descripcion   = descripcion;
        this.detalles      = detalles;
        this.esVegano      = esVegano;
        this.alergenos     = alergenos;
        this.tieneProducto = true;
    }

    public String  getQrId()        { return qrId; }
    public String  getNombre()      { return nombre; }
    public String  getDescripcion() { return descripcion; }
    public String  getDetalles()    { return detalles; }
    public boolean isEsVegano()     { return esVegano; }
    public String  getAlergenos()   { return alergenos; }
    public boolean tieneProducto()  { return tieneProducto; }

    public void limpiar() {
        qrId = ""; nombre = ""; descripcion = "";
        detalles = ""; alergenos = "";
        esVegano = false; tieneProducto = false;
    }
}