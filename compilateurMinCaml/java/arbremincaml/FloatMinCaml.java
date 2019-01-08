package arbremincaml;

import util.MyCompilationException;
import visiteur.ObjVisitor;
import visiteur.Visitor;

public class FloatMinCaml extends Valeur<Float> {
    public FloatMinCaml(Float f) {
        super(f);        
        boolean estNaN = f.isNaN();
        boolean estInfini = f.isInfinite();
        if(estNaN || estInfini)
        {            
            String strValeur = null;
            if(estNaN)
            {
                strValeur = "NaN";
            }
            else
            {
                strValeur = ((f >= 0)?"+":"-")+"infini";
            }
            throw new MyCompilationException(strValeur+" n'est pas une valeur valide pour un nombre flottant en ASML");
        }
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