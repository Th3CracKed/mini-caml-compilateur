package frontend;

import java.util.HashSet;
import java.util.List;

/**
 * Classe représentant l'environnement d'une closure (la liste de ses variables libres et des fonctions qu'elle appelle (car certaines peuvent devoir être appelé
 * avec call_closure, il faut donc que la fonction reçoivent en paramètre ou créé cette closure))
 */
public class EnvironnementClosure
{
    private final List<String> variablesLibres;
    private final HashSet<String> fonctionsAppelees;
    
    /**
     * Créé l'environnement de closure contenant la liste des variables libres (dans variables libres) et la liste des fonctions appelées (dans fonctionsAppelees)
     * @param variablesLibres la liste des variables libres
     * @param fonctionsAppelees la liste des fonctions appelées
     */
    public EnvironnementClosure(List<String> variablesLibres, HashSet<String> fonctionsAppelees)
    {
        this.variablesLibres = variablesLibres;
        this.fonctionsAppelees = fonctionsAppelees;
    }

    /**
     * Renvoie la liste des variables libres
     * @return la liste des variables libres
     */
    public List<String> getVariablesLibres()
    {
        return variablesLibres;
    }
    
    /**
     * Renvoie la liste des fonctions appelées
     * @return la liste des fonctions appelées
     */
    public HashSet<String> getFonctionsAppelees() {
        return fonctionsAppelees;
    }
}
