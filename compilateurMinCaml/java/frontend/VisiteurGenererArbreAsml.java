package frontend;

import arbremincaml.*;
import arbreasml.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import util.*;
import visiteur.*;

public class VisiteurGenererArbreAsml implements ObjVisitor<NoeudAsml> {

    private final List<FunDefAsml> funDefs;
    
    private final HashMap<String,String> funCamlVersAsml;
    private final HashMap<String,EnvironnementClosure> closures;
    
    public VisiteurGenererArbreAsml(HashMap<String,EnvironnementClosure> closures)
    {
        funDefs = new ArrayList<>();
        funCamlVersAsml = new HashMap<>();
        funCamlVersAsml.put(Constantes.PRINT_INT_CAML, Constantes.PRINT_INT_ASML);
        funCamlVersAsml.put(Constantes.PRINT_NEWLINE_CAML, Constantes.PRINT_NEWLINE_ASML);
        this.closures = closures;
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
        throw new NotYetImplementedException();
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
        /*if(donneesOpBinaire.getE1() instanceof IntAsml)
        {            
            String idString = Id.genIdString();
            VarAsml var = new VarAsml(idString);
            return new LetAsml(idString, (IntAsml)donneesOpBinaire.getE1(), new SubAsml(var, (VarOuIntAsml)donneesOpBinaire.getE2()));
        }
        else
        {*/
            return new SubAsml((VarAsml)donneesOpBinaire.getE1(), (VarOuIntAsml)donneesOpBinaire.getE2());
        //}
    }

    @Override
    public NoeudAsml visit(FNeg e) {
        throw new NotYetImplementedException();
    }

    @Override
    public NoeudAsml visit(FAdd e) {
        throw new NotYetImplementedException();
    }

    @Override
    public NoeudAsml visit(FSub e) {
        throw new NotYetImplementedException();
    }

    @Override
    public NoeudAsml visit(FMul e) {
        throw new NotYetImplementedException();
    }

    @Override
    public NoeudAsml visit(FDiv e) {
        throw new NotYetImplementedException();
    }

    @Override
    public NoeudAsml visit(Eq e) {
        DonneesOperateurBinaire<NoeudAsml> donneesOpBinaire = recupererDonneesOperateurCommutatif(e);
        return new IfEqIntAsml((VarAsml)donneesOpBinaire.getE1(), (VarOuIntAsml)donneesOpBinaire.getE2(), IntAsml.vrai(), IntAsml.faux());
    }

    @Override
    public NoeudAsml visit(LE e) {
        DonneesOperateurBinaire<NoeudAsml> donneesOpBinaire = new DonneesOperateurBinaire<>(e, this);
        if(donneesOpBinaire.getE1() instanceof IntAsml)
        {            
            /*String idString = Id.genIdString();
            VarAsml var = new VarAsml(idString);
            IfLEIntAsml ifLE = new IfLEIntAsml(var, (VarOuIntAsml)donneesOpBinaire.getE2(), IntAsml.vrai(), IntAsml.faux());
            return new LetAsml(idString, (IntAsml)donneesOpBinaire.getE1(), ifLE);*/
            return new IfGEIntAsml((VarAsml)donneesOpBinaire.getE2(), (VarOuIntAsml)donneesOpBinaire.getE1(), IntAsml.vrai(), IntAsml.faux());
        }
        else
        {
            return new IfLEIntAsml((VarAsml)donneesOpBinaire.getE1(), (VarOuIntAsml)donneesOpBinaire.getE2(), IntAsml.vrai(), IntAsml.faux());
        }
    }

