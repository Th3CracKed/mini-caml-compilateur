package arbreasml;

/**
 * Classe mère des noeuds ASML If comparant des entiers
 */
public abstract class IfIntAsml extends IfAsml {
    private final VarOuIntAsml e2;

    /**
     * Créé un noeud ASML correspondant à un If comparant des entiers avec pour éléments comparés e1 et e2
     * et exécutant le branche eIf si la condition est vraie et eElse sinon
     * @param e1 le premier élément comparé dans la condition du if
     * @param e2 le second élément comparé dans la condition du if
     * @param eIf la branche a exécuter quand la condition est vraie
     * @param eElse la branche a exécuter quand la condition est fausse
     */
    public IfIntAsml(VarAsml e1, VarOuIntAsml e2, AsmtAsml eIf, AsmtAsml eElse) {
        super(e1, eIf, eElse);
        this.e2 = e2;
    }

    /**
     * Renvoie le second élément comparé dans la condition du if
     * @return le second élément comparé dans la condition du if
     */
    public VarOuIntAsml getE2() {
        return e2;
    }  
}
