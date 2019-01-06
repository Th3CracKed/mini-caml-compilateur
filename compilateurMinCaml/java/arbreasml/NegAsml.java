package arbreasml;

import visiteur.ObjVisiteurAsml;
import visiteur.VisiteurAsml;

public class NegAsml extends NegBaseAsml {
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
