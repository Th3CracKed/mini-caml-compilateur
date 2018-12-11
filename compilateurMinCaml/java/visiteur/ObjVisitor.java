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


public interface ObjVisitor<E> {
    E visit(Unit e);
    E visit(Bool e);
    E visit(Int e);
    E visit(Float e);
    E visit(Not e);
    E visit(Neg e);
    E visit(Add e);
    E visit(Sub e);
    E visit(FNeg e);
    E visit(FAdd e);
    E visit(FSub e);
    E visit(FMul e);
    E visit(FDiv e);
    E visit(Eq e);
    E visit(LE e);
    E visit(If e);
    E visit(Let e);
    E visit(Var e);
    E visit(LetRec e);
    E visit(App e);
    E visit(Tuple e);
    E visit(LetTuple e);
    E visit(Array e);
    E visit(Get e);
    E visit(Put e);
}


