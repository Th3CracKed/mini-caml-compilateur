package arbreasml;

import visiteur.ObjVisiteurAsml;
import visiteur.VisiteurAsml;

/**
 * Noeud ASML correspondant aux variables
 */
public class VarAsml implements VarOuIntAsml {
    private String idString;

    /**
     * Créé un noeud ASML Var avec pour identificateur idString
     * @param idString l'identificateur de la variable
     */
    public VarAsml(String idString) {
        setIdString(idString);
    }

    /**
     * Renvoie l'identificateur de la variable
     * @return l'identificateur de la variable
     */
    public String getIdString(){
        return idString;
    }    

    /**
     * Change l'identificateur de la variable par idString
     * @param idString le nouvel identificateur de la variable
     */
    public final void setIdString(String idString) {
        this.idString = idString;
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
