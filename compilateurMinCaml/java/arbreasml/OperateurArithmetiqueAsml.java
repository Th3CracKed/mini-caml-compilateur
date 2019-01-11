package arbreasml;

/**
 * Classe mère des opérateur arithmétiques en ASML (add, sub, fadd, fsub, fmul et fdiv)
 */
public abstract class OperateurArithmetiqueAsml implements ExpAsml{
    private final VarAsml e1;

    /**
     * Créé un noeud ASML correspondant à un opérateur arithmétique
     * @param e1 le premier opérande de l'opérateur
     */
    public OperateurArithmetiqueAsml(VarAsml e1) {
        this.e1 = e1;
    }

    /**
     * Renvoie le premier opérande de l'opérateur
     * @return le premier opérande de l'opérateur
     */
    public VarAsml getE1() {
        return e1;
    }
}
