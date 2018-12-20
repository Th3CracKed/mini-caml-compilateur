package arbreasml;

import java.util.List;
import visiteur.ObjVisiteurAsml;
import visiteur.VisiteurAsml;

public class CallClosureAsml extends CallBaseAsml{

    private final VarAsml var;
    
    public CallClosureAsml(VarAsml var, List<VarAsml> arguments) {
        super(arguments);
        this.var = var;
    }    

    public VarAsml getVar() {
        return var;
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
