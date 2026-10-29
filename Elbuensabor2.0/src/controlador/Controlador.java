package controlador;

import modelo.*;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;

public class Controlador {
    private Catalogo catalogo;
    private Venta venta;

    public Controlador() {
        catalogo = new Catalogo();
        venta = new Venta(catalogo);
    }

    public ArrayList<Producto> listarProductos() {
        return catalogo.listar();
    }

    public void registrarProducto(String codigo, String nombre,
                                  double precio, int stock) {
        catalogo.registrar(codigo, nombre, precio, stock);
    }

    public void editarProducto(String codigo, String nombre, double precio) {
        catalogo.editar(codigo, nombre, precio);
    }

    public void reponerStock(String codigo, int cantidad) {
        catalogo.reponerStock(codigo, cantidad);
    }

    public Pedido nuevoPedido(String cliente) {
        return venta.nuevoPedido(cliente);
    }

    public void agregarProducto(Pedido pedido, String codigo, int cantidad) {
        venta.agregarProducto(pedido, codigo, cantidad);
    }

    public void confirmarPedido(Pedido pedido) {
        venta.confirmar(pedido);
    }

    public ArrayList<Pedido> listarPedidos() {
        return venta.listar();
    }

    public String generarReporte(LocalDate fecha) {
        return venta.generarReporte(fecha);
    }

    public File guardarReporte(String texto, LocalDate fecha) {
        return venta.guardarReporte(texto, fecha);
    }
}
