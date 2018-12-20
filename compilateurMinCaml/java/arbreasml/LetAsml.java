package arbreasml;

import visiteur.ObjVisiteurAsml;
import visiteur.VisiteurAsml;

public class LetAsml implements AsmtAsml{
    private final String idString;
    private final ExpAsml e1;
    private final AsmtAsml e2;

    public LetAsml(String idString, ExpAsml e1, AsmtAsml e2) {
        this.idString = idString;
        this.e1 = e1;
        this.e2 = e2;
    }

    public String getIdString() {
        return idString;
    }
    
    public ExpAsml getE1() {
        return e1;
    }

    public AsmtAsml getE2() {
        return e2;
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
