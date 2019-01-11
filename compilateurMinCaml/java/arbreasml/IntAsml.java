package arbreasml;

import util.MyCompilationException;
import visiteur.ObjVisiteurAsml;
import visiteur.VisiteurAsml;

/**
 * Noeud ASML correspondant aux valeurs entières. Cette classe justifie l'utilisation d'interfaces pour certains NoeudAsml (ExpAsml, VarOuIntAsml,...) car l'héritage multiple
 * n'existe pas en Java.
 */
public class IntAsml extends ValeurAsml<Integer> implements VarOuIntAsml {

    /**
     * Créé un noeud ASML Int avec pour valeur valeur
     * @param valeur la valeur de l'entier
     * @throws MyCompilationException si valeur est strictement négative
     */
    public IntAsml(int valeur) {
        super(valeur);
        if(valeur < 0)
        {
            throw new MyCompilationException("Utiliser un noeud neg pour le moins unaire");
        }
    }

    /**
     * Créé et renvoie un noeud IntAsml correspondant à la constante entière remplaçant le booléen vrai du MinCaml
     * @return le noeud IntAsml correspondant à la constante entière remplaçant le booléen vrai
     */
    public static IntAsml vrai()
    {
        return new IntAsml(1);
    }
    
    /**
     * Créé et renvoie un noeud IntAsml correspondant à la constante entière remplaçant le booléen faux du MinCaml
     * @return le noeud IntAsml correspondant à la constante entière remplaçant le booléen faux
     */
    public static IntAsml faux()
    {
        return new IntAsml(0);
    }
    
    /**
     * Créé et renvoie un noeud IntAsml correspondant à la constante entière remplaçant la valeur nil du MinCaml
     * @return le noeud IntAsml correspondant à la constante entière remplaçant le booléen vrai
     */
    public static IntAsml nil() {
        return new IntAsml(0);
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
