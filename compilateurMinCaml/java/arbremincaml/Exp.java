package arbremincaml;

import visiteur.ObjVisitor;
import visiteur.Visitor;

/**
 * Classe mère des noeuds MinCaml
 */
public abstract class Exp {
    /**
     * Méthode accept permettant au noeud d'être visité par le visiteur (ne renvoyant pas de resultat) passé en paramètre. Cette méthode doit être
     * redefinie dans toutes les classes concrètes héritant de celle-ci et appeler la méthode visit du visiteur en lui passant l'objet courant (this)
     * @param v le visiteur
     */
    public abstract void accept(Visitor v);

    /**
     * Méthode accept permettant au noeud d'être visité par le visiteur (renvoyant un resultat) passé en paramètre. Cette méthode doit être
     * redefinie dans toutes les classes concrètes héritant de celle-ci et renvoyer le resultat de la méthode visit du visiteur en lui passant l'objet courant (this)
     * @param <E> le type du resultat renvoyé par l'opération visit du visiteur v
     * @param v le visiteur
     * @return le resultat de l'opération visit du visiteur v appliqué à l'objet courant (this)
     */
    public abstract <E> E accept(ObjVisitor<E> v);
}
