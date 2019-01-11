package backend;

import java.util.HashMap;
import util.Constantes;
import util.MyCompilationException;

/**
 * Classe représentant l'environnement courant (qui contient les variables actives et les registres et les adresses mémoire occupées par une variable active).
 * Cette classe est utilisée pour l'allocation de registre avec les algorithme linear scan et tree scan.
 */
public class Environnement implements Cloneable
{
    private int decalage;
    private HashMap<Integer,Boolean> registresAlloues;
    private HashMap<Integer,Boolean> adressesAllouees;
    private HashMap<String, Integer> variablesAllouees;
    
    /**
     * Créé un environnement vide, c'est-à-dire avec aucune variable actives et tous les registre et adresses mémoires non allouées.
     */
    public Environnement()
    {
        setDecalage(0);
        setRegistresAlloues(new HashMap<>());    
        setVariablesAllouees(new HashMap<>());
        setAdressesAllouees(new HashMap<>());
        for(int registreVariableLocale : VisiteurAllocationRegistre.REGISTRES_VARIABLES_LOCALES)
        {
            registresAlloues.put(registreVariableLocale, false);
        }  
    }
    
    /**
     * Alloue et renvoie un emplacement mémoire (un registre ou une adresse mémoire) non encore alloué et le marque comme alloué. Si un registre est disponible
     * (le cas idéal), ce registre est renvoyé. Sinon, si une adresse mémoire qui a déjà été allouée dans la fonction courante est disponible, cette adresse est renvoyée
     * (par exemple si l'adresse dans FP contient une variable qui n'est plus utilisée, on peut l'utiliser pour une autre variable de la même manière que pour les registres
     * et cela évite de devoir augmenter la taille de l'environnement de la fonction). Sinon la taille de l'environnement de la fonction est augmenté et la nouvelle adresse
     * de l'environnement est renvoyé.
     * @param idString
     * @return un emplacement mémoire (un registre ou une adresse mémoire) non encore alloué
     */
    public EmplacementMemoire emplacementSuivant(String idString)
    {
        for(int numRegistre : registresAlloues.keySet())
        {
            if(!registresAlloues.get(numRegistre))
            {
                registresAlloues.put(numRegistre, true);
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
    
    /**
     * Libère l'emplacement mémoire (adresse mémoire ou registre) contenant la variable d'identifiant idString
     * @param idString l'identifiant de la variable contenue dans l'emplacement mémoire à libérer
     */
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
    
    /**
     * Renvoie la table de hachage associant à un identifiant de variable le numéro de registre ou le décalage de l'adresse mémoire de son emplacement en mémoire.
     * Ces deux types de valeurs sont distinctes car les parametres de fonction ne sont pas alloues dans cette classe (ils le sont dans les visiteurs de l'allocation
     * de registre),un decalage est donc toujours inférieurs ou egal a 0 (les décalage strictement supérieurs à 0 sont utilisés pour les paramètres de fonctions)
     * et un numeros de registre toujours strictement positifs (R0 est utilisé pour le premier paramètre et la valeur de retour d'une fonction). Par exemple 8 représente
     * R8 et -4 représenter l'adresse mémoire FP-4.
     * @return la table de hachage associant à un identifiant de variable le numéro de registre ou le décalage de l'adresse mémoire de son emplacement en mémoire
     */
    public HashMap<String, Integer> getVariablesAllouees() {
        return variablesAllouees;
    }
        
    /**
     * Définit variablesAlloues comme la nouvelle valeur de la table de hachage associant à un identifiant de variable le numéro de registre ou le décalage de 
     * l'adresse mémoire de son emplacement en mémoire 
     * @param variablesAllouees la nouvelle valeur de la table de hachage associant à un identifiant de variable le numéro de registre ou le décalage de 
     * l'adresse mémoire de son emplacement en mémoire 
     */
    public final void setVariablesAllouees(HashMap<String, Integer> variablesAllouees) {
        this.variablesAllouees = variablesAllouees;
    }
    
    /**
     * Définit decalage comme la nouvelle valeur du décalage par rapport à FP de la dernière adresse de l'environnement de la fonction courante
     * @param decalage la nouvelle valeur du décalage par rapport à FP de la dernière adresse de l'environnement de la fonction courante
     */
    private void setDecalage(int decalage) {
        this.decalage = decalage;
    }
    
    /**
     * Définit registresAlloues comme la nouvelle valeur de la table de hachage associant à un numéro de registre vrai si il est alloué et faux sinon
     * @param registresAlloues la nouvelle valeur de la table de hachage associant à un numéro de registre vrai si il est alloué et faux sinon
     */
    public final void setRegistresAlloues(HashMap<Integer,Boolean> registresAlloues) {
        this.registresAlloues = registresAlloues;
    }
    
    /**
     * Définit adressesAllouees comme la nouvelle valeur de la table de hachage associant à un décalage par rapport à FP vrai si l'adresse FP+decalage est allouée et faux sinon
     * @param adressesAllouees la nouvelle valeur de la table de hachage associant à un décalage par rapport à FP vrai si l'adresse FP+decalage est allouée et faux sinon
     */
    public final void setAdressesAllouees(HashMap<Integer,Boolean> adressesAllouees) {
        this.adressesAllouees = adressesAllouees;
    }
    
    /**
     * Renvoie une copie de l'environnement courant (this) ou les attribut qui sont des objets (registresAlloues, adressesAllouees et variablesAllouees) sont eux aussi clonés.
     * Cela permet de sauvegarder des environnement dans les visiteurs de l'allocation de registre (si on ajoute un élément une des table de hachage du clone, il n'est pas
     * ajouté à la table de hachage correspondante de l'objet cloné).
     * @return une copie de l'environnement courant (this) ou les attribut qui sont des objets (registresAlloues, adressesAllouees et variablesAllouees) sont eux aussi clonés.
     */
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
