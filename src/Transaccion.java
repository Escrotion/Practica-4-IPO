import java.time.LocalDate;

public class Transaccion {
    private final String idTransaccion;   // clave primaria
    private LocalDate fecha;
    private double importe;

    public Transaccion(String id, LocalDate fecha, double importe) {
        this.idTransaccion = id;
        this.fecha = fecha;
        this.importe = importe;
    }
    public String getIdTransaccion() { return idTransaccion; }
    public LocalDate getFecha() { return fecha; }
    public double getImporte() { return importe; }
    public void setFecha(LocalDate f) { this.fecha = f; }
    public void setImporte(double i) { this.importe = i; }
}