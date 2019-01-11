package arbreasml;

/**
 * Classe mère des valeurs immédiates en ASML (les entiers et Nop, qui est une expression et à donc une valeur)
 * @param <E> le type des valeurs (par exemple Integer pour les noeuds correspondant aux valeurs entières)
 */
public abstract class ValeurAsml<E> implements ExpAsml {
    private final E valeur;
    
    /**
     * Créé une valeur immédiate avec pour valeur valeur
     * @param valeur la valeur du noeud
     */
    public ValeurAsml(E valeur)
    {
        this.valeur = valeur;
    }
    
    /**
     * Renvoie la valeur du noeud
     * @return la valeur du noeud
     */
    public E getValeur()
    {
        return this.valeur;
    }
}
