import javax.swing.ImageIcon;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class GestorIdiomas {
    private final Map<String, List<String>> textos = new LinkedHashMap<>();
    private final Map<String, List<String>> imagenes = new LinkedHashMap<>();
    private final List<Runnable> oyentes = new ArrayList<>();
    private String actual;
    private Path base = Paths.get("");


    public void cargar(Path fichero, Path base) throws IOException {
        this.base = base;
        cargar(fichero);
    }

    /** Lee el fichero de idiomas (externo al jar). */
    public void cargar(Path fichero) throws IOException {
        List<String> lineas = new ArrayList<>();
        for (String l : Files.readAllLines(fichero, StandardCharsets.UTF_8)) {
            if (!l.isBlank()) lineas.add(l.trim());
        }
        try {
            int pos = 0;
            int numIdiomas = Integer.parseInt(lineas.get(pos++));
            for (int i = 0; i < numIdiomas; i++) {
                String codigo = lineas.get(pos++);
                int numCadenas = Integer.parseInt(lineas.get(pos++));
                List<String> cadenas = new ArrayList<>();
                for (int j = 0; j < numCadenas; j++) cadenas.add(lineas.get(pos++));
                int numImg = Integer.parseInt(lineas.get(pos++));
                List<String> rutas = new ArrayList<>();
                for (int j = 0; j < numImg; j++) rutas.add(lineas.get(pos++));
                textos.put(codigo, cadenas);
                imagenes.put(codigo, rutas);
            }
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            throw new IOException("Formato de fichero de idiomas incorrecto", e);
        }
        if (textos.isEmpty()) throw new IOException("No hay idiomas");
        actual = textos.keySet().iterator().next();   // por defecto, el primero
    }

    public Set<String> getCodigos() { return Collections.unmodifiableSet(textos.keySet()); }
    public String getActual() { return actual; }

    public void setIdioma(String codigo) {
        if (!textos.containsKey(codigo)) return;
        actual = codigo;
        oyentes.forEach(Runnable::run);   // avisa a la interfaz para refrescarse
    }

    /** Texto del idioma activo; nunca lanza excepción si falta una cadena. */
    public String t(int indice) { return t(actual, indice); }

    public String t(String codigo, int indice) {
        List<String> l = textos.get(codigo);
        if (l == null || indice < 0 || indice >= l.size()) return "#" + indice;
        return l.get(indice);
    }

    /** Imagen del idioma activo, o null si no existe el archivo. */
    public ImageIcon imagen(int indice) {
        List<String> l = imagenes.get(actual);
        if (l == null || indice >= l.size()) return null;
        Path p = base.resolve(l.get(indice)).normalize();
        return Files.exists(p) ? new ImageIcon(p.toString()) : null;
    }

    public void addOyente(Runnable r) { oyentes.add(r); }
}