package arbremincaml;

import java.math.BigInteger;
import java.util.HashSet;
import util.Constantes;
import util.MyCompilationException;

public class Id {
    private String idString;
    private static HashSet<String> idUtilises = initIdUtilises();
    
    private static HashSet<String> initIdUtilises()
    {
        idUtilises = new HashSet<>();
        idUtilises.addAll(Constantes.FONCTION_EXTERNES_MINCAML);
        idUtilises.addAll(Constantes.MOTS_RESERVES_ASML);
        idUtilises.addAll(Constantes.FONCTION_EXTERNES_ASML);
        idUtilises.addAll(Constantes.FONCTION_EXTERNES_ARM);
        idUtilises.addAll(Constantes.LABELS_PRIVES_ARM);
        idUtilises.add(Constantes.NOM_FONCTION_MAIN_ASML);
        idUtilises.add(Constantes.NOM_FONCTION_MAIN_ARM);
        return idUtilises;
    }
    
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
    
    public static String genIdStringAvecPrefixe(String prefixe)
    {
        String idString = null;
        do
        {            
            x = x.add(BigInteger.ONE);
            idString = prefixe + x;
        }while(idUtilises.contains(idString));  
        idUtilises.add(idString);      
        return idString;
    }
    
    public static String genIdString()
    {
         return genIdStringAvecPrefixe("v");
    }
    
    public static String genLabelString()
    {
        return genIdStringAvecPrefixe(Constantes.DEBUT_LABEL_ASML+"f");
    }
    
    public static boolean estUnLabel(String idString) {
        return idString.startsWith(Constantes.DEBUT_LABEL_ASML);
    }

}
