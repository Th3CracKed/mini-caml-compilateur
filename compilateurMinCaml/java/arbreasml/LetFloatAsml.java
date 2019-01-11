package arbreasml;

import util.MyCompilationException;
import visiteur.ObjVisiteurAsml;
import visiteur.VisiteurAsml;

/**
 * Noeud ASML LetFloat (déclaration de label correspondant à un nombre flottant)
 */
public class LetFloatAsml extends FunDefAsml {
    private final float valeur;
    
    /**
     * Créé un noeud ASML LetFloat avec pour label label et pour valeur valeur
     * @param label le label du nombre flottant
     * @param valeur la valeur du nombre flottant
     * @throws MyCompilationException si valeur vaut NaN, moins l'infini ou plus l'infini
     */
    public LetFloatAsml(String label, float valeur) {
        super(label);        
        this.valeur = valeur;
        Float valeurFloat = valeur;
        boolean estNaN = valeurFloat.isNaN();
        boolean estInfini = valeurFloat.isInfinite();
        if(estNaN || estInfini)
        {            
            String strValeur = null;
            if(estNaN)
            {
                strValeur = "NaN";
            }
            else
            {
                strValeur = ((valeurFloat >= 0)?"plus":"moins")+" l'infini";
            }
            throw new MyCompilationException(strValeur+" n'est pas une valeur valide pour un nombre flottant en ASML");
        }
    }

    /**
     * Renvoie la valeur du nombre flottant
     * @return la valeur du nombre flottant
     */
    public float getValeur() {
        return valeur;
    }
    
    @Override
    public void accept(VisiteurAsml v) {
        v.visit(this);
    }

    @Override
    public <E> E accept(ObjVisiteurAsml<E> v) {
        return v.visit(this);
    }
}
