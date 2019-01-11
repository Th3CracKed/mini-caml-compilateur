package arbreasml;

/**
 * Interface fonctionnelle (à une seule méthode) correspondant à un constructeur de noeud héritant de IfFloatAsml, utilisée dans VisiteurGenererArbreAsml 
 * pour factoriser la création de noeud ASML If comparant des nombres flottants. Il est ainsi possible de passer une référence sur le constructeur d'un noeud hériant de 
 * IfFloatAsml (par exemple IfEqFloatAsml::new) à une méthode prennant un paramètre un ConstructeurIfFloat
 */
public interface ConstructeurIfFloat {
    /**
     * Creer un noeud hériant de IfFloatAsml
     * @param e1 le premier élément comparé dans la condition du if
     * @param e2 le second élément comparé dans la condition du if
     * @param eIf la branche a exécuter quand la condition est vraie
     * @param eElse la branche a exécuter quand la condition est fausse
     * @return le noeud if créé
     */
    IfFloatAsml creerIf(VarAsml e1, VarAsml e2, AsmtAsml eIf, AsmtAsml eElse);
}
