import arbremincaml.Exp;
import arbremincaml.Parser;
import arbremincaml.Lexer;
import EXEMPLESASUPPRIMER.*;
import arbreasml.*;
import backend.*;
import frontend.*;
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import typage.*;
import util.*;

public class Main {

    private static final int CODE_RETOUR_ERREUR = 1;
    
    public static void main(String argv[]) {
        /*File dossierTests = new File("C:\\Users\\Justin Kossonogow\\Desktop\\SYNCHRONISE_DRIVE\\mini-caml-compilateur\\compilateurMinCaml\\tests\\mincaml\\valid");
        for (File fichier : dossierTests.listFiles()) {
            argv = new String[]{fichier.getAbsolutePath(), "-o", "out.s"};
            lancerCompilateur(argv);
        }*/
        //argv = new String[]{"C:\\Users\\Justin Kossonogow\\Desktop\\SYNCHRONISE_DRIVE\\mini-caml-compilateur\\compilateurMinCaml\\tests\\mincaml\\valid\\funcomp.ml", "-o", "out.s"};
        lancerCompilateur(argv);
    }

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
                        System.out.println("1");
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
            //e.printStackTrace();
            String message = e.getMessage();
            System.err.println("Exception " + e.getClass().getName() + " levée");
            if(message != null && !message.isEmpty())
            {
                System.err.println(" " + message);
            }
            System.exit(CODE_RETOUR_ERREUR);
        }
    }

    private static void afficherHelp() {
        System.out.println("in.ml -o out.s          : output ARM\n"
                + "-h                      : display help\n"
                + "-v                      : display version\n"
                + "in.ml -t                : type check only\n"
                + "in.ml -p                : parse only\n"
                + "in.ml -asml -o out.asml : output ASML");
    }

    public static void compiler(String nomFichierEntree, String nomFichierSortie, boolean typeCheck, boolean parseOnly, boolean outputASML) throws FileNotFoundException {
        /* ========= */ System.out.println("======================ENTREE : " + nomFichierEntree);
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
            expression.accept(new PrintVisitor());
            System.out.println();
            LinkedList<EquationType> equationsType = expression.accept(new VisiteurGenererEquationType());
            /* ========= */ System.out.println("======================EQU");
            equationsType.forEach(x -> System.out.println(x));
            System.out.println();
            equationsType = SolveurEquationType.resoudreEquations(equationsType);
            /* ========= */ System.out.println("======================SOL");
            equationsType.forEach(x -> System.out.println(x));
            System.out.println();
            if (!typeCheck) {

                expression = expression.accept(new KNormVisitor());
                /* ========= */ System.out.println("======================APRES KNORM");
                expression.accept(new PrintVisitor());
                System.out.println();
                // ??? expression = expression.accept(new VisiteurLetDansEqLeIf());
                expression.accept(new VisiteurAlphaConversion());
                /* ========= */ System.out.println("======================APRES ALPHA");
                expression.accept(new PrintVisitor());
                System.out.println();
                expression = expression.accept(new VisiteurBetaReduction());
                /* ========= */ System.out.println("======================APRES BETA");
                expression.accept(new PrintVisitor());
                System.out.println();
                expression = expression.accept(new VisiteurLetImbriques());
                /* ========= */ System.out.println("======================APRES LET");
                expression.accept(new PrintVisitor());
                System.out.println();
                expression = expression.accept(new VisiteurInlineExpansion());
                /* ========= */ System.out.println("======================APRES INLINE");
                expression.accept(new PrintVisitor());
                System.out.println();
                expression = expression.accept(new VisiteurLetImbriques());
                /* ========= */ System.out.println("======================APRES LET UNE DEUXIEME FOIS"); // l'inline expansion peut rajouter des let imbriqués
                expression.accept(new PrintVisitor());
                System.out.println();
                expression = expression.accept(new VisiteurConstantFolding());
                /* ========= */ System.out.println("======================APRES CONSTANT");
                expression.accept(new PrintVisitor());
                System.out.println();
                expression = expression.accept(new VisiteurDefinitionsInutiles());
                /* ========= */ System.out.println("======================APRES DEF INUTILES");
                expression.accept(new PrintVisitor());
                System.out.println();
                VisiteurConversionClosure vClosure = new VisiteurConversionClosure();
                expression.accept(vClosure);
                /* ========= */ System.out.println("======================APRES CONV CLOSURE");
                HashMap<String, EnvironnementClosure> closures = vClosure.getClosures();
                closures.forEach((k,v)->System.out.println(k+" est une closure avec pour variable(s) libre(s) "+v.getVariablesLibres()));
                System.out.println();
                VisiteurGenererArbreAsml visGenAbAsml = new VisiteurGenererArbreAsml(closures);
                AsmtAsml corpsFunMain = (AsmtAsml) expression.accept(visGenAbAsml);
                NoeudAsml arbreAsml = new ProgrammeAsml(FunDefConcreteAsml.creerMainFunDef(corpsFunMain), visGenAbAsml.getFunDefs());
                /* ========= */ System.out.println("======================ASML");
                arbreAsml.accept(new VisiteurGenererCodeAsml(System.out));
                if (outputASML) {
                    PrintStream fichierSortieASML = new PrintStream(nomFichierSortie);
                    arbreAsml.accept(new VisiteurGenererCodeAsml(fichierSortieASML));
                } else {
                    VisiteurRegistrePile visAllocationRegistre = new VisiteurRegistrePile(closures);
                    arbreAsml.accept(visAllocationRegistre);/* ========= */ System.out.println("\n======================EMPLACEMENTS DES VARIABLES");
                    System.out.println(visAllocationRegistre.getEmplacementsVar());
                    System.out.println();                    
                    PrintStream fichierSortieARM = new PrintStream(nomFichierSortie);
                    arbreAsml.accept(new VisiteurGenererCodeArm(visAllocationRegistre.getEmplacementsVar(), fichierSortieARM));
                    /* ========= */ System.out.println("======================ARM");
                    arbreAsml.accept(new VisiteurGenererCodeArm(visAllocationRegistre.getEmplacementsVar(), System.out));
                }
            }
        }

    }

}
