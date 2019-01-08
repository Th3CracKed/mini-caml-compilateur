package backend;

import arbreasml.*;
import frontend.EnvironnementClosure;
import java.util.HashMap;
import java.util.Stack;
import util.Constantes;
import visiteur.*;

public class VisiteurRegistrePile implements VisiteurAsml {

    private int decalage;
    private final Stack<EmplacementMemoire> anciensDecalages;
    
    private final HashMap<String, EmplacementMemoire> emplacementsVar;
    private final HashMap<String, EnvironnementClosure> closures;
    
    public HashMap<String, EmplacementMemoire> getEmplacementsVar()
    {
        return emplacementsVar;
    }
    
    public VisiteurRegistrePile(HashMap<String, EnvironnementClosure> closures)
    {
        remettreDecalageAZero();
        anciensDecalages = new Stack<>();
        emplacementsVar = new HashMap<>();
        this.closures = closures;
    }
    
    private void remettreDecalageAZero()
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
    
    private int decalageSuivant()
    {
        int dernierDecalage = decalage;
        decalage -= Constantes.TAILLE_MOT_MEMOIRE;
        return dernierDecalage;
    }

    @Override
    public void visit(LetAsml e) {
        sauvegarderDecalage(); 
        e.getE1().accept(this);
        restaurerDecalage();        
        sauvegarderDecalage(); 
        emplacementsVar.put(e.getIdString(), new AdressePile(decalageSuivant())); 
        e.getE2().accept(this);
        restaurerDecalage();
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
        remettreDecalageAZero();
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
        /*int indEntier = nbParametresSupplementaires;
        int indFloat = 0;
        for(int i = 0 ; i < e.getArguments().size() ; i++)
        {
            int indParametre = indEntier+indFloat;
            EmplacementMemoire emplacement = null;
            if(variablesFloat.contains(e.getArguments().get(i).getIdString()))
            {
                if(indFloat<Constantes.REGISTRES_PARAMETRES.length)
                {
                    emplacement = new Registre(Constantes.REGISTRES_PARAMETRES[indFloat]);
                }
                else
                {
                    emplacement = new AdressePile((e.getArguments().size()-indParametre+Constantes.NB_REGISTRES_SAUVEGARDE_APPELE)*Constantes.TAILLE_MOT_MEMOIRE);
                }
                indFloat++;
            }
            else
            {
                if(indEntier<Constantes.REGISTRES_PARAMETRES.length)
                {
                    emplacement = new Registre(Constantes.REGISTRES_PARAMETRES[indEntier]);
                }
                else
                {
                    emplacement = new AdressePile((e.getArguments().size()-indParametre+Constantes.NB_REGISTRES_SAUVEGARDE_APPELE)*Constantes.TAILLE_MOT_MEMOIRE);
                }
                indEntier++;
            }
            emplacementsVar.put(e.getArguments().get(i).getIdString(), emplacement);
        }
        */
        e.getAsmt().accept(this);
    }
    
    @Override
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
    }
}
