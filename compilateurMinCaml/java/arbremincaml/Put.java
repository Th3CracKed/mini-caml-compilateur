package arbremincaml;

import visiteur.ObjVisitor;
import visiteur.Visitor;

/**
 * Noeud MinCaml correspondant aux accés à un tableau en écriture
 */
public class Put extends AccesTableau {
    private final Exp e3;

    /**
     * Créé un noeud MinCaml correspondant à une écriture de la valeur de e3 à l'indice e2 du tableau e1
     * @param e1 le tableau auquel on accède
     * @param e2 l'indice de l'élément du tableau auquel on accède
     * @param e3 la valeur écrite dans le tableau
     */
    public Put(Exp e1, Exp e2, Exp e3) {
        super(e1,e2);
        this.e3 = e3;
    }   
    
    /**
     * Renvoie la valeur écrite dans le tableau
     * @return la valeur écrite dans le tableau
     */
    public Exp getE3() {
        return e3;
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