package arbremincaml;

import visiteur.ObjVisitor;
import visiteur.Visitor;

/**
 * Noeud If en MinCaml
 */
public class If extends Exp {
    private final Exp e1;
    private final Exp e2;
    private final Exp e3;

    /**
     * Créé un noeud If en MinCaml avec pour condition e1 et executant e2 si e1 est vrai et e3 sinon
     * @param e1 la condition du noeud If
     * @param e2 l'instruction a exécuter si e1 est vraie
     * @param e3 l'instruction a exécuter si e1 est fausse
     */
    public If(Exp e1, Exp e2, Exp e3) {
        this.e1 = e1;
        this.e2 = e2;
        this.e3 = e3;
    }   

    /**
     * Renvoie la condition du noeud If
     * @return la condition du noeud If
     */
    public Exp getE1() {
        return e1;
    }

    /**
     * Renvoie l'instruction a exécuter si e1 est vraie
     * @return l'instruction a exécuter si e1 est vraie
     */
    public Exp getE2() {
        return e2;
    }
    
    /**
     * Renvoie l'instruction a exécuter si e1 est fausse
     * @return l'instruction a exécuter si e1 est fausse
     */
    public Exp getE3() {
        return e3;
    }

    @Override
    public <E> E accept(ObjVisitor<E> v) {
        return v.visit(this);
    }
    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}