package arbremincaml;

import visiteur.ObjVisitor;
import visiteur.Visitor;

/**
 * Noeud MinCaml correspondant au Let
 */
public class Let extends Exp {
    private final Id id;
    private final Type t;
    private final Exp e1;
    private final Exp e2;

    /**
     * Créé un noeud MinCaml Let déclarant une variable d'identificateur id et de type t, affectant la valeur du noeud e1 à cette variable et avec le noeud e2 après le mot clé in du Let
     * @param id l'identificateur de la variable déclarée
     * @param t le type de la variable déclarée
     * @param e1 le noeud à affecter à la variable déclarée
     * @param e2 le noeud apparaissant après le mot clé in du let
     */
    public Let(Id id, Type t, Exp e1, Exp e2) {
        this.id = id;
        this.t = t;
        this.e1 = e1;
        this.e2 = e2;
    }

    @Override
    public <E> E accept(ObjVisitor<E> v) {
        return v.visit(this);
    }
    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
    
    /**
     * Renvoie l'identificateur de la variable déclarée
     * @return l'identificateur de la variable déclarée
     */
    public Id getId() {
        return id;
    }

    /**
     * Renvoie le type de la variable déclarée
     * @return le type de la variable déclarée
     */
    public Type getT() {
        return t;
    }

    /**
     * Renvoie le noeud à affecter à la variable déclarée
     * @return le noeud à affecter à la variable déclarée
     */
    public Exp getE1() {
        return e1;
    }

    /**
     * Renvoie le noeud apparaissant après le mot clé in du let
     * @return le noeud apparaissant après le mot clé in du let
     */
    public Exp getE2() {
        return e2;
    }
}