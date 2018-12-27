package arbreasml;

import java.util.ArrayList;
import java.util.List;
import util.Constantes;
import visiteur.ObjVisiteurAsml;
import visiteur.VisiteurAsml;

public class FunDefConcreteAsml extends FunDefAsml {
    
    private final AsmtAsml asmt;
    
    public FunDefConcreteAsml(String label, AsmtAsml asmt, List<VarAsml> arguments)
    {
        super(label, arguments);
        this.asmt = asmt;
    }

    public static FunDefConcreteAsml creerMainFunDef(AsmtAsml asmt)
    {
        return new FunDefConcreteAsml(Constantes.NOM_FONCTION_MAIN_ASML, asmt, new ArrayList<>());
    }
    
    public boolean estMainFunDef()
    {
        return this.getLabel().equals(Constantes.NOM_FONCTION_MAIN_ASML);
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
