package arbremincaml;

import visiteur.ObjVisitor;
import visiteur.Visitor;

/**
 * Noeud MinCaml correspondant à l'instruction let rec
 */
public class LetRec extends Exp {
    private final FunDef fd;
    private final Exp e;
        
    /**
     * Créé un noeud MinCaml correspondant à l'instruction let rec, déclarant la fonction fd et avec l'expression e après le mot clé in
     * @param fd la fonction déclarée
     * @param e la partie du let rec après le mot clé in
     */
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

    /**
     * Renvoie la fonction déclarée
     * @return la fonction déclarée
     */
    public FunDef getFd() {
        return fd;
    }

    /**
     * Renvoie la partie du let rec après le mot clé in
     * @return la partie du let rec après le mot clé in
     */
    public Exp getE() {
        return e;
    }
}