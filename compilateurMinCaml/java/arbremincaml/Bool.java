package arbremincaml;

import visiteur.ObjVisitor;
import visiteur.Visitor;

/**
 * Noeuds MinCaml correspondants aux valeurs booléennes
 */
public class Bool extends Valeur<Boolean> {
    
    /** Créé un noeud MinCaml correspondant à la valeur booléenne b
     * @param b la valeur booléenne du noeud
     */
    public Bool(boolean b) {
        super(b);
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
