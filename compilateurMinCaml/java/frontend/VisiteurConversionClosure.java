package frontend;

import arbremincaml.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import util.Constantes;
import visiteur.Visitor;

public class VisiteurConversionClosure implements Visitor {
    private final HashMap<String,EnvironnementClosure> closures;   
    
    public VisiteurConversionClosure()
    {
        closures = new HashMap<>();
    }   
    
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
    
    private class VisiteurVariablesLibres implements Visitor
    {
        private final HashSet<String> variablesLibres;
        private final HashSet<String> variablesLiees;
        private final HashSet<String> fonctionsAppelees;
        
        public VisiteurVariablesLibres(HashSet<String> variablesLiees)
        {
            variablesLibres = new HashSet<>();
            this.variablesLiees = variablesLiees; 
            this.fonctionsAppelees = new HashSet<>();
        }

        public HashSet<String> getVariablesLibres()
        {
            return variablesLibres;
        }
        
        public HashSet<String> getFonctionsAppelees() {
            return fonctionsAppelees;
        }
        
        @Override
        public void visit(Var e)
        {
            String idString = e.getId().getIdString();
            if(!variablesLiees.contains(idString) && !Constantes.FONCTION_EXTERNES_MINCAML.contains(idString))
            {
                variablesLibres.add(idString);
            }
        }
        
        @Override
        public void visit(Let e)
        {
            e.getE1().accept(this);
            String idString = e.getId().getIdString();
            variablesLiees.add(idString);
            e.getE2().accept(this);
            variablesLiees.remove(idString);
        }
    
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

    private class VisiteurFonctionsDOrdreSuperieur implements Visitor
    {
        private boolean estUneFonctionDOrdreSuperieur;
        private final String labelFonction;
        
        public VisiteurFonctionsDOrdreSuperieur(String labelFonction)
        {
            this.labelFonction = labelFonction;
            setEstUneFonctionDOrdreSuperieur(false);
        }

        public boolean getEstUneFonctionDOrdreSuperieur()
        {
            return estUneFonctionDOrdreSuperieur;
        }

        private void setEstUneFonctionDOrdreSuperieur(boolean estUneFonctionDOrdreSuperieur)
        {
            this.estUneFonctionDOrdreSuperieur = estUneFonctionDOrdreSuperieur;
        }
        
        @Override
        public void visit(App e)
        {            
            for(Exp parametre : e.getEs())
            {
                parametre.accept(this);
            }
        }
        
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
