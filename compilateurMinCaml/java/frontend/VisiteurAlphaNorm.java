package frontend;

import arbresyntaxique.Add;
import arbresyntaxique.App;
import arbresyntaxique.Array;
import arbresyntaxique.Bool;
import arbresyntaxique.Eq;
import arbresyntaxique.Exp;
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
import util.NotYetImplementedException;
import visiteur.ObjVisitor;

public class VisiteurAlphaNorm implements ObjVisitor<Exp> {

    @Override
    public Unit visit(Unit e) {
        throw new NotYetImplementedException();
    }

    @Override
    public Bool visit(Bool e) {
        throw new NotYetImplementedException();
    }

    @Override
    public Int visit(Int e) {
        throw new NotYetImplementedException();
    }

    @Override
    public Float visit(Float e) { 
        throw new NotYetImplementedException();
    }
    
    @Override
    public Let visit(Not e) {
        throw new NotYetImplementedException();
    }

    @Override
    public Let visit(Neg e) {
        throw new NotYetImplementedException();   
    }

    @Override
    public Let visit(Add e) {
        throw new NotYetImplementedException();
    }

	
    @Override
    public Let visit(Sub e) {
	throw new NotYetImplementedException();
    }

    @Override
    public Exp visit(FNeg e){
      throw new NotYetImplementedException();
    }

    @Override
    public Let visit(FAdd e){
       throw new NotYetImplementedException();
    }

    @Override
    public Let visit(FSub e){
        throw new NotYetImplementedException();
    }

    @Override
    public Let visit(FMul e) {
       throw new NotYetImplementedException();
    }

    @Override
    public Let visit(FDiv e){
        throw new NotYetImplementedException();
    }

    @Override
    public Let visit(Eq e){
        throw new NotYetImplementedException();
    }

    @Override
    public Let visit(LE e){
        throw new NotYetImplementedException();
    }

    @Override
    public If visit(If e){        
        throw new NotYetImplementedException();
    }

    @Override
    public Let visit(Let e) {
      throw new NotYetImplementedException();
    }

    @Override
    public Var visit(Var e){
        throw new NotYetImplementedException();
    }

    @Override
    public Exp visit(LetRec e){
       throw new NotYetImplementedException();
    }

    @Override
    public Exp visit(App e){
        throw new NotYetImplementedException();
    }

    @Override
    public Tuple visit(Tuple e){
       throw new NotYetImplementedException();
    }

    @Override
    public LetTuple visit(LetTuple e){
        throw new NotYetImplementedException();
    }

    @Override
    public Let visit(Array e){
        throw new NotYetImplementedException();
   }

    @Override
    public Let visit(Get e){
        throw new NotYetImplementedException();
    }

    @Override
    public Let visit(Put e){
        throw new NotYetImplementedException();
    }
}


