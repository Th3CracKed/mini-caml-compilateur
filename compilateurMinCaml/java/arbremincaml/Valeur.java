package arbremincaml;

public abstract class Valeur<E> extends Exp  {
    private E valeur;

    public Valeur(E valeur) {
        setValeur(valeur);
    }
    
    public E getValeur()
    {
        return valeur;
    }

    protected final void setValeur(E valeur) {
        this.valeur = valeur;
    }
}
