package util;

public class NotYetImplementedException extends CompilationException
{
    public NotYetImplementedException()
    {
        super("Fonctionnalité non implémentée");
    }
}
