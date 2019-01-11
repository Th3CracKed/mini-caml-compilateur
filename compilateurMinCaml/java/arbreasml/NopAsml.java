package arbreasml;

import visiteur.ObjVisiteurAsml;
import visiteur.VisiteurAsml;

/**
 * Noeud ASML Nop (instruction n'ayant aucun effet). Les noeuds nop sont une expression, ils ont donc une valeur : pour cette raison nop hérite de ValeurAsml passe 
 * à son constructeur la valeur null afin que la valeur de tous les nop soient la même
 */
public class NopAsml extends ValeurAsml<Object> {
    /**
     * Créé un noeud ASML Nop
     */
    public NopAsml()
    {
        super(null);
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
