package typage;

import arbremincaml.*;
import java.util.LinkedList;
import java.util.List;
import util.MyCompilationException;

/**
 * Classe contenant la méthode permettant de résoudre une liste d'équations de type
 */
public class SolveurEquationType {
    private static final String MESSAGE_PROGRAMME_MAL_TYPE = "Le programme spécifié en entrée n'est pas correctement typé";
    
    /**
     * Renvoie vrai si le type e1 est une variable de type qui a le même nom que la variable de type e2 et faux sinon
     * @param e1 le type
     * @param e2 la variable de type
     * @return vrai si le type e1 est une variable de type qui a le même nom que la variable de type e2 et faux sinon
     */
    private static boolean TVarEtEgales(Type e1, TVar e2)
    {
        return (e1 instanceof TVar && ((TVar)e1).getV().equals(e2.getV()));
    }
            
    /**
     * Renvoie vrai si object est une instance d'au moins une d'une des classes dans classes (ou d'une de leur classe fille) et faux sinon
     * @param object l'objet
     * @param classes les classes
     * @return vrai si object est et du type d'une des classes dans classes (ou d'une de leur classe fille) et faux sinon
     */
    private static boolean instanceOf(Object object, Class... classes)
    {
        for(Class classe : classes)
        {
            if(classe.isInstance(object))
            {
                return true;
            }
        }                
        return false;
    }
    
    /**
     * Renvoie la liste des équation de résolues correspondant à la liste listeEquation des équation à résoudre
     * @param listeEquations la liste des équation de type à résoudre
     * @return la liste des équation de résolues correspondant à la liste listeEquation des équation à résoudre
     */
    public static LinkedList<EquationType> resoudreEquations(LinkedList<EquationType> listeEquations)
    {
        return resoudreEquations(listeEquations, 0);
    }
    
