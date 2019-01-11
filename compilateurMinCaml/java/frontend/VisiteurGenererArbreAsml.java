package frontend;

import arbremincaml.*;
import arbreasml.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import util.*;
import visiteur.*;

public class VisiteurGenererArbreAsml implements ObjVisitor<NoeudAsml> {
    private final LinkedList<FunDefAsml> funDefs;
    private final HashMap<String,EnvironnementClosure> closures;
    private final HashSet<String> tVarFloats;
    private final HashSet<String> idStringVarFloats;
    
    private static final HashMap<String,String> FONCTIONS_CAML_VERS_ASML = new HashMap<>();
    static
    {        
        FONCTIONS_CAML_VERS_ASML.put(Constantes.PRINT_INT_CAML, Constantes.PRINT_INT_ASML);
        FONCTIONS_CAML_VERS_ASML.put(Constantes.PRINT_NEWLINE_CAML, Constantes.PRINT_NEWLINE_ASML);
        FONCTIONS_CAML_VERS_ASML.put(Constantes.SIN_CAML, Constantes.SIN_ASML);
        FONCTIONS_CAML_VERS_ASML.put(Constantes.COS_CAML, Constantes.COS_ASML);
        FONCTIONS_CAML_VERS_ASML.put(Constantes.SQRT_CAML, Constantes.SQRT_ASML);
        FONCTIONS_CAML_VERS_ASML.put(Constantes.ABS_FLOAT_CAML, Constantes.ABS_FLOAT_ASML);
        FONCTIONS_CAML_VERS_ASML.put(Constantes.INT_OF_FLOAT_CAML, Constantes.INT_OF_FLOAT_ASML);
        FONCTIONS_CAML_VERS_ASML.put(Constantes.FLOAT_OF_INT_CAML, Constantes.FLOAT_OF_INT_ASML);
        FONCTIONS_CAML_VERS_ASML.put(Constantes.TRUNCATE_CAML, Constantes.TRUNCATE_ASML);
    }
    
    public VisiteurGenererArbreAsml(HashMap<String,EnvironnementClosure> closures, HashSet<String> tVarFloats)
    {
        funDefs = new LinkedList<>();
        this.closures = closures;       
        this.tVarFloats = tVarFloats;
        this.idStringVarFloats = new HashSet<>();
    } 
    
    public List<FunDefAsml> getFunDefs()
    {
        return funDefs;
    }
        
    @Override
    public NoeudAsml visit(Unit e) {
        return IntAsml.nil();
    }

    @Override
    public NoeudAsml visit(Bool e) {
        return e.getValeur()?IntAsml.vrai():IntAsml.faux();
    }

    @Override
    public NoeudAsml visit(Int e) {
        return new IntAsml(e.getValeur());
    }

    @Override
    public NoeudAsml visit(FloatMinCaml e) {
        String labelFloat = Id.genLabelFloat();
        funDefs.addFirst(new LetFloatAsml(labelFloat, e.getValeur())); // on utilise addFirst pour les float et addLast pour les définitions de fonctions pour les faire apparaitre dans cet ordre dans le code asml
        String idStringVar = Id.genIdString();
        return new LetAsml(idStringVar, new VarAsml(labelFloat), new MemLectureAsml(new VarAsml(idStringVar), new IntAsml(0)));
    }
    
    private VarAsml visitOpUnaireWorker(OperateurUnaire e)
    {
        return (VarAsml)e.getE().accept(this);
    }

    @Override
    public NoeudAsml visit(Not e) {
        return new IfEqIntAsml(visitOpUnaireWorker(e), IntAsml.vrai(), IntAsml.faux(), IntAsml.vrai());
    }

    @Override
    public NoeudAsml visit(Neg e) {
        return new NegAsml(visitOpUnaireWorker(e));
    }

    private DonneesOperateurBinaire<NoeudAsml> recupererDonneesOperateurCommutatif(OperateurBinaire e)
    {
        DonneesOperateurBinaire<NoeudAsml> donneesOpBinaire = new DonneesOperateurBinaire<>(e, this);
        if(donneesOpBinaire.getE1() instanceof IntAsml)
        {
            donneesOpBinaire.echangerDonnees();
        }
        return donneesOpBinaire;
    }
    
