package backend;

import arbreasml.*;
import frontend.EnvironnementClosure;
import java.util.HashMap;
import util.MyCompilationException;

/**
 * Visiteur allouant un registre par variable et levant une exception pour les programmes avec plus de variables locales que de registres reservés aux variables locales
 */
public class VisiteurAllocationRegistreSimple extends VisiteurAllocationRegistre {
    private int indiceRegistreVarSuivant;
    
    /**
     * Visiteur allouant un registre par variable et levant une exception pour les programmes avec plus de variables locales que de registres reservés aux variables locales
     * @param closures les closures du programme
     */
    public VisiteurAllocationRegistreSimple(HashMap<String, EnvironnementClosure> closures)
    {
        super(closures);
        indiceRegistreVarSuivant = 0;
    }

    /**
     * Visite le noeud e. Dans ce cas, alloue le prochain registre on alloué à la variable déclarée puis visite e1 et e2.
     * @param e le noeud à visiter 
     */
    @Override
    public void visit(LetAsml e) {
        Registre registre = new Registre(registreVarSuivant());
        getEmplacementsVar().put(e.getIdString(), registre);        
        e.getE1().accept(this);
        e.getE2().accept(this);
    }
    
    /**
     * Si il y a au moins un registre non alloué, renvoie le numéro du prochain registre non alloué et le marque comme alloué, sinon lève une exception
     * @return le numéro du prochain registre non alloué
     * @throws MyCompilationException si tous les registres sont déjà alloués
     */
    private int registreVarSuivant(){
        if(indiceRegistreVarSuivant == REGISTRES_VARIABLES_LOCALES[REGISTRES_VARIABLES_LOCALES.length-1])
        {
            throw new MyCompilationException("Les programmes avec plus de "+REGISTRES_VARIABLES_LOCALES.length+" variable(s) locale(s) ne sont pas supportés");
        }
        int registre = REGISTRES_VARIABLES_LOCALES[indiceRegistreVarSuivant];
        indiceRegistreVarSuivant++;
        return registre;
    }

}
