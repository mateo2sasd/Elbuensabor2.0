package modelo;

import java.io.*;
import java.util.ArrayList;

public class Catalogo {
    private ArrayList<Producto> productos;
    private File archivo;

    public Catalogo() {
        archivo = Archivo.archivo("productos.txt");
        productos = Generico.crearLista();
        cargar();
    }

    private void cargar() {
        try {
            for (String linea : Archivo.leerLineas(archivo))
                Generico.agregarVarios(productos,
                        Producto.desdeLinea(linea));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void guardar() {
        ArrayList<String> lineas = Generico.crearLista();

        for (Producto producto : productos)
            Generico.agregarVarios(lineas, producto.toLinea());

        try {
            Archivo.escribirLineas(archivo, lineas);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public ArrayList<Producto> listar() {
        return new ArrayList<>(productos);
    }

    public Producto buscar(String codigo) {
        if (codigo == null) return null;

        for (Producto producto : productos)
            if (producto.getCodigo().equalsIgnoreCase(codigo.trim()))
                return producto;

        return null;
    }

    public void registrar(String codigo, String nombre,
                          double precio, int stock) {
        Producto producto = new Producto(codigo, nombre, precio, stock);

        if (buscar(producto.getCodigo()) != null)
            throw new IllegalArgumentException("El código ya existe.");

        Generico.agregarVarios(productos, producto);
        guardar();
    }

    public void editar(String codigo, String nombre, double precio) {
        Producto producto = buscar(codigo);

        if (producto == null)
            throw new IllegalArgumentException("Producto no encontrado.");

        producto.editar(nombre, precio);
        guardar();
    }

    public void reponerStock(String codigo, int cantidad) {
        Producto producto = buscar(codigo);

        if (producto == null)
            throw new IllegalArgumentException("Producto no encontrado.");

        producto.reponer(cantidad);
        guardar();
    }
}
