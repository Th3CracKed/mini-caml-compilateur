package arbremincaml;


import visiteur.ObjVisitor;
import visiteur.Visitor;
import java.util.List;

public class Tuple extends Valeur<Tuple> {
    private final List<Exp> es;

    public Tuple(List<Exp> es) {
        super(null);
        setValeur(this);
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
    
    @Override
    public boolean equals(Object o)
    {
        if(o instanceof Tuple)
        {
            List<Exp> esAutre = ((Tuple)o).getEs();
            if(es.size() != esAutre.size())
            {
                return false;
            }
            for(int i = 0 ; i < es.size() ; i++)
            {
                if(!(es.get(i) instanceof Valeur) || !(esAutre.get(i) instanceof Valeur) || !es.get(i).equals(esAutre.get(i)))
                {
                    return false;
                }
            }
            return true;
        }
        else
        {
            return false;
        }
    }
}