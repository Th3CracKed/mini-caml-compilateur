package arbremincaml;

import visiteur.ObjVisitor;
import visiteur.Visitor;

public class Bool extends Valeur<Boolean> {
    public Bool(Boolean b) {
        super(b);
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
