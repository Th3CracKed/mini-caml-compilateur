package arbremincaml;

import visiteur.ObjVisitor;
import visiteur.Visitor;

/**
 * Noeud de l'arbre MinCaml correspondant au moins unaire pour les nombres flottants
 */
public class FNeg extends OperateurUnaire {
    /**
     * Créé un noeud de l'arbre MinCaml correspondant au moins unaire pour les nombres flottants avec l'opérande e
     * @param e l'opérande du moins unaire
     */
    public FNeg(Exp e) {
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
