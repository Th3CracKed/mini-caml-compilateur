package arbremincaml;

/**
 * Classe mère des noeuds correspondants aux valeurs en MinCaml, c'est-à-dire les entiers, nombres flottants, booléens, nil et tuples. En réalité, seul les tuples dont les
 * composantes sont des valeurs sont réellement des valeurs, mais faire hériter Tuple de Valeur simplifie la propagation de constante pour les tuples).
 * @param <E> le type de la valeur, qui doit avoir rédéfinit sa méthode equals pour comparer des éléments de type E de la manière souhaitée
 */
public abstract class Valeur<E> extends Exp  {
    private E valeur;

    /**
     * Créé un noeud MinCaml représentant la valeur valeur
     * @param valeur la valeur du noeud
     */
    public Valeur(E valeur) {
        setValeur(valeur);
    }
    
    /**
     * Renvoie la valeur du noeud
     * @return la valeur du noeud
     */
    public E getValeur()
    {
        return valeur;
    }

    /**
     * Définit valeur comme nouvelle valeur de noeud
     * @param valeur la nouvelle valeur du noeud
     */
    protected final void setValeur(E valeur) {
        this.valeur = valeur;
    }
}
