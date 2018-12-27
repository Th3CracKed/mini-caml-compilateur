package typage;

import arbremincaml.TArray;
import arbremincaml.TUnit;
import arbremincaml.TInt;
import arbremincaml.TBool;
import arbremincaml.TFun;
import arbremincaml.TTuple;
import arbremincaml.TVar;
import arbremincaml.Type;
import java.util.HashMap;
import java.util.LinkedList;
import util.MyCompilationException;
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
        //System.out.println("/////////////////// ETAPE RESOLUTION"); listeEquations.forEach(System.out::println);
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
                    throw new MyCompilationException(messageMalType);
                }
            }
            else if(t1Tete instanceof TFun)
            {
                if(t2Tete instanceof TFun)
                {
                    listeEquations.addFirst(new EquationType(((TFun) t1Tete).getT1(), ((TFun) t2Tete).getT1()));
                    listeEquations.addFirst(new EquationType(((TFun) t1Tete).getT2(), ((TFun) t2Tete).getT2()));
                    return resoudreEquations(listeEquations);
                }
                else
                {
                    throw new MyCompilationException(messageMalType);
                }
            }
            else if(t1Tete instanceof TVar)
            {
                TVar t1TeteTVar = (TVar)t1Tete;
                for(EquationType equation : listeEquations)
                {
                    remplacer(equation, t1TeteTVar, t2Tete);
                }       
                boolean contientVariable = contientVar(t2Tete);
                if(contientVariable)
                {
                    listeEquations.addLast(teteListe);
                }
                LinkedList<EquationType> resultat = resoudreEquations(listeEquations);                
                if(!contientVariable)
                {                    
                    resultat.addFirst(teteListe);
                }
                return resultat;
            }     
            else
            {
                throw new NotYetImplementedException();
            }
        }
    }
    
    private static boolean contientVar(Type type)
    {
        if(type instanceof TVar)
        {
            return true;
        }
        else if(type instanceof TFun)
        {
            TFun typeTFun = (TFun)type;
            return contientVar(typeTFun.getT1()) || contientVar(typeTFun.getT2());
        }
        else if(type instanceof TTuple)
        {
            throw new NotYetImplementedException();
        }
        else if(type instanceof TArray)
        {
            throw new NotYetImplementedException();
        }
        else
        {
            return false;
        }
    }
    private static void remplacer(EquationType equationDOrigine, TVar variable, Type valeurVariable)
    {
        Type t1 = equationDOrigine.getT1();
        Type t2 = equationDOrigine.getT2();
        if(TVarEtEgales(t1, variable))
        {
            equationDOrigine.setT1(valeurVariable);
        }
        else
        {
            remplacer(t1, variable, valeurVariable);
        }
        if(TVarEtEgales(t2, variable))
        {
            equationDOrigine.setT2(valeurVariable);
        }
        else
        {
            remplacer(t2, variable, valeurVariable);
        }
    }
    
    private static void remplacer(Type typeDOrigine, TVar variable, Type valeurVariable)
    {
        if(typeDOrigine instanceof TFun)
        {
            TFun typeDorigineFun = (TFun)typeDOrigine;
            Type t1 = typeDorigineFun.getT1();
            Type t2 = typeDorigineFun.getT2();
            if(TVarEtEgales(t1, variable))
            {
                typeDorigineFun.setT1(valeurVariable);
            }
            else
            {
                remplacer(t1, variable, valeurVariable);
            }
            if(TVarEtEgales(t2, variable))
            {
                typeDorigineFun.setT2(valeurVariable);
            }
            else
            {
                remplacer(t2, variable, valeurVariable);
            }
        }
        else if (typeDOrigine instanceof TTuple)
        {
            throw new NotYetImplementedException();
        }
        else if (typeDOrigine instanceof TArray)
        {
            throw new NotYetImplementedException();
        }
    }
}
