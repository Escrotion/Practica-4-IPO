import javax.swing.JFrame;

public class DialogoModificacion extends DialogoOperacion {
    public DialogoModificacion(JFrame padre, GestorIdiomas gi, GestorTransacciones g) {
        super(padre, gi, g, Textos.MODIFICACION, true, true);
    }
    @Override protected String ejecutar() throws RegistroException {
        // Se valida todo antes de modificar nada
        String id = leerId();
        gestor.consulta(id);                          // lanza ERR_NO_EXISTE si no está
        gestor.modificacion(id, leerFecha(), leerImporte());
        return gi.t(Textos.EXITO);
    }
}