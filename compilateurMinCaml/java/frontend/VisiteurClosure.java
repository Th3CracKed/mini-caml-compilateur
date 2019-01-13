package frontend;

import arbremincaml.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import util.Constantes;
import visiteur.Visitor;

/**
 * Visiteur déterminant les fonctions dont les appels doivent être convertis en appel de closure
 */
public class VisiteurClosure implements Visitor {
    private final HashMap<String,EnvironnementClosure> closures;   
    
    /**
     * Créé un visiteur déterminant les fonctions dont les appels doivent être convertis en appel de closure
     */
    public VisiteurClosure()
    {
        closures = new HashMap<>();
    }   
    
    /**
     * Renvoie une description fonctions (leurs variables libres et les appels de closures qu'elles effectuent) dont les appels doivent être convertis en appel de closure
     * @return une description fonctions (leurs variables libres et les appels de closures qu'elles effectuent) dont les appels doivent être convertis en appel de closure
     */
    public HashMap<String,EnvironnementClosure> getClosures()
    {
        HashMap<String,EnvironnementClosure> closuresRenvoyes = new HashMap<>(closures);
        Set<String> nomsClosures = closures.keySet();
        for(String nomFonction : nomsClosures)
        {
            EnvironnementClosure env = closures.get(nomFonction);
            for(String nomFonctionAppelee : env.getFonctionsAppelees())
            {
                if(!nomFonctionAppelee.equals(nomFonction) && closures.containsKey(nomFonctionAppelee))
                {
                    env.getVariablesLibres().add(nomFonctionAppelee);
                }
            }
        }
        return closuresRenvoyes;
    }
    
    /**
     * Visite le noeud e. Dans ce cas, si la fonction visitée a des variables libres ou est utilisée autrement qu'en l'appelant, l'ajoute à la liste des closures.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(LetRec e)
    {
        FunDef funDef = e.getFd();
        Exp eFundef = funDef.getE();
        e.getE().accept(this);
        eFundef.accept(this);
        List<Id> args = funDef.getArgs();
        Id id = funDef.getId();
        HashSet<String> idStringParametres = new HashSet<>();
        for(Id idParametre : args)
        {
            idStringParametres.add(idParametre.getIdString());
        }
        VisiteurVariablesLibres vVarLibres = new VisiteurVariablesLibres(idStringParametres);
        eFundef.accept(vVarLibres);
        List<String> variablesLibres = new ArrayList(vVarLibres.getVariablesLibres());
        variablesLibres.removeAll(idStringParametres);
        String idString = id.getIdString();
        VisiteurFonctionsDOrdreSuperieur vFunOrdreSup = new VisiteurFonctionsDOrdreSuperieur(idString);
        e.accept(vFunOrdreSup);
        boolean estUneFonctionDOrdreSuperieur = vFunOrdreSup.getEstUneFonctionDOrdreSuperieur();       
        if(!variablesLibres.isEmpty() || estUneFonctionDOrdreSuperieur)
        {
            closures.put(idString, new EnvironnementClosure(variablesLibres, vVarLibres.getFonctionsAppelees()));
        }
    }
    
    /**
     * Visiteur déterminant la liste des variables libres et des fonctions appelées par une fonction
     */
    private class VisiteurVariablesLibres implements Visitor
    {
        private final HashSet<String> variablesLibres;
        private final HashSet<String> variablesLiees;
        private final HashSet<String> fonctionsAppelees;
        
        /**
         * Créé un visiteur déterminant la liste des variables libres d'une fonction
         * @param variablesLiees les variables à considérer comme liées pour le prochain noeud visité
         */
        public VisiteurVariablesLibres(HashSet<String> variablesLiees)
        {
            variablesLibres = new HashSet<>();
            this.variablesLiees = variablesLiees; 
            this.fonctionsAppelees = new HashSet<>();
        }

        /**
         * Renvoie un ensemble contenant les variables libres du noeud auquel on a appliqué ce visiteur en premier
         * @return un ensemble contenant les variables libres du noeud auquel on a appliqué ce visiteur en premier
         */
        public HashSet<String> getVariablesLibres()
        {
            return variablesLibres;
        }
        
        /**
         * Renvoie un ensemble contenant les fonctions appelées dans noeud auquel on a appliqué ce visiteur en premier
         * @return un ensemble contenant les fonctions appelées dans noeud auquel on a appliqué ce visiteur en premier
         */
        public HashSet<String> getFonctionsAppelees() {
            return fonctionsAppelees;
        }
        
        /**
        * Visite le noeud e. Dans ce cas, si e n'est pas dans la liste des variables liées, ajoute e à la liste des variables libres
        * @param e le noeud à visiter
        */
        @Override
        public void visit(Var e)
        {
            String idString = e.getId().getIdString();
            if(!variablesLiees.contains(idString) && !Constantes.FONCTION_EXTERNES_MINCAML.contains(idString))
            {
                variablesLibres.add(idString);
            }
        }
        
