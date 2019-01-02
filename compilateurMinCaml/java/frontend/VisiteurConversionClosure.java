package frontend;

import arbremincaml.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
        return closures;
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
        String idString = id.getIdString();
        VisiteurFonctionsDOrdreSuperieur vFunOrdreSup = new VisiteurFonctionsDOrdreSuperieur(idString);
        e.accept(vFunOrdreSup);
        boolean estUneFonctionDOrdreSuperieur = vFunOrdreSup.getEstUneFonctionDOrdreSuperieur();        
        if(!variablesLibres.isEmpty() || estUneFonctionDOrdreSuperieur)
        {
            getClosures().put(idString, new EnvironnementClosure(variablesLibres));
        }
    }
    
    private class VisiteurVariablesLibres implements Visitor
    {
        private final HashSet<String> variablesLibres;
        private final HashSet<String> variablesLiees;
        
        public VisiteurVariablesLibres(HashSet<String> variablesLiees)
        {
            variablesLibres = new HashSet<>();
            this.variablesLiees = variablesLiees; 
        }

        public HashSet<String> getVariablesLibres()
        {
            return variablesLibres;
        }
        
        @Override
        public void visit(Var e)
        {
            String idString = e.getId().getIdString();
            if(!variablesLiees.contains(idString) && !Constantes.FONCTION_EXTERNES_MINCAML.contains(idString) && !Id.estUnLabel(idString))
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
            variablesLiees.addAll(idsStringParametres);            
            funDef.getE().accept(this);
            variablesLiees.removeAll(idsStringParametres);
            e.getE().accept(this);
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
