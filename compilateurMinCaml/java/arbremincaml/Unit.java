package arbremincaml;

import visiteur.ObjVisitor;
import visiteur.Visitor;

public class Unit extends Valeur<Boolean> {
    
    public Unit()
    {
        super(true); // on compare des element de meme type en comparant leur valeur (getValeur()) avec la methodes equals (de cette façon () = () vaudra true)
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