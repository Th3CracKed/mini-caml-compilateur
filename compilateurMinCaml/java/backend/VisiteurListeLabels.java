package backend;

import arbreasml.*;
import java.util.HashSet;
import util.Constantes;
import util.NotYetImplementedException;
import visiteur.VisiteurAsml;

public class VisiteurListeLabels implements VisiteurAsml {
        
    private final HashSet<String> labels;
    
    public VisiteurListeLabels()
    {
        labels = new HashSet<>(Constantes.FONCTION_EXTERNES_ASML);
    }
    
    public HashSet<String> getLabels()
    {
        return labels;
    }
    
    private void visiteFunDefWorker(FunDefAsml e)
    {
        labels.add(e.getLabel());
    }
    
    @Override
    public void visit(FunDefConcreteAsml e) {
        visiteFunDefWorker(e);
    }
    
    @Override
    public void visit(LetFloatAsml e) {
        throw new NotYetImplementedException(); // visiteFunDefWorker(e);
    }
}
