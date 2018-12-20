package arbreasml;

import visiteur.ObjVisiteurAsml;
import visiteur.VisiteurAsml;

public class IfEquFloatAsml extends IfFloatAsml {

    public IfEquFloatAsml(VarAsml e1, VarAsml e2, AsmtAsml eIf, AsmtAsml eElse) {
        super(e1, e2, eIf, eElse);
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
