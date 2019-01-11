package arbremincaml;

/**
 * Classe représentant le type d'un tableau
 */
public class TArray extends Type{
    private Type t;
    
    /**
     * Créé une instance de TArray représentant le type des tableaux contenant des éléments de type t
     * @param t le type des éléments du tableau
     */
    public TArray(Type t)
    {
        setT(t);
    }    
    
    /**
     * Renvoie le type des éléments du tableau
     * @return le type des éléments du tableau
     */
    public Type getT()
    {
        return t;
    }
    
    /**
     * Définit t comme le nouveau type des éléments du tableau
     * @param t le nouveau type des éléments du tableau
     */
    public final void setT(Type t)
    {
        this.t = t;
    }
    
    /**
     * Renvoie la représentation du type sous forme de chaîne
     * @return la représentation du type sous forme de chaîne
     */
    @Override
    public String toString()
    {
        return "["+t+"]";
    }
}
