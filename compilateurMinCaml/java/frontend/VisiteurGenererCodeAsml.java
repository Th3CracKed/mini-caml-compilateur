package frontend;

import arbreasml.*;
import java.io.PrintStream;
import util.NotYetImplementedException;
import visiteur.VisiteurAsml;
import util.GenerateurDeCode;

public class VisiteurGenererCodeAsml extends GenerateurDeCode implements VisiteurAsml {
    
    public VisiteurGenererCodeAsml(PrintStream fichierSortie)
    {
        super(fichierSortie);
    }  
    
    private void acceptEtAugmenterIndentation(NoeudAsml e)
    {
        augmenterNiveauIndentation();        
        ecrire("\n");
        e.accept(this);
        diminuerNiveauIndentation();
    }
    
    @Override
    public void visit(AddAsml e) {        
        visitOpArithmetiqueIntWorker(e, "add");
    }

    private void visitFunDefWorker(FunDefAsml e)
    {
        ecrireAvecIndentation("let "+e.getLabel());
    }
    
    @Override
    public void visit(FunDefConcreteAsml e) {
        visitFunDefWorker(e);
        for(VarAsml argument : e.getArguments())
        {
            ecrire(" ");
            argument.accept(this);
        }
        ecrire(" = ");
        acceptEtAugmenterIndentation(e.getAsmt());
    }

    @Override
    public void visit(IntAsml e) {
        ecrireAvecIndentation(e.getValeur());
    }

    @Override
    public void visit(LetAsml e) {
        ecrireAvecIndentation("let "+e.getIdString()+" = ");        
        acceptSansIndentationSaufIf(e.getE1());
        ecrire("\n");
        ecrireAvecIndentation("in\n");
        e.getE2().accept(this);
    }

    @Override
    public void visit(NegAsml e) {
        ecrireAvecIndentation("neg ");
        acceptSansIndentationSaufIf(e.getE());
    }

    @Override
    public void visit(NopAsml e) {        
        ecrireAvecIndentation("nop");
    }

    @Override
    public void visit(ProgrammeAsml e) {
        for(FunDefAsml functionAuxiliaire : e.getFunDefs())
        {            
            functionAuxiliaire.accept(this);
            ecrire("\n\n");
        }
        e.getMainFunDef().accept(this);
    }

    private void visitOpArithmetiqueIntWorker(OperateurArithmetiqueIntAsml e, String nomOperateur)
    {                
        ecrireAvecIndentation(nomOperateur+" ");
        acceptSansIndentationSaufIf(e.getE1());
        ecrire(" ");
        acceptSansIndentationSaufIf(e.getE2());
    }
    
    @Override
    public void visit(SubAsml e) {
        visitOpArithmetiqueIntWorker(e, "sub");
    }

    @Override
    public void visit(VarAsml e) {
        ecrire(e.getIdString());
    }

    @Override
    public void visit(NewAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public void visit(FNegAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public void visit(FAddAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public void visit(FSubAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public void visit(FMulAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public void visit(FDivAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public void visit(CallAsml e) {
        ecrireAvecIndentation("call "+e.getIdString());
        for(VarAsml argument : e.getArguments())
        {
            ecrire(" ");
            argument.accept(this);
        }
    }

    @Override
    public void visit(CallClosureAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public void visit(MemLectureAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public void visit(MemEcritureAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public void visit(LetFloatAsml e) {
        throw new NotYetImplementedException();
    }

    private void acceptSansIndentationSaufIf(NoeudAsml e)
    {
        if(e instanceof IfAsml)
        {       
            acceptEtAugmenterIndentation(e);
        }
        else
        {            
            setIndentationActivee(false);            
            e.accept(this);
            setIndentationActivee(true);
        }
    }
    
    private void visitIfIntWorker(IfIntAsml e, String representationOperateur)
    {
        ecrireAvecIndentation("if ");
        acceptSansIndentationSaufIf(e.getE1());        
        ecrire(" "+representationOperateur+" ");
        acceptSansIndentationSaufIf(e.getE2()); 
        ecrire("\n");
        ecrireAvecIndentation("then ");
        acceptEtAugmenterIndentation(e.getESiVrai());
        ecrire("\n");
        ecrireAvecIndentation("else ");
        acceptEtAugmenterIndentation(e.getESiFaux());
    }
    
    @Override
    public void visit(IfEqIntAsml e) {
        visitIfIntWorker(e, "=");        
    }

    @Override
    public void visit(IfLEIntAsml e) {
        visitIfIntWorker(e, "<=");
    }

    @Override
    public void visit(IfGEIntAsml e) {
        visitIfIntWorker(e, ">=");
    }

    @Override
    public void visit(IfEquFloatAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public void visit(IfLEFloatAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public void visit(LabelFloatAsml e) {
        throw new NotYetImplementedException();
    }
   
    
}
