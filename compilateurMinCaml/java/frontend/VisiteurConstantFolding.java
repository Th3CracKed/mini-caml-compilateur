package frontend;

import arbremincaml.*;

import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import util.NotYetImplementedException;
import visiteur.DonneesOperateurBinaire;
import visiteur.ObjVisitor;
import visiteur.ObjVisitorExp;
import visiteur.UtilVisiteur;


public class VisiteurConstantFolding extends ObjVisitorExp 
{    
    private final HashMap<String,Valeur > varConstante;
    
    public VisiteurConstantFolding()
    {
        varConstante = new HashMap<>();
    }
    
    @Override
    public Exp visit(LE e) {   
        DonneesOperateurBinaire<Exp> donneesOpBinaire = new DonneesOperateurBinaire<>(e, this);      
        LE le = new LE(donneesOpBinaire.getE1(),donneesOpBinaire.getE2());
        Valeur valeurLe =  le.accept(new VisiteurCalculValeurConstante());
        VisiteurEffetDeBord v = new VisiteurEffetDeBord();
        e.accept(v);
        return (valeurLe == null || v.getAUnEffetDeBord())?super.visit(e):valeurLe;
    }
    
    @Override
    public Exp visit(Eq e) {
        DonneesOperateurBinaire<Exp> donneesOpBinaire = new DonneesOperateurBinaire<>(e, this);      
        Eq eq = new Eq(donneesOpBinaire.getE1(),donneesOpBinaire.getE2());
        Valeur valeurEq =  eq.accept(new VisiteurCalculValeurConstante());
        VisiteurEffetDeBord v = new VisiteurEffetDeBord();
        e.accept(v);
        return (valeurEq == null || v.getAUnEffetDeBord())?super.visit(e):valeurEq;
    }
    
   @Override
    public Exp visit(Not e) {
        Not not = new Not(UtilVisiteur.visitObjOpUnaireWorker(e, this));
        Valeur valeurNot =  not.accept(new VisiteurCalculValeurConstante());
        VisiteurEffetDeBord v = new VisiteurEffetDeBord();
        e.accept(v);
        return (valeurNot == null || v.getAUnEffetDeBord())?super.visit(e):valeurNot;
    }
    
