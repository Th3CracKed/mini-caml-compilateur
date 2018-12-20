package arbreasml;

public abstract class ValeurAsml<E> implements ExpAsml {
    private final E valeur;
    
    public ValeurAsml(E valeur)
    {
        this.valeur = valeur;
    }
    
    public E getValeur()
    {
        return this.valeur;
    }
}
