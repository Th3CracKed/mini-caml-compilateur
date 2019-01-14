package frontend;

import arbreasml.*;
import java.io.PrintStream;
import visiteur.VisiteurAsml;
import util.GenerateurDeCode;

/**
 * Visiteur générant du code ASML
 */
public class VisiteurGenererCodeAsml extends GenerateurDeCode implements VisiteurAsml {
    
    /**
     * Créé un visiteur générant du code ASML
     * @param fichierSortie le fichier dans lequel généré le code ASML
     */
    public VisiteurGenererCodeAsml(PrintStream fichierSortie)
    {
        super(fichierSortie);
    }  
    
    /**
     * Augmente le niveau d'indentation, visite e puis diminue le niveau d'indentation
     * @param e le noeud à visiter
     */
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
    
    /**
     * Visite le noeud e.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(AddAsml e) {        
        visitOpArithmetiqueIntWorker(e, "add");
    }

    /**
     * Méthode factorisant les méthodes visit s'appliquant à des noeuds héritant de FunDefAsml
     * @param e le noeud à visiter
     */
    private void visitFunDefWorker(FunDefAsml e)
    {
        ecrireAvecIndentation("let "+e.getLabel());
    }
    
    /**
     * Visite le noeud e.
     * @param e le noeud à visiter
     */
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

    /**
     * Visite le noeud e.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(IntAsml e) {
        ecrireAvecIndentation(e.getValeur());
    }

    /**
     * Visite le noeud e.
     * @param e le noeud à visiter
     */
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

    /**
     * Méthode factorisant les méthodes visit s'appliquant à des noeuds héritant de NegBaseAsml
     * @param e le noeud à visiter
     * @param nomOperateur le nom de l'opérateur correspondant à e (neg ou fneg)
     */
    private void visitNegBaseWorker(NegBaseAsml e, String nomOperateur)
    {
        ecrireAvecIndentation(nomOperateur+" ");
        acceptSansIndentationSaufIf(e.getE());
    }
    
    /**
     * Visite le noeud e.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(NegAsml e) {
        visitNegBaseWorker(e, "neg");
    }

    /**
     * Visite le noeud e.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(NopAsml e) {        
        ecrireAvecIndentation("nop");
    }

    /**
     * Visite le noeud e.
     * @param e le noeud à visiter
     */
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

    /**
     * Méthode factorisant le code des fonctions visit s'appliquant à un noeud héritant de OperateurArithmetiqueAsml
     * @param e le noeud à visiter
     * @param nomOperateur le nom de l'opérateur correspondant à e (comme add, fadd,...)
     */
    private void visitOpArithmetiqueWorker(OperateurArithmetiqueAsml e, String nomOperateur)
    {                
        ecrireAvecIndentation(nomOperateur+" ");
        acceptSansIndentationSaufIf(e.getE1());
        ecrire(" ");
    }
    
    /**
     * Méthode factorisant le code des fonctions visit s'appliquant à un noeud héritant de OperateurArithmetiqueIntAsml
     * @param e le noeud à visiter
     * @param nomOperateur le nom de l'opérateur correspondant à e (comme add, sub,...)
     */
    private void visitOpArithmetiqueIntWorker(OperateurArithmetiqueIntAsml e, String nomOperateur)
    {                
        visitOpArithmetiqueWorker(e, nomOperateur);
        acceptSansIndentationSaufIf(e.getE2());
    }
    
    /**
     * Méthode factorisant le code des fonctions visit s'appliquant à un noeud héritant de OperateurArithmetiqueFloatAsml
     * @param e le noeud à visiter
     * @param nomOperateur le nom de l'opérateur correspondant à e (comme fadd, fsub,...)
     */
    private void visitOpArithmetiqueFloatWorker(OperateurArithmetiqueFloatAsml e, String nomOperateur)
    {                
        visitOpArithmetiqueWorker(e, nomOperateur);
        acceptSansIndentationSaufIf(e.getE2());
    }
    
    /**
     * Visite le noeud e.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(SubAsml e) {
        visitOpArithmetiqueIntWorker(e, "sub");
    }

    /**
     * Visite le noeud e.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(VarAsml e) {
        ecrire(e.getIdString());
    }

    /**
     * Visite le noeud e.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(FNegAsml e) {
        visitNegBaseWorker(e, "fneg");
    }

    /**
     * Visite le noeud e.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(FAddAsml e) {
        visitOpArithmetiqueFloatWorker(e, "fadd");
    }

    /**
     * Visite le noeud e.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(FSubAsml e) {
        visitOpArithmetiqueFloatWorker(e, "fsub");
    }

    /**
     * Visite le noeud e.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(FMulAsml e) {
        visitOpArithmetiqueFloatWorker(e, "fmul");
    }

    /**
     * Visite le noeud e.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(FDivAsml e) {
        visitOpArithmetiqueFloatWorker(e, "fdiv");
    }

    /**
     * Méthode factorisant le code des fonctions visit s'appliquant à un noeud héritant de CallBaseAsml
     * @param e le noeud à visiter
     */
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
    
