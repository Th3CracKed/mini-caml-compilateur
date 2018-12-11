import EXEMPLESASUPPRIMER.*;
import arbresyntaxique.*;
import visiteur.*;
import java.io.*;
import java.util.HashSet;
import util.*;

public class Main {
        private static final int CODE_RETOUR_ERREUR = 1;
        
  static public void main(String argv[]) {   
      try
      {
            String nomFichierEntree = null;
            String nomFichierSortie = "codeGenere.s";
            HashSet<String> optionsDejaRencontrees = new HashSet<>();
            for(int i = 0 ; i < argv.length ; i++)
            {
                switch (argv[i]) {
                    case "-o":
                        i++;
                        nomFichierSortie = argv[i];
                    case "-h":
                        System.out.println("-o : output file\n" +
                                "-h : display help\n" +
                                "-v : display version\n" +
                                "-t : type check only\n" +
                                "-p : parse only\n" +
                                "-asml : output ASML");
                        break;
                    case "-v":
                        System.out.println("1.0");
                        break;
                    case "-t":
                        throw new NotYetImplementedException();
                    case "-p":
                        throw new NotYetImplementedException();
                    case "-asml":
                        throw new NotYetImplementedException();
                    default:
                        if(nomFichierEntree == null)
                        {
                            throw new RuntimeException("Un seul fichier à compiler doit être spécifié");
                        }
                        nomFichierEntree = argv[i];
                        break;
                }   
                if(optionsDejaRencontrees.contains(argv[i]))
                {
                    throw new RuntimeException("Un seul fichier à compiler doit être spécifié");
                }
                optionsDejaRencontrees.add(argv[i]);
            }
            nomFichierEntree = "rsc\\mincaml\\gcd.ml";

            /*Parser p = new Parser(new Lexer(new FileReader(nomFichierEntree)));
            Exp expression = (Exp) p.parse().value;      
            assert (expression != null);

            System.out.println("------ AST ------");
            expression.accept(new PrintVisitor());
            System.out.println();

            System.out.println("------ Height of the AST ----");
            int height = Height.computeHeight(expression);
            System.out.println("using Height.computeHeight: " + height);

            ObjVisitor<Integer> v = new HeightVisitor();
            height = expression.accept(v);
            System.out.println("using HeightVisitor: " + height);*/
      }
      catch(Exception e)
      {
          System.err.println(e.getMessage());
          System.exit(CODE_RETOUR_ERREUR);
      }      
  }
}

