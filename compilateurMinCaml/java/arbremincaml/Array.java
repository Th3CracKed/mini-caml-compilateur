package arbremincaml;

import visiteur.ObjVisitor;
import visiteur.Visitor;

/**
 * Noeud MinCaml correspondant à la création de tableau
 */
public class Array extends Exp {
    private final Exp e1;
    private final Exp e2;

    /**
     * Créé un noeud MinCaml correspondant à la création d'un tableau de taille e1 avec ses éléments initialisés à e2
     * @param e1 la taille du tableau à créer
     * @param e2 la valeur initiale des éléments du tableau à créer
     */
    public Array(Exp e1, Exp e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    /**
     * Renvoie la taille du tableau à créer
     * @return la taille du tableau à créer
     */
    public Exp getE1() {
        return e1;
    }

    /**
     * Renvoie la valeur initiale des éléments du tableau à créer
     * @return la valeur initiale des éléments du tableau à créers
     */
    public Exp getE2() {
        return e2;
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