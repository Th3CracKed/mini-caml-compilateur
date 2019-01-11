package arbremincaml;

/**
 * Classe mère des opérateur binaires en MinCaml (add, sub, fadd, fsub, fmul, fdiv, eq et le)
 */
public abstract class OperateurBinaire extends Exp {
    private final Exp e1;
    private final Exp e2;

    /**
     * Créé un noeud MinCaml correspondant à un opérateur binaire avec pour opérandes e1 et e2
     * @param e1 le premier opérande de l'opérateur
     * @param e2 le second opérande de l'opérateur
     */
    public OperateurBinaire(Exp e1, Exp e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    /**
     * Renvoie le premier opérande de l'opérateur
     * @return le premier opérande de l'opérateur
     */
    public Exp getE1() {
        return e1;
    }

    /**
     * Renvoie le second opérande de l'opérateur
     * @return le second opérande de l'opérateur
     */
    public Exp getE2() {
        return e2;
    }    
}
