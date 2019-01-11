package arbreasml;

import visiteur.ObjVisiteurAsml;
import visiteur.VisiteurAsml;

/**
 * Noeud ASML new (allocation de mémoire)
 */
public class NewAsml implements ExpAsml{
    private final VarOuIntAsml e;
    
    /**
     * Créé un noeud ASML new allouant le nombre d'octet indiqué par la variable ou l'entier e
     * @param e l'entier ou la variable contenant le nombre d'octet à allouer
     */
    public NewAsml(VarOuIntAsml e)
    {
        this.e = e;
    }    

    /**
     * Renvoie l'entier ou la variable contenant le nombre d'octet à allouer
     * @return l'entier ou la variable contenant le nombre d'octet à allouer
     */
    public VarOuIntAsml getE() {
        return e;
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
