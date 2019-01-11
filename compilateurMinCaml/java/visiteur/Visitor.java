package visiteur;

import arbremincaml.*;

/**
 * Interface du visiteur applicable à des noeuds MinCaml et ne renvoyant pas de valeur. Une implémentation par défault est fourni pour toutes les méthodes visit : elle ne fait rien
 * pour les noeud terminant et applique le visiteur courant (this) aux fils des noeud non terminaux. Cela permet de factoriser le code de plusieurs visiteur car
 * beaucoup d'entre eux ne font un traitement particulier que pour quelques noeuds (dans ce cas il faut qu'ils redéfinissent la méthode visit prennant en paramètre ce
 * type de noeud) et utilisent l'implémentation par défaut des méthodes visit pour les autres noeud.
 */
public interface Visitor {

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode ne fait rien
     * @param e le noeud à visiter
     */
    default void visit(Unit e) {
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode ne fait rien
     * @param e le noeud à visiter
     */
    default void visit(Bool e) {
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode ne fait rien
     * @param e le noeud à visiter
     */
    default void visit(Int e) {
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode ne fait rien
     * @param e le noeud à visiter
     */
    default void visit(FloatMinCaml e) { 
        
    }    
    
    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(Not e) {
        visitOpUnaireWorker(e, this);
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(Neg e) {
        visitOpUnaireWorker(e, this);
    }
    
    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(Add e) {
        visitOpBinaireWorker(e, this);
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(Sub e) {
	visitOpBinaireWorker(e, this);
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(FNeg e){
        visitOpUnaireWorker(e, this);
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(FAdd e){
        visitOpBinaireWorker(e, this);
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(FSub e){
        visitOpBinaireWorker(e, this);
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(FMul e) {
       visitOpBinaireWorker(e, this);
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(FDiv e){
        visitOpBinaireWorker(e, this);
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(Eq e){
        visitOpBinaireWorker(e, this);
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(LE e){
        visitOpBinaireWorker(e, this);
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(If e){        
        e.getE1().accept(this);
        e.getE2().accept(this);
        e.getE3().accept(this);
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(Let e) {      
      e.getE1().accept(this);
      e.getE2().accept(this);
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode ne fait rien
     * @param e le noeud à visiter
     */
    default void visit(Var e){
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(LetRec e){
       e.getE().accept(this);
       e.getFd().getE().accept(this);
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(App e){
        e.getE().accept(this);
        for(Exp argument : e.getEs())
        {
            argument.accept(this);
        }
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(Tuple e){
       for(Exp composante : e.getEs())
       {
           composante.accept(this);
       }
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(LetTuple e){
        e.getE1().accept(this);
        e.getE2().accept(this);
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(Array e){
        e.getE1().accept(this);
        e.getE2().accept(this);
   }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(Get e){
        visitAccesTabWorker(e, this);
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(Put e){
        visitAccesTabWorker(e, this);
        e.getE3().accept(this);
    }
    
    /**
     * Applique le visiteur visiteur aux fils du noeud e présents dans la classe OperateurUnaire (permet de factoriser le code des méthodes visit s'appliquant aux noeuds héritant de OperateurUnaire)
     * @param e le noeud à visiter
     * @param visiteur le visiteur à appliquer à e
     */
    public static void visitOpUnaireWorker(OperateurUnaire e, Visitor visiteur) {
        e.getE().accept(visiteur);
    }

    /**
     * Applique le visiteur visiteur aux fils du noeud e présents dans la classe OperateurBinaire (permet de factoriser le code des méthodes visit s'appliquant aux noeuds héritant de OperateurBinaire)
     * @param e le noeud à visiter
     * @param visiteur le visiteur à appliquer à e
     */
    public static void visitOpBinaireWorker(OperateurBinaire e, Visitor visiteur) {
        e.getE1().accept(visiteur);
        e.getE2().accept(visiteur);
    }
    
    /**
     * Applique le visiteur visiteur aux fils du noeud e présents dans la classe AccesTableau (permet de factoriser le code des méthodes visit s'appliquant aux noeuds héritant de AccesTableau)
     * @param e le noeud à visiter
     * @param visiteur le visiteur à appliquer à e
     */
    public static void visitAccesTabWorker(AccesTableau e, Visitor visiteur) {
        e.getE1().accept(visiteur);
        e.getE2().accept(visiteur);
    }
}


