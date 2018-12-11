package visiteur;

import arbresyntaxique.Add;
import arbresyntaxique.App;
import arbresyntaxique.Array;
import arbresyntaxique.Bool;
import arbresyntaxique.Eq;
import arbresyntaxique.FAdd;
import arbresyntaxique.FDiv;
import arbresyntaxique.FMul;
import arbresyntaxique.FNeg;
import arbresyntaxique.FSub;
import arbresyntaxique.Float;
import arbresyntaxique.Get;
import arbresyntaxique.If;
import arbresyntaxique.Int;
import arbresyntaxique.LE;
import arbresyntaxique.Let;
import arbresyntaxique.LetRec;
import arbresyntaxique.LetTuple;
import arbresyntaxique.Neg;
import arbresyntaxique.Not;
import arbresyntaxique.Put;
import arbresyntaxique.Sub;
import arbresyntaxique.Tuple;
import arbresyntaxique.Unit;
import arbresyntaxique.Var;

public interface Visitor {

    void visit(Unit e);
    void visit(Bool e);
    void visit(Int e);
    void visit(Float e);
    void visit(Not e);
    void visit(Neg e);
    void visit(Add e);
    void visit(Sub e);
    void visit(FNeg e);
    void visit(FAdd e);
    void visit(FSub e);
    void visit(FMul e);
    void visit(FDiv e);
    void visit(Eq e);
    void visit(LE e);
    void visit(If e);
    void visit(Let e);
    void visit(Var e);
    void visit(LetRec e);
    void visit(App e);
    void visit(Tuple e);
    void visit(LetTuple e);
    void visit(Array e);
    void visit(Get e);
    void visit(Put e);
}