    @Override
    public NoeudAsml visit(If e) {
        Exp e1 = e.getE1();
        NoeudAsml e1Accepte = e.getE1().accept(this);
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
            if (e1 instanceof Eq) {   
                return new IfEqIntAsml((VarAsml)gaucheOpRel, droiteOpRel, e2Accepte, e3Accepte);
            }
            else // if(e1 instanceof LE)
            {
                if(aPermute)
                {
                    return new IfGEIntAsml((VarAsml)gaucheOpRel, droiteOpRel, e2Accepte, e3Accepte);
                }
                else
                {
                    return new IfLEIntAsml((VarAsml)gaucheOpRel, droiteOpRel, e2Accepte, e3Accepte);
                }
            }
        }        
        else //if(e1 instanceof Var)
        {
            return new IfEqIntAsml((VarAsml)e1Accepte, IntAsml.vrai(), e2Accepte, e3Accepte);
        }
        /*else  // (e1 instanceof Bool)
        {
            boolean boolE1 = ((Bool)e1).getValeur();
            if(boolE1)
            {                
                return e2Accepte;
            }
            else
            {                
                return e3Accepte;
            }
        }*/
        /*ExpAsml e1 = (ExpAsml)e.getE1().accept(this);
        ExpAsml e2 = (ExpAsml)e.getE2().accept(this);
        VarAsml var = new VarAsml(Id.genIdString());
        return new LetAsml(var.getIdString(), e1, new IfEqIntAsml(var, IntAsml.vrai(), e1, e2));*/
    }

    @Override
    public NoeudAsml visit(Let e) {
        String idString = e.getId().getIdString();
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
        NoeudAsml e1Accepte = e.getE1().accept(this);   
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
            eFunDefAccepte = (AsmtAsml)eFunDef.accept(this);
            List<String> env = envClosure.getVariablesLibres();
            int envSize = env.size();
            for(int i = 0 ; i < envSize ; i++)
            {
                expAccepte = new LetAsml(Id.genIdString(), new MemEcritureAsml(new VarAsml(idStringVarClosure), new IntAsml(i+1), new VarAsml(env.get(i))), expAccepte);
                eFunDefAccepte = new LetAsml(env.get(i), new MemLectureAsml(new VarAsml(Constantes.SELF_ASML), new IntAsml(i+1)), eFunDefAccepte);
            }
            String idVarAdresseFun = Id.genIdString();
            expAccepte = new LetAsml(Id.genIdString(), new MemEcritureAsml(new VarAsml(idStringVarClosure), new IntAsml(0), new VarAsml(idVarAdresseFun)), expAccepte);
            expAccepte = new LetAsml(idVarAdresseFun, new VarAsml(idString), expAccepte); 
            expAccepte = new LetAsml(idStringVarClosure, new NewAsml(new IntAsml(Constantes.TAILLE_MOT_MEMOIRE*(envSize+1))), expAccepte);               
            funDefs.add(new FunDefConcreteAsml(idString, eFunDefAccepte, args));
            return expAccepte;
        }        
        funDefs.add(new FunDefConcreteAsml(idString, eFunDefAccepte, args));
        return expAccepte;
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
        public void visit(Var e)
        {
            Id id = e.getId();
            if(id.getIdString().equals(ancienNom))
            {
                id.setIdString(nouveauNom);
            }
        }
    }

    @Override
    public NoeudAsml visit(App e) {
        List<VarAsml> arguments = new ArrayList<>();     
        VarAsml varFonction = (VarAsml)e.getE().accept(this);
        String nomFonction = varFonction.getIdString();
        NoeudAsml resultat = null;
        String nomFonctionTraduiteAsml = funCamlVersAsml.get(nomFonction);
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
                /*if(argument instanceof Var)
                {*/
                    //String idString = ((Var)argument).getId().getIdString();
                    arguments.add((VarAsml)argument.accept(this));
                /*}
                else
                {
                    String nouvelId = Id.genIdString();
                    arguments.add(new VarAsml(nouvelId));
                    resultat = new LetAsml(nouvelId, (ExpAsml)argument.accept(this), (AsmtAsml)resultat);
                }*/
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
        return new CallAsml(Constantes.CREATE_ARRAY_ASML, Arrays.asList((VarAsml)e.getE1().accept(this), (VarAsml)e.getE2().accept(this)));
    }

    @Override
    public NoeudAsml visit(Get e) {
        return new MemLectureAsml((VarAsml)e.getE1().accept(this), (VarOuIntAsml)e.getE2().accept(this));
    }

    @Override
    public NoeudAsml visit(Put e) {
        return new MemEcritureAsml((VarAsml)e.getE1().accept(this), (VarOuIntAsml)e.getE2().accept(this), (VarAsml)e.getE3().accept(this));
    }
    
}
