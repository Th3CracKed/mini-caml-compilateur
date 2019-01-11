import arbremincaml.Exp;
import arbremincaml.Parser;
import arbremincaml.Lexer;
import arbreasml.*;
import arbremincaml.*;
import backend.*;
import frontend.*;
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import typage.*;
import util.*;

/*
 Classe contenant la méthode point d'entrée du compilateur (main)
*/
public class Main {

    private static final int CODE_RETOUR_ERREUR = 1;
    
    /**
     * Méthode point d'entrée du compilateur
     * @param argv les arguments de la ligne de commande
     */
    public static void main(String argv[]) {
        lancerCompilateur(argv);
    }

    /**
     * Méthode lançant le compilateur avec les arguments de la ligne de commande contenus dans son paramètre argv.
     * @param argv les arguments de la ligne de commande
     */
    static public void lancerCompilateur(String argv[]) {
        try {
            String nomFichierEntree = null;
            String nomFichierSortie = null;
            HashSet<String> optionsDejaRencontrees = new HashSet<>();
            boolean typeCheck = false;
            boolean parseOnly = false;
            boolean outputASML = false;
            boolean tousLesCasBons = true;
            boolean aBesoinDeFichierEntree = false;
            if (argv.length == 0) {
                afficherHelp();
                tousLesCasBons = false;
            }
            for (int i = 0; i < argv.length; i++) {
                switch (argv[i]) {
                    case "-o":
                        i++;
                        if (i == argv.length) {
                            afficherHelp();
                            tousLesCasBons = false;
                        } else {
                            nomFichierSortie = argv[i];
                            aBesoinDeFichierEntree = true;
                        }
                        break;
                    case "-h":
                        afficherHelp();
                        tousLesCasBons = false;
                        break;
                    case "-v":
                        System.out.println("Version "+1);
                        if (i == argv.length - 1) {
                            tousLesCasBons = false;
                        }
                        break;
                    case "-t":
                        typeCheck = true;
                        break;
                    case "-p":
                        parseOnly = true;
                        break;
                    case "-asml":
                        outputASML = true;
                        aBesoinDeFichierEntree = true;
                        break;
                    default:
                        if (nomFichierEntree != null) {
                            tousLesCasBons = false;
                            throw new MyCompilationException("Un seul fichier à compiler doit être spécifié");
                        }
                        nomFichierEntree = argv[i];
                        break;
                }
                if (optionsDejaRencontrees.contains(argv[i])) {
                    throw new MyCompilationException("L'option " + argv[i] + " ne peut pas etre utilisée 2 fois");
                }
                optionsDejaRencontrees.add(argv[i]);
            }
            if (aBesoinDeFichierEntree && nomFichierEntree == null) {
                afficherHelp();
            } else {
                if (tousLesCasBons) {
                    compiler(nomFichierEntree, nomFichierSortie, typeCheck, parseOnly, outputASML);
                }
            }
        } catch (Exception e) {
            String message = e.getMessage();
            System.err.print("Exception " + e.getClass().getName() + " levée");
            if(message != null && !message.isEmpty())
            {
                System.err.print(" : " + message);
            }
            System.err.println();
            System.exit(CODE_RETOUR_ERREUR);
        }
    }

    /**
     * Affiche l'aide du compilateur en indiquant les options utilisables en ligne de commande et leur signification
     */
    private static void afficherHelp() {
        System.out.println("in.ml -o out.s          : output ARM\n"
                         + "-h                      : display help\n"
                         + "-v                      : display version\n"
                         + "in.ml -t                : type check only\n"
                         + "in.ml -p                : parse only\n"
                         + "in.ml -asml -o out.asml : output ASML");
    }

