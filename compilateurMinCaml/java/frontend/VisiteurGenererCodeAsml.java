package frontend;

import arbreasml.*;
import java.io.PrintStream;
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
        if(e instanceof VarAsml)
        {
            ecrireAvecIndentation("");
        }
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
        AsmtAsml e2 = e.getE2();
        if(e2 instanceof VarAsml)
        {
            ecrireAvecIndentation("");
        }
        e2.accept(this);
    }

    private void visitNegBaseWorker(NegBaseAsml e, String nomOperateur)
    {
        ecrireAvecIndentation(nomOperateur+" ");
        acceptSansIndentationSaufIf(e.getE());
    }
    @Override
    public void visit(NegAsml e) {
        visitNegBaseWorker(e, "neg");
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
            if(functionAuxiliaire instanceof FunDefConcreteAsml)
            {
                ecrire("\n\n");
            }
        }
        e.getMainFunDef().accept(this);
    }

    private void visitOpArithmetiqueWorker(OperateurArithmetiqueAsml e, String nomOperateur)
    {                
        ecrireAvecIndentation(nomOperateur+" ");
        acceptSansIndentationSaufIf(e.getE1());
        ecrire(" ");
    }
    
    private void visitOpArithmetiqueIntWorker(OperateurArithmetiqueIntAsml e, String nomOperateur)
    {                
        visitOpArithmetiqueWorker(e, nomOperateur);
        acceptSansIndentationSaufIf(e.getE2());
    }
    
    private void visitOpArithmetiqueFloatWorker(OperateurArithmetiqueFloatAsml e, String nomOperateur)
    {                
        visitOpArithmetiqueWorker(e, nomOperateur);
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
    public void visit(FNegAsml e) {
        visitNegBaseWorker(e, "neg");
    }

    @Override
    public void visit(FAddAsml e) {
        visitOpArithmetiqueFloatWorker(e, "fadd");
    }

    @Override
    public void visit(FSubAsml e) {
        visitOpArithmetiqueFloatWorker(e, "fsub");
    }

    @Override
    public void visit(FMulAsml e) {
        visitOpArithmetiqueFloatWorker(e, "fmul");
    }

    @Override
    public void visit(FDivAsml e) {
        visitOpArithmetiqueFloatWorker(e, "fdiv");
    }

    private void visitCallWorker(CallBaseAsml e)
    {
        if(e.getArguments().isEmpty())
        {
            ecrire(" ()");
        }
        else
        {            
            for(VarAsml argument : e.getArguments())
            {
                ecrire(" ");
                argument.accept(this);
            }
        }
    }
    
    @Override
    public void visit(CallAsml e) {
        ecrireAvecIndentation("call "+e.getIdString());
        visitCallWorker(e);
    }

    @Override
    public void visit(CallClosureAsml e) {
        ecrireAvecIndentation("call_closure "+e.getVar().getIdString());
        visitCallWorker(e);
    }    

    @Override
    public void visit(NewAsml e) {
        ecrireAvecIndentation("new ");
        e.getE().accept(this);
    }

    private void visitMemWorker(MemAsml e)
    {
        ecrireAvecIndentation("mem(");
        e.getTableau().accept(this);
        ecrire(" + ");
        e.getIndice().accept(this);
        ecrire(")");
    }
    
    @Override
    public void visit(MemLectureAsml e) {
        visitMemWorker(e);
    }

    @Override
    public void visit(MemEcritureAsml e) {
        visitMemWorker(e);
        ecrire(" <- ");
        e.getValeurEcrite().accept(this);
    }
    
    @Override
    public void visit(LetFloatAsml e) {
        visitFunDefWorker(e);
        ecrire(" = "+e.getValeur()+"\n");
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
    
    private void visitIfWorkerDebut(IfAsml e, String representationOperateur)
    {
        ecrireAvecIndentation("if ");
        acceptSansIndentationSaufIf(e.getE1());        
        ecrire(" "+representationOperateur+" ");
    }
    
    private void visitIfWorkerFin(IfAsml e)
    {
        ecrire("\n");
        ecrireAvecIndentation("then ");
        acceptEtAugmenterIndentation(e.getESiVrai());
        ecrire("\n");
        ecrireAvecIndentation("else ");
        acceptEtAugmenterIndentation(e.getESiFaux());
    }
    
    private void visitIfIntWorker(IfIntAsml e, String representationOperateur)
    {
        visitIfWorkerDebut(e, representationOperateur);
        acceptSansIndentationSaufIf(e.getE2()); 
        visitIfWorkerFin(e);
    }
    
    private void visitIfFloatWorker(IfFloatAsml e, String representationOperateur)
    {
        visitIfWorkerDebut(e, representationOperateur);
        acceptSansIndentationSaufIf(e.getE2()); 
        visitIfWorkerFin(e);
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
    public void visit(IfEqFloatAsml e) {
        visitIfFloatWorker(e, "=.");
    }

    @Override
    public void visit(IfLEFloatAsml e) {
        visitIfFloatWorker(e, "<=.");
    }       
}
