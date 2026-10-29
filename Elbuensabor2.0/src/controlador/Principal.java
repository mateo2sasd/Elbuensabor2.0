package controlador;

import javax.swing.SwingUtilities;
import vista.Ventana;

public class Principal {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Controlador controlador = new Controlador();
                Ventana ventana = new Ventana(controlador);
                ventana.setVisible(true);
            } catch (Exception e) {
                javax.swing.JOptionPane.showMessageDialog(null,
                        e.getMessage(), "Error al iniciar",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
