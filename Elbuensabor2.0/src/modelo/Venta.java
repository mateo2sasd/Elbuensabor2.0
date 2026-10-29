package modelo;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class Venta {
    private ArrayList<Pedido> pedidos;
    private Catalogo catalogo;
    private File archivo;

    public Venta(Catalogo catalogo) {
        this.catalogo = catalogo;
        this.archivo = Archivo.archivo("pedidos.txt");
        this.pedidos = Generico.crearLista();
        cargar();
    }

    private void cargar() {
        try {
            for (String linea : Archivo.leerLineas(archivo))
                Generico.agregarVarios(pedidos, Pedido.desdeLinea(linea));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void guardar() {
        ArrayList<String> lineas = Generico.crearLista();

        for (Pedido pedido : pedidos)
            Generico.agregarVarios(lineas, pedido.toLinea());

        try {
            Archivo.escribirLineas(archivo, lineas);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private int siguienteId() {
        int max = 0;
        for (Pedido pedido : pedidos)
            max = Math.max(max, pedido.getId());
        return max + 1;
    }

    public Pedido nuevoPedido(String cliente) {
        if (cliente == null || cliente.trim().isEmpty())
            cliente = "Sin nombre";

        return new Pedido(siguienteId(),
                LocalDateTime.now().withNano(0), cliente);
    }

    public void agregarProducto(Pedido pedido, String codigo, int cantidad) {
        Producto producto = catalogo.buscar(codigo);

        if (producto == null)
            throw new IllegalArgumentException("Producto no encontrado.");

        if (cantidad <= 0)
            throw new IllegalArgumentException("Cantidad inválida.");

        if (pedido.cantidadDe(codigo) + (long)cantidad > producto.getStock())
            throw new IllegalArgumentException(
                    "Stock insuficiente. Disponible: " + producto.getStock());

        pedido.agregarDetalle(producto, cantidad);
    }

    public void confirmar(Pedido pedido) {
        if (pedido == null || pedido.getDetalles().isEmpty())
            throw new IllegalArgumentException("Pedido vacío.");

        for (Detalle detalle : pedido.getDetalles()) {
            Producto producto = catalogo.buscar(detalle.getCodigoProducto());

            if (producto == null || !producto.tieneStock(detalle.getCantidad()))
                throw new IllegalArgumentException(
                        "Stock insuficiente: " + detalle.getNombreProducto());
        }

        for (Detalle detalle : pedido.getDetalles())
            catalogo.buscar(detalle.getCodigoProducto())
                    .descontar(detalle.getCantidad());

        Generico.agregarVarios(pedidos, pedido);
        catalogo.guardar();
        guardar();
    }

    public ArrayList<Pedido> listar() {
        return new ArrayList<>(pedidos);
    }

    public String generarReporte(LocalDate fecha) {
        int cantidad = 0;
        double subtotal = 0;
        double iva = 0;
        double total = 0;

        Map<String, Integer> unidades = new LinkedHashMap<>();
        Map<String, Double> ingresos = new LinkedHashMap<>();

        for (Pedido pedido : pedidos) {
            if (!pedido.getFecha().toLocalDate().equals(fecha))
                continue;

            cantidad++;
            subtotal += pedido.getSubtotal();
            iva += pedido.getIVA();
            total += pedido.getTotal();

            for (Detalle detalle : pedido.getDetalles()) {
                String nombre = detalle.getCodigoProducto() + " - " +
                        detalle.getNombreProducto();

                unidades.put(nombre, unidades.getOrDefault(nombre, 0)
                        + detalle.getCantidad());

                ingresos.put(nombre, ingresos.getOrDefault(nombre, 0.0)
                        + detalle.getSubtotal());
            }
        }

        ArrayList<String> ranking = new ArrayList<>(unidades.keySet());
        ranking.sort((a, b) -> Integer.compare(
                unidades.get(b), unidades.get(a)));

        StringBuilder sb = new StringBuilder();
        sb.append("CAFETERÍA EL BUEN SABOR\n");
        sb.append("REPORTE DE VENTAS\n\n");
        sb.append("Fecha: ").append(fecha).append("\n");
        sb.append("Pedidos: ").append(cantidad).append("\n");

        sb.append(String.format(Locale.US,
                "Subtotal: $%.2f\nIVA: $%.2f\nTotal: $%.2f\n\n",
                subtotal, iva, total));

        sb.append("PRODUCTOS DE MAYOR DEMANDA\n\n");

        if (ranking.isEmpty())
            sb.append("No existen ventas en esta fecha.\n");

        int posicion = 1;

        for (String nombre : ranking) {
            sb.append(String.format(Locale.US,
                    "%d. %s | %d unidades | $%.2f\n",
                    posicion++, nombre, unidades.get(nombre),
                    ingresos.get(nombre)));
        }

        return sb.toString();
    }

    public File guardarReporte(String reporte, LocalDate fecha) {
        File archivoReporte = Archivo.archivo(
                "reporte_" + fecha + ".txt");

        try {
            Archivo.escribirLineas(archivoReporte,
                    Arrays.asList(reporte.split("\\R")));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return archivoReporte;
    }
}
