import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public abstract class DialogoOperacion extends JDialog {
    protected final GestorIdiomas gi;
    protected final GestorTransacciones gestor;
    protected final JTextField txtId = new JTextField(15);
    protected final JTextField txtFecha = new JTextField(15);
    protected final JTextField txtImporte = new JTextField(15);
    private final int tituloTexto;

    protected DialogoOperacion(JFrame padre, GestorIdiomas gi, GestorTransacciones gestor,
                               int tituloTexto, boolean pedirFecha, boolean pedirImporte) {
        super(padre, gi.t(tituloTexto), true);   // modal
        this.gi = gi;
        this.gestor = gestor;
        this.tituloTexto = tituloTexto;

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        form.add(new JLabel(gi.t(Textos.ID)));
        form.add(txtId);
        if (pedirFecha) {
            form.add(new JLabel(gi.t(Textos.FECHA)));
            form.add(txtFecha);
        }
        if (pedirImporte) {
            form.add(new JLabel(gi.t(Textos.IMPORTE)));
            form.add(txtImporte);
        }

        JButton bAceptar = new JButton(gi.t(Textos.ACEPTAR));
        JButton bCancelar = new JButton(gi.t(Textos.CANCELAR));
        bAceptar.addActionListener(e -> aceptar());
        bCancelar.addActionListener(e -> dispose());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        botones.add(bAceptar);
        botones.add(bCancelar);

        add(form, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(bAceptar);   // Enter = Aceptar
        pack();
        setLocationRelativeTo(padre);
    }

    /** Realiza la operación y devuelve el mensaje que se mostrará al terminar. */
    protected abstract String ejecutar() throws RegistroException;

    private void aceptar() {
        try {
            String mensaje = ejecutar();
            Window dueno = getOwner();
            dispose();                              // la ventana se cierra al completarse
            JOptionPane.showMessageDialog(dueno, mensaje, gi.t(tituloTexto),
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (RegistroException e) {
            JOptionPane.showMessageDialog(this, gi.t(e.getCodigoError()),
                    gi.t(Textos.TITULO_ERROR), JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- lectura y validación de campos ----------
    protected String leerId() throws RegistroException {
        String id = txtId.getText().trim();
        if (id.isEmpty()) throw new RegistroException(Textos.ERR_CAMPO_VACIO);
        return id;
    }

    protected LocalDate leerFecha() throws RegistroException {
        String s = txtFecha.getText().trim();
        if (s.isEmpty()) throw new RegistroException(Textos.ERR_CAMPO_VACIO);
        try {
            return LocalDate.parse(s);               // aaaa-mm-dd
        } catch (DateTimeParseException e) {
            throw new RegistroException(Textos.ERR_FECHA);
        }
    }

    protected double leerImporte() throws RegistroException {
        String s = txtImporte.getText().trim();
        if (s.isEmpty()) throw new RegistroException(Textos.ERR_CAMPO_VACIO);
        NumberFormat nf = NumberFormat.getNumberInstance(locale());
        ParsePosition pp = new ParsePosition(0);
        Number n = nf.parse(s, pp);
        if (n == null || pp.getIndex() != s.length())   // rechaza "12abc"
            throw new RegistroException(Textos.ERR_IMPORTE);
        return n.doubleValue();
    }

    protected Locale locale() { return Locale.forLanguageTag(gi.getActual()); }
}