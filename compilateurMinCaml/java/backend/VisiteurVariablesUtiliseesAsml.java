package backend;

import arbreasml.VarAsml;
import java.util.HashSet;
import visiteur.VisiteurAsml;

/**
 * Visiteur permettant de déterminer les variables utilisées dans un programme ASML
 */
public class VisiteurVariablesUtiliseesAsml implements VisiteurAsml {

    private final HashSet<String> variablesUtilisees;

    /**
     * Créé un visiteur permettant de déterminer les variables utilisées dans un programme ASML
     */
    public VisiteurVariablesUtiliseesAsml() {
        variablesUtilisees = new HashSet<>();
    }

    /**
     * Renvoie les variables utilisées dans le programme ASML
     * @return les variables utilisées dans le programme ASML
     */
    public HashSet<String> getVariablesUtilisees() {
        return variablesUtilisees;
    }

    /**
     * Visite le noeud e. Dans ce cas, ajoute l'identifiant de e à la liste des identifiants de variables utilisés
     * @param e le noeud à visiter 
     */
    @Override
    public void visit(VarAsml e) {
        getVariablesUtilisees().add(e.getIdString());
    }
}