    private Exp creerNoeudResultat(Valeur valeur)
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
    }
    
    @Override
    public Exp visit(Neg e) {
        Neg neg = new Neg(UtilVisiteur.visitObjOpUnaireWorker(e, this));
        Valeur valeurNeg =  neg.accept(new VisiteurCalculValeurConstante());
        VisiteurEffetDeBord v = new VisiteurEffetDeBord();
        e.accept(v);
        return (valeurNeg == null || v.getAUnEffetDeBord())?super.visit(e):creerNoeudResultat(valeurNeg);
    }
    
    
    @Override
    public Exp visit(Sub e) {   
        DonneesOperateurBinaire<Exp> donneesOpBinaire = new DonneesOperateurBinaire<>(e, this); 
        Sub newSub = new Sub(donneesOpBinaire.getE1(),donneesOpBinaire.getE2());
        Valeur valeurSub =  newSub.accept(new VisiteurCalculValeurConstante());     
        VisiteurEffetDeBord v = new VisiteurEffetDeBord();
        e.accept(v);   
        return (valeurSub == null || v.getAUnEffetDeBord())?super.visit(e):creerNoeudResultat(valeurSub);
    }
   
    @Override
    public Exp visit(Add e) {
        DonneesOperateurBinaire<Exp> donneesOpBinaire = new DonneesOperateurBinaire<>(e, this); 
        Add newAdd =   new Add(donneesOpBinaire.getE1(),donneesOpBinaire.getE2());
        Valeur valeurAdd =  newAdd.accept(new VisiteurCalculValeurConstante());     
        VisiteurEffetDeBord v = new VisiteurEffetDeBord();
        e.accept(v);    
        return (valeurAdd == null || v.getAUnEffetDeBord())?super.visit(e):creerNoeudResultat(valeurAdd);
    }
     
   @Override
   public Exp visit(Var e) {
       Valeur valeurVar = varConstante.get(e.getId().getIdString());
       if(valeurVar == null || (valeurVar.getValeur() instanceof Integer && (Integer)valeurVar.getValeur() < 0))
       {           
           return super.visit(e);
       }
       else 
       {
           return valeurVar;
       }
   }
    
    @Override
    public Exp visit(Let e) 
    {        
        Exp e1 = e.getE1();
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
        }
    }

    @Override
    public Exp visit(If e){   
        If nouveauIf = new If(e.getE1().accept(this), e.getE2().accept(this), e.getE3().accept(this));
        Valeur valeur =  nouveauIf.accept(new VisiteurCalculValeurConstante());  
        VisiteurEffetDeBord v = new VisiteurEffetDeBord();
        e.accept(v);       
        return (valeur == null || v.getAUnEffetDeBord())?super.visit(e):creerNoeudResultat(valeur);
    }
    
    private class VisiteurCalculValeurConstante implements ObjVisitor<Valeur>
    {        
        @Override
        public Valeur visit(Not e) {
            Valeur valeur = UtilVisiteur.visitObjOpUnaireWorker(e, this);
            if(valeur == null)
            {
                return null;
            }
            Boolean bool = (Boolean) valeur.getValeur();
            return new Bool(!bool);
        }

        @Override
        public Valeur visit(Neg e) {
            Valeur valeur = UtilVisiteur.visitObjOpUnaireWorker(e, this);
            if(valeur == null)
            {
                return null;
            }
            Integer valeurInteger = (Integer) valeur.getValeur();
            valeurInteger *= -1;
            return new Int( valeurInteger ); 
        }

        private Valeur visitOpArithmetiqueIntWorker(OperateurArithmetiqueInt e, BinaryOperator<Integer> operateur)
        {
            DonneesOperateurBinaire<Valeur> donneesOpBinaire = new DonneesOperateurBinaire<>(e, this);       
            Valeur valeur1 = donneesOpBinaire.getE1();
            Valeur valeur2 = donneesOpBinaire.getE2();    
            if(valeur1 == null || valeur2 == null)
            {
                return null;
            }
            return new Int(operateur.apply((Integer)valeur1.getValeur(),(Integer)valeur2.getValeur())); 
        }
        
        @Override
        public Valeur visit(Add e) {
            return visitOpArithmetiqueIntWorker(e, (a,b)->(a+b));
        }

        @Override
        public Valeur visit(Sub e) {
            return visitOpArithmetiqueIntWorker(e, (a,b)->(a-b));
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
        
        private Valeur visitOpRelationnelWorker(OperateurRelationnel e, BiFunction<Object, Object, Boolean> operateur)
        {
            DonneesOperateurBinaire<Valeur> donneesOpBinaire = new DonneesOperateurBinaire<>(e, this);    
            Valeur valeur1 = donneesOpBinaire.getE1();
            Valeur valeur2 = donneesOpBinaire.getE2();
            if(valeur1 == null || valeur2 == null)
            {
                return null;
            }
            return new Bool(  operateur.apply(valeur1.getValeur(), valeur2.getValeur()) );
        }
        
        @Override
        public Valeur visit(Eq e) {
            return visitOpRelationnelWorker(e, (a,b)->a.equals(b));// on peut utiliser la méthode equals car les valeurs sont soit des Boolean soit des Integer
            
        }

        @Override
        public Valeur visit(LE e) {
            return visitOpRelationnelWorker(e, (a,b)->(Integer)a<=(Integer)b);       
        }

        @Override
        public Valeur visit(If e) {
            Valeur valeurE1 = e.getE1().accept(this); 
            Valeur valeurE2 = e.getE2().accept(this); 
            Valeur valeurE3 = e.getE3().accept(this);        
            if(valeurE1 != null)
            {
                boolean valE1 = (Boolean)valeurE1.getValeur();
                if(valE1 &&  valeurE2 != null)
                {
                    return valeurE2;
                }
                else if(!valE1 &&  valeurE3 != null)
                {
                    return valeurE3;
                }
            }
            if(valeurE2 != null && valeurE3 != null && valeurE2.getValeur().equals(valeurE3.getValeur()))
            {
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
            throw new NotYetImplementedException();
        }

        @Override
        public Valeur visit(App e) {
            return null;
        }

        @Override
        public Valeur visit(Tuple e) {
            throw new NotYetImplementedException();
        }

        @Override
        public Valeur visit(LetTuple e) {
           throw new NotYetImplementedException();
        }

        @Override
        public Valeur visit(Array e) {
            throw new NotYetImplementedException();
        }

        @Override
        public Valeur visit(Get e) {
           throw new NotYetImplementedException();
        }

        @Override
        public Valeur visit(Put e) {
            throw new NotYetImplementedException();
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
            return e ;
        }   
    }
}