    @Override
    public NoeudAsml visit(Add e) {        
        DonneesOperateurBinaire<NoeudAsml> donneesOpBinaire = recupererDonneesOperateurCommutatif(e);
        return new AddAsml((VarAsml)donneesOpBinaire.getE1(), (VarOuIntAsml)donneesOpBinaire.getE2());
    }

    @Override
    public NoeudAsml visit(Sub e) {
        DonneesOperateurBinaire<NoeudAsml> donneesOpBinaire = new DonneesOperateurBinaire<>(e, this);
        return new SubAsml((VarAsml)donneesOpBinaire.getE1(), (VarOuIntAsml)donneesOpBinaire.getE2());
    }

    @Override
    public NoeudAsml visit(FNeg e) {
        return new FNegAsml(visitOpUnaireWorker(e));
    }

    private NoeudAsml visitOperateurArithmetiqueFloatWorker(OperateurArithmetiqueFloat e, BiFunction<VarAsml,VarAsml,NoeudAsml> constructeur)
    {
        VarAsml e1 = (VarAsml)e.getE1().accept(this);
        VarAsml e2 = (VarAsml)e.getE2().accept(this);
        return constructeur.apply(e1, e2);
    }
    
    @Override
    public NoeudAsml visit(FAdd e) {
        return visitOperateurArithmetiqueFloatWorker(e, FAddAsml::new);
    }

    @Override
    public NoeudAsml visit(FSub e) {
        return visitOperateurArithmetiqueFloatWorker(e, FSubAsml::new);
    }

    @Override
    public NoeudAsml visit(FMul e) {
        return visitOperateurArithmetiqueFloatWorker(e, FMulAsml::new);
    }

    @Override
    public NoeudAsml visit(FDiv e) {
        return visitOperateurArithmetiqueFloatWorker(e, FDivAsml::new);
    }

    private NoeudAsml visitOperateurRelationnelWorker(OperateurRelationnel e)
    {
        If noeudIf = new If(e, new Bool(true), new Bool(false));
        return noeudIf.accept(this);
    }
    
    @Override
    public NoeudAsml visit(Eq e) {
        return visitOperateurRelationnelWorker(e);
    }

    @Override
    public NoeudAsml visit(LE e) {
        return visitOperateurRelationnelWorker(e);
    }

    @Override
    public NoeudAsml visit(If e) {
        Exp e1 = e.getE1();
        AsmtAsml e2Accepte = (AsmtAsml)e.getE2().accept(this);
        AsmtAsml e3Accepte = (AsmtAsml)e.getE3().accept(this);
        if(e1 instanceof OperateurRelationnel)
        {
            OperateurRelationnel e1OpRel = (OperateurRelationnel)e1;
            VarOuIntAsml gaucheOpRel = (VarOuIntAsml)e1OpRel.getE1().accept(this);
            VarOuIntAsml droiteOpRel = (VarOuIntAsml)e1OpRel.getE2().accept(this);
            boolean aPermute = false;
            if(gaucheOpRel instanceof IntAsml)
            {
                VarOuIntAsml temp = gaucheOpRel;
                gaucheOpRel = droiteOpRel;
                droiteOpRel = temp;
                aPermute = true;
            }
            VarAsml gaucheOpRelVar = (VarAsml)gaucheOpRel;
            if(idStringVarFloats.contains(gaucheOpRelVar.getIdString()))
            {
                VarAsml droiteOpRelVar = (VarAsml)droiteOpRel;
                if (e1 instanceof Eq) {                    
                    return new IfEqFloatAsml(gaucheOpRelVar, droiteOpRelVar, IntAsml.vrai(), IntAsml.faux());
                }
                else // if(e1 instanceof LE)
                {
                    return new IfLEFloatAsml(gaucheOpRelVar, droiteOpRelVar, IntAsml.vrai(), IntAsml.faux());
                }
            }
            else
            {                
                if (e1 instanceof Eq) {
                    return new IfEqIntAsml(gaucheOpRelVar, droiteOpRel, e2Accepte, e3Accepte);
                }
                else // if(e1 instanceof LE)
                {
                    if(aPermute)
                    {
                        return new IfGEIntAsml(gaucheOpRelVar, droiteOpRel, e2Accepte, e3Accepte);
                    }
                    else
                    {
                        return new IfLEIntAsml(gaucheOpRelVar, droiteOpRel, e2Accepte, e3Accepte);
                    }
                }
            }
        }        
        else //if(e1 instanceof Var)
        {
            return new IfEqIntAsml((VarAsml)e.getE1().accept(this), IntAsml.vrai(), e2Accepte, e3Accepte);
        }
    }

