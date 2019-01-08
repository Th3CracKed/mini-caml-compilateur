import arbremincaml.Exp;
import arbremincaml.Parser;
import arbremincaml.Lexer;
import EXEMPLESASUPPRIMER.*;
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

public class Main {

    private static final int CODE_RETOUR_ERREUR = 1;
    
    public static float myCos(float z)
    {
        /*
        float[] angle = new float[]{0.785398163f,0.099668652f,0.009999667f,0.001f,0.0001f,1e-5f,1e-6f,1e-7f,1e-8f};
        float x = 1.0f;
        float y = 0.0f;
        float r = 1.0f;
        float z;
        for(int i = 0 ; i < angle.length ; i++)
        {
            while(a >= angle[i])
            {
                float puissance = (float)Math.pow(10.0, (double)i);
                z = x-y/puissance;
                y = y+x/puissance;
                x = z;
                r *= (float)Math.sqrt(1+Math.pow(10.0, (double)(-2*i)));
                a -= angle[i];
            } 
        }
        return x/r;
        */
        float pi = (float)Math.PI;
        float deuxPi = 2.0f*pi;
        float quotient = (float)((int)(z/deuxPi));
        System.out.print(z+" : ");
        //System.out.print(t+" : ");
        z -= quotient*deuxPi;
        float piSur2 = pi/2;
        boolean doitRenvoyerOppose = false;
        if(z <= 0)
        {
            z *= -1;
        }
        if(z >= piSur2)
        {
            if(z >= 3*piSur2)
            {
                z -= deuxPi;
            }
            else
            {
                z = pi - z;
                doitRenvoyerOppose = true;
            }
        }
        if(z <= 0)
        {
            z *= -1;
        }
        System.out.println(z);
        float[] angle = new float[]{.7854f,.46365f,.24498f,.12435f,.06242f,.03124f,.01562f,.00781f,.00391f,.00195f,9.8E-4f,4.9E-4f,2.4E-4f,1.2E-4f,6.E-5f};
        float x = .60725f;
        float y = 0.0f;
        for(int i = 0 ; i < angle.length ; i++)
        {
            float s = 1.0f;
            if(z < 0)
            {
                s *= -1.0f;
            }
            float puissance = (float)Math.pow(2.0, (double)i);
            float temp = x-s*y/puissance;
            y += s*x/puissance;
            x = temp;
            z -= s*angle[i];
        }
        if(doitRenvoyerOppose)
        {
            x *= -1;
        }
        return x;
    }
    
    public static double inverseFact(int n)
    {
        double resultat = 1;
        for(int i = 1 ; i <= n ; i++)
        {
            resultat *= 1.0/i;
        }
        return resultat;
    }
    public static double myCos2(double t)
    {
        double resultat = 0;
        int signe = 1;
        for(int i = 0 ; i <= 20 ; i+=2)
        {
            double terme = (double)Math.pow(t, i)*inverseFact(i);
            resultat += terme*signe;
            double coefficient = signe*inverseFact(i);
            System.out.print("+ "+(float)coefficient/*(new BigDecimal(coefficient, new MathContext(8, RoundingMode.HALF_EVEN))).stripTrailingZeros().toPlainString()*/+"*x^"+i);
            signe *= -1;
        }
        System.out.println();
        //return resultat;
        double pi = (float)Math.PI;
        double deuxPi = 2.0f*pi;
        double quotient = (float)((int)(t/deuxPi));
        //System.out.print(t+" : ");
        t -= quotient*deuxPi;
        double piSur2 = pi/2;
        boolean doitRenvoyerOppose = false;
        if(t <= 0)
        {
            t *= -1;
        }
        if(t >= piSur2)
        {
            if(t >= 3*piSur2)
            {
                t -= deuxPi;
            }
            else
            {
                t = pi - t;
                doitRenvoyerOppose = true;
            }
        }
        /*if(t<=-piSur2-0.001 || t>=piSur2+0.001)
        {
            throw new RuntimeException(t+"");
        }*/
        //System.out.println(t);
        //1.0, -0.5, 0.041666668, -0.0013888889, 2.4801588E-5, -2.755732E-7, 2.0876758E-9
        double[] coefficients = new double[]{1, -0.5, 0.0416666679084301, -0.001388888922519982, 0.0000248015876422869, -0.0000002755731998149713, 0.00000000208767581000302}; // abs(cos(x)-(x^0*1.0+x^2*-0.5+x^4*0.041666668+x^6*-0.0013888889+x^8*2.4801588E-5+x^10*-2.755732E-7+x^12*2.0876758E-9)) <= 2^-23 si -pi/2 <= x <= pi/2 (2^-23 est la précision (plus petite valeur strictement positive) des flottants simple précision). Ce polynôme est la somme des monomes de degre <= 12 du développement en série entière de cos(x)
        double res = coefficients[coefficients.length-1];
        for(int i = coefficients.length-2 ; i >= 0 ; i--)
        {
            res *= t*t;
            res += coefficients[i];
        }
        if(doitRenvoyerOppose)
        {
            res *= -1;
        }
        /*float resultat = 0;
        for(int i = 0 ; i < coefficients.length ; i++)
        {
            resultat += coefficients[i]*Math.pow(t, (float)(2*i));
        }*/
        return res;
        /*int n = 24;
        for(int i = 0 ; i <= n ; i++)
        {
            float xi = ((float)Math.PI/2.0f)*((float)i/(float)n);
            System.out.print(xi+", ");
            //System.out.print("("+xi+", "+(float)Math.cos(xi)+")");
        }
        System.out.println();
        for(int i = 0 ; i <= n ; i++)
        {
            float xi = ((float)Math.PI/2.0f)*((float)i/(float)n);
            System.out.print(Math.cos(xi)+", ");
            //System.out.print("("+xi+", "+(float)Math.cos(xi)+")");
        }
        System.out.println();
        return resultat;*/
        /*float p = 0;
        int n = 15;
        for(int i = 0 ; i <= n ; i++)
        {
            float ai = ((float)Math.PI/2.0f)*((float)i/(float)n);
            float fi = (float)Math.cos(ai);
            float L = 1;
            for(int j = 0 ; j <= n ; j++)
            {
                if(j != i)
                {
                    float aj = ((float)Math.PI/2.0f)*((float)j/(float)n);
                    L *= (t - aj)/(ai - aj);
                }
            }
            p *= p+L*fi;
        }        
        return p;*/
        /*(i) P := 0
(ii) Pour i ∈ [0 : d] faire
(a) L := 1
(b) Pour j ∈ [0 : i−1;i+1 : d], L := L×(t −aj)/(ai −aj)
(c) P := P+L× fi*/
    }
    
