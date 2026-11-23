import javax.swing.JFrame;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class DialogoConsulta extends DialogoOperacion {
    public DialogoConsulta(JFrame padre, GestorIdiomas gi, GestorTransacciones g) {
        super(padre, gi, g, Textos.CONSULTA, false, false);
    }
    @Override protected String ejecutar() throws RegistroException {
        Transaccion t = gestor.consulta(leerId());
        String fecha = t.getFecha().format(
                DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale()));
        String importe = NumberFormat.getCurrencyInstance(locale()).format(t.getImporte());
        return gi.t(Textos.ID) + ": " + t.getIdTransaccion() + "\n"
                + gi.t(Textos.FECHA) + ": " + fecha + "\n"
                + gi.t(Textos.IMPORTE) + ": " + importe;
    }
}