    @Override
    public NoeudAsml visit(Let e) {
        String idString = e.getId().getIdString();
        if(tVarFloats.contains(((TVar)e.getT()).getV()))
        {
            idStringVarFloats.add(idString);
        }
        Exp e1 = e.getE1();
        AsmtAsml e2Accepte = (AsmtAsml)e.getE2().accept(this);
        if(e1 instanceof Sub)
        {
            DonneesOperateurBinaire<NoeudAsml> donneesOpBinaire = new DonneesOperateurBinaire<>((Sub)e1, this);
            if(donneesOpBinaire.getE1() instanceof IntAsml)
            {            
                String idStringFils = Id.genIdString();
                VarAsml var = new VarAsml(idStringFils);
                LetAsml let = new LetAsml(idString, new SubAsml(var, (VarOuIntAsml)donneesOpBinaire.getE2()), e2Accepte);
                return new LetAsml(idStringFils, (IntAsml)donneesOpBinaire.getE1(), let);
            }
        }
        else if(e1 instanceof LE)
        {
            DonneesOperateurBinaire<NoeudAsml> donneesOpBinaire = new DonneesOperateurBinaire<>((LE)e1, this);
            if(donneesOpBinaire.getE1() instanceof IntAsml)
            {            
                String idStringFils = Id.genIdString();
                VarAsml var = new VarAsml(idStringFils);
                IfLEIntAsml ifLE = new IfLEIntAsml(var, (VarOuIntAsml)donneesOpBinaire.getE2(), IntAsml.vrai(), IntAsml.faux());                
                LetAsml let = new LetAsml(idString, ifLE, e2Accepte);
                return new LetAsml(idStringFils, (IntAsml)donneesOpBinaire.getE1(), let);
            }
        }
        else if(e1 instanceof Tuple)
        {
            List<Exp> es = ((Tuple)e1).getEs();
            int esSize = es.size();
            AsmtAsml resultat = (AsmtAsml)e.getE2().accept(this);
            for(int i = esSize-1 ; i >= 0 ; i--)
            {
                resultat = new LetAsml(Id.genIdString(), new MemEcritureAsml(new VarAsml(idString), new IntAsml(i), (VarAsml)es.get(i).accept(this)), resultat);
            }
            return new LetAsml(idString, new NewAsml(new IntAsml(esSize*Constantes.TAILLE_MOT_MEMOIRE)), resultat);
        }
        else if(e1 instanceof FloatMinCaml)
        {
            String labelFloat = Id.genLabelFloat();
            funDefs.addFirst(new LetFloatAsml(labelFloat, ((FloatMinCaml)e1).getValeur())); // on utilise addFirst pour les float et addLast pour les définitions de fonctions pour les faire apparaitre dans cet ordre dans le code asml
            String idStringVar = Id.genIdString();
            return new LetAsml(idStringVar, new VarAsml(labelFloat), new LetAsml(idString, new MemLectureAsml(new VarAsml(idStringVar), new IntAsml(0)), e2Accepte));
        }  
        NoeudAsml e1Accepte = e1.accept(this);   
        if(e1Accepte instanceof LetAsml)
        {
            LetAsml e1AccepteLet = (LetAsml)e1Accepte;
            return new LetAsml(e1AccepteLet.getIdString(), e1AccepteLet.getE1(), new LetAsml(idString, (ExpAsml)e1AccepteLet.getE2(), e2Accepte));
        }
        else
        {
            return new LetAsml(idString, (ExpAsml)e1Accepte, e2Accepte);
        }
    }

    @Override
    public NoeudAsml visit(Var e) {
        return new VarAsml(e.getId().getIdString());
    }

