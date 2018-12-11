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
import arbresyntaxique.Id;
import arbresyntaxique.If;
import arbresyntaxique.Int;
import arbresyntaxique.LE;
import arbresyntaxique.Let;
import arbresyntaxique.LetRec;
import arbresyntaxique.LetTuple;
import arbresyntaxique.Neg;
import arbresyntaxique.Not;
import arbresyntaxique.OperateurBinaire;
import arbresyntaxique.OperateurUnaire;
import arbresyntaxique.Put;
import arbresyntaxique.Sub;
import arbresyntaxique.Tuple;
import arbresyntaxique.Type;
import arbresyntaxique.Unit;
import arbresyntaxique.Var;
import util.NotYetImplementedException;
import visiteur.ObjVisitor;

public class KNormVisitor implements ObjVisitor<Exp> {

    @Override
    public Unit visit(Unit e) {
        return e;
    }

    @Override
    public Bool visit(Bool e) {
        return e;
    }

    @Override
    public Int visit(Int e) {
        return e;
    }

    @Override
    public Float visit(Float e) { 
        throw new NotYetImplementedException();
        //return e;
    }

    private class CreateurNoeudOpUnaire
    {
        private final Exp exp;
        private final Id newId;
        private final Type newtype;
        private final Var var;
        
        public CreateurNoeudOpUnaire(OperateurUnaire e)
        {
            this.exp =  e.getE().accept(KNormVisitor.this);
            this.newId = Id.gen();
            this.newtype = Type.gen();
            this.var = new Var(newId);
        }
        public Var getVar() {
            return var;
        }
        public Let creerNouveauNoeud(OperateurUnaire opUnaire)
        {
             return new Let(newId, newtype, exp, opUnaire) ;
        } 
    }

    @Override
    public Let visit(Not e) {
        CreateurNoeudOpUnaire resWorker =  new CreateurNoeudOpUnaire(e);
        Not not = new Not(resWorker.getVar());
        return resWorker.creerNouveauNoeud(not);
    }

    @Override
    public Let visit(Neg e) {
        CreateurNoeudOpUnaire resWorker =  new CreateurNoeudOpUnaire(e);
        Neg neg = new Neg(resWorker.getVar());
        return resWorker.creerNouveauNoeud(neg);     
    }

    private class CreateurNoeudOpBinaire
    {
        private final Exp e1;
        private final Exp e2;
        private final Id newId1; 
        private final Id newId2; 
        private final Var newVar1; 
        private final Var newVar2; 
        private final Type newType1;
        private final Type newType2;
        
        public CreateurNoeudOpBinaire(OperateurBinaire e)
        {
            this.e1 = e.getE1().accept(KNormVisitor.this);
            this.e2 = e.getE2().accept(KNormVisitor.this);
            this.newId1 = Id.gen();
            this.newId2 = Id.gen();
            this.newVar1 = new Var(newId1);
            this.newVar2 = new Var(newId2);
            this.newType1 = Type.gen();
            this.newType2 = Type.gen();
        }
        
        public Let creerNoeud(OperateurBinaire e)
        {            
            return new Let(newId1, newType1, e1,
                  new Let(newId2, newType2, e2, e));
        }

        public Var getNewVar1() {
            return newVar1;
        }

        public Var getNewVar2() {
            return newVar2;
        }
    }
    
    @Override
    public Let visit(Add e) {
        CreateurNoeudOpBinaire result = new CreateurNoeudOpBinaire(e);
        return result.creerNoeud(new Add(result.getNewVar1(), result.getNewVar2()));
    }

	
    @Override
    public Let visit(Sub e) {
	CreateurNoeudOpBinaire result = new CreateurNoeudOpBinaire(e);
        return result.creerNoeud(new Sub(result.getNewVar1(), result.getNewVar2()));
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
        CreateurNoeudOpBinaire result = new CreateurNoeudOpBinaire(e);
        return result.creerNoeud(new Eq(result.getNewVar1(), result.getNewVar2()));
    }

    @Override
    public Let visit(LE e){
        CreateurNoeudOpBinaire result = new CreateurNoeudOpBinaire(e);
        return result.creerNoeud(new LE(result.getNewVar1(), result.getNewVar2()));
    }

    @Override
    public If visit(If e){   
        throw new NotYetImplementedException();
        /*Exp e1 = e.getE1().accept(this);
        Exp e2 = e.getE2().accept(this);
        Exp e3 = e.getE3().accept(this);
        return new If(e1, e2, e3);*/
    }

    @Override
    public Let visit(Let e) {
      Exp e1 = e.getE1().accept(this);
      Exp e2 = e.getE2().accept(this);
      return new Let(e.getId(), e.getT(), e1, e2);
    }

    @Override
    public Var visit(Var e){
        return e;
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
        /*
        Exp e1 = e.getE1().accept(this);
        Exp e2 = e.getE2().accept(this);
        Exp e3 = e.getE3().accept(this);

        Id new_var1 = Id.gen();
        Type new_type1 = Type.gen();
        Id new_var2 = Id.gen();
        Type new_type2 = Type.gen();
        Id new_var3 = Id.gen();
        Type new_type3 = Type.gen();

        Let res = 
        new Let(new_var1, new_type1, e1,
          new Let(new_var2, new_type2, e2,
              new Let(new_var3, new_type3, e3,
                new Put(new Var(new_var1), new Var(new_var2), new Var(new_var3)))));
        return res;*/
    }
}


