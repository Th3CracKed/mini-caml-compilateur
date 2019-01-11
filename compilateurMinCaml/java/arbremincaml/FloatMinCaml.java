package arbremincaml;

import util.MyCompilationException;
import visiteur.ObjVisitor;
import visiteur.Visitor;

public class FloatMinCaml extends Valeur<Float> {
    
    /**
     * Créé un noeud MinCaml correspondant à la valeur immédiate de nombre flottant f. Cette classe s'appelaient initialement Float mais a été renommé pour éviter
     * la confusion avec la classe Float disponible dans le package java.lang (et donc utilisable sous le nom de Float sans l'importer)
     * @param f la valeur du nombre flottant
     * @throws MyCompilationException si f vaut NaN, moins l'infini ou plus l'infini
     */
    public FloatMinCaml(float f) {
        super(f);   
        Float valeurFloat = f;
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
            throw new MyCompilationException(strValeur+" n'est pas une valeur valide pour un nombre flottant en MinCaml");
        }
    }

    @Override
    public <E> E accept(ObjVisitor<E> v) {
        return v.visit(this);
    }
    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}