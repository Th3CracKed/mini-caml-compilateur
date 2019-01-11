package arbreasml;

import java.util.List;
import visiteur.ObjVisiteurAsml;
import visiteur.VisiteurAsml;

/**
 * Classe correspondant au noeud ASML de l'appel de fonction classique
 */
public class CallAsml extends CallBaseAsml{

    private String idString;
    
    /**
     * Créé un noeud ASML Call pour appeler la fonction de label idString avec les arguments dans arguments
     * @param idString le label de la fonction
     * @param arguments les arguments passés à la fonction
     */
    public CallAsml(String idString, List<VarAsml> arguments) {
        super(arguments);
        setIdString(idString);
    }    

    /**
     * Renvoie le label de la fonction
     * @return le label de la fonction
     */
    public String getIdString() {
        return idString;
    }
    /**
     * Change le label de la fonction par la valeur du paramètre idString
     * @param idString la nouvelle valeur du label de la fonction
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
