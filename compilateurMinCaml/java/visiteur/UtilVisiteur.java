package visiteur;

import arbreasml.*;
import arbremincaml.*;

public class UtilVisiteur {

    public static void visitOpUnaireWorker(OperateurUnaire e, Visitor visiteur) {
        e.getE().accept(visiteur);
    }

    public static void visitOpBinaireWorker(OperateurBinaire e, Visitor visiteur) {
        e.getE1().accept(visiteur);
        e.getE2().accept(visiteur);
    }
    
    public static void visitAccesTabWorker(AccesTableau e, Visitor visiteur) {
        e.getE1().accept(visiteur);
        e.getE2().accept(visiteur);
    }
    
    public static void visitMemWorker(MemAsml e, VisiteurAsml visiteur) {
        e.getTableau().accept(visiteur);
        e.getIndice().accept(visiteur);
    }

    public static void visitOpArithmetiqueWorker(OperateurArithmetiqueAsml e, VisiteurAsml visiteur) {
        e.getE1().accept(visiteur);
    }
    
    public static void visitOpArithmetiqueIntWorker(OperateurArithmetiqueIntAsml e, VisiteurAsml visiteur) {
        visitOpArithmetiqueWorker(e, visiteur);
        e.getE2().accept(visiteur);
    }
    
    public static void visitOpArithmetiqueFloatWorker(OperateurArithmetiqueFloatAsml e, VisiteurAsml visiteur) {
        visitOpArithmetiqueWorker(e, visiteur);
        e.getE2().accept(visiteur);
    }
    
    public static void visitDebutIfWorker(IfAsml e, VisiteurAsml visiteur)
    {
        e.getE1().accept(visiteur);
    }
    
    public static void visitFinIfWorker(IfAsml e, VisiteurAsml visiteur)
    {
        e.getESiVrai().accept(visiteur);
        e.getESiFaux().accept(visiteur);
    }
    
    public static void visitIfIntWorker(IfIntAsml e, VisiteurAsml visiteur)
    {
        visitDebutIfWorker(e, visiteur);
        e.getE2().accept(visiteur);
        visitFinIfWorker(e, visiteur);
    }
    
    public static void visitIfFloatWorker(IfFloatAsml e, VisiteurAsml visiteur)
    {
        visitDebutIfWorker(e, visiteur);
        e.getE2().accept(visiteur);
        visitFinIfWorker(e, visiteur);
    }
    
    public static void visitCallBaseWorker(CallBaseAsml e, VisiteurAsml visiteur)
    {
        for (VarAsml argument : e.getArguments())
        {
            argument.accept(visiteur);
        }
    }
    
    public static void visitNegBaseWorker(NegBaseAsml e, VisiteurAsml visiteur) {
        e.getE().accept(visiteur);
    }
}
