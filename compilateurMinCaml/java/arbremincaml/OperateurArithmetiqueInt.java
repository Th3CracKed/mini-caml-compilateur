package arbremincaml;

/**
 * Classe mère des opérateur arithmétiques pour les entiers en MinCaml (add et sub)
 */
public abstract class OperateurArithmetiqueInt extends OperateurBinaire {
    
    /**
     * Créé un noeud MinCaml correspondant à un opérateur arithmétique pour les entiers avec pour opérandes e1 et e2
     * @param e1 le premier opérande de l'opérateur
     * @param e2 le second opérande de l'opérateur
     */
    public OperateurArithmetiqueInt(Exp e1, Exp e2) {
        super(e1, e2);
    }
}
