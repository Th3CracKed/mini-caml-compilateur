package arbresyntaxique;

import visiteur.ObjVisitor;
import visiteur.Visitor;

public class Float extends Valeur<java.lang.Float> {
    public Float(java.lang.Float f) {
        super(f);
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