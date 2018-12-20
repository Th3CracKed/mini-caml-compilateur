package arbreasml;

import java.util.List;
import visiteur.ObjVisiteurAsml;
import visiteur.VisiteurAsml;

public class ProgrammeAsml implements NoeudAsml {
    private final FunDefConcreteAsml mainFunDef;
    private final List<FunDefAsml> funDefs;
    
    public ProgrammeAsml(FunDefConcreteAsml mainFunDef, List<FunDefAsml> funDefs)
    {
        this.mainFunDef = mainFunDef;
        this.funDefs = funDefs;
    }

    public FunDefConcreteAsml getMainFunDef() {
        return mainFunDef;
    }

    public List<FunDefAsml> getFunDefs() {
        return funDefs;
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
