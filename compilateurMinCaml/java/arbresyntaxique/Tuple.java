package arbresyntaxique;


import visiteur.ObjVisitor;
import visiteur.Visitor;
import java.util.List;

public class Tuple extends Exp {
    private final List<Exp> es;

    public Tuple(List<Exp> es) {
        this.es = es;
    }

    public List<Exp> getEs()
    {
        return es;
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