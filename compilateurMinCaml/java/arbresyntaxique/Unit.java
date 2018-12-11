package arbresyntaxique;

import visiteur.ObjVisitor;
import visiteur.Visitor;

public class Unit extends Exp {
    @Override
    public <E> E accept(ObjVisitor<E> v) {
        return v.visit(this);
    }
    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}