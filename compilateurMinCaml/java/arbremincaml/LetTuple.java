package arbremincaml;


import visiteur.ObjVisitor;
import visiteur.Visitor;
import java.util.List;

/**
 * Noeud MinCaml correspondant à l'instruction let pour la déclaration de tuple
 */
public class LetTuple extends Exp {
    private final List<Id> ids;
    private final List<Type> ts;
    private final Exp e1;
    private final Exp e2;

    /**
     * Créé un noeud MinCaml Let déclarant les variable d'identificateurs dans la liste ids et de types respectifs dans la liste ts, affectant la valeur du noeud e1 
     * au tuple ayant pour composantes les variables déclarées et avec le noeud e2 après le mot clé in du Let
     * @param ids les identificateurs des la variables déclarées
     * @param ts les types des la variables déclarées
     * @param e1 le noeud à affecter au tuple ayant pour composantes les variables déclarées
     * @param e2 le noeud apparaissant après le mot clé in du let
     */
    public LetTuple(List<Id> ids, List<Type> ts, Exp e1, Exp e2) {
        this.ids = ids;
        this.ts = ts;
        this.e1 = e1;
        this.e2 = e2;
    }    

    /**
     * Renvoie les identificateurs des la variables déclarées
     * @return les identificateurs des la variables déclarées 
     */
    public List<Id> getIds() {
        return ids;
    }

    /**
     * Renvoie les types des la variables déclarées
     * @return les types des la variables déclarées
     */
    public List<Type> getTs() {
        return ts;
    }

    /**
     * Renvoie le noeud à affecter au tuple ayant pour composantes les variables déclarées
     * @return le noeud à affecter au tuple ayant pour composantes les variables déclarées
     */
    public Exp getE1() {
        return e1;
    }

    /**
     * Renvoie le noeud apparaissant après le mot clé in du let
     * @return le noeud apparaissant après le mot clé in du let
     */
    public Exp getE2() {
        return e2;
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