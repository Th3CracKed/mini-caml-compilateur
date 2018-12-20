package arbremincaml;

import visiteur.ObjVisitor;
import visiteur.Visitor;

public class Int extends Valeur<Integer> {
    public Int(Integer i) {
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