package backend;

import arbreasml.*;
import frontend.EnvironnementClosure;
import java.util.HashMap;
import java.util.Stack;
import util.Constantes;
import visiteur.VisiteurAsml;

public class VisiteurAllocationRegistreLinearScan implements VisiteurAsml {
    private Environnement environnement;
    private final Stack<Environnement> anciensEnvironnements;
    private final HashMap<String, EmplacementMemoire> emplacementsVar;
    private final HashMap<String, EnvironnementClosure> closures;
    
    public HashMap<String, EmplacementMemoire> getEmplacementsVar()
    {
        return emplacementsVar;
    }
    
    public VisiteurAllocationRegistreLinearScan(HashMap<String, EnvironnementClosure> closures)
    {
        anciensEnvironnements = new Stack<>();
        emplacementsVar = new HashMap<>();     
        this.closures = closures;
        viderEnvironnement();
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
    
    @Override
    public void visit(LetAsml e) {
        //LA TAILLE DE L'ENVIRONNEMENT NE SE CALCULE PLUS DE LA MEME MANIERE DANS LA GENERATION DE CODE ARM; visiteurTailleEnvironnement implement Visiteur et non objVisiteur    
        sauvegarderEnvironnement(); 
        e.getE1().accept(this);
        restaurerEnvironnement();            
        emplacementsVar.put(e.getIdString(), environnement.emplacementSuivant()); 
        sauvegarderEnvironnement(); 
        e.getE2().accept(this);
        restaurerEnvironnement();
    }

    @Override
    public void visit(FunDefConcreteAsml e)
    {
        int nbParametresSupplementaires = 0;
        if(closures.containsKey(e.getLabel()))
        {
            nbParametresSupplementaires = 1;
            emplacementsVar.putIfAbsent(Constantes.SELF_ASML, new Registre(Constantes.REGISTRES_PARAMETRES[0])); 
        }
        //remettreDecalageAZero();
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
                emplacement = new AdressePile((e.getArguments().size()-j+Constantes.REGISTRE_SAUVEGARDES_APPELE.length)*Constantes.TAILLE_MOT_MEMOIRE);
            }
            emplacementsVar.put(e.getArguments().get(i).getIdString(), emplacement);
        }
        e.getAsmt().accept(this);
    }
    
    /*@Override
    public void visit(IfEqIntAsml e) {
        visitIfIntWorker(e);
    }

    @Override
    public void visit(IfLEIntAsml e) {
        visitIfIntWorker(e);
    }
    
    @Override
    public void visit(IfGEIntAsml e) {
        visitIfIntWorker(e);
    }

    private void visitDebutIfWorker(IfAsml e)
    {
        e.getE1().accept(this);
    }
    
    private void visitFinIfWorker(IfAsml e)
    {
        sauvegarderEnvironnement(); 
        e.getESiVrai().accept(this);
        restaurerEnvironnement();
        sauvegarderEnvironnement();
        e.getESiFaux().accept(this);
        restaurerEnvironnement();
    }
    
    private void visitIfIntWorker(IfIntAsml e)
    {
        visitDebutIfWorker(e);
        e.getE2().accept(this);         
        visitFinIfWorker(e);
    }   
    
    private void visitIfFloatWorker(IfFloatAsml e)
    {
        visitDebutIfWorker(e);
        e.getE2().accept(this);         
        visitFinIfWorker(e);
    }  
    
    @Override
    public void visit(IfEqFloatAsml e) {
        visitIfFloatWorker(e);
    }

    @Override
    public void visit(IfLEFloatAsml e) {
        visitIfFloatWorker(e);
    } */
}
