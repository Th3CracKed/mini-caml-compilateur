package arbremincaml;

import visiteur.ObjVisitor;
import visiteur.Visitor;

/**
 * Noeud MinCaml représentant une variable
 */
public class Var extends Exp {
    private final Id id;

    /**
     * Créé un noeud MinCaml représentant une variable d'identifiant id
     * @param id l'identifiant de la variable
     */
    public Var(Id id) {
        this.id = id;
    }

    /**
     * Renvoie l'identifiant de la variable
     * @return l'identifiant de la variable
     */
    public Id getId(){
        return id;
    }
        
    @Override
    public <E> E accept(ObjVisitor<E> v) {
        return v.visit(this);
    }
    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}