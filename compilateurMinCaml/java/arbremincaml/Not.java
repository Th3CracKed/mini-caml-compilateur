package arbremincaml;

import visiteur.ObjVisitor;
import visiteur.Visitor;

/**
 * Noeud MinCaml correspondant à l'opérateur booléen not
 */
public class Not extends OperateurUnaire {
    /**
     * Créé un noeud MinCaml correspondant à l'opérateur booléen not appliqué à e
     * @param e l'opérande de l'opérateur
     */
    public Not(Exp e) {
        super(e);
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