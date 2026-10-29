package vista;
import controlador.Controlador;
import modelo.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.Locale;

public class Ventana extends JFrame {
    private Controlador controlador;
    private JPanel contenido;
    private JTable tablaProductos, tablaItems;
    private DefaultTableModel modeloProductos, modeloItems;
    private JComboBox<Producto> comboProductos;
    private JTextField txtCliente, txtCantidad;
    private JLabel lblSubtotal, lblIVA, lblTotal;
    private JTextArea areaReporte;
    private Pedido pedidoActual;

    private final Color VERDE = new Color(215, 233, 213);
    private final Color FONDO = new Color(240, 240, 240);

    public Ventana(Controlador controlador) {
        this.controlador = controlador;

        setTitle("Cafetería El Buen Sabor");
        setSize(780, 420);
        setMinimumSize(new Dimension(760, 400));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel superior = new JPanel(new BorderLayout());
        superior.setBackground(VERDE);
        superior.setPreferredSize(new Dimension(780, 33));

        JLabel titulo = new JLabel("   Cafetería El Buen Sabor");
        titulo.setFont(new Font("Arial", Font.BOLD, 12));
        superior.add(titulo, BorderLayout.WEST);
        add(superior, BorderLayout.NORTH);

        JPanel lateral = new JPanel(new BorderLayout());
        lateral.setBackground(VERDE);
        lateral.setPreferredSize(new Dimension(150, 0));
        lateral.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel menu = new JPanel(new GridLayout(3, 1, 0, 9));
        menu.setOpaque(false);

        JButton btnProductos = new JButton("Productos");
        JButton btnPedidos = new JButton("Pedidos");
        JButton btnReportes = new JButton("Reportes");

        menu.add(btnProductos);
        menu.add(btnPedidos);
        menu.add(btnReportes);
        lateral.add(menu, BorderLayout.NORTH);

        JButton salir = new JButton("Salir");
        salir.addActionListener(e -> System.exit(0));
        lateral.add(salir, BorderLayout.SOUTH);
        add(lateral, BorderLayout.WEST);

        contenido = new JPanel(new BorderLayout());
        contenido.setBackground(FONDO);
        contenido.setBorder(BorderFactory.createEmptyBorder(18, 20, 15, 18));
        add(contenido, BorderLayout.CENTER);

        btnProductos.addActionListener(e -> mostrarProductos());
        btnPedidos.addActionListener(e -> mostrarPedidos());
        btnReportes.addActionListener(e -> mostrarReportes());

        mostrarProductos();
    }

    private void limpiarContenido(String titulo) {
        contenido.removeAll();
        contenido.setBackground(FONDO);

        JLabel encabezado = new JLabel(titulo);
        encabezado.setFont(new Font("Arial", Font.BOLD, 17));
        contenido.add(encabezado, BorderLayout.NORTH);

        contenido.revalidate();
        contenido.repaint();
    }

