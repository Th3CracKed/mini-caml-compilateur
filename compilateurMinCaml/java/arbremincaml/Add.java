package arbremincaml;

import visiteur.ObjVisitor;
import visiteur.Visitor;

/**
 * Noeud de l'arbre MinCaml correspondant à l'addition d'entiers
 */
public class Add extends OperateurArithmetiqueInt {
    
    /**
     * Cree un noeud MinCaml Add avec comme opérandes e1 et e2
     * @param e1 le premier opérande du noeud
     * @param e2 le second opérande du noeud
     */
    public Add(Exp e1, Exp e2) {
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