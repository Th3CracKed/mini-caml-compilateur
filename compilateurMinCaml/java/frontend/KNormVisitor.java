package frontend;

import arbremincaml.*;
import java.util.LinkedList;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import util.NotYetImplementedException;
import visiteur.ObjVisitorExp;

public class KNormVisitor extends ObjVisitorExp {

    @Override
    public Let visit(App e) {
        LinkedList<Exp> vars = new LinkedList<>();
        Var varFonction = new Var(Id.gen());
        for (int i = 0; i < e.getEs().size(); i++) {
            vars.add(new Var(Id.gen()));
        }
        Exp resultat = new App(varFonction, vars);
        for (int i = 0; i < e.getEs().size(); i++) {
            resultat = new Let(((Var) vars.get(i)).getId(), Type.gen(), e.getEs().get(i).accept(this), resultat);
        }
        return new Let(varFonction.getId(), Type.gen(), e.getE().accept(this), resultat);
    }

    @Override
    public FloatMinCaml visit(FloatMinCaml e) {
        throw new NotYetImplementedException();
        //return e;
    }

    private Let visitOpUnaireWorker(OperateurUnaire e, Function<Exp, ? extends OperateurUnaire> constructeurOpUnaire)
    {
        Var var = new Var(Id.gen());
        return new Let(var.getId(), Type.gen(), e.getE().accept(this), constructeurOpUnaire.apply(var));
    }
    
    @Override
    public Let visit(Not e) {
        return visitOpUnaireWorker(e, Not::new);
    }

    @Override
    public Let visit(Neg e) {
        return visitOpUnaireWorker(e, Neg::new);
    }

    private class CreateurNoeudOpBinaire {

        private final Exp e1;
        private final Exp e2;
        private final Id newId1;
        private final Id newId2;
        private final Var newVar1;
        private final Var newVar2;
        private final Type newType1;
        private final Type newType2;

        public CreateurNoeudOpBinaire(OperateurBinaire e) {
            this.e1 = e.getE1().accept(KNormVisitor.this);
            this.e2 = e.getE2().accept(KNormVisitor.this);
            this.newId1 = Id.gen();
            this.newId2 = Id.gen();
            this.newVar1 = new Var(newId1);
            this.newVar2 = new Var(newId2);
            this.newType1 = Type.gen();
            this.newType2 = Type.gen();
        }

        public Let creerNoeud(OperateurBinaire e) {
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

    private Let visitOpBinaireWorker(OperateurBinaire e, BiFunction<Exp, Exp, ? extends OperateurBinaire> constructeurOpBinaire)
    {
        Var var1 = new Var(Id.gen());
        Var var2 = new Var(Id.gen());
        Exp e1 = e.getE1().accept(this);
        Exp e2 = e.getE2().accept(this);
        return new Let(var1.getId(), Type.gen(), e1,
                    new Let(var2.getId(), Type.gen(), e2, constructeurOpBinaire.apply(var1, var2)));
    }
    
    @Override
    public Let visit(Add e) {
        return visitOpBinaireWorker(e, Add::new);
    }

    @Override
    public Let visit(Sub e) {
        return visitOpBinaireWorker(e, Sub::new);
    }

    @Override
    public Let visit(Eq e) {
        return visitOpBinaireWorker(e, Eq::new);
    }

    @Override
    public Let visit(LE e) {
        return visitOpBinaireWorker(e, LE::new);
    }

    private Exp visitIfWorker(Exp e1, Exp e2, Exp e3, BinaryOperator<Exp> createurOpRel) {
        Eq e1Eq = (Eq) e1;
        Var varE1Gauche = new Var(Id.gen());
        Var varE1Droite = new Var(Id.gen());
        return new Let(varE1Gauche.getId(), Type.gen(), e1Eq.getE1(), new Let(varE1Droite.getId(), Type.gen(), e1Eq.getE2(), new If(createurOpRel.apply(varE1Gauche, varE1Droite), e2, e3)));
    }

    @Override
    public Exp visit(If e) {
        Exp e1 = e.getE1().accept(this);
        Exp e2 = e.getE2().accept(this);
        Exp e3 = e.getE3().accept(this);
        BinaryOperator<Exp> createurEq = Eq::new;
        if (e1 instanceof Eq) {
            return visitIfWorker(e1, e2, e3, createurEq);
        }
        else if(e1 instanceof LE)
        {
            return visitIfWorker(e1, e2, e3, LE::new);
        }
        else
        {
            return visitIfWorker(new Eq(e1, new Bool(true)), e2, e3, createurEq); 
        }
    }

    @Override
    public Let visit(Put e) {
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
