package arbreasml;

/**
 * Classe mère des noeuds ASML correspondant aux moins unaires (celui pour les entiers et celui pour les flottants)
 */
public abstract class NegBaseAsml implements ExpAsml {
    private final VarAsml e;
    
    /**
     * Créé un noeud ASML correspondant à un moins unaire avec pour opérande e
     * @param e l'opérande du moins unaire
     */
    public NegBaseAsml(VarAsml e)
    {
        this.e = e;
    }    

    /**
     * Renvoie l'opérande du moins unaire
     * @return l'opérande du moins unaire
     */
    public VarAsml getE() {
        return e;
    }
}
