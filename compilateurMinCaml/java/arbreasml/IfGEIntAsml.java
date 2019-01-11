package arbreasml;

import visiteur.ObjVisiteurAsml;
import visiteur.VisiteurAsml;

/**
 * Noeud ASML If comparant des entiers avec l'opérateur supérieur ou égal
 */
public class IfGEIntAsml extends IfIntAsml {

    /**
     * Créé un noeud ASML correspondant à un If comparant des entiers avec l'opérateur supérieur ou égal avec pour éléments comparés e1 et e2 et exécutant le branche eIf
     * si la condition est vraie et eElse sinon
     * @param e1 le premier élément comparé dans la condition du if
     * @param e2 le second élément comparé dans la condition du if
     * @param eIf la branche a exécuter quand la condition est vraie
     * @param eElse la branche a exécuter quand la condition est fausse
     */
    public IfGEIntAsml(VarAsml e1, VarOuIntAsml e2, AsmtAsml eIf, AsmtAsml eElse) {
        super(e1, e2, eIf, eElse);
    }

    @Override
    public void accept(VisiteurAsml v) {
        v.visit(this);
    }

    @Override
    public <E> E accept(ObjVisiteurAsml<E> v) {
        return v.visit(this);
    }
    
}
