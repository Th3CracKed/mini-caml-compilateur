package arbreasml;

import java.util.List;
import visiteur.ObjVisiteurAsml;
import visiteur.VisiteurAsml;

public class CallAsml extends CallBaseAsml{

    private final String idString;
    
    public CallAsml(String idString, List<VarAsml> arguments) {
        super(arguments);
        this.idString = idString;
    }    

    public String getIdString() {
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
