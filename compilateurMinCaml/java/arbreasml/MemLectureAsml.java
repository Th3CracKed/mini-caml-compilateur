package arbreasml;

import visiteur.ObjVisiteurAsml;
import visiteur.VisiteurAsml;

public class MemLectureAsml extends MemAsml {
    
    public MemLectureAsml(VarAsml tableau, VarOuIntAsml indice) {
        super(tableau, indice);
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
