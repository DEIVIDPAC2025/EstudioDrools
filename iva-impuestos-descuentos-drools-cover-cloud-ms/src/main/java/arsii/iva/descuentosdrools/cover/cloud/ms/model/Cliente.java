package arsii.iva.descuentosdrools.cover.cloud.ms.model;

public class Cliente {
    private String nombre;
    private int edad;
    private double compra;
    private double descuento;

    public Cliente(String nombre, int edad, double compra) {
        this.nombre = nombre;
        this.edad = edad;
        this.compra = compra;
    }

    public String getNombre() { return nombre; }
    public int getEdad() { return edad; }
    public double getCompra() { return compra; }
    public double getDescuento() { return descuento; }

    public void setDescuento(double descuento) { this.descuento = descuento; }
}

