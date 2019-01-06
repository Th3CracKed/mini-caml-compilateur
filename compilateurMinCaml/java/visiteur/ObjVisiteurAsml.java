package visiteur;

import arbreasml.*;

public interface ObjVisiteurAsml<E>{
    E visit(AddAsml e);
    E visit(FunDefConcreteAsml e);
    E visit(IntAsml e);
    E visit(LetAsml e);
    E visit(NegAsml e);
    E visit(NopAsml e);
    E visit(ProgrammeAsml e);
    E visit(SubAsml e);
    E visit(VarAsml e);  
    E visit(NewAsml e);
    E visit(FNegAsml e);
    E visit(FAddAsml e);
    E visit(FSubAsml e);
    E visit(FMulAsml e);
    E visit(FDivAsml e);
    E visit(CallAsml e);
    E visit(CallClosureAsml e);
    E visit(MemLectureAsml e);
    E visit(MemEcritureAsml e);
    E visit(LetFloatAsml e);
    E visit(IfEqIntAsml e);
    E visit(IfLEIntAsml e);
    E visit(IfGEIntAsml e);
    E visit(IfEqFloatAsml e);
    E visit(IfLEFloatAsml e);
}
