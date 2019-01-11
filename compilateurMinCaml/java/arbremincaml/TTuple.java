package arbremincaml;

import java.util.List;

/**
 * Classe représentant le type d'un tuple
 */
public class TTuple extends Type {
    private final List<Type> ts;
    
    /**
     * Créé une instance de TTuple représentant le type d'un tuple dont les composantes ont les types dans la liste ts
     * @param ts les type des composantes du tuple
     */
    public TTuple(List<Type> ts)
    {
        this.ts = ts;
    }
    
    /**
     * Renvoie les type des composantes du tuple
     * @return les type des composantes du tuple
     */
    public List<Type> getTs()
    {
        return ts;
    }
    
    /**
     * Renvoie la représentation du type sous forme de chaîne
     * @return la représentation du type sous forme de chaîne
     */
    @Override
    public String toString()
    {
        String resultat = "(";
        for(int i = 0 ; i < ts.size() ; i++)
        {
            if(i >= 1)
            {
                resultat += ", ";
            }
            resultat += ts.get(i);
        }
        return resultat+")";
    }
}
