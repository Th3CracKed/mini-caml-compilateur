package visiteur;

import arbreasml.*;

/**
 * Interface du visiteur applicable à des noeuds ASML et renvoyant une valeur
 * @param <E> le type des valeurs renvoyées par le visiteur
 */
public interface ObjVisiteurAsml<E>{
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(AddAsml e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(FunDefConcreteAsml e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(IntAsml e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(LetAsml e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(NegAsml e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(NopAsml e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(ProgrammeAsml e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(SubAsml e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(VarAsml e);  
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(NewAsml e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(FNegAsml e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(FAddAsml e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(FSubAsml e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(FMulAsml e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(FDivAsml e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(CallAsml e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(CallClosureAsml e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(MemLectureAsml e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(MemEcritureAsml e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(LetFloatAsml e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(IfEqIntAsml e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(IfLEIntAsml e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(IfGEIntAsml e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(IfEqFloatAsml e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(IfLEFloatAsml e);
}
