package arbremincaml;

import java.math.BigInteger;

/**
 * Classe mère des classes représentant un type
 */
public abstract class Type {
    private static BigInteger x = BigInteger.ZERO; // le compteur du nombre de variables de type est un BigInteger et non un type comme int ou long qui ont une valeur maximale (même si il est peut probable de l'atteindre)
    
    /**
     * Génère et renvoie une variable de type dont le nom n'a pas déjà été alloué à une autre variable de type
     * @return la variable de type générée
     */
    public static Type gen() {
        Type resultat = new TVar("?" + x);
        x = x.add(BigInteger.ONE);
        return resultat;
    }
    
    /**
     * Renvoie la représentation du type sous forme de chaîne
     * @return la représentation du type sous forme de chaîne
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName(); 
    }
}

