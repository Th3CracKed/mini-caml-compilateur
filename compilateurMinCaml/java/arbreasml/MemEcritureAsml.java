package arbreasml;

import visiteur.ObjVisiteurAsml;
import visiteur.VisiteurAsml;

public class MemEcritureAsml extends MemAsml {

    private final VarAsml valeurEcrite;
    
    public MemEcritureAsml(VarAsml tableau, VarOuIntAsml indice, VarAsml valeurEcrite) {
        super(tableau, indice);
        this.valeurEcrite = valeurEcrite; 
    }   
    
    public VarAsml getValeurEcrite() {
        return valeurEcrite;
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