    @Override
    public NoeudAsml visit(LetRec e) {    
        FunDef funDef = e.getFd();
        Exp exp = e.getE();        
        Exp eFunDef = funDef.getE();
        String idString = funDef.getId().getIdString();
        EnvironnementClosure envClosure = closures.get(idString);
        List<VarAsml> args = new ArrayList<>();
        for(Id idArg : funDef.getArgs())
        {
            args.add(new VarAsml(idArg.getIdString()));
        }
        AsmtAsml expAccepte = null;
        AsmtAsml eFunDefAccepte = null;
        if(envClosure == null) // s'il n'y a pas besoin de closure pour cette fonction
        {          
            expAccepte = (AsmtAsml)exp.accept(this);
            eFunDefAccepte = (AsmtAsml)eFunDef.accept(this);
        }
        else
        {
            eFunDef.accept(new VisiteurRenommage(idString, Constantes.SELF_ASML));
            VarAsml varClosure = new VarAsml(Id.genIdString());
            String idStringVarClosure = varClosure.getIdString();
            exp.accept(new VisiteurRenommage(idString, idStringVarClosure));
            expAccepte = (AsmtAsml)exp.accept(this);            
            expAccepte.accept(new VisiteurRenommageAsml(idString, idStringVarClosure));
            eFunDefAccepte = (AsmtAsml)eFunDef.accept(this);
            List<String> env = envClosure.getVariablesLibres();
            int envSize = env.size();
            for(int i = 0 ; i < envSize ; i++)
            {
                boolean estUnLabel = false;
                String nomVarLibreEFunDef = null;
                String nomVarIntermediaire = null;
                nomVarLibreEFunDef = Id.genIdString();
                eFunDefAccepte.accept(new VisiteurRenommageAsml(env.get(i), nomVarLibreEFunDef));
                if(Id.estUnLabel(env.get(i)))
                {
                    nomVarIntermediaire = Id.genIdString();
                    estUnLabel = true;
                }
                else
                {
                    nomVarIntermediaire = env.get(i);
                }
                expAccepte = new LetAsml(Id.genIdString(), new MemEcritureAsml(new VarAsml(idStringVarClosure), new IntAsml(i+1), new VarAsml(nomVarIntermediaire)), expAccepte);
                if(estUnLabel)
                {
                    expAccepte = new LetAsml(nomVarIntermediaire, new VarAsml(env.get(i)), expAccepte);
                } 
                eFunDefAccepte = new LetAsml(nomVarLibreEFunDef, new MemLectureAsml(new VarAsml(Constantes.SELF_ASML), new IntAsml(i+1)), eFunDefAccepte);
                if(estUnLabel)
                {
                    eFunDefAccepte = (AsmtAsml)eFunDefAccepte.accept(new VisiteurRenommageCallAsml(env.get(i), nomVarLibreEFunDef));
                } 
            }
            String idVarAdresseFun = Id.genIdString();
            expAccepte = new LetAsml(Id.genIdString(), new MemEcritureAsml(new VarAsml(idStringVarClosure), new IntAsml(0), new VarAsml(idVarAdresseFun)), expAccepte);
            expAccepte = new LetAsml(idVarAdresseFun, new VarAsml(idString), expAccepte);            
            expAccepte = new LetAsml(idStringVarClosure, new NewAsml(new IntAsml(Constantes.TAILLE_MOT_MEMOIRE*(envSize+1))), expAccepte);     
            eFunDefAccepte.accept(new VisiteurRenommageAsml(idString, Constantes.SELF_ASML));
        }        
        funDefs.addLast(new FunDefConcreteAsml(idString, eFunDefAccepte, args));  // on utilise addFirst pour les float et addLast pour les définitions de fonctions pour les faire apparaitre dans cet ordre dans le code asml
        return expAccepte;
    }

