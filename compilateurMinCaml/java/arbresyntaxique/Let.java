package arbresyntaxique;

import visiteur.ObjVisitor;
import visiteur.Visitor;

public class Let extends Exp {
    private final Id id;
    private final Type t;
    private final Exp e1;
    private final Exp e2;

    public Let(Id id, Type t, Exp e1, Exp e2) {
        this.id = id;
        this.t = t;
        this.e1 = e1;
        this.e2 = e2;
    }

    @Override
    public <E> E accept(ObjVisitor<E> v) {
        return v.visit(this);
    }
    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
    
    public Id getId() {
        return id;
    }

    public Type getT() {
        return t;
    }

    public Exp getE1() {
        return e1;
    }

    public Exp getE2() {
        return e2;
    }
}