    /**
     * Renvoie la liste des équation de résolues correspondant à la liste listeEquation des équation à résoudre
     * @param listeEquations la liste des équation de type à résoudre     * 
     * @param nbAppelsSansModifierListe le nombre d'appels récursifs consécutif à cette méthode depuis la dernière modification de la liste listeEquations
     * @return la liste des équation de résolues correspondant à la liste listeEquation des équation à résoudre
     */
    private static LinkedList<EquationType> resoudreEquations(LinkedList<EquationType> listeEquations, int nbAppelsSansModifierListe)
    {
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
            if(teteListe.getT2() instanceof TVar) // permet d'éviter d'avoir une instance de TVar dans t2 et une instance d'un autre type dans t1 (cela limite le nombre de cas à traiter)
            {
                teteListe.echange();
            }
            Type t1Tete = teteListe.getT1();
            Type t2Tete = teteListe.getT2();
            if(instanceOf(t1Tete, TInt.class, TBool.class, TUnit.class, TFloat.class, TNombre.class)) // si le premier type de l'équation en tête de liste est un type connu non composé d'autres types, il faut vérifier que le second type de l'équation en tête de liste est un type connu et unifiable avec le premier
            {      
                // une equation de la forme ?1 = TNombre signifie ?1 = Int ou ?2 = Float (TInt et TFloat heritent de TNombre donc (new TInt()) instanceof TNombre est
                // vrai mais (new TNombre()) instanceof TInt()) est faux. On utilise ce type d'equation pour l'operateur inférieur ou égal qui permet de comparer
                // 2 flottants ou 2 entiers (mais pas un entier et un flottant) : le type des operandes doit etre le meme et etre egal a TInt ou TFloat
                if(t1Tete.getClass().isInstance(t2Tete) || t2Tete.getClass().isInstance(t1Tete)) 
                {
                    return resoudreEquations(listeEquations, 0);
                }
                else
                {
                    throw new MyCompilationException(MESSAGE_PROGRAMME_MAL_TYPE);
                }
            }
            else if(t1Tete instanceof TFun) 
            {
                if(t2Tete instanceof TFun) // si les deux types de l'équation sont des type de fonctions il faut rajouter à la liste des équation deux équations indiquant que les types des paramètres et des résultats de ces deux fonctions doivent être égaux
                {
                    listeEquations.addFirst(new EquationType(((TFun) t1Tete).getT1(), ((TFun) t2Tete).getT1()));
                    listeEquations.addFirst(new EquationType(((TFun) t1Tete).getT2(), ((TFun) t2Tete).getT2()));
                    return resoudreEquations(listeEquations, 0);
                }
                else // si le premier de l'équation en tête de liste est un type de fonction et pas le second, le programme est mal typé
                {
                    throw new MyCompilationException(MESSAGE_PROGRAMME_MAL_TYPE);
                }
            }
            else if(t1Tete instanceof TTuple) 
            {
                if(t2Tete instanceof TTuple) // si les deux types de l'équation sont des type de tuples il faut rajouter à la liste des équation les équations indiquant que les types des composantes (dans l'ordre) de ces deux tuples doivent être égaux
                {
                    List<Type> ts1 = ((TTuple) t1Tete).getTs();
                    List<Type> ts2 = ((TTuple) t2Tete).getTs();
                    int ts1Size = ts1.size();
                    if(ts1Size != ts2.size()) // si les deux types de l'équation sont des types de tuples avec un nombre différents de composantes, le programme est mal typé
                    {
                        throw new MyCompilationException(MESSAGE_PROGRAMME_MAL_TYPE);
                    }
                    for(int i = 0 ; i < ts1Size ; i++)
                    {                        
                        listeEquations.addFirst(new EquationType(ts1.get(i), ts2.get(i)));
                    }
                    return resoudreEquations(listeEquations, 0);
                }
                else  // si le premier de l'équation en tête de liste est un type de tuple et pas le second, le programme est mal typé
                {
                    throw new MyCompilationException(MESSAGE_PROGRAMME_MAL_TYPE);
                }
            }
            else if(t1Tete instanceof TArray)
            {
                if(t2Tete instanceof TArray) // si les deux types de l'équation sont des type de tableaux il faut rajouter à la liste des équation l'équations indiquant que les types des éléments de ces deux tableaux doivent être égaux
                {
                    listeEquations.addFirst(new EquationType(((TArray) t1Tete).getT(), ((TArray) t2Tete).getT()));
                    return resoudreEquations(listeEquations, 0);
                }
                else // si le premier de l'équation en tête de liste est un type de tableau et pas le second, le programme est mal typé
                {
                    throw new MyCompilationException(MESSAGE_PROGRAMME_MAL_TYPE);
                }
            }
            else // if(t1Tete instanceof TVar)
            {
                TVar t1TeteTVar = (TVar)t1Tete;
                 // si l'équation est de la forme a = b avec a une variable de type et b un type contenant a (comme Int->a), le programme est mal typé. Cela peut se
                 // produire pour un programmme contenant une fonction se renvoyant elle-même et il est nécessaire de lancer une exception dans ce cas pour assurer
                 // la terminaison de l'algorithme
                if((!(t2Tete instanceof TVar) || !((TVar)t2Tete).getV().equals(t1TeteTVar.getV())) && contientCetteVariable(t2Tete, t1TeteTVar))
                {
                    throw new MyCompilationException(MESSAGE_PROGRAMME_MAL_TYPE);
                }
                // remplacer les occurences du premier type de l'équation en tête de liste (une variable) par le deuxième type de l'équation en tête de liste
                boolean aRemplace = false;
                for(EquationType equation : listeEquations)
                {
                    aRemplace |= remplacer(equation, t1TeteTVar, t2Tete);
                }       
                boolean contientVariable = contientVar(t2Tete);
                // si le deuxieme type de l'équation est un type connu, il faut ajouter l'équation en tête de liste à la liste des équations résolues, sinon (si il contient une variable 
                // comme dans (Int -> x), il faut remettre l'équation en tête de liste dans la liste des équations à résoudre
                if(contientVariable)
                {
                    listeEquations.addLast(teteListe);
                }
                LinkedList<EquationType> resultat = resoudreEquations(listeEquations, (aRemplace||!contientVariable)?0:nbAppelsSansModifierListe+1);                
                if(!contientVariable)
                {                    
                    resultat.addFirst(teteListe);
                }
                return resultat;
            }  
        }
    }
    
    /**
     * Renvoie vrai si le type type contient une variable (comme x -> Int) et faux sinon
     * @param type le type pour lequel on vérifie si il contient une variable
     * @return vrai si le type type contient une variable et faux sinon
     */
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
    
    /**
     * Renvoie vrai si le type type contient la variable var (comme dans var -> Int) et faux sinon
     * @param type le type pour lequel on vérifie si il contient la variable var
     * @param var la variable pour laquelle on vérifie si le type type la contient
     * @return vrai si le type type contient la variable var et faux sinon
     */
    private static boolean contientCetteVariable(Type type, TVar var)
    {
        if(type instanceof TVar && ((TVar)type).getV().equals(var.getV()))
        {
            return true;
        }
        else if(type instanceof TFun)
        {
            TFun typeTFun = (TFun)type;
            return contientCetteVariable(typeTFun.getT1(), var) || contientCetteVariable(typeTFun.getT2(), var);
        }
        else if(type instanceof TTuple)
        {
            return ((TTuple) type).getTs().stream().anyMatch(x->contientCetteVariable(x, var));
        }
        else if(type instanceof TArray)
        {
            return contientCetteVariable(((TArray)type).getT(), var);
        }
        else
        {
            return false;
        }
    }
    
    /**
     * Remplace les occurences de la variable de type variable dans l'équation equationDOrigine par le type valeurVariable et renvoie vrai si au moins une occurences a été remplacée et faux sinon
     * @param equationDOrigine l'équation dans laquelle remplacer les occurrences de variable par valeurVariable
     * @param variable la variable de type à remplacer
     * @param valeurVariable le type par lequel remplacer la variable de type variable
     * @return vrai si au moins une occurences de variable a été remplacée dans l'équation equationDOrigine et faux sinon
     */
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
    
    /**
     * Remplace les occurences de la variable de type variable dans le type typeDOrigine par le type valeurVariable et renvoie vrai si au moins une occurences a été remplacée et faux sinon
     * @param equationDOrigine le type dans lequelle remplacer les occurrences de variable par valeurVariable
     * @param variable la variable de type à remplacer
     * @param valeurVariable le type par lequel remplacer la variable de type variable
     * @return vrai si au moins une occurences de variable a été remplacée dans le type typeDOrigine et faux sinon
     */
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
