package visiteur;

import arbreasml.IfIntAsml;
import arbremincaml.OperateurBinaire;
import arbremincaml.OperateurUnaire;
import arbreasml.NegAsml;
import arbreasml.OperateurArithmetiqueIntAsml;

public class UtilVisiteur {

    public static void visitOpUnaireWorker(OperateurUnaire e, Visitor visiteur) {
        e.getE().accept(visiteur);
    }

    public static void visitOpBinaireWorker(OperateurBinaire e, Visitor visiteur) {
        e.getE1().accept(visiteur);
        e.getE2().accept(visiteur);
    }

    public static void visitOpArithmetiqueIntWorker(OperateurArithmetiqueIntAsml e, VisiteurAsml visiteur) {
        e.getE1().accept(visiteur);
        e.getE2().accept(visiteur);
    }
    
    public static void visitIfIntWorker(IfIntAsml e, VisiteurAsml visiteur)
    {
        e.getE1().accept(visiteur);
        e.getE2().accept(visiteur);
        e.getESiVrai().accept(visiteur);
        e.getESiFaux().accept(visiteur);
    }
}
