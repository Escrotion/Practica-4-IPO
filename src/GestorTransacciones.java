import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class GestorTransacciones {

    // Códigos de error (luego serán posiciones en el fichero de idiomas)
    public static final int ERR_ID_DUPLICADO = Textos.ERR_ID_DUPLICADO;
    public static final int ERR_NO_EXISTE    = Textos.ERR_NO_EXISTE;
    public static final int ERR_CAMPO_VACIO  = Textos.ERR_CAMPO_VACIO;
    public static final int ERR_FECHA        = Textos.ERR_FECHA;
    public static final int ERR_IMPORTE      = Textos.ERR_IMPORTE;
    public static final int ERR_FICHERO      = Textos.ERR_FICHERO;

    private final Map<String, Transaccion> datos = new LinkedHashMap<>();
    private final Path fichero;

    public GestorTransacciones(Path fichero) { this.fichero = fichero; }

    // ---------- CRUD ----------
    public void alta(Transaccion t) throws RegistroException {
        if (t.getIdTransaccion().isBlank()) throw new RegistroException(ERR_CAMPO_VACIO);
        if (datos.containsKey(t.getIdTransaccion())) throw new RegistroException(ERR_ID_DUPLICADO);
        datos.put(t.getIdTransaccion(), t);
    }

    public void baja(String id) throws RegistroException {
        if (datos.remove(id) == null) throw new RegistroException(ERR_NO_EXISTE);
    }

    public Transaccion consulta(String id) throws RegistroException {
        Transaccion t = datos.get(id);
        if (t == null) throw new RegistroException(ERR_NO_EXISTE);
        return t;
    }

    public void modificacion(String id, LocalDate fecha, double importe) throws RegistroException {
        consulta(id).setFecha(fecha);
        datos.get(id).setImporte(importe);
    }

    public Collection<Transaccion> todas() { return Collections.unmodifiableCollection(datos.values()); }

    // ---------- Persistencia TSV ----------
    public void cargar() throws RegistroException {
        if (!Files.exists(fichero)) return;   // primera ejecución: sin datos
        try (BufferedReader br = Files.newBufferedReader(fichero, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.isBlank()) continue;
                String[] c = linea.split("\t");
                datos.put(c[0], new Transaccion(c[0], LocalDate.parse(c[1]), Double.parseDouble(c[2])));
            }
        } catch (IOException | DateTimeParseException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new RegistroException(ERR_FICHERO);
        }
    }

    public void guardar() throws RegistroException {
        try (BufferedWriter bw = Files.newBufferedWriter(fichero, StandardCharsets.UTF_8)) {
            for (Transaccion t : datos.values()) {
                // Formato independiente del idioma: fecha ISO y punto decimal
                bw.write(t.getIdTransaccion() + "\t" + t.getFecha() + "\t" + t.getImporte());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RegistroException(ERR_FICHERO);
        }
    }
}