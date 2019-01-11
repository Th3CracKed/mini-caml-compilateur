package arbreasml;

import java.util.List;

/**
 * Classe mère pour les noeuds ASML correspondant aux appels de fonctions classique et aux appels de closures
 */
public abstract class CallBaseAsml implements ExpAsml {
    private final List<VarAsml> arguments;
    
    /**
     * Cree un noeud ASML CallBase avec les arguments dans arguments 
     * @param arguments les arguments passés à la fonction
     */
    public CallBaseAsml(List<VarAsml> arguments)
    {
        this.arguments = arguments;
    }  

    /**
     * Renvoie la liste des arguments passés à la fonction
     * @return la liste des arguments passés à la fonction
     */
    public List<VarAsml> getArguments() {
        return arguments;
    }
}
