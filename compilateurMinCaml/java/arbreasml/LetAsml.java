package arbreasml;

import visiteur.ObjVisiteurAsml;
import visiteur.VisiteurAsml;

/**
 * Noeud ASML correspondant au Let
 */
public class LetAsml implements AsmtAsml{
    private final String idString;
    private final ExpAsml e1;
    private final AsmtAsml e2;

    /**
     * Créé un noeud ASML Let déclarant une variable d'identificateur idString, affectant la valeur du noeud e1 à cette variable et avec le noeud e2 après le mot clé in du Let
     * @param idString l'identificateur de la variable déclarée
     * @param e1 le noeud à affecter à la variable déclarée
     * @param e2 le noeud apparaissant après le mot clé in du let
     */
    public LetAsml(String idString, ExpAsml e1, AsmtAsml e2) {
        this.idString = idString;
        this.e1 = e1;
        this.e2 = e2;
    }

    /**
     * Renvoie l'identificateur de la variable déclarée
     * @return l'identificateur de la variable déclarée
     */
    public String getIdString() {
        return idString;
    }
    
    /**
     * Renvoie le noeud à affecter à la variable déclarée
     * @return le noeud à affecter à la variable déclarée
     */
    public ExpAsml getE1() {
        return e1;
    }

    /**
     * Renvoie le noeud apparaissant après le mot clé in du let
     * @return le noeud apparaissant après le mot clé in du let
     */
    public AsmtAsml getE2() {
        return e2;
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
