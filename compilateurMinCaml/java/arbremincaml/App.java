package arbremincaml;


import visiteur.ObjVisitor;
import visiteur.Visitor;
import java.util.List;

/**
 * Classe correspondant au noeud ASML de l'appel de fonction
 */
public class App extends Exp {
    private final Exp e;
    private final List<Exp> es;

    /**
     * Créé un noeud ASML Call pour appeler la fonction de label idString avec les arguments dans arguments
     * @param e la fonction appelée
     * @param es les arguments passés à la fonction
     */
    public App(Exp e, List<Exp> es) {
        this.e = e;
        this.es = es;
    }
    
    /**
     * Renvoie la fonction appelée
     * @return la fonction appelée
     */
    public Exp getE() {
        return e;
    }

    /**
     * Renvoie les arguments passés à la fonction
     * @return les arguments passés à la fonction
     */
    public List<Exp> getEs() {
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