package arbreasml;

/**
 * Classe mère des noeuds ASML correspondant aux accés mémoire (lecture ou écriture)
 */
public abstract class MemAsml implements ExpAsml {
    private final VarAsml tableau;
    private final VarOuIntAsml indice;

    /**
     * Créé un noeud ASML pour un accés en mémoire à l'adresse tableau+4*indice
     * @param tableau l'adresse de base
     * @param indice le decalage (un indice) par rapport à l'adresse de base pour obtenir l'adresse à laquelle on accède
     */
    public MemAsml(VarAsml tableau, VarOuIntAsml indice) {
        this.tableau = tableau;
        this.indice = indice;
    }   
    
    /**
     * Renvoie l'adresse de base de l'accès en mémoire
     * @return l'adresse de base de l'accès en mémoire
     */
    public VarAsml getTableau() {
        return tableau;
    }

    /**
     * Renvoie le decalage (un indice) par rapport à l'adresse de base de l'adresse à laquelle on accède
     * @return le decalage (un indice) par rapport à l'adresse de base de l'adresse à laquelle on accède
     */
    public VarOuIntAsml getIndice() {
        return indice;
    }
    
}
