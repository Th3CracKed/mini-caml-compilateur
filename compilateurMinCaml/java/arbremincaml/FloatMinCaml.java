package arbremincaml;

import visiteur.ObjVisitor;
import visiteur.Visitor;

public class FloatMinCaml extends Valeur<Float> {
    public FloatMinCaml(java.lang.Float f) {
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