package arbreasml;

/**
 * Classe mère des noeuds correspondant à la catégorie syntaxique FunDef (les déclarations de fonction ou de nombre flottant) décrite dans asml.html
 */
public abstract class FunDefAsml implements NoeudAsml {
    private final String label;

    /**
     * Créé un noeud ASML FunDef avec pour label label
     * @param label 
     */
    public FunDefAsml(String label)
    {
        this.label = label;
    }
    
    /**
     * Renvoie le label de la fonction
     * @return le label de la fonction
     */
    public String getLabel() {
        return label;
    }    
}
