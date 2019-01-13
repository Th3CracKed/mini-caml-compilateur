package frontend;

import arbremincaml.*;
import java.util.HashSet;
import java.util.List;
import visiteur.ObjVisitorExp;
import visiteur.Visitor;

/**
 * Visiteur éliminant les définitions de variables (avec un noeud Let ou LetRec) et de fonctions non utilisées et telles que l'expression affectée à cette variable 
 * n'a pas d'effet de bord (on considére que tout appel de fonction ou toute écriture dans un tableau a un effet de bord)
 */
public class VisiteurDefinitionsInutiles extends ObjVisitorExp {

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud.
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Exp visit(Let e) {
        Exp e2 = e.getE2().accept(this);
        Id id = e.getId();
        VisiteurVariablesUtilisees visVarUtilisees = new VisiteurVariablesUtilisees();
        e2.accept(visVarUtilisees);
        VisiteurEffetDeBord visEffetDeBord = new VisiteurEffetDeBord();
        e.getE1().accept(visEffetDeBord);
        if (visVarUtilisees.getVariablesUtilisees().contains(id.getIdString()) || visEffetDeBord.getAUnEffetDeBord()) {
            Exp e1 = e.getE1().accept(this);
            return new Let(id, Type.gen(), e1, e2);
        } else {
            return e2;
        }
    }
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud.
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Exp visit(LetRec e) {
        Exp exp = e.getE().accept(this);
        FunDef funDef = e.getFd();
        Id id = funDef.getId();
        VisiteurVariablesUtilisees visVarUtilisees = new VisiteurVariablesUtilisees();
        exp.accept(visVarUtilisees);
        if (visVarUtilisees.getVariablesUtilisees().contains(id.getIdString())) {
            Exp eFunDef = funDef.getE().accept(this);
            return new LetRec(new FunDef(id, funDef.getType(), funDef.getArgs(), eFunDef), exp);
        } else {
            return exp;
        }
    }
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud.
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Exp visit(LetTuple e)
    {
        Exp e2 = e.getE2().accept(this);
        List<Id> ids = e.getIds();
        VisiteurVariablesUtilisees visVarUtilisees = new VisiteurVariablesUtilisees();
        e2.accept(visVarUtilisees);        
        if (ids.stream().anyMatch(x->visVarUtilisees.getVariablesUtilisees().contains(x.getIdString())))
        {
            Exp e1 = e.getE1().accept(this);
            return new LetTuple(ids, e.getTs(), e1, e2);
        }
        else
        {
            return e2;
        }
    }
    
    /**
     * Visiteur déterminant les variables utilisée dans un noeud mincaml
     */
    private class VisiteurVariablesUtilisees implements Visitor {

        private final HashSet<String> variablesUtilisees;

        /**
        * Créé un visiteur déterminant les variables utilisée dans un noeud mincaml
        */
        public VisiteurVariablesUtilisees() {
            variablesUtilisees = new HashSet<>();
        }

        /**
         * Renvoie l'ensemble des variables utilisées
         * @return l'ensemble des variables utilisées
         */
        public HashSet<String> getVariablesUtilisees() {
            return variablesUtilisees;
        }

        /**
        * Visite le noeud e. Dans ce cas, ajoute l'identifiant de e à la liste des identifiants de variables utilisées
        * @param e le noeud à visiter
        */
        @Override
        public void visit(Var e) {
            variablesUtilisees.add(e.getId().getIdString());
        }
    }
}
