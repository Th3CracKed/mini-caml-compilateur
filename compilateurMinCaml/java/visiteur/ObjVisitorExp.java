package visiteur;

import arbremincaml.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
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
    
    private Exp visitOpUnaireWorker(OperateurUnaire e, Function<Exp, ? extends OperateurUnaire> constructeurOpUnaire)
    {
        return constructeurOpUnaire.apply(e.getE().accept(this));
    }
    
    @Override
    public Exp visit(Not e) { 
        return visitOpUnaireWorker(e, Not::new);
    }

    @Override
    public Exp visit(Neg e) {
        return visitOpUnaireWorker(e, Neg::new);
    }
    
    private Exp visitOpBinaireWorker(OperateurBinaire e, BiFunction<Exp, Exp, ? extends OperateurBinaire> constructeurOpBinaire)
    {
        return constructeurOpBinaire.apply(e.getE1().accept(this), e.getE2().accept(this));
    }
    
    @Override
    public Exp visit(Add e) {
        return visitOpBinaireWorker(e, Add::new);
    }

    @Override
    public Exp visit(Sub e) {
	return visitOpBinaireWorker(e, Sub::new); 
    }

    @Override
    public Exp visit(Eq e){
        return visitOpBinaireWorker(e, Eq::new); 
    }

    @Override
    public Exp visit(LE e){
        return visitOpBinaireWorker(e, LE::new); 
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
    public Exp visit(If e){   
        Exp e1 = e.getE1().accept(this);
        Exp e2 = e.getE2().accept(this);
        Exp e3 = e.getE3().accept(this);
        return new If(e1 , e2, e3);
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
       Exp exp = e.getE().accept(this);
       FunDef funDef = e.getFd();
       FunDef nouvelleFunDef = new FunDef(funDef.getId(), funDef.getType(), funDef.getArgs(), funDef.getE().accept(this));
      return new LetRec(nouvelleFunDef, exp);
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
        List<Exp> es = new ArrayList<>();
        for(Exp composante : e.getEs())
        {
            es.add(composante.accept(this));
        }
        return new Tuple(es);
    }

    @Override
    public Exp visit(LetTuple e){
        return new LetTuple(e.getIds(), e.getTs(), e.getE1().accept(this), e.getE2().accept(this));
    }

    @Override
    public Exp visit(Array e){
        return new Array(e.getE1().accept(this), e.getE2().accept(this));
   }

    @Override
    public Exp visit(Get e){
        return new Get(e.getE1().accept(this), e.getE2().accept(this));
    }

    @Override
    public Exp visit(Put e){
        return new Put(e.getE1().accept(this), e.getE2().accept(this), e.getE3().accept(this));
    }
}
