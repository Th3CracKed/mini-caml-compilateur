package arbremincaml;

import visiteur.ObjVisitor;
import visiteur.Visitor;

public class FMul extends OperateurArithmetiqueFloat {
    public FMul(Exp e1, Exp e2) {
        super(e1,e2);
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