package arbreasml;

/**
 * Classe mère des opérateur arithmétiques pour les entiers en ASML (add et sub)
 */
public abstract class OperateurArithmetiqueIntAsml extends OperateurArithmetiqueAsml{
    private final VarOuIntAsml e2;

    /**
     * Créé un noeud ASML correspondant à un opérateur arithmétique pour les entiers avec pour opérandes e1 et e2
     * @param e1 le premier opérande de l'opérateur
     * @param e2 le second opérande de l'opérateur
     */
    public OperateurArithmetiqueIntAsml(VarAsml e1, VarOuIntAsml e2) {
        super(e1);
        this.e2 = e2;
    }

    /**
     * Renvoie le second opérande de l'opérateur
     * @return le second opérande de l'opérateur
     */
    public VarOuIntAsml getE2() {
        return e2;
    }  
}
