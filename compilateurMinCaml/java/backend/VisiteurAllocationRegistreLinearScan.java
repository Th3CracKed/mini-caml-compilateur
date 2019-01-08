package backend;

import arbreasml.*;
import frontend.EnvironnementClosure;
import java.util.HashMap;
import java.util.Stack;
import util.Constantes;
import visiteur.VisiteurAsml;

public class VisiteurAllocationRegistreLinearScan implements VisiteurAsml {
    private int decalage;
    private final Stack<EmplacementMemoire> anciensDecalages;
    private final HashMap<String, EmplacementMemoire> emplacementsVar;
    private final HashMap<String, EnvironnementClosure> closures;
    
    public HashMap<String, EmplacementMemoire> getEmplacementsVar()
    {
        return emplacementsVar;
    }
    
    public VisiteurAllocationRegistreLinearScan(HashMap<String, EnvironnementClosure> closures)
    {
        //remettreDecalageAZero();
        anciensDecalages = new Stack<>();
        emplacementsVar = new HashMap<>();
        this.closures = closures;
    }
    
    /*private void remettreDecalageAZero()
    {
        decalage = 0;
    }
    
    private void sauvegarderDecalage()
    {
        anciensDecalages.push(new AdressePile(decalage));
    }
    
    private void restaurerDecalage()
    {
        decalage = ((AdressePile)anciensDecalages.pop()).getDecalage();
    }
    
    private EmplacementMemoire emplacementSuivant()
    {
        int dernierDecalage = decalage;
        decalage -= Constantes.TAILLE_MOT_MEMOIRE;
        return dernierDecalage;
    }*/
    
    @Override
    public void visit(LetAsml e) {
        //emplacementsVar.put(e.getIdString(), emplacementSuivant()); 
        //sauvegarderDecalage(); 
        e.getE1().accept(this);
        //restaurerDecalage();
        //sauvegarderDecalage(); 
        e.getE2().accept(this);
        //restaurerDecalage();
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
        LA TAILLE DE L'ENVIRONNEMENT NE SE CALCULE PLUS DE LA MEME MANIERE DANS LA GENERATION DE CODE ARM
        sauvegarderDecalage(); 
        e.getESiVrai().accept(this);
        restaurerDecalage();
        sauvegarderDecalage();
        e.getESiFaux().accept(this);
        restaurerDecalage();
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