        /**
        * Visite le noeud e. Dans ce cas, visite e1, ajoute la variable déclarée à la liste des variables liées, visite e2, puis enleve la variable déclarée de la
        * liste des variables liées
        * @param e le noeud à visiter
        */
        @Override
        public void visit(Let e)
        {
            e.getE1().accept(this);
            String idString = e.getId().getIdString();
            variablesLiees.add(idString);
            e.getE2().accept(this);
            variablesLiees.remove(idString);
        }
    
        /**
        * Visite le noeud e. Dans ce cas, réalise le méthode traitement que pour un noeud Let avec plusieurs variables au lieu d'une seule
        * @param e le noeud à visiter
        */
        @Override
        public void visit(LetTuple e)
        {
            e.getE1().accept(this);
            HashSet<String> idsStringComposantes = new HashSet<>();
            for(Id idComposante : e.getIds())
            {
                idsStringComposantes.add(idComposante.getIdString());
            }
            variablesLiees.addAll(idsStringComposantes);
            e.getE2().accept(this);
            variablesLiees.removeAll(idsStringComposantes);
        }
        
        /**
        * Visite le noeud e. Dans ce cas, réalise le méthode traitement que pour un noeud Let avec plusieurs variables (les paramètres sont ajoutées aux variables liées
        * pendant que le corps de la fonction est visité) au lieu d'une seule. La liste des variables liées est vidée avant et près que le corps de la fonction soit visité
        * @param e le noeud à visiter
        */
        @Override
        public void visit(LetRec e)
        {
            FunDef funDef = e.getFd();            
            HashSet<String> idsStringParametres = new HashSet<>();
            for(Id idParametre : funDef.getArgs())
            {
                idsStringParametres.add(idParametre.getIdString());
            }
            HashSet<String> anciennesVariablesLiees = new HashSet<>(variablesLiees);  
            variablesLiees.clear();
            variablesLiees.addAll(idsStringParametres);            
            funDef.getE().accept(this);
            variablesLiees.clear();
            variablesLiees.addAll(anciennesVariablesLiees);  
            variablesLibres.removeAll(anciennesVariablesLiees);
            variablesLibres.removeAll(idsStringParametres);
            e.getE().accept(this);
        }
        
        /**
        * Visite le noeud e. Dans ce cas, si le résultat de la méthode getE de e est une variable dont l'identifiant est un label de fonction, ajoute ce label
        * à la liste de ceux des fonctions appelées
        * @param e le noeud à visiter
        */
        @Override
        public void visit(App e)
        {    
            Exp fun = e.getE();
            if(fun instanceof Var)
            {
                String idString = ((Var)fun).getId().getIdString();
                if(Id.estUnLabel(idString))
                {
                    fonctionsAppelees.add(idString);
                }
                else
                {
                    fun.accept(this);
                }
            }
            for(Exp parametre : e.getEs())
            {
                parametre.accept(this);
            }
        }
    }

    /**
     * Visiteur déterminant si une fonction est utilisée autrement qu'en l'appelant
     */
    private class VisiteurFonctionsDOrdreSuperieur implements Visitor
    {
        private boolean estUneFonctionDOrdreSuperieur;
        private final String labelFonction;
        
        /**
        * Visiteur déterminant si la fonction de label labelFonction est utilisée autrement qu'en l'appelant
        * @param labelFonction le label de la fonction
        */
        public VisiteurFonctionsDOrdreSuperieur(String labelFonction)
        {
            this.labelFonction = labelFonction;
            setEstUneFonctionDOrdreSuperieur(false);
        }

        /**
         * renvoie vrai si la fonction de label labelFonction est utilisée autrement qu'en l'appelant et faux sinon
         * @return vrai si la fonction de label labelFonction est utilisée autrement qu'en l'appelant et faux sinon
         */
        public boolean getEstUneFonctionDOrdreSuperieur()
        {
            return estUneFonctionDOrdreSuperieur;
        }

        /**
         * Définit si la fonction de label labelFonction est utilisée autrement qu'en l'appelant
         * @param estUneFonctionDOrdreSuperieur le booléen indiquant si la fonction de label labelFonction est utilisée autrement qu'en l'appelant
         */
        private void setEstUneFonctionDOrdreSuperieur(boolean estUneFonctionDOrdreSuperieur)
        {
            this.estUneFonctionDOrdreSuperieur = estUneFonctionDOrdreSuperieur;
        }
        
        /**
        * Visite le noeud e. Dans ce cas, visite les paramètres de l'appel de la fonction (mais pas la fonction appelée car on cherche uniquement les
        * endroits ou la fonction est utilisée autrement qu'en l'appelant
        * @param e le noeud à visiter
        */
        @Override
        public void visit(App e)
        {            
            for(Exp parametre : e.getEs())
            {
                parametre.accept(this);
            }
        }
        
        /**
        * Visite le noeud e. Dans ce cas, si l'identifiant de e est égal à labelFonction, indique que la fonction de label labelFonction est utilisé autrement
        * qu'en l'appelant
        * @param e le noeud à visiter
        */
        @Override
        public void visit(Var e)
        {            
            if(e.getId().getIdString().equals(labelFonction))
            {
                setEstUneFonctionDOrdreSuperieur(true);
            }
        }
    }
}
