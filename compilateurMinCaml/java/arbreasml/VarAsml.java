package arbreasml;

import visiteur.ObjVisiteurAsml;
import visiteur.VisiteurAsml;

public class VarAsml implements VarOuIntAsml {
    private final String idString;

    public VarAsml(String idString) {
        this.idString = idString;
    }

    public String getIdString(){
        return idString;
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
