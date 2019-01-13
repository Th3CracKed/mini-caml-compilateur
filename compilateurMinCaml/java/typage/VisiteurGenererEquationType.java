package typage;

import arbremincaml.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import util.*;
import visiteur.ObjVisitor;

/**
 * Visiteur renvoyant les équations de type d'un programme MinCaml
 */
public class VisiteurGenererEquationType implements ObjVisitor<LinkedList<EquationType>>{

    private final HashMap<String, Type> environnementVar;
    private Type type;
    private final Stack<Type> anciensTypes;

    /**
     * Créé un visiteur renvoyant les équations de type d'un programme MinCaml
     */
    public VisiteurGenererEquationType()
    {
        environnementVar = new HashMap<>();
        // ajout des types des fonctions externes à l'environnement (table de hachage associant un type aux variables du programme
        environnementVar.put(Constantes.PRINT_INT_CAML, new TFun(new TInt(), new TUnit()));
        environnementVar.put(Constantes.PRINT_NEWLINE_CAML, new TFun(new TUnit(), new TUnit()));
        environnementVar.put(Constantes.SIN_CAML, new TFun(new TFloat(), new TFloat()));
        environnementVar.put(Constantes.COS_CAML, new TFun(new TFloat(), new TFloat()));
        environnementVar.put(Constantes.SQRT_CAML, new TFun(new TFloat(), new TFloat()));
        environnementVar.put(Constantes.ABS_FLOAT_CAML, new TFun(new TFloat(), new TFloat()));
        environnementVar.put(Constantes.INT_OF_FLOAT_CAML, new TFun(new TFloat(), new TInt()));
        environnementVar.put(Constantes.FLOAT_OF_INT_CAML, new TFun(new TInt(), new TFloat()));
        environnementVar.put(Constantes.TRUNCATE_CAML, new TFun(new TFloat(), new TInt()));
        type = new TUnit(); // le programme doit avoir le type TUnit(). L'attribut type est le type attendu du prochain noeud visité
        anciensTypes = new Stack<>();
    }
    
