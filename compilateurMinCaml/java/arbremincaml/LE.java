package arbremincaml;

import visiteur.ObjVisitor;
import visiteur.Visitor;

/**
 * Noeud MinCaml correspondant à l'opérateur inférieur ou égal
 */
public class LE extends OperateurRelationnel { 
    /**
     * Créé un noeud MinCaml correspondant à l'opérateur inférieur ou égal appliqué à e1 et e2
     * @param e1 le premier opérande de l'opérateur
     * @param e2 le second opérande de l'opérateur
     */
    public LE(Exp e1, Exp e2) {
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