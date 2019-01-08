package backend;

import java.util.HashMap;
import util.Constantes;
import util.MyCompilationException;

public class Environnement implements Cloneable
{
    private int decalage;
    private int nbRegistreDisponibles;
    private HashMap<Integer,Boolean> registresAlloues;
    private static final int[] REGISTRES_VARIABLES_LOCALES = new int[] {6,8,9,10,12}; // les registres R4 a R12 sont reservees aux variables locales mais R4, R5 et R7 sont utilise comme registre temporaire pour les instructions comme ADD et R11 est utilise comme Frame Pointer (FP)
    
    public Environnement()
    {
        setDecalage(0);
        setNbRegistreDisponibles(REGISTRES_VARIABLES_LOCALES.length);
        setRegistresAlloues(new HashMap<>());    
        for(int registreVariableLocale : REGISTRES_VARIABLES_LOCALES)
        {
            registresAlloues.put(registreVariableLocale, false);
        }      
    }
    
    public EmplacementMemoire emplacementSuivant()
    {
        EmplacementMemoire emplacement = null;
        if(nbRegistreDisponibles >= 1)
        {
            for(int numRegistre : registresAlloues.keySet())
            {
                if(!registresAlloues.get(numRegistre))
                {
                    emplacement = new Registre(numRegistre);
                    registresAlloues.put(numRegistre, true);
                    setNbRegistreDisponibles(nbRegistreDisponibles - 1);
                    break;
                }
            }
        }
        else
        {
            int dernierDecalage = decalage;
            setDecalage(dernierDecalage-Constantes.TAILLE_MOT_MEMOIRE); 
            emplacement = new AdressePile(dernierDecalage);
        }
        return emplacement;
    }  
    
    private void setDecalage(int decalage) {
        this.decalage = decalage;
    }
    
    private void setRegistresAlloues(HashMap<Integer,Boolean> registresAlloues) {
        this.registresAlloues = registresAlloues;
    }

    private void setNbRegistreDisponibles(int nbRegistreDisponibles) {
        this.nbRegistreDisponibles = nbRegistreDisponibles;
    }
    
    @Override
    public Environnement clone()
    {
        Environnement env = null;
        try {
            env = (Environnement)super.clone();
            env.setRegistresAlloues((HashMap<Integer,Boolean>)registresAlloues.clone());
            return env;
        } catch (CloneNotSupportedException ex) {
            throw new MyCompilationException("Erreur lors de l'appel à la méthode clone de la classe Environnement");
        }
    }
}
