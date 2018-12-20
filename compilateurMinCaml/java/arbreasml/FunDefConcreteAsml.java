package arbreasml;

import java.util.ArrayList;
import java.util.List;
import visiteur.ObjVisiteurAsml;
import visiteur.VisiteurAsml;

public class FunDefConcreteAsml extends FunDefAsml {
    
    public static final String NOM_FONCTION_MAIN = "_";
    private final AsmtAsml asmt;
    
    public FunDefConcreteAsml(String label, AsmtAsml asmt, List<VarAsml> arguments)
    {
        super(label, arguments);
        this.asmt = asmt;
    }

    public static FunDefConcreteAsml creerMainFunDef(AsmtAsml asmt)
    {
        return new FunDefConcreteAsml(NOM_FONCTION_MAIN, asmt, new ArrayList<>());
    }
    
    public boolean estMainFunDef()
    {
        return this.getLabel().equals(NOM_FONCTION_MAIN);
    }
    
    public AsmtAsml getAsmt() {
        return asmt;
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
