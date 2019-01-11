package arbreasml;

import visiteur.ObjVisiteurAsml;
import visiteur.VisiteurAsml;

/**
 * Noeud de l'arbre ASML correspondant à la division de nombres flottants
 */
public class FDivAsml extends OperateurArithmetiqueFloatAsml {
    /**
     * Cree un noeud ASML FDiv avec comme opérandes e1 et e2
     * @param e1 le premier opérande du noeud
     * @param e2 le second opérande du noeud
     */
    public FDivAsml(VarAsml e1, VarAsml e2) {
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