    @Override
    public NoeudAsml visit(App e) {
        List<VarAsml> arguments = new ArrayList<>();     
        VarAsml varFonction = (VarAsml)e.getE().accept(this);
        String nomFonction = varFonction.getIdString();        
        NoeudAsml resultat = null;
        String nomFonctionTraduiteAsml = FONCTIONS_CAML_VERS_ASML.get(nomFonction);
        if(nomFonctionTraduiteAsml != null || Id.estUnLabel(nomFonction))
        {
            if(nomFonctionTraduiteAsml != null)
            {
                nomFonction = nomFonctionTraduiteAsml;
            }
            resultat = new CallAsml(nomFonction, arguments);
        }
        else
        {
            resultat = new CallClosureAsml(varFonction, arguments);
        }        
        if(!nomFonction.equals(Constantes.PRINT_NEWLINE_ASML))
        {
            for(Exp argument : e.getEs())
            {
                arguments.add((VarAsml)argument.accept(this));
            }
        }        
        return resultat;
    }

    @Override
    public NoeudAsml visit(Tuple e) {
        // le tuple est traite dans le let ou let tuple
        throw new MyCompilationException("Ce visiteur s'applique à des programmes où les tuples apparaissent uniquement à droite d'une initialisation de variable ou de tuple");
    }

    @Override
    public NoeudAsml visit(LetTuple e) {
        Exp e1 = e.getE1();
        List<Id> ids = e.getIds();
        Exp resultatMinCaml = null;
        if(e1 instanceof Var)
        {
            resultatMinCaml = e.getE2();
            for(int i = ids.size()-1 ; i >= 0 ; i--)
            {
                resultatMinCaml = new Let(ids.get(i), Type.gen(), new Get(e.getE1(), new Int(i)), resultatMinCaml);
            }
        }
        else // if(e1 instanceof Tuple)
        {
            Var var = new Var(Id.gen());
            resultatMinCaml = new Let(var.getId(), Type.gen(), e1, new LetTuple(ids, e.getTs(), var, e.getE2()));
        }
        return resultatMinCaml.accept(this);
    }

    @Override
    public NoeudAsml visit(Array e) {        
        VarAsml e2 = (VarAsml)e.getE2().accept(this);
        String nomFonction = (idStringVarFloats.contains(e2.getIdString()))?Constantes.CREATE_FLOAT_ARRAY_ASML:Constantes.CREATE_ARRAY_ASML;
        return new CallAsml(nomFonction, Arrays.asList((VarAsml)e.getE1().accept(this), e2));
    }

    @Override
    public NoeudAsml visit(Get e) {
        return new MemLectureAsml((VarAsml)e.getE1().accept(this), (VarOuIntAsml)e.getE2().accept(this));
    }

    @Override
    public NoeudAsml visit(Put e) {
        return new MemEcritureAsml((VarAsml)e.getE1().accept(this), (VarOuIntAsml)e.getE2().accept(this), (VarAsml)e.getE3().accept(this));
    }    
    
    private class VisiteurRenommage implements Visitor
    {
        private final String ancienNom;
        private final String nouveauNom;
        
        public VisiteurRenommage(String ancienNom, String nouveauNom)
        {
            this.ancienNom = ancienNom;
            this.nouveauNom = nouveauNom;
        }
        
        @Override
        public void visit(LetRec e)
        {
            e.getE().accept(this);
        }
        
        @Override
        public void visit(Var e)
        {
            Id id = e.getId();
            if(id.getIdString().equals(ancienNom))
            {
                id.setIdString(nouveauNom);
            }
        }
    }
    
    private class VisiteurRenommageAsml implements VisiteurAsml
    {
        private final String ancienNom;
        private final String nouveauNom;
        
        public VisiteurRenommageAsml(String ancienNom, String nouveauNom)
        {
            this.ancienNom = ancienNom;
            this.nouveauNom = nouveauNom;
        }
        
        @Override
        public void visit(VarAsml e)
        {
            if((e.getIdString().equals(getAncienNom())))
            {
                e.setIdString(getNouveauNom());
            }
        }
        
        public String getAncienNom() {
            return ancienNom;
        }
        
        public String getNouveauNom() {
            return nouveauNom;
        }
    }
    
    private class VisiteurRenommageCallAsml implements ObjVisiteurAsml<NoeudAsml>
    {
        private final String ancienNom;
        private final String nouveauNom;
        
        public VisiteurRenommageCallAsml(String ancienNom, String nouveauNom)
        {
            this.ancienNom = ancienNom;
            this.nouveauNom = nouveauNom;
        }
        
