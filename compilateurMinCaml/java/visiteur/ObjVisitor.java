package visiteur;

import arbremincaml.*;

/**
 * Interface du visiteur applicable à des noeuds MinCaml et renvoyant une valeur
 * @param <E> le type des valeurs renvoyées par le visiteur
 */
public interface ObjVisitor<E> {
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(Unit e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(Bool e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(Int e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(FloatMinCaml e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(Not e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(Neg e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(Add e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(Sub e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(FNeg e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(FAdd e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(FSub e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(FMul e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(FDiv e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(Eq e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(LE e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(If e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(Let e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(Var e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(LetRec e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(App e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(Tuple e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(LetTuple e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(Array e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(Get e);
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    E visit(Put e);
}


