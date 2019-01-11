package arbreasml;

import visiteur.ObjVisiteurAsml;
import visiteur.VisiteurAsml;

/**
 * Noeud ASML correspondant aux accés mémoire en écriture
 */
public class MemEcritureAsml extends MemAsml {

    private final VarAsml valeurEcrite;
    
    /**
     * Créé un noeud ASML pour un accés en mémoire écrivant la valeur valeurEcrite à l'adresse tableau+4*indice
     * @param tableau l'adresse de base
     * @param indice le decalage (un indice) par rapport à l'adresse de base pour obtenir l'adresse à laquelle on accède
     * @param valeurEcrite la valeur écrite à l'adresse à laquelle on accède
     */
    public MemEcritureAsml(VarAsml tableau, VarOuIntAsml indice, VarAsml valeurEcrite) {
        super(tableau, indice);
        this.valeurEcrite = valeurEcrite; 
    }   
    
    /**
     * Renvoie la valeur écrite à l'adresse à laquelle on accède
     * @return la valeur écrite à l'adresse à laquelle on accède
     */
    public VarAsml getValeurEcrite() {
        return valeurEcrite;
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
