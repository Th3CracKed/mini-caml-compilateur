package frontend;

import arbremincaml.*;
import arbreasml.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import util.Constantes;
import util.NotYetImplementedException;
import visiteur.DonneesOperateurBinaire;
import visiteur.ObjVisitor;

public class VisiteurGenererArbreAsml implements ObjVisitor<NoeudAsml> {

    private final List<FunDefAsml> funDefs;
    
    private final HashMap<String,String> funCamlVersAsml;
    
    public VisiteurGenererArbreAsml()
    {
        funDefs = new ArrayList<>();
        funCamlVersAsml = new HashMap<>();
        funCamlVersAsml.put(Constantes.PRINT_INT_CAML, Constantes.PRINT_INT_ASML);
        funCamlVersAsml.put(Constantes.PRINT_NEWLINE_CAML, Constantes.PRINT_NEWLINE_ASML);
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
        List<VarAsml> args = new ArrayList<>();
        for(Id idArg : funDef.getArgs())
        {
            args.add(new VarAsml(idArg.getIdString()));
        }
        funDefs.add(new FunDefConcreteAsml(funDef.getId().getIdString(), (AsmtAsml)funDef.getE().accept(this), args));
        return e.getE().accept(this);
    }

    @Override
    public NoeudAsml visit(App e) {
        List<VarAsml> arguments = new ArrayList<>();        
        String nomFonction = ((Var)e.getE()).getId().getIdString();
        String nomFonctionTraduiteAsml = funCamlVersAsml.get(nomFonction);
        if(nomFonctionTraduiteAsml != null)
        {
            nomFonction = nomFonctionTraduiteAsml;
        }
        NoeudAsml resultat = new CallAsml(nomFonction, arguments);
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
        throw new NotYetImplementedException();
    }

    @Override
    public NoeudAsml visit(LetTuple e) {
        throw new NotYetImplementedException();
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
