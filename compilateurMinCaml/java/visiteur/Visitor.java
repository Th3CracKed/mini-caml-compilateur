package visiteur;

import arbremincaml.*;
import util.NotYetImplementedException;

public interface Visitor {

    default void visit(Unit e) {
    }

    default void visit(Bool e) {
    }

    default void visit(Int e) {
    }

    default void visit(FloatMinCaml e) { 
        throw new NotYetImplementedException();
    }    
    
    default void visit(Not e) {
        UtilVisiteur.visitOpUnaireWorker(e, this);
    }

    default void visit(Neg e) {
        UtilVisiteur.visitOpUnaireWorker(e, this);
    }
    
    default void visit(Add e) {
        UtilVisiteur.visitOpBinaireWorker(e, this);
    }

	
    default void visit(Sub e) {
	UtilVisiteur.visitOpBinaireWorker(e, this);
    }

    default void visit(FNeg e){
      throw new NotYetImplementedException();
    }

    default void visit(FAdd e){
       throw new NotYetImplementedException();
    }

    default void visit(FSub e){
        throw new NotYetImplementedException();
    }

    default void visit(FMul e) {
       throw new NotYetImplementedException();
    }

    default void visit(FDiv e){
        throw new NotYetImplementedException();
    }

    default void visit(Eq e){
        UtilVisiteur.visitOpBinaireWorker(e, this);
    }

    default void visit(LE e){
        UtilVisiteur.visitOpBinaireWorker(e, this);
    }

    default void visit(If e){        
        throw new NotYetImplementedException();
    }

    default void visit(Let e) {      
      e.getE1().accept(this);
      e.getE2().accept(this);
    }

    default void visit(Var e){
    }

    default void visit(LetRec e){
       throw new NotYetImplementedException();
    }

    default void visit(App e){
        e.getE().accept(this);
        for(Exp argument : e.getEs())
        {
            argument.accept(this);
        }
    }

    default void visit(Tuple e){
       throw new NotYetImplementedException();
    }

    default void visit(LetTuple e){
        throw new NotYetImplementedException();
    }

    default void visit(Array e){
        throw new NotYetImplementedException();
   }

    default void visit(Get e){
        throw new NotYetImplementedException();
    }

    default void visit(Put e){
        throw new NotYetImplementedException();
    }
}


