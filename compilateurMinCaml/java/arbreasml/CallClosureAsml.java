package arbreasml;

import java.util.List;
import visiteur.ObjVisiteurAsml;
import visiteur.VisiteurAsml;

/**
 * Classe correspondant à un appel de closure
 */
public class CallClosureAsml extends CallBaseAsml{

    private final VarAsml var;
    
    /**
     * Créé un noeud ASML CallClosure avec la closure dans var et les arguments dans arguments
     * @param var la variable contenant la closure
     * @param arguments les arguments passés à la fonction
     */
    public CallClosureAsml(VarAsml var, List<VarAsml> arguments) {
        super(arguments);
        this.var = var;
    }    

    /**
     * Renvoie la variable contenant la closure
     * @return la variable contenant la closure
     */
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
