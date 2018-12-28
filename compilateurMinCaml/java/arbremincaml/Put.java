package arbremincaml;

import visiteur.ObjVisitor;
import visiteur.Visitor;

public class Put extends AccesTableau {
    private final Exp e3;

    public Put(Exp e1, Exp e2, Exp e3) {
        super(e1,e2);
        this.e3 = e3;
    }   
    
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