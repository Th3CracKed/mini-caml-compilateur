package backend;

import java.util.HashMap;
import util.Constantes;
import util.MyCompilationException;

public class Environnement implements Cloneable
{
    private int decalage;
    private HashMap<Integer,Boolean> registresAlloues;
    private HashMap<Integer,Boolean> adressesAllouees;
    private HashMap<String, Integer> variablesAllouees;
    private static final int[] REGISTRES_VARIABLES_LOCALES = new int[] {6,8,9,10,12}; // les registres R4 a R12 sont reservees aux variables locales mais R4, R5 et R7 sont utilise comme registres temporaires pour les instructions comme ADD et R11 est utilise comme Frame Pointer (FP)
    
    public Environnement()
    {
        setDecalage(0);
        setRegistresAlloues(new HashMap<>());    
        setVariablesAllouees(new HashMap<>());
        setAdressesAllouees(new HashMap<>());
        for(int registreVariableLocale : REGISTRES_VARIABLES_LOCALES)
        {
            registresAlloues.put(registreVariableLocale, false);
        }  
    }
    
    public EmplacementMemoire emplacementSuivant(String idString)
    {
        for(int numRegistre : registresAlloues.keySet())
        {
            if(!registresAlloues.get(numRegistre))
            {
                registresAlloues.put(numRegistre, true);
                // les parametres de fonction ne sont pas alloues dans cette classe (ils le sont dans le visiteur), un decalage (inferieurs ou egal a 0) et un numeros de 
                // registre (strictement positifs) ne peuvent donc pas etre egaux donc on peut representer les decalages et les registre par leur valeur sont ambiguite 
                // (par exemple 8 represente R8 et -4 representer FP-4)
                variablesAllouees.put(idString, numRegistre);
                return new Registre(numRegistre);
            }
        }
        for(int decalageCourant : adressesAllouees.keySet())
        {
            if(!adressesAllouees.get(decalageCourant))
            {
                adressesAllouees.put(decalageCourant, true);
                variablesAllouees.put(idString, decalageCourant);
                return new AdresseMemoire(decalageCourant);
            }
        }
        int dernierDecalage = decalage;
        setDecalage(dernierDecalage-Constantes.TAILLE_MOT_MEMOIRE); 
        adressesAllouees.put(dernierDecalage, true);
        variablesAllouees.put(idString, dernierDecalage);
        return new AdresseMemoire(dernierDecalage);
    }  
    
    public void libererEmplacement(String idString)
    {
        int numRegOuDecalage = variablesAllouees.remove(idString);
        Boolean estRegistreAlloue = registresAlloues.get(numRegOuDecalage);
        if(estRegistreAlloue != null) // la variable est allouee dans un registre
        {
            registresAlloues.put(numRegOuDecalage, false);
        }
        else // la variable est allouee sur la pile
        {
            adressesAllouees.put(numRegOuDecalage, false);
        }
    }
    
    public HashMap<String, Integer> getVariablesAllouees() {
        return variablesAllouees;
    }
        
    public final void setVariablesAllouees(HashMap<String, Integer> variablesAllouees) {
        this.variablesAllouees = variablesAllouees;
    }
    
    private void setDecalage(int decalage) {
        this.decalage = decalage;
    }
    
    public final void setRegistresAlloues(HashMap<Integer,Boolean> registresAlloues) {
        this.registresAlloues = registresAlloues;
    }
    
    public final void setAdressesAllouees(HashMap<Integer,Boolean> adressesAllouees) {
        this.adressesAllouees = adressesAllouees;
    }
    
    @Override
    public Environnement clone()
    {
        Environnement env = null;
        try {
            env = (Environnement)super.clone();
            env.setRegistresAlloues((HashMap<Integer,Boolean>)registresAlloues.clone());
            env.setAdressesAllouees((HashMap<Integer,Boolean>)adressesAllouees.clone());
            env.setVariablesAllouees((HashMap<String,Integer>)variablesAllouees.clone());
            return env;
        } catch (CloneNotSupportedException ex) {
            throw new MyCompilationException("Erreur lors de l'appel à la méthode clone de la classe Environnement");
        }
    }
}
