package arbreasml;

/**
 * Classe mère des opérateur arithmétiques pour les nombres flottants en ASML (fadd, fsub, fmul et fdiv)
 */
public abstract class OperateurArithmetiqueFloatAsml extends OperateurArithmetiqueAsml{
    private final VarAsml e2;

    /**
     * Créé un noeud ASML correspondant à un opérateur arithmétique pour les nombres flottants avec pour opérandes e1 et e2
     * @param e1 le premier opérande de l'opérateur
     * @param e2 le second opérande de l'opérateur
     */
    public OperateurArithmetiqueFloatAsml(VarAsml e1, VarAsml e2) {
        super(e1);
        this.e2 = e2;
    }

    /**
     * Renvoie le second opérande de l'opérateur
     * @return le second opérande de l'opérateur
     */
    public VarAsml getE2() {
        return e2;
    }   
}
