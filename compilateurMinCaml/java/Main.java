/*
import arbremincaml.Exp;
import arbremincaml.Parser;
import arbremincaml.Lexer;
import EXEMPLESASUPPRIMER.*;
import arbreasml.*;
import backend.*;
import frontend.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import typage.*;
import util.*;

//test commit

public class Main {
        private static final int CODE_RETOUR_ERREUR = 1;
  static public void main(String argv[]) {   
      try
      {       
            String nomFichierEntree = null;
            String nomFichierSortie = "codeGenere."+(Arrays.asList(argv).contains("-asml")?"asml":"s");
            HashSet<String> optionsDejaRencontrees = new HashSet<>();
            for(int i = 0 ; i < argv.length ; i++)
            {
                switch (argv[i]) {
                    case "-o":
                        i++;
                        nomFichierSortie = argv[i];
                        throw new NotYetImplementedException();
                    case "-h":
                        System.out.println("-o : output file\n" +
                                "-h : display help\n" +
                                "-v : display version\n" +
                                "-t : type check only\n" +
                                "-p : parse only\n" +
                                "-asml : output ASML");
                        break;
                    case "-v":
                        System.out.println("1");
                        break;
                    case "-t":
                        throw new NotYetImplementedException();
                    case "-p":
                        throw new NotYetImplementedException();
                    case "-asml":
                        throw new NotYetImplementedException();
                    default:
                        if(nomFichierEntree != null)
                        {
                            throw new CompilationException("Un seul fichier à compiler doit être spécifié");
                        }
                        nomFichierEntree = argv[i];
                        break;
                }   
                if(optionsDejaRencontrees.contains(argv[i]))
                {
                    throw new CompilationException("L'option "+argv[i]+" ne peut pas etre utilisée 2 fois");
                }
                optionsDejaRencontrees.add(argv[i]);
            }            
            //nomFichierEntree = "rsc/tests/letImbriques/valid/letImbriques.ml";
            compilerFichiersRepertoire(new File("rsc/tests/typechecking/valid"), false, false);
            compilerFichiersRepertoire(new File("rsc/tests/frontend"), false, false);            
            compilerFichiersRepertoire(new File("rsc/tests/typechecking/invalid"), true, false);
            compilerFichiersRepertoire(new File("rsc/tests/backend/registre/valid"), false, true);   
            compilerFichiersRepertoire(new File("rsc/tests/frontend/genererAsml/valid"), false, true);   
      }
      catch(Exception e)
      {
          //e.printStackTrace();
          if(e.getMessage() == null || e.getMessage().isEmpty())
          {
            System.err.println("Exception "+e.getClass().getName()+" levée");
          }
          else
          {
              System.err.println(e.getMessage());
          }
          System.exit(CODE_RETOUR_ERREUR);
      }      
  }
 
  private static void compiler(String nomFichierEntree)
  {              
                System.out.println("=======================================================");
                System.out.println("======   "+nomFichierEntree+" ======");
                System.out.println("=======================================================");
                Parser p = null;
                try
                {
                    p = new Parser(new Lexer(new FileReader(nomFichierEntree)));
                }
                catch(FileNotFoundException e)
                {
                    throw new CompilationException("Le fichier à compiler n'existe pas");
                }
                Exp expression;      
                try {
                    expression = (Exp) p.parse().value;
                } catch (Exception ex) {
                    throw new CompilationException(ex.getMessage());
                }
                assert (expression != null);
                System.out.println("------ ENTREE ------");
                expression.accept(new PrintVisitor());
                System.out.println();
                System.out.println("------ LISTE EQUATIONS DE TYPE NON RESOLUES ------");
                // let x = (let y = 1 in 1) in ()
                // Var x = new Var(Id.gen());
                // Var y = new Var(Id.gen());
                // expression = new Let(x.getId(), Type.gen(), new Let(y.getId(), Type.gen(), new Int(1), new Int(1)), new Unit());
                LinkedList<EquationType> equationsType = expression.accept(new VisiteurGenererEquationType());
                for(EquationType equationType : equationsType)
                {                
                    System.out.println(equationType.getT1()+" = "+equationType.getT2());
                }
                System.out.println(); 
                System.out.println("------ LISTE EQUATIONS DE TYPE RESOLUES ------");
                equationsType = SolveurEquationType.resoudreEquations(equationsType);
                for(EquationType equationType : equationsType)
                {                
                    System.out.println(equationType.getT1()+" = "+equationType.getT2());
                }
                System.out.println();
                System.out.println("------ PROGRAMME APRES KNORMALISATION(ET ETAPES PRECEDENTES) ------");
                expression = expression.accept(new KNormVisitor());
                // ??? expression = expression.accept(new VisiteurLetDansEqLeIf());
                expression.accept(new PrintVisitor());
                System.out.println(); 
                System.out.println("------ PROGRAMME APRES ALPHACONVERSIONS (ET ETAPES PRECEDENTES)  ------");
                expression.accept(new VisiteurAlphaConversion());
                expression.accept(new PrintVisitor());
                System.out.println();  
                System.out.println("------ PROGRAMME APRES BETAREDUCTION (ET ETAPES PRECEDENTES)  ------");
                expression = expression.accept(new VisiteurBetaReduction());
                expression.accept(new PrintVisitor());
                System.out.println();  
                System.out.println("------ PROGRAMME APRES REDUCTION LET IMBRIQUES (ET ETAPES PRECEDENTES)  ------");
                expression = expression.accept(new VisiteurLetImbriques());
                expression.accept(new PrintVisitor());
                System.out.println();  
                System.out.println("------ PROGRAMME APRES INLINE EXPANSION (ET ETAPES PRECEDENTES)  ------");
                expression = expression.accept(new VisiteurInlineExpansion());
                expression.accept(new PrintVisitor());
                System.out.println();  
                System.out.println("------ PROGRAMME APRES CONSTANT FOLDING (ET ETAPES PRECEDENTES)  ------");
                expression = expression.accept(new VisiteurConstantFolding());
                expression.accept(new PrintVisitor());
                System.out.println();  
                System.out.println("------ PROGRAMME APRES ELIMINATION DEFINITIONS INUTILES (ET ETAPES PRECEDENTES)  ------");
                expression = expression.accept(new VisiteurDefinitionsInutiles());
                expression.accept(new PrintVisitor());
                System.out.println();
                System.out.println("------ PROGRAMME APRES CLOSURE CONVERSION (ET ETAPES PRECEDENTES)  ------");
                expression = expression.accept(new VisiteurConversionClosure());
                expression.accept(new PrintVisitor());
                System.out.println();
                System.out.println("------ CODE ASML GENERE  ------");
                NoeudAsml arbreAsml = new ProgrammeAsml(FunDefConcreteAsml.creerMainFunDef((AsmtAsml)expression.accept(new VisiteurGenererArbreAsml())), new ArrayList<>());
                arbreAsml.accept(new VisiteurGenererCodeAsml(System.out));
                System.out.println();  
                // System.out.println("------ PROGRAMME APRES 13 BIT IMMEDIATE OPTIMIZATION (ET ETAPES PRECEDENTES)   ------");
                // arbreAsml = arbreAsml.accept(new VisiteurImmediatConstante());
                // arbreAsml = arbreAsml.accept(new VisiteurImmediatDefinition());  
                // arbreAsml.accept(new VisiteurGenererCodeAsml(System.out));
                System.out.println("------ EMPLACEMENT MEMOIRE DES VARIABLES ------");
                VisiteurRegistrePile visAllocationRegistre = new VisiteurRegistrePile();
                arbreAsml.accept(visAllocationRegistre);
                System.out.println(visAllocationRegistre.getEmplacementsVar());
                System.out.println();
                System.out.println("------ LABELS (FONCTION ET FLOAT) DU PROGRAMME ------");
                VisiteurListeLabels visListeLabels = new VisiteurListeLabels();
                arbreAsml.accept(visListeLabels);
                System.out.println(visListeLabels.getLabels());
                System.out.println();
                System.out.println("------ CODE ARM GENERE ------");
                arbreAsml.accept(new VisiteurGenererCodeArm(visAllocationRegistre.getEmplacementsVar(), System.out, visListeLabels.getLabels()));
                System.out.println();
  }
  
  private static void testerBackend(String nomFichierEntree)
  {
                System.out.println("=======================================================");
                System.out.println("======   "+nomFichierEntree+" ======");
                System.out.println("=======================================================");
                Parser p = null;
                try
                {
                    p = new Parser(new Lexer(new FileReader(nomFichierEntree)));
                }
                catch(FileNotFoundException e)
                {
                    throw new CompilationException("Le fichier à compiler n'existe pas");
                }
                Exp expression;      
                try {
                    expression = (Exp) p.parse().value;
                } catch (Exception ex) {
                    throw new CompilationException(ex.getMessage());
                }
                assert (expression != null);
                System.out.println("------ ENTREE ------");
                expression.accept(new PrintVisitor());
                System.out.println();
                System.out.println("------ LISTE EQUATIONS DE TYPE NON RESOLUES ------");
                // let x = (let y = 1 in 1) in ()
                // Var x = new Var(Id.gen());
                // Var y = new Var(Id.gen());
                // expression = new Let(x.getId(), Type.gen(), new Let(y.getId(), Type.gen(), new Int(1), new Int(1)), new Unit());
                LinkedList<EquationType> equationsType = expression.accept(new VisiteurGenererEquationType());
                for(EquationType equationType : equationsType)
                {                
                    System.out.println(equationType.getT1()+" = "+equationType.getT2());
                }
                System.out.println(); 
                System.out.println("------ LISTE EQUATIONS DE TYPE RESOLUES ------");
                equationsType = SolveurEquationType.resoudreEquations(equationsType);
                for(EquationType equationType : equationsType)
                {                
                    System.out.println(equationType.getT1()+" = "+equationType.getT2());
                }
                System.out.println();                
                System.out.println("------ CODE ASML GENERE  ------");
                NoeudAsml arbreAsml = new ProgrammeAsml(FunDefConcreteAsml.creerMainFunDef((AsmtAsml)expression.accept(new VisiteurGenererArbreAsml())), new ArrayList<>());
                arbreAsml.accept(new VisiteurGenererCodeAsml(System.out));
                System.out.println();  
                // System.out.println("------ PROGRAMME APRES 13 BIT IMMEDIATE OPTIMIZATION (ET ETAPES PRECEDENTES)   ------");
                // arbreAsml = arbreAsml.accept(new VisiteurImmediatConstante());
                // arbreAsml = arbreAsml.accept(new VisiteurImmediatDefinition());
                // arbreAsml.accept(new VisiteurGenererCodeAsml(System.out));
                System.out.println("------ EMPLACEMENT MEMOIRE DES VARIABLES ------");
                VisiteurRegistrePile visAllocationRegistre = new VisiteurRegistrePile();
                arbreAsml.accept(visAllocationRegistre);
                System.out.println(visAllocationRegistre.getEmplacementsVar());
                System.out.println();
                System.out.println("------ LABELS (FONCTION ET FLOAT) DU PROGRAMME ------");
                VisiteurListeLabels visListeLabels = new VisiteurListeLabels();
                arbreAsml.accept(visListeLabels);
                System.out.println(visListeLabels.getLabels());
                System.out.println();
                System.out.println("------ CODE ARM GENERE ------");
                arbreAsml.accept(new VisiteurGenererCodeArm(visAllocationRegistre.getEmplacementsVar(), System.out, visListeLabels.getLabels()));
                System.out.println();
  }
  
  private static void compilerFichiersRepertoire(File fichier, boolean doitLancerUneException, boolean testerSeulementBackend) {
        if(fichier.isDirectory())
        {
            for(File fichiers : fichier.listFiles())
            {
                compilerFichiersRepertoire(fichiers, doitLancerUneException, testerSeulementBackend);
            }
        }
        else
        {
            Exception ex = null;
            try
            {            
                if(testerSeulementBackend)
                {
                    testerBackend(fichier.getPath());
                }
                else
                {                    
                    compiler(fichier.getPath());
                }
            }
            catch(Exception e)
            {
                ex = e;//throw e;
            }
            finally
            {
                if(doitLancerUneException != (ex != null))
                {
                    throw ((ex == null) ? new RuntimeException("Un programme invalide a passé la compilation") : new RuntimeException(ex.getMessage()));
                }
            }
        }
    }
 }





 */

