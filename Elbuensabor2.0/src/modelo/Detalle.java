package modelo;

import java.util.Locale;

public class Detalle {
    private String codigoProducto;
    private String nombreProducto;
    private int cantidad;
    private double precioUnitario;

    public Detalle(String codigo, String nombre, int cantidad, double precio) {
        if (cantidad <= 0 || !Double.isFinite(precio) || precio <= 0)
            throw new IllegalArgumentException("Datos del producto inválidos.");
        this.codigoProducto = codigo;
        this.nombreProducto = nombre;
        this.cantidad = cantidad;
        this.precioUnitario = precio;
    }

    public String getCodigoProducto() { return codigoProducto; }
    public String getNombreProducto() { return nombreProducto; }
    public int getCantidad() { return cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }

    public void sumarCantidad(int cantidad) {
        if (cantidad <= 0)
            throw new IllegalArgumentException("Cantidad inválida.");
        this.cantidad = Math.addExact(this.cantidad, cantidad);
    }

    public double getSubtotal() {
        return cantidad * precioUnitario;
    }

    public String toLinea() {
        return codigoProducto + "~" + nombreProducto + "~" + cantidad +
                "~" + String.format(Locale.US, "%.2f", precioUnitario);
    }

    public static Detalle desdeLinea(String linea) {
        String[] p = linea.split("~", -1);
        if (p.length != 4)
            throw new IllegalArgumentException("Detalle inválido.");
        return new Detalle(p[0], p[1], Integer.parseInt(p[2]),
                Double.parseDouble(p[3]));
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%s x%d = $%.2f",
                nombreProducto, cantidad, getSubtotal());
    }
}
