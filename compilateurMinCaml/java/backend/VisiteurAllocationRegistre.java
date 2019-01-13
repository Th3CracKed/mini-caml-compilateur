package backend;

import arbreasml.FunDefConcreteAsml;
import frontend.EnvironnementClosure;
import java.util.HashMap;
import util.Constantes;
import visiteur.VisiteurAsml;

/**
 * Classe mère des visiteurs réalisant l'allocation de registre permettant de factoriser l'allocation des paramètres de fonction qui est toujours la même
 * (elle est fixée par les conventions d'appel)
 */
public abstract class VisiteurAllocationRegistre implements VisiteurAsml
{
    private final HashMap<String, EmplacementMemoire> emplacementsVar;       
    private final HashMap<String, EnvironnementClosure> closures;
    // les registres R4 a R12 sont reservees aux variables locales mais R4, R5 et R7 sont utilise comme registres temporaires pour les instructions comme ADD et R11 
    // est utilise comme Frame Pointer (FP)
    public static final int[] REGISTRES_VARIABLES_LOCALES = new int[] {6,8,9,10,12};
    
    /**
     * Créé un visiteur réalisant l'allocation de registre. Il faut qu'il connaissant les closures du programme car les fonctions appelées avec call_closure
     * ont un paramètre supplémentaire (implicite en ASML mais qu'il faut explicitement passer à la fonction en ARM) %self
     * @param closures les closures du programme ASML
     */
    public VisiteurAllocationRegistre(HashMap<String, EnvironnementClosure> closures)
    {
        emplacementsVar = new HashMap<>();
        this.closures = closures;
    }
    
    /**
     * Renvoie une table de hachage associant aux identifiants de variables l'emplacement mémoire dans lequel la variable est allouée
     * @return une table de hachage associant aux identifiants de variables l'emplacement mémoire dans lequel la variable est allouée
     */
    public HashMap<String, EmplacementMemoire> getEmplacementsVar()
    {
        return emplacementsVar;
    }
    
    /**
     * Visite le noeud e. Dans ce cas, alloue pour le paramètre de numéro i (en numérotant les paramètres à partir de 0) le registre Ri si i 
     * est inférieur ou égal 3 et l'adresse FP+(n-i)*4 sinon (n est le nombre de paramètres). Les fonctions appelées avec call_closure ont un paramètre supplémentaire 
     * (implicite en ASML mais qu'il faut explicitement passer à la fonction en ARM) %self qu'il faut prendre en compte
     * @param e le noeud à visiter
     */
    @Override
    public void visit(FunDefConcreteAsml e)
    {
        int nbParametresSupplementaires = 0;
        if(closures.containsKey(e.getLabel()))
        {
            nbParametresSupplementaires = 1;
            getEmplacementsVar().putIfAbsent(Constantes.SELF_ASML, new Registre(Constantes.REGISTRES_PARAMETRES[0])); 
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
            getEmplacementsVar().put(e.getArguments().get(i).getIdString(), emplacement);
        }
        e.getAsmt().accept(this);
    }
}
