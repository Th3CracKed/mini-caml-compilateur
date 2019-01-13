package visiteur;

import arbreasml.*;

/**
 * Interface du visiteur applicable à des noeuds ASML et ne renvoyant pas de valeur. Une implémentation par défault est fourni pour toutes les méthodes visit : elle ne fait rien
 * pour les noeud terminant et applique le visiteur courant (this) aux fils des noeud non terminaux. Cela permet de factoriser le code de plusieurs visiteur car
 * beaucoup d'entre eux ne font un traitement particulier que pour quelques noeuds (dans ce cas il faut qu'ils redéfinissent la méthode visit prennant en paramètre ce
 * type de noeud) et utilisent l'implémentation par défaut des méthodes visit pour les autres noeud.
 */
public interface VisiteurAsml {
    
    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(AddAsml e) {
        visitOpArithmetiqueIntWorker(e, this);
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(FunDefConcreteAsml e) {
        for (VarAsml argument : e.getArguments()) {
            argument.accept(this);
        }
        e.getAsmt().accept(this);
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode ne fait rien
     * @param e le noeud à visiter
     */
    default void visit(IntAsml e) {

    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(LetAsml e) {
        e.getE1().accept(this);
        e.getE2().accept(this);
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(NegAsml e) {
        visitNegBaseWorker(e, this);
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode ne fait rien
     * @param e le noeud à visiter
     */
    default void visit(NopAsml e) {

    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(ProgrammeAsml e) {
        e.getMainFunDef().accept(this);
        for (FunDefAsml funDef : e.getFunDefs()) {
            funDef.accept(this);
        }
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(SubAsml e) {
        visitOpArithmetiqueIntWorker(e, this);
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode ne fait rien
     * @param e le noeud à visiter
     */
    default void visit(VarAsml e) {

    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(NewAsml e) {
        e.getE().accept(this);
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(FNegAsml e) {
        visitNegBaseWorker(e, this);
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(FAddAsml e) {
        visitOpArithmetiqueFloatWorker(e, this);
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(FSubAsml e) {
        visitOpArithmetiqueFloatWorker(e, this);
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(FMulAsml e) {
        visitOpArithmetiqueFloatWorker(e, this);
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(FDivAsml e) {
        visitOpArithmetiqueFloatWorker(e, this);
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(CallAsml e) {
        visitCallBaseWorker(e, this);
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(CallClosureAsml e) {
        e.getVar().accept(this);
        visitCallBaseWorker(e, this);
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(MemLectureAsml e) {
        visitMemWorker(e, this);
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(MemEcritureAsml e) {
        visitMemWorker(e, this);
        e.getValeurEcrite().accept(this);
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode ne fait rien
     * @param e le noeud à visiter
     */
    default void visit(LetFloatAsml e) {
        
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(IfEqIntAsml e) {
        visitIfIntWorker(e, this);
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(IfLEIntAsml e) {
        visitIfIntWorker(e, this);
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(IfGEIntAsml e) {
        visitIfIntWorker(e, this);
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(IfEqFloatAsml e) {
        visitIfFloatWorker(e, this);
    }

    /**
     * Visite le noeud e. L'implémentation par défault de cette méthode applique le visiteur courant (this) aux fils de e
     * @param e le noeud à visiter
     */
    default void visit(IfLEFloatAsml e) {
        visitIfFloatWorker(e, this);
    }
    
    
    /**
     * Applique le visiteur visiteur aux fils du noeud e présents dans la classe MemAsml (permet de factoriser le code des méthodes visit s'appliquant aux noeuds héritant de MemAsml)
     * @param e le noeud à visiter
     * @param visiteur le visiteur à appliquer à e
     */
    public static void visitMemWorker(MemAsml e, VisiteurAsml visiteur) {
        e.getTableau().accept(visiteur);
        e.getIndice().accept(visiteur);
    }

    /**
     * Applique le visiteur visiteur aux fils du noeud e présents dans la classe OperateurArithmetiqueAsml (permet de factoriser le code des méthodes visit s'appliquant aux noeuds héritant de OperateurArithmetiqueAsml)
     * @param e le noeud à visiter
     * @param visiteur le visiteur à appliquer à e
     */
    public static void visitOpArithmetiqueWorker(OperateurArithmetiqueAsml e, VisiteurAsml visiteur) {
        e.getE1().accept(visiteur);
    }
    
    /**
     * Applique le visiteur visiteur aux fils du noeud e présents dans la classe OperateurArithmetiqueIntAsml (permet de factoriser le code des méthodes visit s'appliquant aux noeuds héritant de OperateurArithmetiqueIntAsml)
     * @param e le noeud à visiter
     * @param visiteur le visiteur à appliquer à e
     */
    public static void visitOpArithmetiqueIntWorker(OperateurArithmetiqueIntAsml e, VisiteurAsml visiteur) {
        visitOpArithmetiqueWorker(e, visiteur);
        e.getE2().accept(visiteur);
    }
    
    /**
     * Applique le visiteur visiteur aux fils du noeud e présents dans la classe OperateurArithmetiqueFloatAsml (permet de factoriser le code des méthodes visit s'appliquant aux noeuds héritant de OperateurArithmetiqueFloatAsml)
     * @param e le noeud à visiter
     * @param visiteur le visiteur à appliquer à e
     */
    public static void visitOpArithmetiqueFloatWorker(OperateurArithmetiqueFloatAsml e, VisiteurAsml visiteur) {
        visitOpArithmetiqueWorker(e, visiteur);
        e.getE2().accept(visiteur);
    }
    
    /**
     * Applique le visiteur visiteur à l'opérande 1 comparé par le noeud if e (permet de factoriser le code des méthodes visit s'appliquant aux noeuds héritant de IfAsml)
     * @param e le noeud à visiter
     * @param visiteur le visiteur
     */
    public static void visitDebutIfWorker(IfAsml e, VisiteurAsml visiteur)
    {
        e.getE1().accept(visiteur);
    }
    
    /**
     * Applique le visiteur visiteur aux branches du noeud if e (permet de factoriser le code des méthodes visit s'appliquant aux noeuds héritant de IfAsml)
     * @param e le noeud à visiter
     * @param visiteur le visiteur à appliquer à e
     */
    public static void visitFinIfWorker(IfAsml e, VisiteurAsml visiteur)
    {
        e.getESiVrai().accept(visiteur);
        e.getESiFaux().accept(visiteur);
    }
    
    /**
     * Applique le visiteur visiteur aux fils du noeud e présents dans la classe IfIntAsml (permet de factoriser le code des méthodes visit s'appliquant aux noeuds héritant de IfIntAsml)
     * @param e le noeud à visiter
     * @param visiteur le visiteur à appliquer à e
     */
    public static void visitIfIntWorker(IfIntAsml e, VisiteurAsml visiteur)
    {
        visitDebutIfWorker(e, visiteur);
        e.getE2().accept(visiteur);
        visitFinIfWorker(e, visiteur);
    }
    
    /**
     * Applique le visiteur visiteur aux fils du noeud e présents dans la classe IfFloatAsml (permet de factoriser le code des méthodes visit s'appliquant aux noeuds héritant de IfFloatAsml)
     * @param e le noeud à visiter
     * @param visiteur le visiteur à appliquer à e
     */
    public static void visitIfFloatWorker(IfFloatAsml e, VisiteurAsml visiteur)
    {
        visitDebutIfWorker(e, visiteur);
        e.getE2().accept(visiteur);
        visitFinIfWorker(e, visiteur);
    }
    
    /**
     * Applique le visiteur visiteur aux fils du noeud e présents dans la classe CallBaseAsml (permet de factoriser le code des méthodes visit s'appliquant aux noeuds héritant de CallBaseAsml)
     * @param e le noeud à visiter
     * @param visiteur le visiteur à appliquer à e
     */
    public static void visitCallBaseWorker(CallBaseAsml e, VisiteurAsml visiteur)
    {
        for (VarAsml argument : e.getArguments())
        {
            argument.accept(visiteur);
        }
    }
    
    /**
     * Applique le visiteur visiteur aux fils du noeud e présents dans la classe NegBaseAsml (permet de factoriser le code des méthodes visit s'appliquant aux noeuds héritant de NegBaseAsml)
     * @param e le noeud à visiter
     * @param visiteur le visiteur à appliquer à e
     */
    public static void visitNegBaseWorker(NegBaseAsml e, VisiteurAsml visiteur) {
        e.getE().accept(visiteur);
    }
}
