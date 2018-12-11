package util;

public class NotYetImplementedException extends RuntimeException
{
    public NotYetImplementedException()
    {
        super("Fonctionnalité non implémentée");
    }
}
