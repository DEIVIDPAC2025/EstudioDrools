package arsii.iva.descuentosdrools.cover.cloud.ms.model;

public class Factura {
    private String numero;
    private double monto;
    private double iva;

    public Factura(String numero, double monto) {
        this.numero = numero;
        this.monto = monto;
    }

    public String getNumero() { return numero; }
    public double getMonto() { return monto; }
    public double getIva() { return iva; }

    public void setIva(double iva) { this.iva = iva; }
}
