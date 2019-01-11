package arbremincaml;

/**
 * Classe mère des opérateurs unaires en MinCaml (not, neg et fneg)
 */
public abstract class OperateurUnaire extends Exp {
    private final Exp e;

    /**
     * Créé un noeud MinCaml correspondant à un opérateur unaire avec pour opérande e1
     * @param e1 l'opérande de l'opérateur
     */
    OperateurUnaire(Exp e) {
        this.e = e;
    }
    
    /**
     * Renvoie l'opérande de l'opérateur
     * @return l'opérande de l'opérateur
     */
    public Exp getE()
    {
        return e;
    }
}