    /**
     * Visite le noeud e.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(CallAsml e) {
        ecrireAvecIndentation("call "+e.getIdString());
        visitCallWorker(e);
    }

    /**
     * Visite le noeud e.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(CallClosureAsml e) {
        ecrireAvecIndentation("call_closure "+e.getVar().getIdString());
        visitCallWorker(e);
    }    

    /**
     * Visite le noeud e.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(NewAsml e) {
        ecrireAvecIndentation("new ");
        e.getE().accept(this);
    }

    /**
     * Méthode factorisant le code des fonctions visit s'appliquant à un noeud héritant de MemAsml
     * @param e le noeud à visiter
     */
    private void visitMemWorker(MemAsml e)
    {
        ecrireAvecIndentation("mem(");
        e.getTableau().accept(this);
        ecrire(" + ");
        e.getIndice().accept(this);
        ecrire(")");
    }
    
    /**
     * Visite le noeud e.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(MemLectureAsml e) {
        visitMemWorker(e);
    }

    /**
     * Visite le noeud e.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(MemEcritureAsml e) {
        visitMemWorker(e);
        ecrire(" <- ");
        e.getValeurEcrite().accept(this);
    }
    
    /**
     * Visite le noeud e.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(LetFloatAsml e) {
        visitFunDefWorker(e);
        ecrire(" = "+e.getValeur()+"\n");
    }

    /**
     * Visite le noeud en désactivant l'indentation (sauf si il s'agit d'un noeud héritant de IfAsml) puis la réactive. Par exemple, pour let x = add y z,
     * il ne faut pas d'indentation après le égal mais pour let x = if a = b then c else d, il est préférable d'en mettre, car c et d peuvent contenir beaucoup
     * d'instructions
     * @param e 
     */
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
    
    /**
     * Méthode factorisant la génération de code des noeud héritant de IfAsml
     * @param e le noeud à visiter
     * @param representationOperateur le mot clé correspond à l'opérateur relationnel utilisé dans la condition du if (=, >= ou <=)
     */
    private void visitIfWorkerDebut(IfAsml e, String representationOperateur)
    {
        ecrireAvecIndentation("if ");
        acceptSansIndentationSaufIf(e.getE1());        
        ecrire(" "+representationOperateur+" ");
    }
    
    /**
     * Méthode factorisant la génération de code des noeud héritant de IfAsml
     * @param e le noeud à visiter
     */
    private void visitIfWorkerFin(IfAsml e)
    {
        ecrire("\n");
        ecrireAvecIndentation("then ");
        acceptEtAugmenterIndentation(e.getESiVrai());
        ecrire("\n");
        ecrireAvecIndentation("else ");
        acceptEtAugmenterIndentation(e.getESiFaux());
    }
    
    /**
     * Méthode factorisant la génération de code des noeud héritant de representationOperateur
     * @param e le noeud à visiter
     * @param representationOperateur le mot clé correspond à l'opérateur relationnel utilisé dans la condition du if (= ou <=)
     */
    private void visitIfIntWorker(IfIntAsml e, String representationOperateur)
    {
        visitIfWorkerDebut(e, representationOperateur);
        acceptSansIndentationSaufIf(e.getE2()); 
        visitIfWorkerFin(e);
    }
    
    /**
     * Méthode factorisant la génération de code des noeud héritant de IfFloatAsml
     * @param e le noeud à visiter
     * @param representationOperateur le mot clé correspond à l'opérateur relationnel utilisé dans la condition du if (=, >= ou <=)
     */
    private void visitIfFloatWorker(IfFloatAsml e, String representationOperateur)
    {
        visitIfWorkerDebut(e, representationOperateur);
        acceptSansIndentationSaufIf(e.getE2()); 
        visitIfWorkerFin(e);
    }
    
    /**
     * Visite le noeud e.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(IfEqIntAsml e) {
        visitIfIntWorker(e, "=");        
    }

    /**
     * Visite le noeud e.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(IfLEIntAsml e) {
        visitIfIntWorker(e, "<=");
    }

    /**
     * Visite le noeud e.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(IfGEIntAsml e) {
        visitIfIntWorker(e, ">=");
    }

    /**
     * Visite le noeud e.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(IfEqFloatAsml e) {
        visitIfFloatWorker(e, "=.");
    }

    /**
     * Visite le noeud e.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(IfLEFloatAsml e) {
        visitIfFloatWorker(e, "<=.");
    }       
}
