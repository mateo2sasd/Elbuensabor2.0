package modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;

public class Pedido {
    public static final double IVA = 0.15;

    private int id;
    private LocalDateTime fecha;
    private String cliente;
    private ArrayList<Detalle> detalles;

    public Pedido(int id, LocalDateTime fecha, String cliente) {
        this.id = id;
        this.fecha = fecha;
        this.cliente = Archivo.limpiar(cliente);
        this.detalles = new ArrayList<>();
    }

    public int getId() { return id; }
    public LocalDateTime getFecha() { return fecha; }
    public String getCliente() { return cliente; }

    public ArrayList<Detalle> getDetalles() {
        return new ArrayList<>(detalles);
    }

    public int cantidadDe(String codigo) {
        for (Detalle detalle : detalles)
            if (detalle.getCodigoProducto().equalsIgnoreCase(codigo))
                return detalle.getCantidad();
        return 0;
    }

    public void agregarDetalle(Producto producto, int cantidad) {
        for (Detalle detalle : detalles) {
            if (detalle.getCodigoProducto().equals(producto.getCodigo())) {
                detalle.sumarCantidad(cantidad);
                return;
            }
        }

        detalles.add(new Detalle(producto.getCodigo(),
                producto.getNombre(), cantidad, producto.getPrecio()));
    }

    public double getSubtotal() {
        double subtotal = 0;
        for (Detalle detalle : detalles)
            subtotal += detalle.getSubtotal();
        return subtotal;
    }

    public double getIVA() {
        return Math.round(getSubtotal() * IVA * 100.0) / 100.0;
    }

    public double getTotal() {
        return getSubtotal() + getIVA();
    }

    public String toLinea() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < detalles.size(); i++) {
            if (i > 0) sb.append("|");
            sb.append(detalles.get(i).toLinea());
        }
        return id + ";" + fecha + ";" + cliente + ";" + sb;
    }

    public static Pedido desdeLinea(String linea) {
        String[] p = linea.split(";", -1);
        if (p.length != 4)
            throw new IllegalArgumentException("Pedido inválido.");

        Pedido pedido = new Pedido(Integer.parseInt(p[0]),
                LocalDateTime.parse(p[1]), p[2]);

        if (!p[3].isEmpty()) {
            for (String detalle : p[3].split("\\|"))
                pedido.detalles.add(Detalle.desdeLinea(detalle));
        }
        return pedido;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Pedido #").append(id)
          .append(" | ").append(fecha)
          .append(" | Cliente: ").append(cliente).append("\n");

        for (Detalle detalle : detalles)
            sb.append(detalle).append("\n");

        sb.append(String.format(Locale.US,
                "Subtotal: $%.2f | IVA: $%.2f | Total: $%.2f",
                getSubtotal(), getIVA(), getTotal()));
        return sb.toString();
    }
}