    /**
     * Lance le compilateur en lui indiquant l'option choisie (vérification du typage, compilation en ASML)
     * @param nomFichierEntree le nom du fichier en entrée à compiler
     * @param nomFichierSortie le nom du fichier de sortie à créer
     * @param typeCheck booléen vrai si le compilateur doit s'arrêter après la vérification du typage
     * @param parseOnly booléen vrai si le compilateur doit s'arrêter après l'analyse syntaxique
     * @param outputASML booléen vrai si le compilateur doit compiler en ASML
     * @throws FileNotFoundException 
     */
    public static void compiler(String nomFichierEntree, String nomFichierSortie, boolean typeCheck, boolean parseOnly, boolean outputASML) throws FileNotFoundException {
        Parser p = null;
        try {
            
            p = new Parser(new Lexer(new FileReader(nomFichierEntree)));
        } catch (FileNotFoundException e) {
            throw new MyCompilationException("Le fichier à compiler n'existe pas");
        }
        Exp expression;
        try {
            
            expression = (Exp) p.parse().value;
        } catch (Exception ex) {
            throw new MyCompilationException(ex.getMessage());
        }
        if (!parseOnly) {
            LinkedList<EquationType> equationsType = expression.accept(new VisiteurGenererEquationType());
            equationsType = SolveurEquationType.resoudreEquations(equationsType); // les types des variables ne sont pas utiliser mais l'appel à résoudre équation permet de lever une exception si le programme est mal typé pour stopper la compilation
            if (!typeCheck) {
                expression = expression.accept(new KNormVisitor());
                expression.accept(new VisiteurAlphaConversion());
                expression = expression.accept(new VisiteurBetaReduction());
                expression = expression.accept(new VisiteurLetImbriques());
                expression = expression.accept(new VisiteurInlineExpansion());
                expression = expression.accept(new VisiteurLetImbriques()); // l'inline expansion peut rajouter des let imbriqués, il faut donc appliquer VisiteurLetImbriques une deuxieme fois
                expression = expression.accept(new VisiteurConstantFolding());
                expression = expression.accept(new VisiteurDefinitionsInutiles());
                VisiteurConversionClosure vClosure = new VisiteurConversionClosure();
                expression.accept(vClosure);
                // on a besoin d'indiquer au visiteur générant l'arbre ASML quelles sont les variables de type float pour qu'il détermine pour chaque noeud if si
                // s'agit d'une comparaison d'entier ou de float. Il faut donc refaire l'etape du typage car l'arbre a ete modifie.
                HashMap<String, EnvironnementClosure> closures = vClosure.getClosures();
                equationsType = expression.accept(new VisiteurGenererEquationType());
                equationsType = SolveurEquationType.resoudreEquations(equationsType);
                HashSet<String> tVarFloats = new HashSet<>();
                for(EquationType equation : equationsType)
                {
                    Type t1 = equation.getT1();
                    if(t1 instanceof TVar && equation.getT2() instanceof TFloat)
                    {
                        tVarFloats.add(((TVar)t1).getV());
                    }
                }
                VisiteurGenererArbreAsml visGenAbAsml = new VisiteurGenererArbreAsml(closures, tVarFloats);
                AsmtAsml corpsFunMain = (AsmtAsml) expression.accept(visGenAbAsml);              
                NoeudAsml arbreAsml = new ProgrammeAsml(FunDefConcreteAsml.creerMainFunDef(corpsFunMain), visGenAbAsml.getFunDefs());
                if (outputASML) {
                    PrintStream fichierSortieASML = new PrintStream(nomFichierSortie);
                    arbreAsml.accept(new VisiteurGenererCodeAsml(fichierSortieASML));
                } else {
                    VisiteurAllocationRegistre visAllocationRegistre = new VisiteurAllocationRegistreTreeScan(closures);
                    arbreAsml.accept(visAllocationRegistre);                 
                    PrintStream fichierSortieARM = new PrintStream(nomFichierSortie);
                    VisiteurOptionsGenerationDeCode visOptionsGenCodeArm = new VisiteurOptionsGenerationDeCode();
                    arbreAsml.accept(visOptionsGenCodeArm);
                    arbreAsml.accept(new VisiteurGenererCodeArm(visAllocationRegistre.getEmplacementsVar(), fichierSortieARM, visOptionsGenCodeArm.getOptionsGenCodeArm()));
                }
            }
        }

    }

}
