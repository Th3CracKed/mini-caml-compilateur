package arbremincaml;


import visiteur.ObjVisitor;
import visiteur.Visitor;
import java.util.List;

/**
 * Noeud MinCaml représentant un tuple. Tuple hérite de valeur (la valeur d'un tuple est le tuple lui-même) pour permettre à l'étape du constant folding de comparer 
 * des instances de la classe Valeur entre elles en utilisant la méthode equals de leur attribut valeur.
 */
public class Tuple extends Valeur<Tuple> {
    private final List<Exp> es;

    /**
     * Créé un tuple avec les composantes dans la liste es
     * @param es la liste des composantes du tuple
     */
    public Tuple(List<Exp> es) {
        super(null);
        // il n'est pas possible de passer l'objet courant (this) au constructeur de la classe mère, on passe donc null comme valeur puis on la change par l'objet courant 
        // (this) en appelant la méthode setValeur
        setValeur(this); 
        this.es = es;
    }

    /**
     * Renvoie la liste des composantes du tuple
     * @return la liste des composantes du tuple
     */
    public List<Exp> getEs()
    {
        return es;
    }
    
    
    /**
     * Renvoie vrai si l'objet courant (this) est égal à o (et si ces deux objets ont une valeur constante) et faux sinon. Cette méthode est redéfinie pour permettre
     * à l'étape du constant folding de comparer des instances de la classe Valeur entre elles en utilisant la méthode equals de leur attribut valeur.
     * @param o l'objet à comparer à l'objet courant (this)
     * @return vrai si l'objet courant (this) est égal à o et faux sinon
     */
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
    
    @Override
    public <E> E accept(ObjVisitor<E> v) {
        return v.visit(this);
    }
    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}