    /**
     * Créé et renvoie une liste contenant l'équation dont les types sont t1 et t2
     * @param t1 le premier type de l'équation
     * @param t2 le second type de l'équation
     * @return une liste contenant l'équation dont les types sont t1 et t2
     */
    private LinkedList<EquationType> creerSingleton(Type t1, Type t2)
    {
        return new LinkedList<>(Arrays.asList(new EquationType(t1, t2)));
    }
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie une liste contenant l'équation dont
     * les types sont type et une instance de TUnit (le type attendu de nil doit être TUnit)
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public LinkedList<EquationType> visit(Unit e) {
        return creerSingleton(type, new TUnit());
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie une liste contenant l'équation dont
     * les types sont type et une instance de TUnit (le type attendu d'un booléen doit être TBool)
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public LinkedList<EquationType> visit(Bool e) {
        return creerSingleton(type, new TBool());
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie une liste contenant l'équation dont
     * les types sont type et une instance de TUnit (le type attendu d'un entier doit être TInt)
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public LinkedList<EquationType> visit(Int e) {
        return creerSingleton(type, new TInt());
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie une liste contenant l'équation dont
     * les types sont type et une instance de TUnit (le type attendu d'un nombre flottant doit être TFloat)
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public LinkedList<EquationType> visit(FloatMinCaml e) {
        return creerSingleton(type, new TFloat());
    }
    
    /**
     * Change le type attendu du prochain noeud visité
     * @param type le type attendu du prochain noeud visité
     */
    private void changerType(Type type)
    {
        anciensTypes.push(this.type);
        this.type = type;
    }
    
    /**
     * Restaure l'ancien type attendu (celui avant le dernier appel à changerType) du prochain noeud visité.
     */
    private void restaurerType()
    {
        type = anciensTypes.pop();
    }
    
    /**
     * Méthode factorisant le code des méthodes visit s'appliquant à des classe héritant de OperateurUnaire (le type t est le type attendu des opérandes et du résultat
     * de l'opérateur unaire). Définit t comme type attendu de l'opérande et ajoute l'équation composée du type attendu de e et de t à son résultat.
     * @param e le noeud à visiter.
     * @param t le type attendu de l'opérande et du résultat de l'opérateur binaire
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    private LinkedList<EquationType> visitOpUnaireWorker(OperateurUnaire e, Type t)
    {
        changerType(t);
        LinkedList<EquationType> equationsE = e.getE().accept(this);
        restaurerType();
        equationsE.addFirst(new EquationType(type, t));
        return equationsE;
    }
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie le résultat de la méthode visitOpUnaireWorker
     * avec pour paramètre e et une instance de TBool (l'opérande et le résultat de l'opérateur not est de type TBool)
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public LinkedList<EquationType> visit(Not e) {
        return visitOpUnaireWorker(e, new TBool());
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie le résultat de la méthode visitOpUnaireWorker
     * avec pour paramètre e et une instance de TInt (l'opérande et le résultat de l'opérateur neg est de type TInt)
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public LinkedList<EquationType> visit(Neg e) {
        return visitOpUnaireWorker(e, new TInt());
    }

    /**
     * Méthode factorisant le code des méthodes visit s'appliquant à des classe héritant de OperateurBinaire (le type typeOperandes est le type attendu des opérandes et
     * typeOperateur est le type attendu du résultat de l'opérateur binaire). Définit t comme type attendu des opérandes et ajoute l'équation composée du type attendu 
     * de e et de typeOperateur à son résultat.
     * @param e le noeud à visiter.
     * @param typeOperandes le type attendu des opérandes de l'opérateur binaire
     * @param typeOperateur le type attendu du résultat de l'opérateur binaire
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    private LinkedList<EquationType> visitOpBinaireWorker(OperateurBinaire e, Type typeOperandes, Type typeOperateur)
    {
        changerType(typeOperandes);
        LinkedList<EquationType> equationsE1 = e.getE1().accept(this);
        LinkedList<EquationType> equationsE2 = e.getE2().accept(this);
        restaurerType();
        equationsE1.addAll(equationsE2);
        equationsE1.addFirst(new EquationType(typeOperateur, type));
        return equationsE1;
    }
    
    /**
     * Méthode factorisant le code des méthodes visit s'appliquant à des classe héritant de OperateurArithmetiqueInt (le type attendu des opérandes et
     * du résultat est dans ce cas TInt car add et sub s'appliquent à des entiers et ont un résultat qui est un entier).
     * @param e le noeud à visiter.
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    private LinkedList<EquationType> visitOpArithmetiqueIntWorker(OperateurArithmetiqueInt e)
    {
        return visitOpBinaireWorker(e, new TInt(), new TInt());
    }
    
    /**
     * Méthode factorisant le code des méthodes visit s'appliquant à des classe héritant de OperateurRelationnel (le type attendu des opérandes est typeOpérande
     * et le type attendu du résultat est dans ce cas TBool car eq et le ont un résultat qui est un booléen).
     * @param e le noeud à visiter.
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    private LinkedList<EquationType> visitOpRelationnelWorker(OperateurRelationnel e, Type typeOperandes)
    {
        return visitOpBinaireWorker(e, typeOperandes, new TBool());
    }
    
    /**
     * Méthode factorisant le code des méthodes visit s'appliquant à des classe héritant de OperateurArithmetiqueFloat (le type attendu des opérandes et
     * du résultat est dans ce cas TInt car fadd, fsub, fdiv et fmul s'appliquent à des nombres flottants et ont un résultat qui est un nombre flottant).
     * @param e le noeud à visiter.
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    private LinkedList<EquationType> visitOpArithmetiqueFloatWorker(OperateurArithmetiqueFloat e)
    {
        return visitOpBinaireWorker(e, new TFloat(), new TFloat());
    }
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie le résultat de la méthode visitOpArithmetiqueIntWorker
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public LinkedList<EquationType> visit(Add e) {        
        return visitOpArithmetiqueIntWorker(e);
    }
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie le résultat de la méthode visitOpArithmetiqueIntWorker
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public LinkedList<EquationType> visit(Sub e) {
        return visitOpArithmetiqueIntWorker(e);
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie le résultat de la méthode visitOpUnaireWorker
     * avec pour paramètre e et une instance de TFloat (l'opérande et le résultat de l'opérateur fneg est de type TFloat)
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public LinkedList<EquationType> visit(FNeg e) {
        return visitOpUnaireWorker(e, new TFloat());
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie le résultat de la méthode visitOpArithmetiqueFloatWorker
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public LinkedList<EquationType> visit(FAdd e) {
        return visitOpArithmetiqueFloatWorker(e);
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie le résultat de la méthode visitOpArithmetiqueFloatWorker
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public LinkedList<EquationType> visit(FSub e) {
        return visitOpArithmetiqueFloatWorker(e);
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie le résultat de la méthode visitOpArithmetiqueFloatWorker
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public LinkedList<EquationType> visit(FMul e) {
        return visitOpArithmetiqueFloatWorker(e);
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie le résultat de la méthode visitOpArithmetiqueFloatWorker
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public LinkedList<EquationType> visit(FDiv e) {
        return visitOpArithmetiqueFloatWorker(e);
    }
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie le résultat de la méthode visitOpRelationnelWorker
     * avec pour paramètre e et une nouvelle de variables de type (les opérandes d'un noeud eq doivent avoir le même type mais ce type peut être n'importe quel type)
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public LinkedList<EquationType> visit(Eq e) {
        return visitOpRelationnelWorker(e, Type.gen());
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie le résultat de la méthode visitOpRelationnelWorker
     * avec pour paramètre e et une instance de TNombre (l'operateur inférieur ou égal permet de comparer ou  2 flottants ou 2 entiers (mais pas un entier et un flottant) :
     * le type des operandes doit être le même et être égal a TInt ou TFloat (une équation de la forme ?1 = TNombre signifie ?1 = TInt ou ?2 = TFloat (on fait hériter TInt et TFloat de TNombre)
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public LinkedList<EquationType> visit(LE e) {
        Type typeOperandes = Type.gen();
        LinkedList<EquationType> equations = visitOpRelationnelWorker(e, typeOperandes);
        equations.add(new EquationType(typeOperandes, new TNombre()));
        return equations;
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, définit TBool comme le type attendu de la condition du if, 
     * créé une variable de type et la définit comme type attendu des deux branches du if (ces branches doivent avoir le même type mais ce type peut être nimporte lequel)
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public LinkedList<EquationType> visit(If e) {        
        changerType(new TBool());
        LinkedList<EquationType> equations = e.getE1().accept(this);
        restaurerType();
        Type typeIf = Type.gen();
        changerType(typeIf);
        LinkedList<EquationType> equationsE2 = e.getE2().accept(this);
        LinkedList<EquationType> equationsE3 = e.getE3().accept(this);
        restaurerType();
        equations.addAll(equationsE2);
        equations.addAll(equationsE3);
        equations.addFirst(new EquationType(typeIf, type));
        return equations;
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Si la variable e a été déclarée, renvoie la variable de type correspondant au type de cette variable (la table de hachage environnementVar associe à une variable
     * la variable de type de son type) et lève une exception si la variable n'a pas été déclarée
     * @throws MyCompilationException si la variable e n'a pas été déclarée
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public LinkedList<EquationType> visit(Var e) {
        String idString = e.getId().getIdString();
        Type typeE = environnementVar.get(idString);
        if(typeE == null)
        {
            throw new MyCompilationException("La variable "+idString+" n'a pas été déclarée");
        }
        return creerSingleton(typeE, type);
    }
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, définit la variable de type du noeud let (renvoyé par sa
     * méthode getT) comme type attendu de la valeur assignée à la variable déclarée et associe cette variable de type à l'identifiant de la variable avant de visiter
     * le noeud après le mot clé in du let (et restaure la précédente variable de type associée à l'identifiant de la variable après (qui peut être null) pour gérer
     * le cas des variables de même nom (car ce visiteur est appliqué avant l'étape de l'alpha-conversion).
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public LinkedList<EquationType> visit(Let e) {
        Type typeVariable = e.getT();
        String idString = e.getId().getIdString();
        changerType(typeVariable);
        LinkedList<EquationType> equationsE1 = e.getE1().accept(this);
        restaurerType();
        Type ancienTypeVar = environnementVar.get(idString);
        environnementVar.put(idString, typeVariable);
        LinkedList<EquationType> equationsE2 = e.getE2().accept(this);
        environnementVar.put(idString, ancienTypeVar);
        equationsE1.addAll(equationsE2);
        return equationsE1;
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, créé une variable de type pour chaque paramètre de la fonction
     * , pour son type de retour et pour le type de la fonction, définit celle du type de retour comme type attendu du corps de la fonction, associe la variable de type 
     * du type de la fonction à l'identifiant de la variable avant de visiter le noeud après le mot clé in du let rec (et restaure la précédente valeur de variable de type associée 
     * à l'identifiant de la variable après (qui peut être null) pour gérer le cas des variables de même nom (car ce visiteur est appliqué avant l'étape de l'alpha-conversion).
     * De la même manière, le nom des paramètres est associé à leur variable de type le temps que le noeud du corps de la fonction soit visité (et la précédente valeur associée
     * est ensuite restaurée). Enfin, une équation dont le premier type est la variable de type de la fonction et dont le second est le type fonction avec pour types des paramètres
     * les variables de type des paramètres et pour type de retour la variable de type du type de retour est ajoutée au résultat.
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public LinkedList<EquationType> visit(LetRec e) {
        Type typeRetour = Type.gen();
        Type typeFlecheFun = typeRetour;
        FunDef funDef = e.getFd();
        Type typeFun = Type.gen();// funDef.getType() renvoie null
        String idStringFun = funDef.getId().getIdString();
        Type ancienTypeVar = environnementVar.get(idStringFun);
        environnementVar.put(idStringFun, typeFun); 
        HashMap<String, Type> anciensTypesVarsArgs = new HashMap<>();
        List<Id> idsArguments = funDef.getArgs();
        int nbParametres = idsArguments.size();
        for(int i = nbParametres-1 ; i >= 0  ; i--)
        {
            Type typeArgumentCourant = Type.gen();
            String idStringArg = idsArguments.get(i).getIdString();
            anciensTypesVarsArgs.put(idStringArg, environnementVar.get(idStringArg));
            environnementVar.put(idStringArg, typeArgumentCourant);
            typeFlecheFun = new TFun(typeArgumentCourant, typeFlecheFun);
        }
        LinkedList<EquationType> equations = new LinkedList<>(Arrays.asList(new EquationType(typeFun, typeFlecheFun)));
        changerType(typeRetour);
        equations.addAll(funDef.getE().accept(this));  
        restaurerType();    
        environnementVar.putAll(anciensTypesVarsArgs);
        equations.addAll(e.getE().accept(this));
        environnementVar.put(idStringFun, ancienTypeVar);
        return equations;
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, créé une variable de type pour chaque paramètre de la fonction
     * , pour son type de retour et pour le type de la fonction, définit la variable de type de la fonction comme type attendu de la fonction appelée, définit la variable
     * de type de chaque argument comme le type attendu de l'argument corresponant. Enfin, ajoute au résultat une équation de type composée du type attendu de e et de la variable de type
     * du résultat et une autre composée de la variable de type de la fonction et du type fonction avec pour types des paramètres les variables de type des paramètres et 
     * pour type de retour la variable de type du type de retour de chaque argument
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public LinkedList<EquationType> visit(App e) {
        Type typeVarFonction = Type.gen();
        changerType(typeVarFonction);  
        LinkedList<EquationType> equations = e.getE().accept(this);
        restaurerType();
        Type typeResultat = Type.gen();
        TFun typeFun = null;
        List<Exp> arguments = e.getEs();
        for(int i = arguments.size()-1 ; i >= 0 ; i--)
        {
            Type typeArgument = Type.gen();
            changerType(typeArgument);
            equations.addAll(arguments.get(i).accept(this));
            restaurerType();
            typeFun = new TFun(typeArgument, (typeFun == null)?typeResultat:typeFun);
        }
        equations.add(new EquationType(typeVarFonction, typeFun));
        equations.add(new EquationType(type, typeResultat)); 
        return equations;
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, créé une variable de type pour chaque composante du tuple
     * et la définit comme type attendu de la composante correspondant et ajoute au résultat une équation composée du type attendu de e et d'un type tuple composé des 
     * variables de type des composantes
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public LinkedList<EquationType> visit(Tuple e) {
        LinkedList<EquationType> equations = new LinkedList<>();
        List<Type> typesComposante = new ArrayList<>();
        for(Exp composante : e.getEs())
        {
            Type typeComposante = Type.gen();
            typesComposante.add(typeComposante);
            changerType(typeComposante);
            equations.addAll(composante.accept(this));
            restaurerType();
        }
        equations.add(new EquationType(type, new TTuple(typesComposante)));
        return equations;
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, créé une variable de type pour chaque variable déclarée
     * et la définit le type tuple composé de ces variables de type comme type attendu de la valeur affectée au tuple des variables déclarées (renvoyé par la méthode getE1).
     * La variable de type de chaque variable déclarée est associé à l'identifiant de cette variable de visiter le noeud à droite du mot clé in du let (renvoyé
     * par la méthode getE2) puis les anciennes valeurs associées sont restaurées.
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public LinkedList<EquationType> visit(LetTuple e) {
        
        HashMap<String, Type> anciensTypesComposantes = new HashMap<>();
        List<Type> typesComposantes = new ArrayList<>();
        List<Id> idsComposantes = e.getIds();
        for(Id idsComposante : idsComposantes)
        {
            typesComposantes.add(Type.gen());
        }
        changerType(new TTuple(typesComposantes));
        LinkedList<EquationType> equations = e.getE1().accept(this);
        restaurerType();
        for(int i = 0 ; i < idsComposantes.size() ; i++)
        {
            String idString = idsComposantes.get(i).getIdString();
            anciensTypesComposantes.put(idString, environnementVar.get(idString));
            environnementVar.put(idString, typesComposantes.get(i));
        }
        equations.addAll(e.getE2().accept(this));
        environnementVar.putAll(anciensTypesComposantes);
        return equations;
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, définit TInt comme type attendu du premier paramètre de
     * la création de tableau (sa taille), créé une variable de type pour le type des éléments du tableau et la définit comme type attendu du second paramètre de
     * la création de tableau (la valeur initiale des éléments). Enfin, ajoute au résultat l'équation composée du type attendu de e et du type tableau dont le type des
     * éléments est la variable de type du type des éléments du tableau
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public LinkedList<EquationType> visit(Array e) {
        changerType(new TInt());
        LinkedList<EquationType> equations = e.getE1().accept(this);
        restaurerType();
        Type typeElements = Type.gen();
        changerType(typeElements);
        equations.addAll(e.getE2().accept(this));
        restaurerType();
        equations.add(new EquationType(type, new TArray(typeElements)));
        return equations;
    }

    /**
    * Classe permettant à la méthode visitAccesTableauWorker de renvoyer a la fois les équations générées pour un accès à un tableau et la variable de type du type de
    * ses éléments
    */
   private class DonneesAccesTableau {
       private final LinkedList<EquationType> equations;
       private final Type typeElements;

       /**
        * Créé une instance de DonneesAccesTableau dont les équations sont dans equations et dont la variable de type du type des éléments du tableau sont dans typeElements
        * @param equations les équations générées pour un accès à un tableau
        * @param typeElements la variable de type du type des éléments du tableau
        */
       public DonneesAccesTableau(LinkedList<EquationType> equations, Type typeElements)
       {
           this.equations = equations;
           this.typeElements = typeElements;
       }

       /**
        * Renvoie les équations générées pour l'accès au tableau
        * @return les équations générées pour l'accès au tableau
        */
       public LinkedList<EquationType> getEquations() {
           return equations;
       }

       /**
        * Renvoie la variable de type du type des éléments du tableau
        * @return la variable de type du type des éléments du tableau
        */
       public Type getTypeElements() {
           return typeElements;
       }
   }

    /**
     * Méthode factorisant le code des méthodes visit s'appliquant à des classe héritant de AccesTableau. Créé une variable de type pour le type des éléments du tableau,
     * définit le type tableau dont le type des éléments et cette variable de type comme type attendu du tableau accédé et définit TInt comme type attendu de l'indice du
     * tableau.
     * @param e le noeud à visiter.
     * @return un couple composé des équations qui serait générée de façon identiques pour un noeud get et un noeud put et de la variable de type des éléments du tableau
     */
    private DonneesAccesTableau visitAccesTableauWorker(AccesTableau e)
    {
        Type typeElement = Type.gen();
        changerType(new TArray(typeElement));
        LinkedList<EquationType> equations = e.getE1().accept(this);
        restaurerType();
        changerType(new TInt());
        equations.addAll(e.getE2().accept(this));
        restaurerType();
        return new DonneesAccesTableau(equations, typeElement);
    }
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie les équations renvoyées par l'appel de
     * visitAccesTableauWorker sur e et l'équation composée du type attendu de e et du type des éléments du tableau renvoyé par l'appel de visitAccesTableauWorker sur e
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public LinkedList<EquationType> visit(Get e) {
        DonneesAccesTableau donneesAccesTableau = visitAccesTableauWorker(e);
        LinkedList<EquationType> equations = donneesAccesTableau.getEquations();
        equations.add(new EquationType(type, donneesAccesTableau.getTypeElements()));
        return equations;
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, définit le type des éléments du tableau renvoyé 
     * par l'appel de visitAccesTableauWorker sur e comme le type attendu de la valeur écrite dans le tableau et ajoute à son résultat les équations renvoyées 
     * par l'appel de visitAccesTableauWorker sur e et l'équation composée du type attendu de et d'une instance de TUnit (la valeur renvoyé par une écriture 
     * dans un tableau est nil)
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public LinkedList<EquationType> visit(Put e) {
        DonneesAccesTableau donneesAccesTableau = visitAccesTableauWorker(e);
        LinkedList<EquationType> equations = donneesAccesTableau.getEquations();
        changerType(donneesAccesTableau.getTypeElements());
        equations.addAll(e.getE3().accept(this));
        restaurerType();
        equations.add(new EquationType(type, new TUnit()));
        return equations;
    }
    
}