// MAIN QUI RESPECTE l'INTERFACE EN LIGNE DE COMMANDE
import arbremincaml.Exp;
import arbremincaml.Parser;
import arbremincaml.Lexer;
import EXEMPLESASUPPRIMER.*;
import arbreasml.*;
import backend.*;
import frontend.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import typage.*;
import util.*;

public class Main {

    private static final int CODE_RETOUR_ERREUR = 1;

    static public void main(String argv[]) {
        /*File dossierTests = new File("C:\\Users\\Justin Kossonogow\\Desktop\\SYNCHRONISE_DRIVE\\mini-caml-compilateur\\compilateurMinCaml\\tests\\TESTEVALUATIONSH\\valid");
        for (File fichier : dossierTests.listFiles()) {
            argv = new String[]{fichier.getAbsolutePath(), "-o", "out.s"};
            lancerCompilateur(argv);
        }*/
        //argv = new String[]{"C:\\Users\\Justin Kossonogow\\Desktop\\SYNCHRONISE_DRIVE\\mini-caml-compilateur\\compilateurMinCaml\\tests\\TESTEVALUATIONSH\\valid\\ifthenelse2.ml", "-t"};
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
            e.printStackTrace();
            if (e.getMessage() == null || e.getMessage().isEmpty()) {
                System.err.println("Exception " + e.getClass().getName() + " levée");
            } else {
                System.err.println(e.getMessage());
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
                expression = expression.accept(new VisiteurConversionClosure());
                /* ========= */ System.out.println("======================APRES CONV CLOSURE");
                expression.accept(new PrintVisitor());
                System.out.println();
                VisiteurGenererArbreAsml visGenAbAsml = new VisiteurGenererArbreAsml();
                AsmtAsml corpsFunMain = (AsmtAsml) expression.accept(visGenAbAsml);
                NoeudAsml arbreAsml = new ProgrammeAsml(FunDefConcreteAsml.creerMainFunDef(corpsFunMain), visGenAbAsml.getFunDefs());
                /* ========= */ System.out.println("======================ASML");
                arbreAsml.accept(new VisiteurGenererCodeAsml(System.out));
                if (outputASML) {
                    PrintStream fichierSortieASML = new PrintStream(nomFichierSortie);
                    arbreAsml.accept(new VisiteurGenererCodeAsml(fichierSortieASML));
                } else {
                    VisiteurRegistrePile visAllocationRegistre = new VisiteurRegistrePile();
                    arbreAsml.accept(visAllocationRegistre);/* ========= */ System.out.println("\n======================EMPLACEMENTS DES VARIABLES");
                    System.out.println(visAllocationRegistre.getEmplacementsVar());
                    System.out.println();
                    VisiteurListeLabels visListeLabels = new VisiteurListeLabels();
                    arbreAsml.accept(visListeLabels);
                    /* ========= */ System.out.println("======================LABELS");
                    System.out.println(visListeLabels.getLabels());
                    PrintStream fichierSortieARM = new PrintStream(nomFichierSortie);
                    arbreAsml.accept(new VisiteurGenererCodeArm(visAllocationRegistre.getEmplacementsVar(), fichierSortieARM, visListeLabels.getLabels()));
                    /* ========= */ System.out.println("======================ARM");
                    arbreAsml.accept(new VisiteurGenererCodeArm(visAllocationRegistre.getEmplacementsVar(), System.out, visListeLabels.getLabels()));
                }
            }
        }

    }

}
