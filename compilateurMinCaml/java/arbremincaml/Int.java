package arbremincaml;

import visiteur.ObjVisitor;
import visiteur.Visitor;

/**
 * Noeud MinCaml correspondant aux valeurs entières.
 */
public class Int extends Valeur<Integer> {
     /**
     * Créé un noeud MinCaml correspondant à la valeur entière i
     * @param i la valeur de l'entier
     */
    public Int(int i) {
        super(i);
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