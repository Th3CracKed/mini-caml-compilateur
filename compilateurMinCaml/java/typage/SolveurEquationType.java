package typage;

import arbremincaml.*;
import java.util.LinkedList;
import java.util.List;
import jdk.nashorn.internal.codegen.CompilationException;
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
        return resoudreEquations(listeEquations, 0);
    }
    
    private static LinkedList<EquationType> resoudreEquations(LinkedList<EquationType> listeEquations, int nbAppelsSansModifierListe)
    {
        //System.out.println("/////////////////// ETAPE RESOLUTION"); listeEquations.forEach(System.out::println);
        String messageMalType = "Le programme spécifié en entrée n'est pas correctement typé";
        if(listeEquations.isEmpty())
        {
            return new LinkedList<>();
        }
        else
        {          
            if(nbAppelsSansModifierListe == listeEquations.size())
            {
                // permet d'assurer la terminaison de l'algorithme (certains programmes ne permettent pas de determiner le type de toutes leur variable
                // par exemple, dans un programme avec une fonction avec des parametres qui n'est jamais appelee, on ne peut pas connaitre le type de ses
                // parametres. Comme une equation ayant des variables de deux cotes de l'operateur egal est reinseree a la fin de la liste a resoudre,
                // il faut stopper l'execution de l'algorithme si n appels recursifs n'ont pas modifie une liste de n equation(s) encore non resolue(s).
                return listeEquations;
            }
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
                    return resoudreEquations(listeEquations, 0);
                }
                else
                {
                    System.out.println(t1Tete+" = "+t2Tete);
                    throw new MyCompilationException(messageMalType);
                }
            }
            else if(t1Tete instanceof TFun)
            {
                if(t2Tete instanceof TFun)
                {
                    listeEquations.addFirst(new EquationType(((TFun) t1Tete).getT1(), ((TFun) t2Tete).getT1()));
                    listeEquations.addFirst(new EquationType(((TFun) t1Tete).getT2(), ((TFun) t2Tete).getT2()));
                    return resoudreEquations(listeEquations, 0);
                }
                else
                {
                    throw new MyCompilationException(messageMalType);
                }
            }
            else if(t1Tete instanceof TTuple)
            {
                if(t2Tete instanceof TTuple)
                {
                    List<Type> ts1 = ((TTuple) t1Tete).getTs();
                    List<Type> ts2 = ((TTuple) t2Tete).getTs();
                    int ts1Size = ts1.size();
                    if(ts1Size != ts2.size())
                    {
                        throw new MyCompilationException(messageMalType);
                    }
                    for(int i = 0 ; i < ts1Size ; i++)
                    {                        
                        listeEquations.addFirst(new EquationType(ts1.get(i), ts2.get(i)));
                    }
                    return resoudreEquations(listeEquations, 0);
                }
                else
                {
                    throw new MyCompilationException(messageMalType);
                }
            }
            else if(t1Tete instanceof TArray)
            {
                if(t2Tete instanceof TArray)
                {
                    listeEquations.addFirst(new EquationType(((TArray) t1Tete).getT(), ((TArray) t2Tete).getT()));
                    return resoudreEquations(listeEquations, 0);
                }
                else
                {
                    throw new MyCompilationException(messageMalType);
                }
            }
            else if(t1Tete instanceof TVar)
            {
                TVar t1TeteTVar = (TVar)t1Tete;
                boolean aRemplace = false;
                for(EquationType equation : listeEquations)
                {
                    aRemplace |= remplacer(equation, t1TeteTVar, t2Tete);
                }       
                boolean contientVariable = contientVar(t2Tete);
                if(contientVariable)
                {
                    listeEquations.addLast(teteListe);
                }
                LinkedList<EquationType> resultat = resoudreEquations(listeEquations, aRemplace?0:nbAppelsSansModifierListe+1);                
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
            return ((TTuple) type).getTs().stream().anyMatch(SolveurEquationType::contientVar);
        }
        else if(type instanceof TArray)
        {
            return contientVar(((TArray)type).getT());
        }
        else
        {
            return false;
        }
    }
    private static boolean remplacer(EquationType equationDOrigine, TVar variable, Type valeurVariable)
    {
        boolean aRemplace = false;
        Type t1 = equationDOrigine.getT1();
        Type t2 = equationDOrigine.getT2();
        if(TVarEtEgales(t1, variable))
        {
            aRemplace = true;
            equationDOrigine.setT1(valeurVariable);
        }
        else
        {
            aRemplace |= remplacer(t1, variable, valeurVariable);
        }
        if(TVarEtEgales(t2, variable))
        {
            aRemplace = true;
            equationDOrigine.setT2(valeurVariable);
        }
        else
        {
            aRemplace |= remplacer(t2, variable, valeurVariable);
        }
        return aRemplace;
    }
    
    private static boolean remplacer(Type typeDOrigine, TVar variable, Type valeurVariable)
    {
        boolean aRemplace = false;
        if(typeDOrigine instanceof TFun)
        {
            TFun typeDorigineFun = (TFun)typeDOrigine;
            Type t1 = typeDorigineFun.getT1();
            Type t2 = typeDorigineFun.getT2();
            if(TVarEtEgales(t1, variable))
            {
                aRemplace = true;
                typeDorigineFun.setT1(valeurVariable);
            }
            else
            {
                aRemplace |= remplacer(t1, variable, valeurVariable);
            }
            if(TVarEtEgales(t2, variable))
            {
                aRemplace = true;
                typeDorigineFun.setT2(valeurVariable);
            }
            else
            {
                aRemplace |= remplacer(t2, variable, valeurVariable);
            }
        }
        else if (typeDOrigine instanceof TTuple)
        {
            List<Type> types = ((TTuple)typeDOrigine).getTs();
            for(int i = 0 ; i < types.size() ; i++)
            {
                if(TVarEtEgales(types.get(i), variable))
                {
                    aRemplace = true;
                    types.set(i, valeurVariable);
                }
                else
                {
                    aRemplace |= remplacer(types.get(i), variable, valeurVariable);
                }
            }
        }
        else if (typeDOrigine instanceof TArray)
        {
            TArray typeDorigineArray = (TArray)typeDOrigine;
            Type t = typeDorigineArray.getT();
            if(TVarEtEgales(t, variable))
            {
                aRemplace = true;
                typeDorigineArray.setT(valeurVariable);
            }
            else
            {
                aRemplace |= remplacer(t, variable, valeurVariable);
            }
        }
        return aRemplace;
    }
}
