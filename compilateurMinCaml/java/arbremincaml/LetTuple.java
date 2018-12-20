package arbremincaml;


import visiteur.ObjVisitor;
import visiteur.Visitor;
import java.util.List;

public class LetTuple extends Exp {
    private final List<Id> ids;
    private final List<Type> ts;
    private final Exp e1;
    private final Exp e2;

    public LetTuple(List<Id> ids, List<Type> ts, Exp e1, Exp e2) {
        this.ids = ids;
        this.ts = ts;
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

    public List<Id> getIds() {
        return ids;
    }

    public List<Type> getTs() {
        return ts;
    }

    public Exp getE1() {
        return e1;
    }

    public Exp getE2() {
        return e2;
    }
}