package arbremincaml;

import visiteur.ObjVisitor;
import visiteur.Visitor;

/**
 * Noeud de l'arbre MinCaml correspondant à la soustraction d'entiers
 */
public class Sub extends OperateurArithmetiqueInt {
    /**
     * Créé un noeud MinCaml correspondant à la soustraction d'entiers avec pour opérandes e1 et e2
     * @param e1 le premier opérande du noeud
     * @param e2 le second opérande du noeud
     */
    public Sub(Exp e1, Exp e2) {
        super(e1,e2);
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