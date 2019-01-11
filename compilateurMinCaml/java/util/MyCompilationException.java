package util;

/**
 * Classe des exceptions spécifique à ce projet
 */
public class MyCompilationException extends RuntimeException {
    /**
     * Créé une exception spécifique à ce projet avec le message message
     * @param message le message de l'exception
     */
    public MyCompilationException(String message)
    {
        super(message);
    }
}
