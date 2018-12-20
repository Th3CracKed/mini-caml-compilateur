package frontend;

import arbremincaml.*;
import java.util.LinkedList;
import util.NotYetImplementedException;
import visiteur.ObjVisitorExp;

public class KNormVisitor extends ObjVisitorExp {

    @Override    
    public Let visit(App e)
    {
        LinkedList<Exp> vars = new LinkedList<>();
        Var varFonction = new Var(Id.gen());
        for(int i = 0 ; i < e.getEs().size() ; i++)
        {
            vars.add(new Var(Id.gen()));
        }
        Exp resultat = new App(varFonction, vars);
        for(int i = 0 ; i < e.getEs().size() ; i++)
        {
            resultat = new Let(((Var)vars.get(i)).getId(), Type.gen(), e.getEs().get(i).accept(this), resultat);
        }
        return new Let(varFonction.getId(), Type.gen(), e.getE().accept(this), resultat);
    }
    
    
    @Override
    public FloatMinCaml visit(FloatMinCaml e) { 
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


