import javax.swing.JFrame;

public class DialogoAlta extends DialogoOperacion {
    public DialogoAlta(JFrame padre, GestorIdiomas gi, GestorTransacciones g) {
        super(padre, gi, g, Textos.ALTA, true, true);
    }
    @Override protected String ejecutar() throws RegistroException {
        gestor.alta(new Transaccion(leerId(), leerFecha(), leerImporte()));
        return gi.t(Textos.EXITO);
    }
}