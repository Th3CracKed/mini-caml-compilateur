package arbreasml;

import visiteur.ObjVisiteurAsml;
import visiteur.VisiteurAsml;

/**
 * Noeud ASML correspondant aux accés mémoire en lecture
 */
public class MemLectureAsml extends MemAsml {
    
    /**
     * Créé un noeud ASML pour un accés en mémoire en lecture à l'adresse tableau+4*indice
     * @param tableau l'adresse de base
     * @param indice le decalage (un indice) par rapport à l'adresse de base pour obtenir l'adresse à laquelle on accède
     */
    public MemLectureAsml(VarAsml tableau, VarOuIntAsml indice) {
        super(tableau, indice);
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
