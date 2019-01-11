package arbremincaml;

import java.math.BigInteger;
import java.util.HashSet;
import util.Constantes;
import util.MyCompilationException;

/** 
 * Identificant de variable, de fonction ou de nombre flottant en MinCaml et en ASML
 */
public class Id {
    private String idString;
    private static final String DEBUT_LABEL_GENERE_FONCTION_ASML = Constantes.DEBUT_LABEL_ASML+"f";
    private static final String DEBUT_LABEL_GENERE_FLOAT_ASML = Constantes.DEBUT_LABEL_ASML+"float";    
    private static final HashSet<String> ID_UTILISES = new HashSet<>();   
    static
    {
        ID_UTILISES.addAll(Constantes.FONCTION_EXTERNES_MINCAML);
        ID_UTILISES.addAll(Constantes.MOTS_RESERVES_ASML);
        ID_UTILISES.addAll(Constantes.FONCTION_EXTERNES_ASML);
        ID_UTILISES.addAll(Constantes.FONCTION_EXTERNES_ARM);
        ID_UTILISES.addAll(Constantes.LABELS_PRIVES_ARM);
        ID_UTILISES.add(Constantes.NOM_FONCTION_MAIN_ASML);
        ID_UTILISES.add(Constantes.NOM_FONCTION_MAIN_ARM);
    }
    
    /**
     * Créé un identifiant de variable, de fonction ou de nombre flottant identifié par la chaine idString
     * @param idString la chaîne de caractère identifiant la variable, la fonction ou le nombre flottant 
     */
    public Id(String idString) {
        this.setIdString(idString);
    }
    
    /**
     * Renvoie la représentation de l'identifiant sous forme de chaîne
     * @return la représentation de l'identifiant sous forme de chaîne
     */
    @Override
    public String toString() {
        return idString;
    }
    
    /**
     * Renvoie la chaîne de caractère identifiant la variable, la fonction ou le nombre flottant 
     * @return la chaîne de caractère identifiant la variable, la fonction ou le nombre flottant 
     */
    public String getIdString()
    {
        return idString;
    }
    
    /**
     * Assigne la valeur idString à la chaîne de caractère identifiant la variable, la fonction ou le nombre flottant 
     * @param idString la nouvelle valeur de la chaîne de caractère identifiant la variable, la fonction ou le nombre flottant 
     */
    public final void setIdString(String idString) {        
        if(idString == null)
        {
            throw new MyCompilationException("Un id de variable ne peut pas être null");
        }
        this.idString = idString;
        ID_UTILISES.add(idString);
    }
        
    private static BigInteger x = BigInteger.ONE.negate(); // le compteur servant à générer un identifiant de variable est un BigInteger et non un type comme int ou long qui ont une valeur maximale (même si il est peut probable de l'atteindre)
    
    /**
     * Génère et renvoie un identifiant de variable qui n'a pas encore été attribué
     * @return le nouvel identifiant de variable
     */
    public static Id gen() {
        return new Id(genIdString());
    }
    
    /**
     * Génère et renvoie une chaine commençant par prefixe et qui n'a pas encore été attribué pour identifier une variable, une fonction ou un nombre flottant 
     * @param prefixe le préfixe par lequelle la chaîne renvoyer doit commencer
     * @return la nouvelle chaîne identifiant la variable, la fonction ou le nombre flottant 
     */
    public static String genIdStringAvecPrefixe(String prefixe)
    {
        String idString = null;
        do
        {            
            x = x.add(BigInteger.ONE);
            idString = prefixe + x;
        }while(ID_UTILISES.contains(idString));  
        ID_UTILISES.add(idString);      
        return idString;
    }
    
    /**
     * Génère et renvoie une chaine n'ayant pas encore été attribué commençant par le préfixe indiquant qu'il s'agit d'un nom de variable généré par le compilateur 
     * @return la nouvelle chaîne identifiant la variable
     */
    public static String genIdString()
    {
         return genIdStringAvecPrefixe("v");
    }
    
     /**
     * Génère et renvoie une chaine n'ayant pas encore été attribué commençant par le préfixe indiquant qu'il s'agit d'un nom de fonction généré par le compilateur 
     * @return la nouvelle chaîne identifiant la fonction
     */
    public static String genLabelFonction()
    {
        return genIdStringAvecPrefixe(DEBUT_LABEL_GENERE_FONCTION_ASML);
    }
    
    /**
     * Génère et renvoie une chaine n'ayant pas encore été attribué commençant par le préfixe indiquant qu'il s'agit d'un nom de nombre flottant généré par le compilateur 
     * @return la nouvelle chaîne identifiant le nombre flottant
     */
    public static String genLabelFloat()
    {
        return genIdStringAvecPrefixe(DEBUT_LABEL_GENERE_FLOAT_ASML);
    }
    
    /**
     * Renvoie vrai si la chaine idString est un label en ASML (c'est-à-dire si il commence par un _) et faux sinon
     * @param idString la chaîne pour laquelle la méthode indique si elle est un label
     * @return vrai si la chaine idString est un label en ASML (c'est-à-dire si il commence par un _) et faux sinon 
     */
    public static boolean estUnLabel(String idString)
    {
        return idString.startsWith(Constantes.DEBUT_LABEL_ASML);
    }

}
