package arbremincaml;

import visiteur.ObjVisitor;
import visiteur.Visitor;

public abstract class Exp {
    public abstract void accept(Visitor v);

    public abstract <E> E accept(ObjVisitor<E> v);
}
