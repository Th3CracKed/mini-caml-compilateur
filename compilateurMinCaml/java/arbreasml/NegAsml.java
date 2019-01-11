package arbreasml;

import visiteur.ObjVisiteurAsml;
import visiteur.VisiteurAsml;

/**
 * Noeud ASML correspondant au moins unaire des entiers
 */
public class NegAsml extends NegBaseAsml {
    /**
     * Créé un noeud ASML neg avec pour opérande e
     * @param e l'opérande
     */
    public NegAsml(VarAsml e) {
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
