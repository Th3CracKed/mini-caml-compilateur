package arbremincaml;

import visiteur.ObjVisitor;
import visiteur.Visitor;

/**
 * Noeud MinCaml correspondant aux accés à un tableau en lecture
 */
public class Get extends AccesTableau {
    
    /**
     * Créé un noeud MinCaml correspondant à un accés en lecture au tableau e1 à l'indice e2
     * @param e1 le tableau auquel on accède
     * @param e2 l'indice de l'élément du tableau auquel on accède
     */
    public Get(Exp e1, Exp e2) {
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