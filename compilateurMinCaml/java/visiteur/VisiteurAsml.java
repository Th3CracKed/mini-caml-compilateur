package visiteur;

import arbreasml.*;
import util.NotYetImplementedException;

public interface VisiteurAsml {

    default void visit(AddAsml e) {
        UtilVisiteur.visitOpArithmetiqueIntWorker(e, this);
    }

    default void visit(FunDefConcreteAsml e) {
        for (VarAsml argument : e.getArguments()) {
            argument.accept(this);
        }
        e.getAsmt().accept(this);
    }

    default void visit(IntAsml e) {

    }

    default void visit(LetAsml e) {
        e.getE1().accept(this);
        e.getE2().accept(this);
    }

    default void visit(NegAsml e) {
        e.getE().accept(this);
    }

    default void visit(NopAsml e) {

    }

    default void visit(ProgrammeAsml e) {
        e.getMainFunDef().accept(this);
        for (FunDefAsml funDef : e.getFunDefs()) {
            funDef.accept(this);
        }
    }

    default void visit(SubAsml e) {
        UtilVisiteur.visitOpArithmetiqueIntWorker(e, this);
    }

    default void visit(VarAsml e) {

    }

    default void visit(NewAsml e) {
        throw new NotYetImplementedException();
    }

    default void visit(FNegAsml e) {
        throw new NotYetImplementedException();
    }

    default void visit(FAddAsml e) {
        throw new NotYetImplementedException();
    }

    default void visit(FSubAsml e) {
        throw new NotYetImplementedException();
    }

    default void visit(FMulAsml e) {
        throw new NotYetImplementedException();
    }

    default void visit(FDivAsml e) {
        throw new NotYetImplementedException();
    }

    default void visit(CallAsml e) {
        for (VarAsml argument : e.getArguments()) {
            argument.accept(this);
        }
    }

    default void visit(CallClosureAsml e) {
        throw new NotYetImplementedException();
    }

    default void visit(MemLectureAsml e) {
        throw new NotYetImplementedException();
        //UtilVisiteur.visitMemWorker(e, this);
    }

    default void visit(MemEcritureAsml e) {
        throw new NotYetImplementedException();
        /*UtilVisiteur.visitMemWorker(e, this);
        e.getValeurEcrite().accept(this);*/
    }

    default void visit(LetFloatAsml e) {
        throw new NotYetImplementedException();
    }

    default void visit(IfEqIntAsml e) {
        UtilVisiteur.visitIfIntWorker(e, this);
    }

    default void visit(IfLEIntAsml e) {
        UtilVisiteur.visitIfIntWorker(e, this);
    }

    default void visit(IfGEIntAsml e) {
        UtilVisiteur.visitIfIntWorker(e, this);
    }

    default void visit(IfEquFloatAsml e) {
        throw new NotYetImplementedException();
    }

    default void visit(IfLEFloatAsml e) {
        throw new NotYetImplementedException();
    }

    default void visit(LabelFloatAsml e) {
        throw new NotYetImplementedException();
    }
}
