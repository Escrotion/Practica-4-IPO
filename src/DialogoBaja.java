import javax.swing.JFrame;

public class DialogoBaja extends DialogoOperacion {
    public DialogoBaja(JFrame padre, GestorIdiomas gi, GestorTransacciones g) {
        super(padre, gi, g, Textos.BAJA, false, false);
    }
    @Override protected String ejecutar() throws RegistroException {
        gestor.baja(leerId());
        return gi.t(Textos.EXITO);
    }
}