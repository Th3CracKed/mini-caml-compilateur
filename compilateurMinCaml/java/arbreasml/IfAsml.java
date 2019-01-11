package arbreasml;

/**
 * Classe mère des noeuds ASML If
 */
public abstract class IfAsml implements ExpAsml {
    private final AsmtAsml eSiVrai;
    private final AsmtAsml eSiFaux;
    private final VarAsml e1;
    
    /**
     * Créé un noeud ASML correspondant à un If avec pour premier élément comparé e1 et exécutant le branche eSiVrai si la condition est vraie et eSiFaux sinon
     * @param e1 le premier élément comparé dans la condition du if
     * @param eSiVrai la branche a exécuter quand la condition est vraie
     * @param eSiFaux la branche a exécuter quand la condition est fausse
     */
    public IfAsml(VarAsml e1, AsmtAsml eSiVrai, AsmtAsml eSiFaux)
    {
        this.e1 = e1;
        this.eSiVrai = eSiVrai;
        this.eSiFaux = eSiFaux;
    }

    /**
     * Renvoie la branche exécutée si la condition est vraie
     * @return la branche exécutée si la condition est vraie
     */
    public AsmtAsml getESiVrai() {
        return eSiVrai;
    }

    /**
     * Renvoie la branche exécutée si la condition est fausse
     * @return la branche exécutée si la condition est fausse
     */
    public AsmtAsml getESiFaux() {
        return eSiFaux;
    }
    
    /**
     * Renvoie le premier élément comparé dans la condition
     * @return le premier élément comparé dans la condition
     */
    public VarAsml getE1() {
        return e1;
    }
}
