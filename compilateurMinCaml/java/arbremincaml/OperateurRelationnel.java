package arbremincaml;

/**
 * Classe mère des opérateur relationnels en MinCaml (eq et le)
 */
public abstract class OperateurRelationnel extends OperateurBinaire {
    /**
     * Créé un noeud MinCaml correspondant à un opérateur relationnel avec pour opérandes e1 et e2
     * @param e1 le premier opérande de l'opérateur
     * @param e2 le second opérande de l'opérateur
     */
    public OperateurRelationnel(Exp e1, Exp e2) {
        super(e1, e2);
    }
    
}
