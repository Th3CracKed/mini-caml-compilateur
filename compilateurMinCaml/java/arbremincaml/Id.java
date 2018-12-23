package arbremincaml;

import java.math.BigInteger;
import java.util.HashSet;
import util.MyCompilationException;

public class Id {
    private String idString;
    private static final HashSet<String> idUtilises = new HashSet<>();;
    
    public Id(String idString) {
        this.setIdString(idString);
    }
    @Override
    public String toString() {
        return idString;
    }
    
    public String getIdString()
    {
        return idString;
    }
    
    public final void setIdString(String idString) {        
        if(idString == null)
        {
            throw new MyCompilationException("Un id de variable ne peut pas être null");
        }
        this.idString = idString;
        idUtilises.add(idString);
    }
        
    private static BigInteger x = BigInteger.ONE.negate();
    public static Id gen() {
        return new Id(genIdString());
    }
    
    public static String genIdString()
    {
        String idString = null;
        do
        {            
            x = x.add(BigInteger.ONE);
            idString = "v" + x;
        }while(idUtilises.contains(idString));        
        return idString;
    }

}
