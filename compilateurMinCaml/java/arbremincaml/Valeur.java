package arbremincaml;

public abstract class Valeur<E> extends Exp  {
    private final E valeur;

    public Valeur(E valeur) {
        this.valeur = valeur;
    }
    
    public E getValeur()
    {
        return valeur;
    }
}
