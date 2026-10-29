package modelo;

import java.util.ArrayList;

public class Generico {

    @SafeVarargs
    public static <T> ArrayList<T> crearLista(T... elementos) {
        ArrayList<T> lista = new ArrayList<>();
        for (T elemento : elementos)
            lista.add(elemento);
        return lista;
    }

    @SafeVarargs
    public static <T> ArrayList<T> agregarVarios(
            ArrayList<T> lista, T... elementos) {
        for (T elemento : elementos)
            lista.add(elemento);
        return lista;
    }
}
