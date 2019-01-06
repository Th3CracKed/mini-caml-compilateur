package arbreasml;

import visiteur.ObjVisiteurAsml;
import visiteur.VisiteurAsml;

public class FNegAsml extends NegBaseAsml {
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
