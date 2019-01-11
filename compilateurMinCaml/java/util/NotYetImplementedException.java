package util;

/**
 * Exception levée lors de la tentative d'utilisation d'une fonctionnalité non implémentée de notre compilateur
 */
public class NotYetImplementedException extends MyCompilationException
{
    public NotYetImplementedException()
    {
        super("Fonctionnalité non implémentée");
    }
}
