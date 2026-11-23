public class RegistroException extends Exception {
    private final int codigoError;   // índice de la cadena en el fichero de idiomas
    public RegistroException(int codigoError) {
        super("Error " + codigoError);
        this.codigoError = codigoError;
    }
    public int getCodigoError() { return codigoError; }
}