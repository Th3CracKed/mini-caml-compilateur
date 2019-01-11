package visiteur;

import arbremincaml.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Classe mère des visiteur s'appliquant à un arbre MinCaml et renvoyant un arbre MinCaml. Les méthodes visit sont implémentées de la façon suivante : les méthodes 
 * s'appliquant à un noeud terminal (comme Int) renvoyent ce noeud et les autres créé et renvoyent un nouveau noeud du même type que celui qu'il prennent en paramètre
 * avec pour fils le résultat de l'appliquation du visiteur sur les fils du noeuds qu'ils prennent en paramètre. Cela permet de factoriser le code de plusieurs visiteurs car
 * beaucoup d'entre eux ne font un traitement particulier que pour quelques noeuds (dans ce cas il faut qu'ils redéfinissent la méthode visit prennant en paramètre ce
 * type de noeud) et utilisent l'implémentation des méthodes visit de cette classe pour les autres noeuds
 * @author Justin Kossonogow
 */
public abstract class ObjVisitorExp implements ObjVisitor<Exp> {
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans cette implémentation, renvoie le noeud e passé en paramètre
     * @param e le noeud à visiter
     * @return le noeud e passé en paramètre
     */
    @Override
    public Exp visit(Unit e) {
        return e;
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans cette implémentation, renvoie le noeud e passé en paramètre
     * @param e le noeud à visiter
     * @return le noeud e passé en paramètre
     */
    @Override
    public Exp visit(Bool e) {
        return e;
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans cette implémentation, renvoie le noeud e passé en paramètre
     * @param e le noeud à visiter
     * @return le noeud e passé en paramètre
     */
    @Override
    public Exp visit(Int e) {
        return e;
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans cette implémentation, renvoie le noeud e passé en paramètre
     * @param e le noeud à visiter
     * @return le noeud e passé en paramètre
     */
    @Override
    public Exp visit(FloatMinCaml e) { 
        return e;
    }
    
    /**
     * Renvoie un nouvel opérateur unaire avec pour fils le résultat de l'application visiteur courant (this) au fils de e
     * @param e l'opérateur unaire passé en paramètre
     * @param constructeurOpUnaire instance d'une classe dont la méthode apply prend en paramètre une instance de Exp et créé un nouvel opérateur unaire avec pour fils cette instance de Exp
     * @return un nouvel opérateur unaire avec pour fils le résultat de l'application visiteur courant (this) sur le fils de e
     */
    private Exp visitOpUnaireWorker(OperateurUnaire e, Function<Exp, ? extends OperateurUnaire> constructeurOpUnaire)
    {
        return constructeurOpUnaire.apply(e.getE().accept(this));
    }
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans cette implémentation, renvoie un nouveau noeud du 
     * même type que celui passé en paramètre avec pour fils les résultats de l'application du visiteur courant aux fils de noeud e passé en paramètre
     * @param e le noeud à visiter
     * @return le noeud e passé en paramètre
     */
    @Override
    public Exp visit(Not e) { 
        return visitOpUnaireWorker(e, Not::new);
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans cette implémentation, renvoie un nouveau noeud du 
     * même type que celui passé en paramètre avec pour fils les résultats de l'application du visiteur courant aux fils de noeud e passé en paramètre
     * @param e le noeud à visiter
     * @return le noeud e passé en paramètre
     */
    @Override
    public Exp visit(Neg e) {
        return visitOpUnaireWorker(e, Neg::new);
    }
    
    /**
     * Renvoie un nouvel opérateur binaire avec pour fils le résultat de l'application visiteur courant (this) aux fils de e
     * @param e l'opérateur binaire passé en paramètre
     * @param constructeurOpUnaire instance d'une classe dont la méthode apply prend en paramètre deux instances de Exp et créé un nouvel opérateur binaire avec pour fils ces instances de Exp
     * @return un nouvel opérateur binaire avec pour fils le résultat de l'application visiteur courant (this) sur les fils de e
     */
    private Exp visitOpBinaireWorker(OperateurBinaire e, BiFunction<Exp, Exp, ? extends OperateurBinaire> constructeurOpBinaire)
    {
        return constructeurOpBinaire.apply(e.getE1().accept(this), e.getE2().accept(this));
    }
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans cette implémentation, renvoie un nouveau noeud du 
     * même type que celui passé en paramètre avec pour fils les résultats de l'application du visiteur courant aux fils de noeud e passé en paramètre
     * @param e le noeud à visiter
     * @return le noeud e passé en paramètre
     */
    @Override
    public Exp visit(Add e) {
        return visitOpBinaireWorker(e, Add::new);
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans cette implémentation, renvoie un nouveau noeud du 
     * même type que celui passé en paramètre avec pour fils les résultats de l'application du visiteur courant aux fils de noeud e passé en paramètre
     * @param e le noeud à visiter
     * @return le noeud e passé en paramètre
     */
    @Override
    public Exp visit(Sub e) {
	return visitOpBinaireWorker(e, Sub::new); 
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans cette implémentation, renvoie un nouveau noeud du 
     * même type que celui passé en paramètre avec pour fils les résultats de l'application du visiteur courant aux fils de noeud e passé en paramètre
     * @param e le noeud à visiter
     * @return le noeud e passé en paramètre
     */
    @Override
    public Exp visit(Eq e){
        return visitOpBinaireWorker(e, Eq::new); 
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans cette implémentation, renvoie un nouveau noeud du 
     * même type que celui passé en paramètre avec pour fils les résultats de l'application du visiteur courant aux fils de noeud e passé en paramètre
     * @param e le noeud à visiter
     * @return le noeud e passé en paramètre
     */
    @Override
    public Exp visit(LE e){
        return visitOpBinaireWorker(e, LE::new); 
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans cette implémentation, renvoie un nouveau noeud du 
     * même type que celui passé en paramètre avec pour fils les résultats de l'application du visiteur courant aux fils de noeud e passé en paramètre
     * @param e le noeud à visiter
     * @return le noeud e passé en paramètre
     */
    @Override
    public Exp visit(FNeg e){
      return visitOpUnaireWorker(e, FNeg::new);
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans cette implémentation, renvoie un nouveau noeud du 
     * même type que celui passé en paramètre avec pour fils les résultats de l'application du visiteur courant aux fils de noeud e passé en paramètre
     * @param e le noeud à visiter
     * @return le noeud e passé en paramètre
     */
    @Override
    public Exp visit(FAdd e){
       return visitOpBinaireWorker(e, FAdd::new); 
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans cette implémentation, renvoie un nouveau noeud du 
     * même type que celui passé en paramètre avec pour fils les résultats de l'application du visiteur courant aux fils de noeud e passé en paramètre
     * @param e le noeud à visiter
     * @return le noeud e passé en paramètre
     */
    @Override
    public Exp visit(FSub e){
        return visitOpBinaireWorker(e, FSub::new); 
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans cette implémentation, renvoie un nouveau noeud du 
     * même type que celui passé en paramètre avec pour fils les résultats de l'application du visiteur courant aux fils de noeud e passé en paramètre
     * @param e le noeud à visiter
     * @return le noeud e passé en paramètre
     */
    @Override
    public Exp visit(FMul e) {
       return visitOpBinaireWorker(e, FMul::new); 
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans cette implémentation, renvoie un nouveau noeud du 
     * même type que celui passé en paramètre avec pour fils les résultats de l'application du visiteur courant aux fils de noeud e passé en paramètre
     * @param e le noeud à visiter
     * @return le noeud e passé en paramètre
     */
    @Override
    public Exp visit(FDiv e){
        return visitOpBinaireWorker(e, FDiv::new); 
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans cette implémentation, renvoie un nouveau noeud du 
     * même type que celui passé en paramètre avec pour fils les résultats de l'application du visiteur courant aux fils de noeud e passé en paramètre
     * @param e le noeud à visiter
     * @return le noeud e passé en paramètre
     */
    @Override
    public Exp visit(If e){   
        Exp e1 = e.getE1().accept(this);
        Exp e2 = e.getE2().accept(this);
        Exp e3 = e.getE3().accept(this);
        return new If(e1 , e2, e3);
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans cette implémentation, renvoie un nouveau noeud du 
     * même type que celui passé en paramètre avec pour fils les résultats de l'application du visiteur courant aux fils de noeud e passé en paramètre
     * @param e le noeud à visiter
     * @return le noeud e passé en paramètre
     */
    @Override
    public Exp visit(Let e) {
      Exp e1 = e.getE1().accept(this);
      Exp e2 = e.getE2().accept(this);
      return new Let(e.getId(), Type.gen(), e1 , e2);
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans cette implémentation, renvoie le noeud e passé en paramètre
     * @param e le noeud à visiter
     * @return le noeud e passé en paramètre
     */
    @Override
    public Exp visit(Var e){
        return e;
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans cette implémentation, renvoie un nouveau noeud du 
     * même type que celui passé en paramètre avec pour fils les résultats de l'application du visiteur courant aux fils de noeud e passé en paramètre
     * @param e le noeud à visiter
     * @return le noeud e passé en paramètre
     */
    @Override
    public Exp visit(LetRec e){
       Exp exp = e.getE().accept(this);
       FunDef funDef = e.getFd();
       FunDef nouvelleFunDef = new FunDef(funDef.getId(), funDef.getType(), funDef.getArgs(), funDef.getE().accept(this));
      return new LetRec(nouvelleFunDef, exp);
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans cette implémentation, renvoie un nouveau noeud du 
     * même type que celui passé en paramètre avec pour fils les résultats de l'application du visiteur courant aux fils de noeud e passé en paramètre
     * @param e le noeud à visiter
     * @return le noeud e passé en paramètre
     */
    @Override
    public Exp visit(App e){
        Exp fonction = e.getE().accept(this);
        List<Exp> arguments = new ArrayList<>();
        for(Exp argument : e.getEs())
        {
            arguments.add(argument.accept(this));
        }
        return new App(fonction, arguments);
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans cette implémentation, renvoie un nouveau noeud du 
     * même type que celui passé en paramètre avec pour fils les résultats de l'application du visiteur courant aux fils de noeud e passé en paramètre
     * @param e le noeud à visiter
     * @return le noeud e passé en paramètre
     */
    @Override
    public Exp visit(Tuple e){
        List<Exp> es = new ArrayList<>();
        for(Exp composante : e.getEs())
        {
            es.add(composante.accept(this));
        }
        return new Tuple(es);
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans cette implémentation, renvoie un nouveau noeud du 
     * même type que celui passé en paramètre avec pour fils les résultats de l'application du visiteur courant aux fils de noeud e passé en paramètre
     * @param e le noeud à visiter
     * @return le noeud e passé en paramètre
     */
    @Override
    public Exp visit(LetTuple e){
        return new LetTuple(e.getIds(), e.getTs(), e.getE1().accept(this), e.getE2().accept(this));
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans cette implémentation, renvoie un nouveau noeud du 
     * même type que celui passé en paramètre avec pour fils les résultats de l'application du visiteur courant aux fils de noeud e passé en paramètre
     * @param e le noeud à visiter
     * @return le noeud e passé en paramètre
     */
    @Override
    public Exp visit(Array e){
        return new Array(e.getE1().accept(this), e.getE2().accept(this));
   }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans cette implémentation, renvoie un nouveau noeud du 
     * même type que celui passé en paramètre avec pour fils les résultats de l'application du visiteur courant aux fils de noeud e passé en paramètre
     * @param e le noeud à visiter
     * @return le noeud e passé en paramètre
     */
    @Override
    public Exp visit(Get e){
        return new Get(e.getE1().accept(this), e.getE2().accept(this));
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans cette implémentation, renvoie un nouveau noeud du 
     * même type que celui passé en paramètre avec pour fils les résultats de l'application du visiteur courant aux fils de noeud e passé en paramètre
     * @param e le noeud à visiter
     * @return le noeud e passé en paramètre
     */
    @Override
    public Exp visit(Put e){
        return new Put(e.getE1().accept(this), e.getE2().accept(this), e.getE3().accept(this));
    }
}
