package arbreasml;

import visiteur.ObjVisiteurAsml;
import visiteur.VisiteurAsml;

/**
 * Noeud de l'arbre ASML correspondant au moins unaire pour des nombres flottants
 */
public class FNegAsml extends NegBaseAsml {
    
    /**
     * Créé un noeud ASML FNeg avec pour opérande e
     * @param e l'opérande e
     */
    public FNegAsml(VarAsml e)
    {
        super(e);
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
