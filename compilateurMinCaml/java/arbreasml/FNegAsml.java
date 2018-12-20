package arbreasml;

import visiteur.ObjVisiteurAsml;
import visiteur.VisiteurAsml;

public class FNegAsml implements ExpAsml {
    private final VarAsml e;
    public FNegAsml(VarAsml e)
    {
        this.e = e;
    }    

    public VarAsml getE() {
        return e;
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
