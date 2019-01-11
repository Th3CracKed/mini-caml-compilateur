package arbremincaml;

/**
 * Variable de type
 */
public class TVar extends Type {
    private final String v;
    
    /**
     * Créé une variable de type identifiée par la chaine v
     * @param v la chaine identifiant la variable de type
     */
    public TVar(String v) {
        this.v = v;
    }
    
    /**
     * Renvoie la chaine identifiant la variable de type
     * @return la chaine identifiant la variable de type
     */
    public String getV()
    {
        return v;
    }
    
    /**
     * Renvoie la représentation du type sous forme de chaîne
     * @return la représentation du type sous forme de chaîne
     */
    @Override
    public String toString() {
        return v; 
    }
}
