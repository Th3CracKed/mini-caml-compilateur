package arbremincaml;

import visiteur.ObjVisitor;
import visiteur.Visitor;

/**
 * Noeud MinCaml correspondant à la valeur nil (représentée par () en MinCaml). Sa valeur est toujours le booléen true pour permettre à l'étape du constant folding de comparer 
 * des instances de la classe Valeur entre elles en utilisant la méthode equals (redéfinie dans java.lang.Boolean pour comparer des booléens correctement) 
 * de leur attribut valeur (ainsi () = () sera remplacé par le booléen vrai).
 */
public class Unit extends Valeur<Boolean> {
    
    /**
     * Créé un noeud MinCaml correspondant à la valeur nil
     */
    public Unit()
    {
        super(true);
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