    private DefaultTableModel crearModelo(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    private void mostrarProductos() {
        limpiarContenido("Gestión de productos,");

        modeloProductos = crearModelo(
                "Código", "Nombre", "Precio", "Stock");

        tablaProductos = new JTable(modeloProductos);
        tablaProductos.setRowHeight(40);
        tablaProductos.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        tablaProductos.getTableHeader().setPreferredSize(
                new Dimension(0, 43));

        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);
        centro.setBorder(BorderFactory.createEmptyBorder(32, 0, 25, 15));
        centro.add(new JScrollPane(tablaProductos), BorderLayout.CENTER);
        contenido.add(centro, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        botones.setOpaque(false);

        JButton nuevo = new JButton("Nuevo");
        JButton editar = new JButton("Editar");
        JButton reponer = new JButton("Reponer stock");

        nuevo.addActionListener(e -> formularioProducto(null));

        editar.addActionListener(e -> {
            Producto producto = productoSeleccionado();
            if (producto != null) formularioProducto(producto);
        });

        reponer.addActionListener(e -> reponerStock());

        botones.add(nuevo);
        botones.add(editar);
        botones.add(reponer);
        contenido.add(botones, BorderLayout.SOUTH);

        cargarProductos();
        contenido.revalidate();
        contenido.repaint();
    }

    private void cargarProductos() {
        modeloProductos.setRowCount(0);

        for (Producto p : controlador.listarProductos()) {
            modeloProductos.addRow(new Object[]{
                p.getCodigo(),
                p.getNombre(),
                String.format(Locale.US, "$%.2f", p.getPrecio()),
                p.getStock()
            });
        }
    }

    private Producto productoSeleccionado() {
        int fila = tablaProductos.getSelectedRow();

        if (fila < 0) {
            error("Seleccione un producto de la tabla.");
            return null;
        }

        String codigo = modeloProductos.getValueAt(
                tablaProductos.convertRowIndexToModel(fila), 0).toString();

        for (Producto p : controlador.listarProductos()) {
            if (p.getCodigo().equals(codigo))
                return p;
        }

        return null;
    }

    private void formularioProducto(Producto producto) {
        boolean editar = producto != null;

        JTextField codigo = new JTextField(
                editar ? producto.getCodigo() : "");

        JTextField nombre = new JTextField(
                editar ? producto.getNombre() : "");

        JTextField precio = new JTextField(
                editar ? String.valueOf(producto.getPrecio()) : "");

        JTextField stock = new JTextField(
                editar ? String.valueOf(producto.getStock()) : "0");

        codigo.setEditable(!editar);
        stock.setEditable(!editar);

        JPanel panel = new JPanel(new GridLayout(4, 2, 8, 8));

        panel.add(new JLabel("Código:"));
        panel.add(codigo);
        panel.add(new JLabel("Nombre:"));
        panel.add(nombre);
        panel.add(new JLabel("Precio:"));
        panel.add(precio);
        panel.add(new JLabel("Stock:"));
        panel.add(stock);

        int opcion = JOptionPane.showConfirmDialog(
                this, panel,
                editar ? "Editar producto" : "Nuevo producto",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (opcion != JOptionPane.OK_OPTION) return;

        try {
            double valor = Double.parseDouble(
                    precio.getText().trim().replace(",", "."));

            if (editar) {
                controlador.editarProducto(
                        codigo.getText(), nombre.getText(), valor);
            } else {
                controlador.registrarProducto(
                        codigo.getText(), nombre.getText(), valor,
                        Integer.parseInt(stock.getText().trim()));
            }

            cargarProductos();
            mensaje("Producto guardado correctamente.");

        } catch (NumberFormatException ex) {
            error("Precio o stock inválido.");
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void reponerStock() {
        Producto producto = productoSeleccionado();
        if (producto == null) return;

        JTextField cantidad = new JTextField();

        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 5));
        panel.add(new JLabel("Cantidad a reponer:"));
        panel.add(cantidad);

        int opcion = JOptionPane.showConfirmDialog(
                this, panel,
                "Reponer stock - " + producto.getNombre(),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (opcion != JOptionPane.OK_OPTION) return;

        try {
            controlador.reponerStock(
                    producto.getCodigo(),
                    Integer.parseInt(cantidad.getText().trim()));

            cargarProductos();
            mensaje("Stock actualizado.");

        } catch (NumberFormatException ex) {
            error("Ingrese un número entero.");
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void mostrarPedidos() {
        limpiarContenido("Gestión de pedidos,");

        JPanel formulario = new JPanel(new BorderLayout(5, 5));
        formulario.setOpaque(false);

        JPanel campos = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 8));
        campos.setOpaque(false);

        txtCliente = new JTextField(12);
        comboProductos = new JComboBox<>();
        comboProductos.setPreferredSize(new Dimension(160, 28));
        txtCantidad = new JTextField("1", 4);

        for (Producto p : controlador.listarProductos())
            comboProductos.addItem(p);

        campos.add(new JLabel("Cliente:"));
        campos.add(txtCliente);
        campos.add(new JLabel("Producto:"));
        campos.add(comboProductos);
        campos.add(new JLabel("Cantidad:"));
        campos.add(txtCantidad);

        JPanel acciones = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 0));
        acciones.setOpaque(false);

        JButton agregar = new JButton("Agregar producto");
        agregar.addActionListener(e -> agregarAlPedido());
        acciones.add(agregar);

        formulario.add(campos, BorderLayout.NORTH);
        formulario.add(acciones, BorderLayout.SOUTH);

        modeloItems = crearModelo(
                "Código", "Producto", "Cantidad", "Precio", "Subtotal");

        tablaItems = new JTable(modeloItems);
        tablaItems.setRowHeight(35);

        JPanel centro = new JPanel(new BorderLayout(5, 10));
        centro.setOpaque(false);
        centro.add(formulario, BorderLayout.NORTH);
        centro.add(new JScrollPane(tablaItems), BorderLayout.CENTER);
        contenido.add(centro, BorderLayout.CENTER);

        JPanel inferior = new JPanel(new BorderLayout());
        inferior.setOpaque(false);

        JPanel totales = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 15, 5));
        totales.setOpaque(false);

        lblSubtotal = new JLabel("Subtotal: $0.00");
        lblIVA = new JLabel("IVA: $0.00");
        lblTotal = new JLabel("TOTAL: $0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 14));

        totales.add(lblSubtotal);
        totales.add(lblIVA);
        totales.add(lblTotal);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.setOpaque(false);

        JButton confirmar = new JButton("Confirmar pedido");
        JButton cancelar = new JButton("Cancelar pedido");

        confirmar.addActionListener(e -> confirmarPedido());
        cancelar.addActionListener(e -> cancelarPedido());

        botones.add(cancelar);
        botones.add(confirmar);

        inferior.add(totales, BorderLayout.NORTH);
        inferior.add(botones, BorderLayout.SOUTH);
        contenido.add(inferior, BorderLayout.SOUTH);

        if (pedidoActual != null) {
            txtCliente.setText(pedidoActual.getCliente());
            txtCliente.setEditable(false);
        }

        actualizarPedido();
        contenido.revalidate();
        contenido.repaint();
    }

