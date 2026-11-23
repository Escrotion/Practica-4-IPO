import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class VentanaPrincipal extends JFrame {
    private final GestorIdiomas gi;
    private final GestorTransacciones gestor;

    private final JMenu mArchivo = new JMenu();
    private final JMenu mOperaciones = new JMenu();
    private final JMenu mIdioma = new JMenu();
    private final JMenu mAyuda = new JMenu();
    private final JMenuItem iAlta = new JMenuItem();
    private final JMenuItem iBaja = new JMenuItem();
    private final JMenuItem iConsulta = new JMenuItem();
    private final JMenuItem iModificacion = new JMenuItem();
    private final JMenuItem iSalir = new JMenuItem();
    private final JMenuItem iAcercaDe = new JMenuItem();

    private final JLabel lblBandera = new JLabel();
    private final JLabel lblImagen = new JLabel();

    public VentanaPrincipal(GestorIdiomas gi, GestorTransacciones gestor) {
        this.gi = gi;
        this.gestor = gestor;

        // Menú
        JMenuBar barra = new JMenuBar();
        mArchivo.add(iSalir);
        mOperaciones.add(iAlta);
        mOperaciones.add(iBaja);
        mOperaciones.add(iConsulta);
        mOperaciones.add(iModificacion);
        mAyuda.add(iAcercaDe);
        barra.add(mArchivo);
        barra.add(mOperaciones);
        barra.add(mIdioma);
        barra.add(mAyuda);
        barra.add(Box.createHorizontalGlue());   // empuja la bandera a la derecha
        barra.add(lblBandera);
        setJMenuBar(barra);

        // Contenido: la imagen internacionalizada
        lblImagen.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblImagen, BorderLayout.CENTER);

        // Acciones (las operaciones se conectan en el paso 5)
        iSalir.addActionListener(e -> salir());
        iAlta.addActionListener(e -> new DialogoAlta(this, gi, gestor).setVisible(true));
        iBaja.addActionListener(e -> new DialogoBaja(this, gi, gestor).setVisible(true));
        iConsulta.addActionListener(e -> new DialogoConsulta(this, gi, gestor).setVisible(true));
        iModificacion.addActionListener(e -> new DialogoModificacion(this, gi, gestor).setVisible(true));
        iAcercaDe.addActionListener(e -> JOptionPane.showMessageDialog(
                this, gi.t(Textos.TITULO_APP), gi.t(Textos.ACERCA_DE),
                JOptionPane.INFORMATION_MESSAGE));

        // Cerrar con la X también guarda los datos
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { salir(); }
        });

        // Cada cambio de idioma refresca toda la interfaz
        gi.addOyente(this::refrescar);
        refrescar();

        setSize(600, 450);
        setLocationRelativeTo(null);
    }

    /** Aplica el idioma activo a todos los textos, la bandera y la imagen. */
    private void refrescar() {
        setTitle(gi.t(Textos.TITULO_APP));
        mArchivo.setText(gi.t(Textos.MENU_ARCHIVO));
        mOperaciones.setText(gi.t(Textos.MENU_OPERACIONES));
        mIdioma.setText(gi.t(Textos.MENU_IDIOMA));
        mAyuda.setText(gi.t(Textos.MENU_AYUDA));
        iAlta.setText(gi.t(Textos.ALTA));
        iBaja.setText(gi.t(Textos.BAJA));
        iConsulta.setText(gi.t(Textos.CONSULTA));
        iModificacion.setText(gi.t(Textos.MODIFICACION));
        iSalir.setText(gi.t(Textos.SALIR));
        iAcercaDe.setText(gi.t(Textos.ACERCA_DE));

        // Los botones de los JOptionPane también deben traducirse (rúbrica: 15%)
        UIManager.put("OptionPane.okButtonText", gi.t(Textos.ACEPTAR));
        UIManager.put("OptionPane.cancelButtonText", gi.t(Textos.CANCELAR));

        lblBandera.setIcon(escalar(gi.imagen(Textos.IMG_BANDERA), 32, 22));
        lblImagen.setIcon(escalar(gi.imagen(Textos.IMG_INTERNACIONAL), 400, 300));

        reconstruirMenuIdiomas();
    }

    /** Un elemento por cada idioma presente en el fichero (dinámico). */
    private void reconstruirMenuIdiomas() {
        mIdioma.removeAll();
        ButtonGroup grupo = new ButtonGroup();
        for (String codigo : gi.getCodigos()) {
            JRadioButtonMenuItem item =
                    new JRadioButtonMenuItem(gi.t(codigo, Textos.NOMBRE_IDIOMA));
            item.setSelected(codigo.equals(gi.getActual()));
            item.addActionListener(e -> gi.setIdioma(codigo));
            grupo.add(item);
            mIdioma.add(item);
        }
    }

    private Icon escalar(ImageIcon icono, int ancho, int alto) {
        if (icono == null) return null;
        Image img = icono.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    /** Ventana de error emergente, con título y botón traducidos. */
    public void mostrarError(int codigo) {
        JOptionPane.showMessageDialog(this, gi.t(codigo),
                gi.t(Textos.TITULO_ERROR), JOptionPane.ERROR_MESSAGE);
    }

    private void salir() {
        try {
            gestor.guardar();
        } catch (RegistroException e) {
            mostrarError(e.getCodigoError());
        }
        dispose();
        System.exit(0);
    }
}