        @Override
        public NoeudAsml visit(CallAsml e)
        {
            if(e.getIdString().equals(ancienNom))
            {
                return new CallClosureAsml(new VarAsml(nouveauNom), e.getArguments());
            }
            else
            {
                return e;
            }
        }

        @Override
        public NoeudAsml visit(AddAsml e) {
            return e;
        }

        @Override
        public NoeudAsml visit(FunDefConcreteAsml e) {
            AsmtAsml asmt = (AsmtAsml)e.getAsmt().accept(this);
            return new FunDefConcreteAsml(e.getLabel(), asmt, e.getArguments());
        }

        @Override
        public NoeudAsml visit(IntAsml e) {
            return e;
        }

        @Override
        public NoeudAsml visit(LetAsml e) {
            ExpAsml e1 = (ExpAsml)e.getE1().accept(this);
            AsmtAsml e2 = (AsmtAsml)e.getE2().accept(this);
            return new LetAsml(e.getIdString(), e1, e2);
        }

        @Override
        public NoeudAsml visit(NegAsml e) {
            return e;
        }

        @Override
        public NoeudAsml visit(NopAsml e) {
            return e;
        }

        @Override
        public NoeudAsml visit(ProgrammeAsml e) {
            List<FunDefAsml> fundefs = new ArrayList<>();
            for(FunDefAsml funDef : e.getFunDefs())
            {
                fundefs.add((FunDefAsml)funDef.accept(this));
            }
            return new ProgrammeAsml((FunDefConcreteAsml)e.getMainFunDef().accept(this), fundefs);
        }

        @Override
        public NoeudAsml visit(SubAsml e) {
            return e;
        }

        @Override
        public NoeudAsml visit(VarAsml e) {
            return e;
        }

        @Override
        public NoeudAsml visit(NewAsml e) {
            return e;
        }

        @Override
        public NoeudAsml visit(FNegAsml e) {
            return e;
        }

        @Override
        public NoeudAsml visit(FAddAsml e) {
            return e;
        }

        @Override
        public NoeudAsml visit(FSubAsml e) {
            return e;
        }

        @Override
        public NoeudAsml visit(FMulAsml e) {
            return e;
        }

        @Override
        public NoeudAsml visit(FDivAsml e) {
            return e;
        }

        @Override
        public NoeudAsml visit(CallClosureAsml e) {
            return e;
        }

        @Override
        public NoeudAsml visit(MemLectureAsml e) {
            return e;
        }

        @Override
        public NoeudAsml visit(MemEcritureAsml e) {
            return e;
        }

        @Override
        public NoeudAsml visit(LetFloatAsml e) {
            return e;
        }
        
        private NoeudAsml visitIfIntWorker(IfIntAsml e, ConstructeurIfInt constructeurIf)
        {
            return constructeurIf.creerIf(e.getE1(), e.getE2(), (AsmtAsml)e.getESiVrai().accept(this), (AsmtAsml)e.getESiFaux().accept(this));
        }
        
        @Override
        public NoeudAsml visit(IfEqIntAsml e) {
            return visitIfIntWorker(e, IfEqIntAsml::new);
        }

        @Override
        public NoeudAsml visit(IfLEIntAsml e) {
            return visitIfIntWorker(e, IfLEIntAsml::new);
        }

        @Override
        public NoeudAsml visit(IfGEIntAsml e) {
            return visitIfIntWorker(e, IfGEIntAsml::new);
        }

        private NoeudAsml visitIfFloatWorker(IfFloatAsml e, ConstructeurIfFloat constructeurIf)
        {
            return constructeurIf.creerIf(e.getE1(), e.getE2(), (AsmtAsml)e.getESiVrai().accept(this), (AsmtAsml)e.getESiFaux().accept(this));
        }
        
        @Override
        public NoeudAsml visit(IfEqFloatAsml e) {
            return visitIfFloatWorker(e, IfEqFloatAsml::new);
        }

        @Override
        public NoeudAsml visit(IfLEFloatAsml e) {
            return visitIfFloatWorker(e, IfLEFloatAsml::new);
        }
    }
}
