package typage;

import arbremincaml.TUnit;
import arbremincaml.TInt;
import arbremincaml.TBool;
import arbremincaml.TFun;
import arbremincaml.TVar;
import arbremincaml.Type;
import java.util.LinkedList;
import util.CompilationException;
import util.NotYetImplementedException;


public class SolveurEquationType {
    private static boolean TVarEtEgales(Type e1, TVar e2)
    {
        return (e1 instanceof TVar && ((TVar)e1).getV().equals(e2.getV()));
    }
            
    private static boolean instanceOf(Object object, Class... classes)
    {
        boolean resultat = false;
        for(Class classe : classes)
        {
            resultat |= classe.isInstance(object);
        }                
        return resultat;
    }
    
    public static LinkedList<EquationType> resoudreEquations(LinkedList<EquationType> listeEquations)
    {
        String messageMalType = "Le programme spécifié en entrée n'est pas correctement typé";
        if(listeEquations.isEmpty())
        {
            return new LinkedList<>();
        }
        else
        {          
            EquationType teteListe = listeEquations.pop();   
            if(teteListe.getT2() instanceof TVar)
            {
                teteListe.echange();
            }
            Type t1Tete = teteListe.getT1();
            Type t2Tete = teteListe.getT2();
            if(instanceOf(t1Tete, TInt.class, TBool.class, TUnit.class))
            {                
                if(t1Tete.getClass().isInstance(t2Tete))
                {
                    return resoudreEquations(listeEquations);
                }
                else
                {
                    throw new CompilationException(messageMalType);
                }
            }
            else if(t1Tete instanceof TFun)
            {
                if(t2Tete instanceof TFun)
                {
                    listeEquations.add(0, new EquationType(((TFun) t1Tete).getT1(), ((TFun) t2Tete).getT1()));
                    listeEquations.add(0, new EquationType(((TFun) t1Tete).getT2(), ((TFun) t2Tete).getT2()));
                    return resoudreEquations(listeEquations);
                }
                else
                {
                    throw new CompilationException(messageMalType);
                }
            }
            else if(t1Tete instanceof TVar)
            {
                TVar t1TeteTVar = (TVar)t1Tete;
                for(EquationType equation : listeEquations)
                {
                    if(TVarEtEgales(equation.getT1(), t1TeteTVar))
                    {
                        equation.setT1(t2Tete);
                    }
                    if(TVarEtEgales(equation.getT2(), t1TeteTVar))
                    {
                        equation.setT2(t2Tete);
                    }
                }
                LinkedList<EquationType> resultat = resoudreEquations(listeEquations);
                resultat.addFirst(teteListe);
                return resultat;
            }     
            else
            {
                throw new NotYetImplementedException();
            }
        }
    }
}
