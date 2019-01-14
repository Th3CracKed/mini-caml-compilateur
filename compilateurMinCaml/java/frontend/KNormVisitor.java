package frontend;

import arbremincaml.*;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import visiteur.ObjVisitorExp;

/**
 * Visiteur réalisant la k-normalisation d'un programme MinCaml. La knormalisation est réalisée pour respecter l'ordre d'évaluation en OCaml : par exemple
 * print_int a = print_int b affiche b puis a en OCaml, la knormalisation doit donc donner le résultat let v1 = print_int b in let v2 = print_int a in v1 = v2
 */
public class KNormVisitor extends ObjVisitorExp {

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie le résultat de la k-normalisation de e.
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Let visit(App e) {
        LinkedList<Exp> vars = new LinkedList<>();
        Var varFonction = new Var(Id.gen());
        for (int i = 0; i < e.getEs().size(); i++) {
            vars.add(new Var(Id.gen()));
        }
        Let resultat = new Let(varFonction.getId(), Type.gen(), e.getE().accept(this), new App(varFonction, vars));
        for (int i = 0 ; i < e.getEs().size() ; i++)
        {
            resultat = new Let(((Var) vars.get(i)).getId(), Type.gen(), e.getEs().get(i).accept(this), resultat);
        }
        return resultat;
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie le résultat de la k-normalisation de e.
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public FloatMinCaml visit(FloatMinCaml e) {
        return e;
    }

    /**
     * Méthode factorisant la k-normalisation des noeuds héritant de OperateurUnaire
     * @param e le noeud e à visiter
     * @param constructeurOpUnaire une référence sur le constructeur de la classe d'instanciation de e (par exemple Neg::new pour Neg)
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    private Let visitOpUnaireWorker(OperateurUnaire e, Function<Exp, ? extends OperateurUnaire> constructeurOpUnaire)
    {
        Var var = new Var(Id.gen());
        return new Let(var.getId(), Type.gen(), e.getE().accept(this), constructeurOpUnaire.apply(var));
    }
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie le résultat de la k-normalisation de e.
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Let visit(Not e) {
        return visitOpUnaireWorker(e, Not::new);
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie le résultat de la k-normalisation de e.
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Let visit(Neg e) {
        return visitOpUnaireWorker(e, Neg::new);
    }

    /**
     * Méthode factorisant la k-normalisation des noeuds ayant 2 fils suivants : les opérateurs binaires, Get et Array
     * @param e le noeud e à visiter
     * @param constructeurOpUnaire une référence sur le constructeur de la classe d'instanciation de e (par exemple Add::new pour Add)
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    private Let visitNoeudA2FilsWorker(Exp e1, Exp e2, BiFunction<Exp, Exp, ? extends Exp> constructeur)
    {
        Var var1 = new Var(Id.gen());
        Var var2 = new Var(Id.gen());
        Exp e1Accepte = e1.accept(this);
        Exp e2Accepte = e2.accept(this);
        return new Let(var2.getId(), Type.gen(), e2Accepte,
                    new Let(var1.getId(), Type.gen(), e1Accepte, constructeur.apply(var1, var2)));
    }
    
    /**
     * Méthode factorisant la k-normalisation des noeuds héritant de OperateurBinaire
     * @param e le noeud e à visiter
     * @param constructeurOpBinaire une référence sur le constructeur de la classe d'instanciation de e (par exemple Add::new pour Add)
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    private Let visitOpBinaireWorker(OperateurBinaire e, BiFunction<Exp, Exp, ? extends Exp> constructeurOpBinaire)
    {
        return visitNoeudA2FilsWorker(e.getE1(), e.getE2(), constructeurOpBinaire);
    }
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie le résultat de la k-normalisation de e.
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Let visit(Add e) {
        return visitOpBinaireWorker(e, Add::new);
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie le résultat de la k-normalisation de e.
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Let visit(Sub e) {
        return visitOpBinaireWorker(e, Sub::new);
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie le résultat de la k-normalisation de e.
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Let visit(Eq e) {
        return visitOpBinaireWorker(e, Eq::new);
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie le résultat de la k-normalisation de e.
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Let visit(LE e) {
        return visitOpBinaireWorker(e, LE::new);
    }
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie le résultat de la k-normalisation de e.
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Let visit(FAdd e){
       return visitOpBinaireWorker(e, FAdd::new); 
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie le résultat de la k-normalisation de e.
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Let visit(FSub e){
        return visitOpBinaireWorker(e, FSub::new); 
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie le résultat de la k-normalisation de e.
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Let visit(FNeg e) {
       return visitOpUnaireWorker(e, FNeg::new); 
    }
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie le résultat de la k-normalisation de e.
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Let visit(FMul e) {
       return visitOpBinaireWorker(e, FMul::new); 
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie le résultat de la k-normalisation de e.
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Let visit(FDiv e){
        return visitOpBinaireWorker(e, FDiv::new); 
    }

    /**
     * Méthode factorisant la k-normalisation des noeuds héritant de OperateurBinaire.
     * @param e1 la condition du noeud if
     * @param e2Accepte la branche if du noeud if (déjà k-normalisée)
     * @param e3Accepte la branche else du noeud if (déjà k-normalisée)
     * @param createurOpRel une référence sur le constructeur de la classe de la condition du if (par exemple Eq::new pour Eq)
     * @return le résultat de la k-normalisation de e
     */
    private Let visitIfWorker(OperateurRelationnel e1, Exp e2Accepte, Exp e3Accepte, BinaryOperator<Exp> createurOpRel) {
        Var varE1Gauche = new Var(Id.gen());
        Var varE1Droite = new Var(Id.gen());
        return new Let(varE1Droite.getId(), Type.gen(), e1.getE2().accept(this), new Let(varE1Gauche.getId(), Type.gen(), e1.getE1().accept(this), new If(createurOpRel.apply(varE1Gauche, varE1Droite), e2Accepte, e3Accepte)));
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie le résultat de la k-normalisation de e.
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Let visit(If e) {
        Exp e1 = e.getE1();
        Exp e2Accepte = e.getE2().accept(this);
        Exp e3Accepte = e.getE3().accept(this);
        BinaryOperator<Exp> createurEq = Eq::new;
        if (e1 instanceof Eq) {
            return visitIfWorker((Eq)e1, e2Accepte, e3Accepte, createurEq);
        }
        else if(e1 instanceof LE)
        {
            return visitIfWorker((LE)e1, e2Accepte, e3Accepte, LE::new);
        }
        else
        {
            return visitIfWorker(new Eq(e1, new Bool(true)), e2Accepte, e3Accepte, createurEq); 
        }
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie le résultat de la k-normalisation de e.
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Let visit(Put e) {
        Exp e1 = e.getE1().accept(this);
        Exp e2 = e.getE2().accept(this);
        Exp e3 = e.getE3().accept(this);

        Id new_var1 = Id.gen();
        Type new_type1 = Type.gen();
        Id new_var2 = Id.gen();
        Type new_type2 = Type.gen();
        Id new_var3 = Id.gen();
        Type new_type3 = Type.gen();

        Let res = 
        new Let(new_var3, new_type3, e3,
          new Let(new_var2, new_type2, e2,
              new Let(new_var1, new_type1, e1,
                new Put(new Var(new_var1), new Var(new_var2), new Var(new_var3)))));
        return res;
    }
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie le résultat de la k-normalisation de e.
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Let visit(Get e) {
        return visitNoeudA2FilsWorker(e.getE1(), e.getE2(), Get::new);
    }
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie le résultat de la k-normalisation de e.
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Let visit(Array e) {
        return visitNoeudA2FilsWorker(e.getE1(), e.getE2(), Array::new);
    }
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie le résultat de la k-normalisation de e.
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Let visit(Tuple e) {
        LinkedList<Exp> vars = new LinkedList<>();
        List<Exp> composantes = e.getEs();
        for (Exp composante : composantes) {
            vars.add(new Var(Id.gen()));
        }
        Var var = new Var(Id.gen());
        Let resultat = new Let(var.getId(), Type.gen(), new Tuple(vars), var);
        for (int i = 0; i < composantes.size() ; i++) {
            resultat = new Let(((Var) vars.get(i)).getId(), Type.gen(), composantes.get(i).accept(this), resultat);
        }
        return resultat;
    }
}
