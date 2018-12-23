package util;

public class NotYetImplementedException extends MyCompilationException
{
    public NotYetImplementedException()
    {
        super("Fonctionnalité non implémentée");
    }
}
