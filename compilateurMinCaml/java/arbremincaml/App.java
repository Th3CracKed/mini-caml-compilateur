package arbremincaml;


import visiteur.ObjVisitor;
import visiteur.Visitor;
import java.util.List;

public class App extends Exp {
    private final Exp e;
    private final List<Exp> es;

    public App(Exp e, List<Exp> es) {
        this.e = e;
        this.es = es;
    }

    @Override
    public <E> E accept(ObjVisitor<E> v) {
        return v.visit(this);
    }
    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    public Exp getE() {
        return e;
    }

    public List<Exp> getEs() {
        return es;
    }
}