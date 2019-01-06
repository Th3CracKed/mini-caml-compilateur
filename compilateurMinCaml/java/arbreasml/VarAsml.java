package arbreasml;

import visiteur.ObjVisiteurAsml;
import visiteur.VisiteurAsml;

public class VarAsml implements VarOuIntAsml {
    private String idString;

    public VarAsml(String idString) {
        setIdString(idString);
    }

    public String getIdString(){
        return idString;
    }    

    public final void setIdString(String idString) {
        this.idString = idString;
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