    public static void main(String argv[]) {
        /*java.util.Random rand = new java.util.Random();
        double erreurMax = 0;
        int n = 100;
        for(int i = -n ; i <= n ; i++)
        {
            float randDouble = -1.0f;
            //do
            {
                randDouble = (float)rand.nextInt(1000);
            } //while(randDouble < 0.0f || randDouble > (float)Math.PI/2.0f);    
            //randDouble = ((float)i/(float)n)*(float)Math.PI*0.5f;
            double erreur = Math.abs((float)Math.cos(randDouble)-myCos2(randDouble));
            erreurMax = Math.max(erreurMax, erreur);
        }
        System.out.println("erreur max : "+erreurMax);*/
        /*File dossierTests = new File("C:\\Users\\Justin Kossonogow\\Desktop\\SYNCHRONISE_DRIVE\\mini-caml-compilateur\\compilateurMinCaml\\tests\\typechecking\\valid");
        for (File fichier : dossierTests.listFiles()) {
            argv = new String[]{fichier.getAbsolutePath(), "-o", "out.s"};
            lancerCompilateur(argv);
        }  */
        //argv = new String[]{"C:\\Users\\Justin Kossonogow\\Desktop\\SYNCHRONISE_DRIVE\\mini-caml-compilateur\\compilateurMinCaml\\tests\\mincaml\\valid\\even-odd.ml", "-o", "out.s", "-asml"};
        //argv = new String[]{"C:\\Users\\Justin Kossonogow\\Desktop\\SYNCHRONISE_DRIVE\\mini-caml-compilateur\\compilateurMinCaml\\tests\\typechecking\\invalid\\funRenvoyantFunRenvoyantElleMeme.ml", "-o", "out.s"};
        //System.out.println("myCos2(1.04) : "+myCos2(1.04));
        //System.out.println(0.0416666679084301f);
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
            System.err.print("Exception " + e.getClass().getName() + " levée");
            if(message != null && !message.isEmpty())
            {
                System.err.print(" : " + message);
            }
            System.err.println();
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
                    VisiteurOptionsGenerationDeCode visOptionsGenCodeArm = new VisiteurOptionsGenerationDeCode();
                    arbreAsml.accept(visOptionsGenCodeArm);
                    arbreAsml.accept(new VisiteurGenererCodeArm(visAllocationRegistre.getEmplacementsVar(), fichierSortieARM, visOptionsGenCodeArm.getOptionsGenCodeArm()));
                    /* ========= */ System.out.println("======================ARM");
                    arbreAsml.accept(new VisiteurGenererCodeArm(visAllocationRegistre.getEmplacementsVar(), System.out, visOptionsGenCodeArm.getOptionsGenCodeArm()));
                }
            }
        }

    }

}
