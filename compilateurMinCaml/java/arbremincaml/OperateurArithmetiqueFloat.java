package arbremincaml;

/**
 * Classe mère des opérateur arithmétiques pour les nombres flottants en MinCaml (fadd, fsub, fmul et fdiv)
 */
public abstract class OperateurArithmetiqueFloat extends OperateurBinaire {  
    /**
     * Créé un noeud MinCaml correspondant à un opérateur arithmétique pour les nombres flottants avec pour opérandes e1 et e2
     * @param e1 le premier opérande de l'opérateur
     * @param e2 le second opérande de l'opérateur
     */
    public OperateurArithmetiqueFloat(Exp e1, Exp e2) {
        super(e1, e2);
    }
}
