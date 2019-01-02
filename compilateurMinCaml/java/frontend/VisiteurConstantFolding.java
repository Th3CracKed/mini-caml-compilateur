package frontend;

import arbremincaml.*;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import util.NotYetImplementedException;
import visiteur.DonneesOperateurBinaire;
import visiteur.ObjVisitor;
import visiteur.ObjVisitorExp;

public class VisiteurConstantFolding extends ObjVisitorExp {

    private final HashMap<String, Valeur> varConstante;

    public VisiteurConstantFolding() {
        varConstante = new HashMap<>();
    }
    
    private static boolean estValeurConstante(Exp valeur)
    {
        return (valeur instanceof Valeur && !(valeur instanceof Tuple));
    }
    
    private Exp creerNoeudResultat(Exp e, Exp noeudSiValeurInvalide) {
        Valeur valeurMinCaml = noeudSiValeurInvalide.accept(new VisiteurCalculValeur());
        if (estValeurConstante(valeurMinCaml)) {
            Object val = valeurMinCaml.getValeur();
            VisiteurEffetDeBord vEffetDeBord = new VisiteurEffetDeBord();
            e.accept(vEffetDeBord);
            if(!vEffetDeBord.getAUnEffetDeBord() && (!(val instanceof Integer) || (Integer) val >= 0))
            {
                return valeurMinCaml;
            }
        }
        return noeudSiValeurInvalide;
    }

    @Override
    public Exp visit(LE e) {
        return creerNoeudResultat(e, super.visit(e));
    }

    @Override
    public Exp visit(Eq e) {
        return creerNoeudResultat(e, super.visit(e));
    }

    @Override
    public Exp visit(Add e) {
        return creerNoeudResultat(e, super.visit(e));
    }

    @Override
    public Exp visit(Sub e) {
        Exp valeur = creerNoeudResultat(e, super.visit(e));
        if (valeur instanceof Valeur) {
            return valeur;
        } else {
            return new Sub(e.getE1(), ((Sub)valeur).getE2());
        }
    }

    @Override
    public Exp visit(Not e) {
        return creerNoeudResultat(e, e);
    }

    @Override
    public Exp visit(Neg e) {
        return creerNoeudResultat(e, e);
    }

    /*private Exp creerNoeudResultat(Valeur valeur)
    {
        Object val = valeur.getValeur();
        if(val instanceof Integer && (Integer)val < 0)
        {
            Var var = new Var(Id.gen());
            return new Let(var.getId(), Type.gen(), new Int(Math.abs((Integer)val)), new Neg(var));
        }
        else
        {
            return valeur;
        }
    }*/
    
    @Override
    public Exp visit(Var e) {
        return creerNoeudResultat(e, super.visit(e));
    }

    @Override
    public Exp visit(Let e) {
        //throw new NotYetImplementedException();
        //Exp e1 = e.getE1();
        //Exp e2 = e.getE2();
        //Exp valeurE1 = creerNoeudResultat(e1, e1);
        /*Exp e1Accepte = null;
        Exp e2Accepte = null;*/
        //VisiteurEffetDeBord v1 = new VisiteurEffetDeBord();
        //e1.accept(v1);
        /*if (valeurE1 instanceof Valeur) {
            //e1Accepte = valeurE1;
            varConstante.put(e.getId().getIdString(), (Valeur)valeurE1);
        }*/
        /*Valeur valeurE2 = e2.accept(new VisiteurCalculValeur());
        VisiteurEffetDeBord v2 = new VisiteurEffetDeBord();
        e2.accept(v2);
        if (!v2.getAUnEffetDeBord() && valeurE2 != null && (!(valeurE2.getValeur() instanceof Integer) || (Integer) valeurE2.getValeur() >= 0)) {
            e2Accepte = valeurE2;
        }
        if (e1Accepte == null) {
            e1Accepte = e1.accept(this);
        }
        if (e2Accepte == null) {
            e2Accepte = e2.accept(this);
        }
        return new Let(e.getId(), e.getT(), e1Accepte, e2Accepte);*/  
        Let resultat = (Let)super.visit(e);
        Exp nouvelE1 = resultat.getE1();
        if(nouvelE1 instanceof Valeur)
        {
            varConstante.put(e.getId().getIdString(), (Valeur)nouvelE1);
        }
        return resultat;        
        /*Exp e1 = e.getE1();
        Exp e2 = e.getE2();        
        String idString = e.getId().getIdString();
        Valeur valeurAffecte = e1.accept(new VisiteurCalculValeurConstante());  
        if(valeurAffecte == null)
        {
            return super.visit(e);
        }
        else
        {
            Object val = valeurAffecte.getValeur();
            varConstante.put(idString, valeurAffecte);    
            Exp e2Accepte = e2.accept(this);   
            VisiteurEffetDeBord v = new VisiteurEffetDeBord();
            e.accept(v); 
            if(v.getAUnEffetDeBord())
            {
                return super.visit(e);
            }
            if(val instanceof Integer && (Integer)val < 0)
            {         
                Var var = new Var(Id.gen());
                return new Let(var.getId(), Type.gen(), new Int(Math.abs((Integer)val)), new Let(e.getId(),e.getT(), new Neg(var),e2Accepte));
            }
            else
            {                  
                return new Let(e.getId(),e.getT(),valeurAffecte,e2Accepte);
            }
        }*/
    }

