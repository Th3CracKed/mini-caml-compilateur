package visiteur;

import arbremincaml.*;
import java.util.ArrayList;
import java.util.List;
import util.NotYetImplementedException;

public abstract class ObjVisitorExp implements ObjVisitor<Exp> {
    @Override
    public Exp visit(Unit e) {
        return e;
    }

    @Override
    public Exp visit(Bool e) {
        return e;
    }

    @Override
    public Exp visit(Int e) {
        return e;
    }

    @Override
    public Exp visit(FloatMinCaml e) { 
        throw new NotYetImplementedException();
    }
    
    @Override
    public Exp visit(Not e) { 
        return new Not(UtilVisiteur.visitObjOpUnaireWorker(e, this));
    }

    @Override
    public Exp visit(Neg e) {
        return new Neg(UtilVisiteur.visitObjOpUnaireWorker(e, this));  
    }
    
    @Override
    public Exp visit(Add e) {
        DonneesOperateurBinaire<Exp> donneesOpBin = new DonneesOperateurBinaire<>(e, this);        
        return new Add(donneesOpBin.getE1(), donneesOpBin.getE2());   
    }

    @Override
    public Exp visit(Sub e) {
	DonneesOperateurBinaire<Exp> donneesOpBin = new DonneesOperateurBinaire<>(e, this);         
        return new Sub(donneesOpBin.getE1(), donneesOpBin.getE2());  
    }

    @Override
    public Exp visit(FNeg e){
      throw new NotYetImplementedException();
    }

    @Override
    public Exp visit(FAdd e){
       throw new NotYetImplementedException();
    }

    @Override
    public Exp visit(FSub e){
        throw new NotYetImplementedException();
    }

    @Override
    public Exp visit(FMul e) {
       throw new NotYetImplementedException();
    }

    @Override
    public Exp visit(FDiv e){
        throw new NotYetImplementedException();
    }

    @Override
    public Exp visit(Eq e){
        DonneesOperateurBinaire<Exp> donneesOpBin = new DonneesOperateurBinaire<>(e, this);         
        return new Eq(donneesOpBin.getE1(), donneesOpBin.getE2()); 
    }

    @Override
    public Exp visit(LE e){
        DonneesOperateurBinaire<Exp> donneesOpBin = new DonneesOperateurBinaire<>(e, this);        
        return new LE(donneesOpBin.getE1(), donneesOpBin.getE2()); 
    }

    @Override
    public Exp visit(If e){   
        throw new NotYetImplementedException();
    }

    @Override
    public Exp visit(Let e) {
      Exp e1 = e.getE1().accept(this);
      Exp e2 = e.getE2().accept(this);
      return new Let(e.getId(), e.getT(), e1 , e2);
    }

    @Override
    public Exp visit(Var e){
        return e;
    }

    @Override
    public Exp visit(LetRec e){
       throw new NotYetImplementedException();
    }

    @Override
    public Exp visit(App e){
        Exp fonction = e.getE().accept(this);
        List<Exp> arguments = new ArrayList<>();
        for(Exp argument : e.getEs())
        {
            arguments.add(argument.accept(this));
        }
        return new App(fonction, arguments);
    }

    @Override
    public Exp visit(Tuple e){
       throw new NotYetImplementedException();
    }

    @Override
    public Exp visit(LetTuple e){
        throw new NotYetImplementedException();
    }

    @Override
    public Exp visit(Array e){
        throw new NotYetImplementedException();
   }

    @Override
    public Exp visit(Get e){
        throw new NotYetImplementedException();
    }

    @Override
    public Exp visit(Put e){
        throw new NotYetImplementedException();
    }
}
