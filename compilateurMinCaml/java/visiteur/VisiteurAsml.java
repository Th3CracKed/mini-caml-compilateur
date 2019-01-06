package visiteur;

import arbreasml.*;

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
        UtilVisiteur.visitNegBaseWorker(e, this);
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
        e.getE().accept(this);
    }

    default void visit(FNegAsml e) {
        UtilVisiteur.visitNegBaseWorker(e, this);
    }

    default void visit(FAddAsml e) {
        UtilVisiteur.visitOpArithmetiqueFloatWorker(e, this);
    }

    default void visit(FSubAsml e) {
        UtilVisiteur.visitOpArithmetiqueFloatWorker(e, this);
    }

    default void visit(FMulAsml e) {
        UtilVisiteur.visitOpArithmetiqueFloatWorker(e, this);
    }

    default void visit(FDivAsml e) {
        UtilVisiteur.visitOpArithmetiqueFloatWorker(e, this);
    }

    default void visit(CallAsml e) {
        UtilVisiteur.visitCallBaseWorker(e, this);
    }

    default void visit(CallClosureAsml e) {
        e.getVar().accept(this);
        UtilVisiteur.visitCallBaseWorker(e, this);
    }

    default void visit(MemLectureAsml e) {
        UtilVisiteur.visitMemWorker(e, this);
    }

    default void visit(MemEcritureAsml e) {
        UtilVisiteur.visitMemWorker(e, this);
        e.getValeurEcrite().accept(this);
    }

    default void visit(LetFloatAsml e) {
        
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

    default void visit(IfEqFloatAsml e) {
        UtilVisiteur.visitIfFloatWorker(e, this);
    }

    default void visit(IfLEFloatAsml e) {
        UtilVisiteur.visitIfFloatWorker(e, this);
    }
}
