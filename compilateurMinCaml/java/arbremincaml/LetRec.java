package arbremincaml;

import visiteur.ObjVisitor;
import visiteur.Visitor;

public class LetRec extends Exp {
    private final FunDef fd;
    private final Exp e;
        
    public LetRec(FunDef fd, Exp e) {
        this.fd = fd;
        this.e = e;
    }    

    @Override
    public <E> E accept(ObjVisitor<E> v) {
        return v.visit(this);
    }
    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    public FunDef getFd() {
        return fd;
    }

    public Exp getE() {
        return e;
    }
}