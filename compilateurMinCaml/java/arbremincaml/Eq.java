package arbremincaml;

import visiteur.ObjVisitor;
import visiteur.Visitor;

/**
 * Noeud MinCaml correspondant à l'opérateur égal
 */
public class Eq extends OperateurRelationnel { 
    
    /**
     * Créé un noeud MinCaml correspondant à l'opérateur égal appliqué à e1 et e2
     * @param e1 le premier opérande de l'opérateur
     * @param e2 le second opérande de l'opérateur
     */
    public Eq(Exp e1, Exp e2) {
        super(e1, e2);
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