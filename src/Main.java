import javax.swing.*;
import java.nio.file.*;

public class Main {

    // Carpeta donde está el .jar (o el directorio de trabajo si ejecutas desde IntelliJ)
    private static Path carpetaBase() {
        try {
            Path ruta = Paths.get(Main.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            if (ruta.toString().endsWith(".jar")) return ruta.getParent();
        } catch (Exception ignorado) { }
        return Paths.get("").toAbsolutePath();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Path base = carpetaBase();

            GestorIdiomas gi = new GestorIdiomas();
            try {
                gi.cargar(base.resolve("idiomas.txt"), base);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "idiomas.txt: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            GestorTransacciones gestor =
                    new GestorTransacciones(base.resolve("transacciones.tsv"));
            VentanaPrincipal ventana = new VentanaPrincipal(gi, gestor);
            try {
                gestor.cargar();
            } catch (RegistroException e) {
                ventana.mostrarError(e.getCodigoError());
            }
            ventana.setVisible(true);
        });
    }
}