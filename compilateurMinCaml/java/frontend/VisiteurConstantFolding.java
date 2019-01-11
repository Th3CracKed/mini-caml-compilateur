package frontend;

import arbremincaml.*;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import util.MyCompilationException;
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
            if(!vEffetDeBord.getAUnEffetDeBord() && (!(val instanceof Integer)))
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

    @Override
    public Exp visit(FNeg e){
      return creerNoeudResultat(e, e);
    }

    @Override
    public Exp visit(FAdd e){
       return creerNoeudResultat(e, e);
    }

    @Override
    public Exp visit(FSub e){
        return creerNoeudResultat(e, e);
    }

    @Override
    public Exp visit(FMul e) {
       return creerNoeudResultat(e, e);
    }

    @Override
    public Exp visit(FDiv e){
        return creerNoeudResultat(e, e);
    }
    
    @Override
    public Exp visit(Var e) {
        return creerNoeudResultat(e, super.visit(e));
    }

    @Override
    public Exp visit(Let e) {
        Let resultat = (Let)super.visit(e);
        Exp nouvelE1 = resultat.getE1();
        if(nouvelE1 instanceof Valeur)
        {
            varConstante.put(e.getId().getIdString(), (Valeur)nouvelE1);
        }
        return resultat; 
    }

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

    private void verifierValeurIndice(Valeur valeur)
    {
        if(valeur instanceof Int && ((Int)valeur).getValeur() < 0)
        {
            throw new MyCompilationException("Les indices de tableaux doivent être positifs ou nuls");
        }
    }
    
    @Override
    public Exp visit(Get e) {
        Exp e2 = e.getE2();
        Valeur valeurE2 = e2.accept(new VisiteurCalculValeur());
        verifierValeurIndice(valeurE2);
        return new Get(e.getE1(), (valeurE2 == null) ? e2 : valeurE2);
    }

    @Override
    public Exp visit(Put e) {
        Exp e2 = e.getE2();
        Valeur valeurE2 = e2.accept(new VisiteurCalculValeur());
        verifierValeurIndice(valeurE2);
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
        
        private <T, U> Valeur visitOpBinaireWorker(OperateurBinaire e, BiFunction<U, U, T> operateur, Function<T,Valeur> constructeur) {
            DonneesOperateurBinaire<Valeur> donneesOpBinaire = new DonneesOperateurBinaire<>(e, this);
            Valeur<U> valeur1 = (Valeur<U>)donneesOpBinaire.getE1();
            Valeur<U> valeur2 = (Valeur<U>)donneesOpBinaire.getE2();
            if (valeur1 == null || valeur2 == null) {
                return null;
            }
            return constructeur.apply(operateur.apply(valeur1.getValeur(), valeur2.getValeur()));
        }
        
        private Valeur visitOpArithmetiqueIntWorker(OperateurArithmetiqueInt e, BinaryOperator<Integer> operateur) {
            return visitOpBinaireWorker(e, operateur, Int::new);
        }

        private Valeur visitOpArithmetiqueFloatWorker(OperateurArithmetiqueFloat e, BinaryOperator<Float> operateur) {
            return visitOpBinaireWorker(e, operateur, FloatMinCaml::new);
        }
        
        @Override
        public Valeur visit(Add e) {
            return visitOpArithmetiqueIntWorker(e, (a, b) ->(a + b));
        }

        @Override
        public Valeur visit(Sub e) {
            return visitOpArithmetiqueIntWorker(e, (a, b) -> (a - b));
        }

        @Override
        public Valeur visit(FNeg e) {
            Valeur valeur = e.getE().accept(this);
            if (valeur == null) {
                return null;
            }
            Float valeurFloat = (Float) valeur.getValeur();
            valeurFloat *= -1;
            return new FloatMinCaml(valeurFloat);
        }

        @Override
        public Valeur visit(FAdd e) {
            return visitOpArithmetiqueFloatWorker(e, (a, b) -> (a + b));
        }

        @Override
        public Valeur visit(FSub e) {
            return visitOpArithmetiqueFloatWorker(e, (a, b) -> (a - b));
        }

        @Override
        public Valeur visit(FMul e) {
            return visitOpArithmetiqueFloatWorker(e, (a, b) -> (a * b));
        }

        @Override
        public Valeur visit(FDiv e) {
            return visitOpArithmetiqueFloatWorker(e, (a, b) -> (a / b));
        }

        private Valeur visitOpRelationnelWorker(OperateurRelationnel e, BiFunction operateur) {
            return visitOpBinaireWorker(e, operateur, Bool::new);
        }

        @Override
        public Valeur visit(Eq e) {
            return visitOpRelationnelWorker(e, (a, b) -> a.equals(b)); // on peut utiliser la méthode equals car les valeurs sont soit des Boolean, soit des Integer, soit des Tuple (la classe tuple redefinit la methode equals)
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
            if (composantesAcceptes.stream().anyMatch(x -> x != null)) // si au moins une composante du tuple est constante, on a besoin de le renvoyer
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
            return e;
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
