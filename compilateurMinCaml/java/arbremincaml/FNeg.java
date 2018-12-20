package arbremincaml;

import visiteur.ObjVisitor;
import visiteur.Visitor;

public class FNeg extends OperateurUnaire {
    public FNeg(Exp e) {
        super(e);
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
