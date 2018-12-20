/*package backend;

import arbreasml.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import util.NotYetImplementedException;
import visiteur.*;

public class VisiteurImmediatDefinition implements ObjVisiteurAsml<NoeudAsml> {

    @Override
    public NoeudAsml visit(AddAsml e) {
        return e;
    }

    @Override
    public NoeudAsml visit(FunDefConcreteAsml e) {
        return new FunDefConcreteAsml(e.getLabel(), (AsmtAsml)e.getAsmt().accept(this), e.getArguments());
    }

    @Override
    public NoeudAsml visit(IntAsml e) {
        return e;
    }

    @Override
    public NoeudAsml visit(NegAsml e) {
        return e;
    }

    @Override
    public NoeudAsml visit(NopAsml e) {
        return e;
    }

    @Override
    public NoeudAsml visit(ProgrammeAsml e) {
        FunDefConcreteAsml mainFunDef = (FunDefConcreteAsml)e.getMainFunDef().accept(this);
        List<FunDefAsml> funDefs = new ArrayList<>();
        for(FunDefAsml funDef : e.getFunDefs())
        {
            funDefs.add((FunDefAsml)funDef.accept(this));
        }
        return new ProgrammeAsml(mainFunDef, funDefs);
    }

    @Override
    public NoeudAsml visit(SubAsml e) {
        return e;
    }

    @Override
    public NoeudAsml visit(VarAsml e) {
        return e;
    }

    @Override
    public NoeudAsml visit(NewAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public NoeudAsml visit(FNegAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public NoeudAsml visit(FAddAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public NoeudAsml visit(FSubAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public NoeudAsml visit(FMulAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public NoeudAsml visit(FDivAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public NoeudAsml visit(CallAsml e) {
        return e;
    }

    @Override
    public NoeudAsml visit(CallClosureAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public NoeudAsml visit(MemLectureAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public NoeudAsml visit(MemEcritureAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public NoeudAsml visit(LetFloatAsml e) {
        throw new NotYetImplementedException();
    }   
    

    @Override
    public NoeudAsml visit(IfEqIntAsml e) {
        return new IfEqIntAsml(e.getE1(), e.getE2(), (AsmtAsml)e.getESiVrai().accept(this), (AsmtAsml)e.getESiFaux().accept(this));
    }

    @Override
    public NoeudAsml visit(IfLEIntAsml e) {
        return new IfLEIntAsml(e.getE1(), e.getE2(), (AsmtAsml)e.getESiVrai().accept(this), (AsmtAsml)e.getESiFaux().accept(this));
    }

    @Override
    public NoeudAsml visit(IfGEIntAsml e) {
        return new IfGEIntAsml(e.getE1(), e.getE2(), (AsmtAsml)e.getESiVrai().accept(this), (AsmtAsml)e.getESiFaux().accept(this));
    }

    @Override
    public NoeudAsml visit(IfEquFloatAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public NoeudAsml visit(IfLEFloatAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public NoeudAsml visit(LabelFloatAsml e) {
        throw new NotYetImplementedException();
    }    

    @Override
    public NoeudAsml visit(LetAsml e) {
        ExpAsml e1Accepte = (ExpAsml)e.getE1().accept(this);
        AsmtAsml e2Accepte = (AsmtAsml)e.getE2().accept(this);
        VisiteurVariablesUtilisees visVarUtilisees = new VisiteurVariablesUtilisees();
        e2Accepte.accept(visVarUtilisees);
        VisiteurEffetDeBord visEffetDeBord = new VisiteurEffetDeBord();
        e1Accepte.accept(visEffetDeBord);
        if(!visVarUtilisees.getVariablesUtilisees().contains(e.getIdString()) && !visEffetDeBord.getAUnEffetDeBord())
        {
            return e2Accepte;
        }
        else
        {
            return new LetAsml(e.getIdString(), e1Accepte, e2Accepte);
        }
    }
    
    private class VisiteurVariablesUtilisees implements VisiteurAsml
    {
        private final HashSet<String> variablesUtilisees;
        
        public VisiteurVariablesUtilisees()
        {
            variablesUtilisees = new HashSet<>();
        }
        
        public HashSet<String> getVariablesUtilisees()
        {
            return variablesUtilisees;
        }
        
        @Override
        public void visit(VarAsml e) {
            variablesUtilisees.add(e.getIdString());
        }     
    }
    
    private class VisiteurEffetDeBord implements VisiteurAsml
    {
        private boolean aUnEffetDeBord;
        
        public VisiteurEffetDeBord()
        {
            setAUnEffetDeBord(false);
        }
        
        public boolean getAUnEffetDeBord()
        {
            return aUnEffetDeBord;
        }
        
        private void setAUnEffetDeBord(boolean aUnEffetDeBord)
        {
            this.aUnEffetDeBord = aUnEffetDeBord;
        }
        
        @Override
        public void visit(CallAsml e) {
            setAUnEffetDeBord(true);
        }   
        
        @Override
        public void visit(CallClosureAsml e) {
            throw new NotYetImplementedException(); // setAUnEffetDeBord(true);
        }  
        
        @Override
        public void visit(MemEcritureAsml e) {
            throw new NotYetImplementedException(); // setAUnEffetDeBord(true);
        }  
    }
}*/
