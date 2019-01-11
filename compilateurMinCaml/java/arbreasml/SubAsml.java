package arbreasml;

import visiteur.ObjVisiteurAsml;
import visiteur.VisiteurAsml;

/**
 * Noeud de l'arbre ASML correspondant à la soustraction d'entier
 */
public class SubAsml extends OperateurArithmetiqueIntAsml {

    /**
     * Cree un noeud ASML Sub avec comme opérandes e1 et e2
     * @param e1 le premier opérande du noeud
     * @param e2 le second opérande du noeud
     */
    public SubAsml(VarAsml e1, VarOuIntAsml e2) {
        super(e1, e2);
    }

    @Override
    public void accept(VisiteurAsml v) {
        v.visit(this);
    }

    @Override
    public <E> E accept(ObjVisiteurAsml<E> v) {
        return v.visit(this);
    }
    
}
