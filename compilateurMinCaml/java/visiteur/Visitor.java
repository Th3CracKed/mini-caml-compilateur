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
        e.getE1().accept(this);
        e.getE2().accept(this);
        e.getE3().accept(this);
    }

    default void visit(Let e) {      
      e.getE1().accept(this);
      e.getE2().accept(this);
    }

    default void visit(Var e){
    }

    default void visit(LetRec e){
       e.getE().accept(this);
       e.getFd().getE().accept(this);
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
        /*
       for(Exp composante : e.getEs())
       {
           composante.accept(this);
       }*/
    }

    default void visit(LetTuple e){
        throw new NotYetImplementedException();
        /*
       e.getE1().accept(this);
       e.getE2().accept(this);*/
    }

    default void visit(Array e){
        e.getE1().accept(this);
        e.getE2().accept(this);
   }

    default void visit(Get e){
        UtilVisiteur.visitAccesTabWorker(e, this);
    }

    default void visit(Put e){
        UtilVisiteur.visitAccesTabWorker(e, this);
        e.getE3().accept(this);
    }
}


