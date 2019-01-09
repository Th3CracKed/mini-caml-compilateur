package backend;

import arbreasml.*;
import frontend.EnvironnementClosure;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import util.Constantes;
import visiteur.VisiteurAsml;

public class VisiteurAllocationRegistreLinearScan implements VisiteurAsml {
    private Environnement environnement;
    private final Stack<Environnement> anciensEnvironnements;
    private final HashMap<String, EmplacementMemoire> emplacementsVar;
    private final HashMap<String, EnvironnementClosure> closures;
    private boolean estDansUnIf;
    
    public HashMap<String, EmplacementMemoire> getEmplacementsVar()
    {
        return emplacementsVar;
    }
    
    public VisiteurAllocationRegistreLinearScan(HashMap<String, EnvironnementClosure> closures)
    {
        anciensEnvironnements = new Stack<>();
        emplacementsVar = new HashMap<>();     
        this.closures = closures;
        setEstDansUnIf(false);
        viderEnvironnement();
    }
    
    private void setEstDansUnIf(boolean estDansUnIf) {
        this.estDansUnIf = estDansUnIf;
    }
    
    private void viderEnvironnement()
    {
        environnement = new Environnement();
    }
    
    private void sauvegarderEnvironnement()
    {
        anciensEnvironnements.push((Environnement)environnement.clone());
    }
    
    private void restaurerEnvironnement()
    {
        environnement = anciensEnvironnements.pop();
    }
    
    private void allouerEmplacemment(String idString)
    {
        emplacementsVar.put(idString, environnement.emplacementSuivant(idString)); 
    }
    
    @Override
    public void visit(LetAsml e) {   
        sauvegarderEnvironnement(); 
        e.getE1().accept(this);
        restaurerEnvironnement();            
        sauvegarderEnvironnement(); 
        System.out.println("\nALLOUER "+e.getIdString()+" : "+emplacementsVar);
        allouerEmplacemment(e.getIdString());
        AsmtAsml e2 = e.getE2();
        if(!estDansUnIf)
        {
            VisiteurVariablesUtiliseesAsml visVarUtilisees = new VisiteurVariablesUtiliseesAsml();
            e2.accept(visVarUtilisees);
            List<String> idStringVariables = new ArrayList<>(environnement.getVariablesAllouees().keySet());
            for(String idStringVarDansRegistre : idStringVariables)
            {
                if(!visVarUtilisees.getVariablesUtilisees().contains(idStringVarDansRegistre))
                {
                    System.out.println("\ndans let "+e.getIdString()+" : liberer "+idStringVarDansRegistre);
                    environnement.libererEmplacement(idStringVarDansRegistre);
                }
            }
        }
        e2.accept(this);
        restaurerEnvironnement();
    }

    @Override
    public void visit(FunDefConcreteAsml e)
    {
        viderEnvironnement();
        int nbParametresSupplementaires = 0;
        if(closures.containsKey(e.getLabel()))
        {
            nbParametresSupplementaires = 1;
            emplacementsVar.putIfAbsent(Constantes.SELF_ASML, new Registre(Constantes.REGISTRES_PARAMETRES[0])); 
        }
        for(int i = 0 ; i < e.getArguments().size() ; i++)
        {
            int j = i + nbParametresSupplementaires;
            EmplacementMemoire emplacement = null;
            if(j<Constantes.REGISTRES_PARAMETRES.length)
            {
                emplacement = new Registre(Constantes.REGISTRES_PARAMETRES[j]);
            }
            else
            {
                emplacement = new AdresseMemoire((e.getArguments().size()-j+Constantes.REGISTRE_SAUVEGARDES_APPELE.length)*Constantes.TAILLE_MOT_MEMOIRE);
            }
            emplacementsVar.put(e.getArguments().get(i).getIdString(), emplacement);
        }
        e.getAsmt().accept(this);
    }
    
    @Override
    public void visit(IfEqIntAsml e) {
        visitIfWorker(e);
    }

    @Override
    public void visit(IfLEIntAsml e) {
        visitIfWorker(e);
    }
    
    @Override
    public void visit(IfGEIntAsml e) {
        visitIfWorker(e);
    }
    
    private void visitIfWorker(IfAsml e)
    {
        setEstDansUnIf(true);
        sauvegarderEnvironnement(); 
        e.getESiVrai().accept(this);
        restaurerEnvironnement();
        sauvegarderEnvironnement();
        e.getESiFaux().accept(this);
        restaurerEnvironnement();
        setEstDansUnIf(false);
    }  
    
    @Override
    public void visit(IfEqFloatAsml e) {
        visitIfWorker(e);
    }

    @Override
    public void visit(IfLEFloatAsml e) {
        visitIfWorker(e);
    } 
    
    public class VisiteurVariablesUtiliseesAsml implements VisiteurAsml
    {
        private final HashSet<String> variablesUtilisees;
        
        public VisiteurVariablesUtiliseesAsml()
        {
            variablesUtilisees = new HashSet<>();
        }

        public HashSet<String> getVariablesUtilisees() {
            return variablesUtilisees;
        }
        
        @Override
        public void visit(VarAsml e)
        {
            getVariablesUtilisees().add(e.getIdString());
        }
    }
}
