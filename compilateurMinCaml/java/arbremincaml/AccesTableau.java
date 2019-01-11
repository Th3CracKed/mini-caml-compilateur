package arbremincaml;

/**
 * Classe mère des noeuds MinCaml correspondant aux accés à un tableau en lecture ou en écriture
 */
public abstract class AccesTableau extends Exp
{
    private final Exp e1;
    private final Exp e2;

    /**
     * Créé un noeud MinCaml correspondant à un accés au tableau e1 à l'indice e2
     * @param e1 le tableau auquel on accède
     * @param e2 l'indice de l'élément du tableau auquel on accède
     */
    public AccesTableau(Exp e1, Exp e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    /**
     * Renvoie le tableau auquel on accède
     * @return le tableau auquel on accède
     */
    public Exp getE1() {
        return e1;
    }

    /**
     * Renvoie l'indice de l'élément du tableau auquel on accède
     * @return l'indice de l'élément du tableau auquel on accède
     */
    public Exp getE2() {
        return e2;
    }
}
