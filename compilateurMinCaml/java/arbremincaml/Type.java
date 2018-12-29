package arbremincaml;

import java.math.BigInteger;

public abstract class Type {
    private static BigInteger x = BigInteger.ZERO;
    public static Type gen() {
        Type resultat = new TVar("?" + x);
        x = x.add(BigInteger.ONE);
        return resultat;
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName(); 
    }
}

