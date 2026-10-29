package modelo;

import java.util.Locale;

public class Producto {
    private String codigo;
    private String nombre;
    private double precio;
    private int stock;

    public Producto(String codigo, String nombre, double precio, int stock) {
        if (codigo == null || codigo.trim().isEmpty())
            throw new IllegalArgumentException("Código obligatorio.");
        this.codigo = Archivo.limpiar(codigo).toUpperCase();
        editar(nombre, precio);
        if (stock < 0)
            throw new IllegalArgumentException("Stock inválido.");
        this.stock = stock;
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
    public int getStock() { return stock; }

    public void editar(String nombre, double precio) {
        if (nombre == null || nombre.trim().isEmpty())
            throw new IllegalArgumentException("Nombre obligatorio.");
        if (!Double.isFinite(precio) || precio <= 0)
            throw new IllegalArgumentException("Precio inválido.");
        this.nombre = Archivo.limpiar(nombre);
        this.precio = precio;
    }

    public boolean tieneStock(int cantidad) {
        return cantidad > 0 && cantidad <= stock;
    }

    public void reponer(int cantidad) {
        if (cantidad <= 0)
            throw new IllegalArgumentException("Cantidad inválida.");
        stock = Math.addExact(stock, cantidad);
    }

    public void descontar(int cantidad) {
        if (!tieneStock(cantidad))
            throw new IllegalArgumentException("Stock insuficiente de " + nombre);
        stock -= cantidad;
    }

    public String toLinea() {
        return codigo + ";" + nombre + ";" +
                String.format(Locale.US, "%.2f", precio) + ";" + stock;
    }

    public static Producto desdeLinea(String linea) {
        String[] p = linea.split(";", -1);
        if (p.length != 4)
            throw new IllegalArgumentException("Producto inválido.");
        return new Producto(p[0], p[1], Double.parseDouble(p[2]),
                Integer.parseInt(p[3]));
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre;
    }
}
