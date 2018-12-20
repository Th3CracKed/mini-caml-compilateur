/*package backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import arbreasml.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import util.NotYetImplementedException;
import visiteur.*;

public class VisiteurImmediatConstante implements ObjVisiteurAsml<NoeudAsml> {

    private static final int NB_BITS = 13;
    private final int MIN13BITS = (int) -Math.pow(2, NB_BITS - 1);
    private final int MAX13BITS = (int) Math.pow(2, NB_BITS - 1) - 1;

    private final HashMap<String, ValeurAsml> varConstante;

    public VisiteurImmediatConstante() {
        varConstante = new HashMap<>();
    }

    private <E extends NoeudAsml> NoeudAsml noeudResultat(ValeurAsml valeur, NoeudAsml noeudSiValIncorrect) {
        if (valeur instanceof IntAsml) {
            ValeurAsml<Integer> valeurInteger = (ValeurAsml<Integer>) valeur;
            if (valeurInteger.getValeur() > MIN13BITS && valeurInteger.getValeur() < MAX13BITS) {
                return valeur;
            } else {
                return noeudSiValIncorrect;
            }
        } else if (valeur instanceof NopAsml) {
            return valeur;
        } else {
            return noeudSiValIncorrect;
        }
    }

    @Override
    public NoeudAsml visit(AddAsml e) {
        DonneesOperateurArithmetiqueInt<ExpAsml> donneesOpBinaire = new DonneesOperateurArithmetiqueInt(e, this);
        ValeurAsml valeurAdd = e.accept(new VisiteurCalculValeurConstante());

        if (donneesOpBinaire.getE1() instanceof IntAsml) {
            donneesOpBinaire.echangerDonnees();
        }
        return noeudResultat(valeurAdd, new AddAsml((VarAsml) donneesOpBinaire.getE1(), (VarOuIntAsml) donneesOpBinaire.getE2()));
    }

    @Override
    public NoeudAsml visit(FunDefConcreteAsml e) {
        return new FunDefConcreteAsml(e.getLabel(), (AsmtAsml) e.getAsmt().accept(this), e.getArguments());
    }

    @Override
    public NoeudAsml visit(IntAsml e) {
        return e.accept(new VisiteurCalculValeurConstante());
    }

    @Override
    public NoeudAsml visit(LetAsml e) {
        ExpAsml e1 = e.getE1();
        AsmtAsml e2 = e.getE2();
        String idString = e.getIdString();
        return new LetAsml(idString, (ExpAsml) e1.accept(this), (AsmtAsml) e2.accept(this));
    }

    @Override
    public NoeudAsml visit(NegAsml e) {
        ValeurAsml<Integer> valeurNeg = e.accept(new VisiteurCalculValeurConstante());
        return noeudResultat(valeurNeg, new NegAsml(e.getE()));
    }

    @Override
    public NoeudAsml visit(NopAsml e) {
        ValeurAsml valeurNop = e.accept(new VisiteurCalculValeurConstante());
        return noeudResultat(valeurNop, new NopAsml());
    }

    @Override
    public NoeudAsml visit(ProgrammeAsml e) {
        FunDefConcreteAsml mainFunDefAsmlResult = (FunDefConcreteAsml) e.getMainFunDef().accept(this);
        List<FunDefAsml> funDefs = new ArrayList<>();
        for (FunDefAsml funDef : e.getFunDefs()) {
            FunDefAsml funDefResult = (FunDefAsml) funDef.accept(this);
            funDefs.add(funDefResult);
        }
        return new ProgrammeAsml(mainFunDefAsmlResult, funDefs);
    }

    @Override
    public NoeudAsml visit(SubAsml e) {
        DonneesOperateurArithmetiqueInt<ExpAsml> donneesOpBinaire = new DonneesOperateurArithmetiqueInt(e, this);
        ValeurAsml valeurSub = e.accept(new VisiteurCalculValeurConstante());
        if (donneesOpBinaire.getE1() instanceof IntAsml) {
            donneesOpBinaire.echangerDonnees();
        }
        return noeudResultat(valeurSub, new SubAsml((VarAsml) donneesOpBinaire.getE1(), (VarOuIntAsml) donneesOpBinaire.getE2()));
    }

    @Override
    public NoeudAsml visit(VarAsml e) {
        ValeurAsml valeur = e.accept(new VisiteurCalculValeurConstante());
        return noeudResultat(valeur, new VarAsml(e.getIdString()));
    }

    @Override
    public NoeudAsml visit(NewAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public NoeudAsml visit(FNegAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public NoeudAsml visit(FAddAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public NoeudAsml visit(FSubAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public NoeudAsml visit(FMulAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public NoeudAsml visit(FDivAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public NoeudAsml visit(CallAsml e) {
        return e;
    }

    @Override
    public NoeudAsml visit(CallClosureAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public NoeudAsml visit(MemLectureAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public NoeudAsml visit(MemEcritureAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public NoeudAsml visit(LetFloatAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public NoeudAsml visit(IfEqIntAsml e) {
        VarAsml cond1 = e.getE1();
        VarOuIntAsml cond2 = e.getE2();
        ValeurAsml valeur = e.accept(new VisiteurCalculValeurConstante());
        return noeudResultat(valeur,
                new IfEqIntAsml((VarAsml) cond1.accept(this), (VarOuIntAsml) cond2.accept(this), (AsmtAsml) e.getESiVrai().accept(this), (AsmtAsml) e.getESiVrai().accept(this)));

    }

    @Override
    public NoeudAsml visit(IfLEIntAsml e) {
        VarAsml cond1 = e.getE1();
        VarOuIntAsml cond2 = e.getE2();
        ValeurAsml valeur = e.accept(new VisiteurCalculValeurConstante());
        return noeudResultat(valeur,
                new IfLEIntAsml((VarAsml) cond1.accept(this), (VarOuIntAsml) cond2.accept(this), (AsmtAsml) e.getESiVrai().accept(this), (AsmtAsml) e.getESiVrai().accept(this)));

    }

    @Override
    public NoeudAsml visit(IfGEIntAsml e) {
        VarAsml cond1 = e.getE1();
        VarOuIntAsml cond2 = e.getE2();
        ValeurAsml valeur = e.accept(new VisiteurCalculValeurConstante());
        return noeudResultat(valeur,
                new IfGEIntAsml((VarAsml) cond1.accept(this), (VarOuIntAsml) cond2.accept(this), (AsmtAsml) e.getESiVrai().accept(this), (AsmtAsml) e.getESiVrai().accept(this)));
    }

    @Override
    public NoeudAsml visit(IfEquFloatAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public NoeudAsml visit(IfLEFloatAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public NoeudAsml visit(LabelFloatAsml e) {
        throw new NotYetImplementedException();
    }

    private class VisiteurCalculValeurConstante implements ObjVisiteurAsml<ValeurAsml> {

        private ValeurAsml visitOpArithmetiqueIntWorker(OperateurArithmetiqueIntAsml e, BinaryOperator<Integer> operateur)
        {
            DonneesOperateurArithmetiqueAsml<ValeurAsml> donneesOpBinaire = new DonneesOperateurArithmetiqueAsml<>(e, this);
            if (donneesOpBinaire.getE1() == null || donneesOpBinaire.getE2() == null) {
                return null;
            }            
            Integer valeurExp1Integer = (Integer) donneesOpBinaire.getE1().getValeur();
            Integer valeurExp2Integer = (Integer) donneesOpBinaire.getE2().getValeur();
            Integer valeurRetour = operateur.apply(valeurExp1Integer, valeurExp2Integer);
            return new IntAsml(valeurRetour);
        }
        
        @Override
        public ValeurAsml visit(AddAsml e) {
            return visitOpArithmetiqueIntWorker(e, (a,b)->(a+b));
        }       

        @Override
        public ValeurAsml visit(SubAsml e) {
            return visitOpArithmetiqueIntWorker(e, (a,b)->(a-b));
        }

        @Override
        public ValeurAsml visit(FunDefConcreteAsml e) {
            return null;
        }

        @Override
        public ValeurAsml visit(IntAsml e) {
            return e;
        }

        @Override
        public ValeurAsml visit(LetAsml e) {
            ValeurAsml<Integer> valeurAffecte = e.getE1().accept(this);
            varConstante.put(e.getIdString(), valeurAffecte);
            return e.getE2().accept(this);
        }

        @Override
        public ValeurAsml visit(NegAsml e) {
            ValeurAsml<Integer> valeur = (ValeurAsml<Integer>)UtilVisiteur.visitObjNegWorkerAsml(e, this);
            if (valeur == null) {
                return null;
            }
            Integer valeurInteger = valeur.getValeur();
            valeurInteger *= -1;
            return new IntAsml(valeurInteger);
        }

        @Override
        public ValeurAsml visit(NopAsml e) {
            return e;
        }

        @Override
        public ValeurAsml visit(ProgrammeAsml e) {
            return null;
        }

        @Override
        public ValeurAsml visit(VarAsml e) {
            ValeurAsml valRetour = null;
            if (varConstante.containsKey(e.getIdString())) {
                ValeurAsml<Integer> val = varConstante.get(e.getIdString());
                valRetour = val;
            }
            return valRetour;
        }

        @Override
        public ValeurAsml visit(NewAsml e) {
            throw new NotYetImplementedException();
        }

        @Override
        public ValeurAsml visit(FNegAsml e) {
            throw new NotYetImplementedException();
        }

        @Override
        public ValeurAsml visit(FAddAsml e) {
            throw new NotYetImplementedException();
        }

        @Override
        public ValeurAsml visit(FSubAsml e) {
            throw new NotYetImplementedException();
        }

        @Override
        public ValeurAsml visit(FMulAsml e) {
            throw new NotYetImplementedException();
        }

        @Override
        public ValeurAsml visit(FDivAsml e) {
            throw new NotYetImplementedException();
        }

        @Override
        public ValeurAsml visit(CallAsml e) {
            throw new NotYetImplementedException();
        }

        @Override
        public ValeurAsml visit(CallClosureAsml e) {
            throw new NotYetImplementedException();
        }

        @Override
        public ValeurAsml visit(MemLectureAsml e) {
            throw new NotYetImplementedException();
        }

        @Override
        public ValeurAsml visit(MemEcritureAsml e) {
            throw new NotYetImplementedException();
        }

        @Override
        public ValeurAsml visit(LetFloatAsml e) {
            throw new NotYetImplementedException();
        }
        
        private ValeurAsml visitIfIntWorker(IfIntAsml e, BiFunction<Integer,Integer,Boolean> comparateur)
        {
            VarAsml cond1 = e.getE1();
            VarOuIntAsml cond2 = e.getE2();
            ValeurAsml siVraiValeur = e.getESiVrai().accept(new VisiteurCalculValeurConstante());
            ValeurAsml siFauxValeur = e.getESiFaux().accept(new VisiteurCalculValeurConstante());
            ValeurAsml<Integer> cond1Result = (ValeurAsml<Integer>) cond1.accept(new VisiteurCalculValeurConstante());
            ValeurAsml<Integer> cond2Result = (ValeurAsml<Integer>) cond2.accept(new VisiteurCalculValeurConstante());
            if (cond1Result != null && cond2Result != null) {
                if (comparateur.apply(cond1Result.getValeur(), cond2Result.getValeur())) {
                    return siVraiValeur;
                } else {
                    return siFauxValeur;
                }
            } else if (siVraiValeur.getValeur().equals(siFauxValeur.getValeur())) {
                return siFauxValeur;
            } else {
                return null;
            }
        }
        
        @Override
        public ValeurAsml visit(IfEqIntAsml e) {
            return visitIfIntWorker(e, (a,b)->(a == b));
        }
        
        @Override
        public ValeurAsml visit(IfLEIntAsml e) {
            return visitIfIntWorker(e, (a,b)->(a <= b));
        }

        @Override
        public ValeurAsml visit(IfGEIntAsml e) {
            return visitIfIntWorker(e, (a,b)->(a >= b));
        }

        @Override
        public ValeurAsml visit(IfEquFloatAsml e) {
            throw new NotYetImplementedException();
        }

        @Override
        public ValeurAsml visit(IfLEFloatAsml e) {
            throw new NotYetImplementedException();
        }

        @Override
        public ValeurAsml visit(LabelFloatAsml e) {
            throw new NotYetImplementedException();
        }

    }
}*/