    /*@Override
    public Exp visit(LetRec e) {
        FunDef funDef = e.getFd();
        Exp eFunDef = funDef.getE();
        Exp exp = e.getE();
        Valeur valeurEFunDef = eFunDef.accept(new VisiteurCalculValeurConstante());
        Exp eFunDefAccepte = null;
        Exp expAccepte = null;
        VisiteurEffetDeBord vEFunDef = new VisiteurEffetDeBord();
        eFunDef.accept(vEFunDef);
        if (!vEFunDef.getAUnEffetDeBord() && valeurEFunDef != null && (!(valeurEFunDef.getValeur() instanceof Integer) || (Integer) valeurEFunDef.getValeur() >= 0)) {
            eFunDefAccepte = valeurEFunDef;
        }
        Valeur valeurExp = exp.accept(new VisiteurCalculValeurConstante());
        VisiteurEffetDeBord vExp = new VisiteurEffetDeBord();
        exp.accept(vExp);
        if (!vExp.getAUnEffetDeBord() && valeurExp != null && (!(valeurExp.getValeur() instanceof Integer) || (Integer) valeurExp.getValeur() >= 0)) {
            expAccepte = valeurExp;
        }
        if (eFunDefAccepte == null) {
            eFunDefAccepte = eFunDef.accept(this);
        }
        if (expAccepte == null) {
            expAccepte = exp.accept(this);
        }
        return new LetRec(new FunDef(funDef.getId(), funDef.getType(), funDef.getArgs(), eFunDefAccepte), expAccepte);
    }*/

    @Override
    public Exp visit(App e) {
        return e;
    }

    @Override
    public Exp visit(If e) {
        Exp e2Accepte = e.getE2().accept(this);
        Exp e3Accepte = e.getE3().accept(this);
        If nouveauIf = new If(e.getE1().accept(this), e2Accepte, e3Accepte);
        Valeur valeur = nouveauIf.accept(new VisiteurCalculValeur());
        VisiteurEffetDeBord v = new VisiteurEffetDeBord();
        e.accept(v);
        if (valeur == null || v.getAUnEffetDeBord()) {
            return new If(e.getE1(), e2Accepte, e3Accepte);
        } else {
            return valeur;
        }
    }

    @Override
    public Exp visit(Array e) {
        return e;
    }

    @Override
    public Exp visit(Get e) {
        Exp e2 = e.getE2();
        Valeur valeurE2 = e2.accept(new VisiteurCalculValeur());
        return new Get(e.getE1(), (valeurE2 == null) ? e2 : valeurE2);
    }

    @Override
    public Exp visit(Put e) {
        Exp e2 = e.getE2();
        Valeur valeurE2 = e2.accept(new VisiteurCalculValeur());
        return new Put(e.getE1(), (valeurE2 == null) ? e2 : valeurE2, e.getE3());
    }

    @Override
    public Exp visit(Tuple e) {
        Exp eAccepte = super.visit(e);
        return creerNoeudResultat(e, eAccepte);
    }

    @Override
    public Exp visit(LetTuple e) {
        Tuple valeurAffecte = (Tuple)e.getE1().accept(new VisiteurCalculValeur());             
        if (valeurAffecte != null) {
            List<Id> ids = e.getIds();
            List<Exp> esValeur = ((Tuple)valeurAffecte).getEs();  
            for (int i = 0; i < ids.size() ; i++) {
                if(esValeur.get(i) != null)
                {
                    varConstante.put(ids.get(i).getIdString(), (Valeur) valeurAffecte.getEs().get(i));
                }
            }
        }
        return super.visit(e);
    }

    private class VisiteurCalculValeur implements ObjVisitor<Valeur> {

        @Override
        public Valeur visit(Not e) {
            Valeur valeur = e.getE().accept(this);
            if (valeur == null) {
                return null;
            }
            Boolean bool = (Boolean) valeur.getValeur();
            return new Bool(!bool);
        }

        @Override
        public Valeur visit(Neg e) {
            Valeur valeur = e.getE().accept(this);
            if (valeur == null) {
                return null;
            }
            Integer valeurInteger = (Integer) valeur.getValeur();
            valeurInteger *= -1;
            return new Int(valeurInteger);
        }