    private void agregarAlPedido() {
        try {
            Producto producto =
                    (Producto) comboProductos.getSelectedItem();

            if (producto == null) {
                error("Seleccione un producto.");
                return;
            }

            int cantidad = Integer.parseInt(
                    txtCantidad.getText().trim());

            if (cantidad <= 0) {
                error("La cantidad debe ser mayor a cero.");
                return;
            }

            if (pedidoActual == null) {
                pedidoActual = controlador.nuevoPedido(
                        txtCliente.getText());
            }

            controlador.agregarProducto(
                    pedidoActual, producto.getCodigo(), cantidad);

            txtCliente.setEditable(false);
            actualizarPedido();
            txtCantidad.setText("1");

        } catch (NumberFormatException ex) {
            error("Ingrese una cantidad válida.");
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void actualizarPedido() {
        modeloItems.setRowCount(0);

        if (pedidoActual == null) {
            lblSubtotal.setText("Subtotal: $0.00");
            lblIVA.setText("IVA: $0.00");
            lblTotal.setText("TOTAL: $0.00");
            return;
        }

        for (Detalle detalle : pedidoActual.getDetalles()) {
            modeloItems.addRow(new Object[]{
                detalle.getCodigoProducto(),
                detalle.getNombreProducto(),
                detalle.getCantidad(),
                String.format(Locale.US, "$%.2f",
                        detalle.getPrecioUnitario()),
                String.format(Locale.US, "$%.2f",
                        detalle.getSubtotal())
            });
        }

        lblSubtotal.setText(String.format(Locale.US,
                "Subtotal: $%.2f", pedidoActual.getSubtotal()));

        lblIVA.setText(String.format(Locale.US,
                "IVA: $%.2f", pedidoActual.getIVA()));

        lblTotal.setText(String.format(Locale.US,
                "TOTAL: $%.2f", pedidoActual.getTotal()));
    }

    private void confirmarPedido() {
        if (pedidoActual == null ||
                pedidoActual.getDetalles().isEmpty()) {
            error("Agregue productos al pedido.");
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(
                this,
                String.format(Locale.US,
                        "¿Confirmar pedido por $%.2f?",
                        pedidoActual.getTotal()),
                "Confirmar pedido",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (opcion != JOptionPane.YES_OPTION) return;

        try {
            controlador.confirmarPedido(pedidoActual);

            mensaje("Pedido #" + pedidoActual.getId() +
                    " registrado correctamente.");

            pedidoActual = null;
            mostrarPedidos();

        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void cancelarPedido() {
        pedidoActual = null;
        mostrarPedidos();
    }

    private void mostrarReportes() {
        limpiarContenido("Reportes de ventas,");

        areaReporte = new JTextArea();
        areaReporte.setEditable(false);
        areaReporte.setFont(
                new Font("Monospaced", Font.PLAIN, 13));
        areaReporte.setMargin(new Insets(12, 12, 12, 12));

        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);
        centro.setBorder(
                BorderFactory.createEmptyBorder(25, 0, 15, 0));
        centro.add(new JScrollPane(areaReporte), BorderLayout.CENTER);
        contenido.add(centro, BorderLayout.CENTER);

        JPanel botones = new JPanel(
                new FlowLayout(FlowLayout.LEFT));
        botones.setOpaque(false);

        JButton generar = new JButton("Ventas del día");
        JButton historial = new JButton("Historial de pedidos");
        JButton guardar = new JButton("Guardar reporte");

        generar.addActionListener(e -> {
            areaReporte.setText(
                    controlador.generarReporte(LocalDate.now()));
        });

        historial.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();

            for (Pedido p : controlador.listarPedidos())
                sb.append(p).append("\n\n");

            areaReporte.setText(sb.length() == 0
                    ? "No hay pedidos registrados."
                    : sb.toString());
        });

        guardar.addActionListener(e -> {
            try {
                LocalDate hoy = LocalDate.now();
                String reporte = controlador.generarReporte(hoy);

                java.io.File archivo =
                        controlador.guardarReporte(reporte, hoy);

                areaReporte.setText(reporte);
                mensaje("Reporte guardado en: " + archivo.getPath());

            } catch (Exception ex) {
                error(ex.getMessage());
            }
        });

        botones.add(generar);
        botones.add(historial);
        botones.add(guardar);

        contenido.add(botones, BorderLayout.SOUTH);
        contenido.revalidate();
        contenido.repaint();
    }

    private void mensaje(String texto) {
        JOptionPane.showMessageDialog(
                this, texto,
                "Cafetería El Buen Sabor",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void error(String texto) {
        JOptionPane.showMessageDialog(
                this, texto,
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);
    }
}
