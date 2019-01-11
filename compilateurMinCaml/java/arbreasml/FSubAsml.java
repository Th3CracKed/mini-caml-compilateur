package arbreasml;

import visiteur.ObjVisiteurAsml;
import visiteur.VisiteurAsml;

/**
 * Noeud de l'arbre ASML correspondant à la soustraction de nombres flottants
 */
public class FSubAsml extends OperateurArithmetiqueFloatAsml {
    /**
     * Cree un noeud ASML FSub avec comme opérandes e1 et e2
     * @param e1 le premier opérande du noeud
     * @param e2 le second opérande du noeud
     */
    public FSubAsml(VarAsml e1, VarAsml e2) {
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
