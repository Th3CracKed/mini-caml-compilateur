package backend;

import arbreasml.*;
import java.util.HashMap;
import java.util.Stack;
import util.Constantes;
import visiteur.*;

/** associe a chaque variable locales (et à chaque parametre de fonction) la valeur du decalage par rapport à la valeur du registre FP dans leur environnement **/
public class VisiteurRegistrePile implements VisiteurAsml {

    private int decalage;
    private final Stack<EmplacementMemoire> anciensDecalages;
    
    private final HashMap<String, EmplacementMemoire> emplacementsVar;
    
    public HashMap<String, EmplacementMemoire> getEmplacementsVar()
    {
        return emplacementsVar;
    }
    
    public VisiteurRegistrePile()
    {
        remettreDecalageAZero();
        anciensDecalages = new Stack<>();
        emplacementsVar = new HashMap<>();
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
        emplacementsVar.put(e.getIdString(), new AdressePile(decalageSuivant()));      
        e.getE1().accept(this);
        e.getE2().accept(this);
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
    public void visit(FunDefConcreteAsml e)
    {
        remettreDecalageAZero();
        for(int i = 0 ; i < e.getArguments().size() ; i++)
        {
            EmplacementMemoire emplacement = null;
            if(i<Constantes.REGISTRES_PARAMETRES.length)
            {
                emplacement = new Registre(Constantes.REGISTRES_PARAMETRES[i]);
            }
            else
            {
                emplacement = new AdressePile((e.getArguments().size()-i+Constantes.REGISTRE_SAUVEGARDES_APPELE.length)*Constantes.TAILLE_MOT_MEMOIRE);
            }
            emplacementsVar.put(e.getArguments().get(i).getIdString(), emplacement);
        }
        e.getAsmt().accept(this);
    }
    
    @Override
    public void visit(IfGEIntAsml e) {
        visitIfIntWorker(e);
    }

    private void visitIfWorker(IfAsml e)
    {
        e.getE1().accept(this);
    }
    
    private void visitIfIntWorker(IfIntAsml e)
    {
        visitIfWorker(e);
        e.getE2().accept(this);         
        sauvegarderDecalage(); 
        e.getESiVrai().accept(this);
        restaurerDecalage();
        sauvegarderDecalage();
        e.getESiFaux().accept(this);
        restaurerDecalage();
    }   
}
