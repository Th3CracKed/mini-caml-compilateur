package arbresyntaxique;

import visiteur.ObjVisitor;
import visiteur.Visitor;

public class Var extends Exp {
    private final Id id;

    public Var(Id id) {
        this.id = id;
    }

    public Id getId(){
        return id;
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