        private Valeur visitOpArithmetiqueIntWorker(OperateurArithmetiqueInt e, BinaryOperator<Integer> operateur) {
            DonneesOperateurBinaire<Valeur> donneesOpBinaire = new DonneesOperateurBinaire<>(e, this);
            Valeur valeur1 = donneesOpBinaire.getE1();
            Valeur valeur2 = donneesOpBinaire.getE2();
            if (valeur1 == null || valeur2 == null) {
                return null;
            }
            return new Int(operateur.apply((Integer) valeur1.getValeur(), (Integer) valeur2.getValeur()));
        }

        @Override
        public Valeur visit(Add e) {
            return visitOpArithmetiqueIntWorker(e, (a, b) -> (a + b));
        }

        @Override
        public Valeur visit(Sub e) {
            return visitOpArithmetiqueIntWorker(e, (a, b) -> (a - b));
        }

        @Override
        public Valeur visit(FNeg e) {
            throw new NotYetImplementedException();
        }

        @Override
        public Valeur visit(FAdd e) {
            throw new NotYetImplementedException();
        }

        @Override
        public Valeur visit(FSub e) {
            throw new NotYetImplementedException();
        }

        @Override
        public Valeur visit(FMul e) {
            throw new NotYetImplementedException();
        }

        @Override
        public Valeur visit(FDiv e) {
            throw new NotYetImplementedException();
        }

        private Valeur visitOpRelationnelWorker(OperateurRelationnel e, BiFunction<Object, Object, Boolean> operateur) {
            DonneesOperateurBinaire<Valeur> donneesOpBinaire = new DonneesOperateurBinaire<>(e, this);
            Valeur valeur1 = donneesOpBinaire.getE1();
            Valeur valeur2 = donneesOpBinaire.getE2();
            if (valeur1 == null || valeur2 == null) {
                return null;
            }
            return new Bool(operateur.apply(valeur1.getValeur(), valeur2.getValeur()));
        }

        @Override
        public Valeur visit(Eq e) {
            return visitOpRelationnelWorker(e, (a, b) -> a.equals(b));// on peut utiliser la méthode equals car les valeurs sont soit des Boolean, soit des Integer, soit des Tuple

        }

        @Override
        public Valeur visit(LE e) {
            return visitOpRelationnelWorker(e, (a, b) -> (Integer) a <= (Integer) b);
        }

        @Override
        public Valeur visit(If e) {
            Valeur valeurE1 = e.getE1().accept(this);
            Valeur valeurE2 = e.getE2().accept(this);
            Valeur valeurE3 = e.getE3().accept(this);
            if (valeurE1 != null) {
                boolean valE1 = (Boolean) valeurE1.getValeur();
                if (valE1 && valeurE2 != null) {
                    return valeurE2;
                } else if (!valE1 && valeurE3 != null) {
                    return valeurE3;
                }
            }
            if (valeurE2 != null && valeurE3 != null && valeurE2.getValeur().equals(valeurE3.getValeur())) {
                return valeurE2;
            }
            return null;
        }

        @Override
        public Valeur visit(Let e) {
            Valeur valeurAffecte = e.getE1().accept(this);
            varConstante.put(e.getId().getIdString(), valeurAffecte);
            return e.getE2().accept(this);
        }

        @Override
        public Valeur visit(Var e) {
            return varConstante.get(e.getId().getIdString());
        }

        @Override
        public Valeur visit(LetRec e) {
            return e.getE().accept(this);
        }

        @Override
        public Valeur visit(App e) {
            return null;
        }

        @Override
        public Valeur visit(Tuple e) {
            List<Exp> composantesAcceptes = new ArrayList<>();
            for (Exp composante : e.getEs()) {
                composantesAcceptes.add(composante.accept(this));
            }
            if (composantesAcceptes.stream().anyMatch(x -> x != null)) // si au moins une composante du tuple est constante, on a besoin de la renvoyer
            {
                return new Tuple(composantesAcceptes);
            } else {
                return null;
            }
        }

        @Override
        public Valeur visit(LetTuple e) {
            Valeur valeurAffecte = e.getE1().accept(this);
            if (valeurAffecte != null) {
                Tuple valeurAffecteeTuple = (Tuple) valeurAffecte;
                List<Id> ids = e.getIds();
                for (int i = 0; i < ids.size(); i++) {
                    varConstante.put(ids.get(i).getIdString(), (Valeur) valeurAffecteeTuple.getEs().get(i));
                }
            }
            return e.getE2().accept(this);
        }

        @Override
        public Valeur visit(Array e) {
            return null;
        }

        @Override
        public Valeur visit(Get e) {
            return null;
        }

        @Override
        public Valeur visit(Put e) {
            return null;
        }

        @Override
        public Valeur visit(FloatMinCaml e) {
            throw new NotYetImplementedException();
        }

        @Override
        public Valeur visit(Unit e) {
            return e;
        }

        @Override
        public Valeur visit(Bool e) {
            return e;
        }

        @Override
        public Valeur visit(Int e) {
            return e;
        }
    }
    
}
