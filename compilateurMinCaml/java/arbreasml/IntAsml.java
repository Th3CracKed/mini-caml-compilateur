package arbreasml;

import util.MyCompilationException;
import visiteur.ObjVisiteurAsml;
import visiteur.VisiteurAsml;

public class IntAsml extends ValeurAsml<Integer> implements VarOuIntAsml {

    public IntAsml(int valeur) {
        super(valeur);
        if(valeur < 0)
        {
            throw new MyCompilationException("Utiliser un noeud neg pour le moins unaire");
        }
    }

    public static IntAsml vrai()
    {
        return new IntAsml(1);
    }
    
    public static IntAsml faux()
    {
        return new IntAsml(0);
    }
    
    public static IntAsml nil() {
        return new IntAsml(0);
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
