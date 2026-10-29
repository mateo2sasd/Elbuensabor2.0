package modelo;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Archivo {
    private static final File CARPETA = new File("data");

    public static File archivo(String nombre) {
        if (!CARPETA.exists() && !CARPETA.mkdirs())
            throw new IllegalStateException("No se pudo crear la carpeta data.");
        return new File(CARPETA, nombre);
    }

    public static ArrayList<String> leerLineas(File archivo) throws IOException {
        ArrayList<String> lineas = new ArrayList<>();
        if (!archivo.exists()) return lineas;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(archivo),
                        StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty())
                    lineas.add(linea);
            }
        }
        return lineas;
    }

    public static void escribirLineas(File archivo, List<String> lineas)
            throws IOException {
        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(archivo),
                        StandardCharsets.UTF_8))) {
            for (String linea : lineas)
                pw.println(linea);

            if (pw.checkError())
                throw new IOException("Error al escribir " + archivo);
        }
    }

    public static String limpiar(String texto) {
        if (texto == null) return "";
        return texto.replace(";", " ").replace("|", " ")
                .replace("~", " ").replace("\n", " ").trim();
    }
}
