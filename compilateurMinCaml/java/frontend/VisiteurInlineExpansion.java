package frontend;

import arbremincaml.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import util.Constantes;
import visiteur.*;

public class VisiteurInlineExpansion extends ObjVisitorExp {
    
    private static final int TAILLE_MAX_INLINE_EXPANSION = 10000;//100;    
    private final HashMap<String, FunDef> fonctionAEtendre;
    
    public VisiteurInlineExpansion()
    {
        fonctionAEtendre = new HashMap<>();
    }
    
    @Override
    public Exp visit(LetRec e)
    {
        FunDef funDef = e.getFd();
        int tailleCorpsFonction = funDef.getE().accept(new VisiteurNbNoeudsArbre());
        if(tailleCorpsFonction <= TAILLE_MAX_INLINE_EXPANSION)
        {
            fonctionAEtendre.put(funDef.getId().getIdString(), funDef);
        }
        return super.visit(e);
    }
    
    @Override
    public Exp visit(App e)
    {
        FunDef funDef = null;
        Exp fun = e.getE();
        if(fun instanceof Var)
        {
            funDef = fonctionAEtendre.get(((Var)fun).getId().getIdString());
        }
        if(funDef != null && !Constantes.FONCTION_EXTERNES_MINCAML.contains(((Var)e.getE()).getId().getIdString()))
        {            
            HashMap<String, String> renommage = new HashMap<>();
            List<Id> args = funDef.getArgs();
            for(int i = 0 ; i < args.size() ; i++)
            {
                renommage.put(args.get(i).getIdString(), ((Var)e.getEs().get(i)).getId().getIdString());
            }
            Exp copieCorpsFonction = funDef.getE().accept(new VisiteurCopieArbre());
            copieCorpsFonction.accept(new VisiteurAlphaConversion(renommage));
            return copieCorpsFonction;
        }
        else
        {
            return super.visit(e);
        }
    }   
    
    private class VisiteurNbNoeudsArbre implements ObjVisitor<Integer>
    {

        @Override
        public Integer visit(Unit e) {
            return 1;
        }

        @Override
        public Integer visit(Bool e) {
            return 1;
        }

        @Override
        public Integer visit(Int e) {
            return 1;
        }

        @Override
        public Integer visit(FloatMinCaml e) {
            return 1;
        }

        private Integer visitOpUnaireWorker(OperateurUnaire e)
        {
            return 1 + e.getE().accept(this);
        }
        
        @Override
        public Integer visit(Not e) {
            return visitOpUnaireWorker(e);
        }

        @Override
        public Integer visit(Neg e) {
            return visitOpUnaireWorker(e);
        }
        
        private Integer visitOpBinaireWorker(OperateurBinaire e)
        {
            return 1 + e.getE1().accept(this) + e.getE2().accept(this);
        }

        @Override
        public Integer visit(Add e) {
            return visitOpBinaireWorker(e);
        }

        @Override
        public Integer visit(Sub e) {
            return visitOpBinaireWorker(e);
        }

        @Override
        public Integer visit(FNeg e) {
            return visitOpUnaireWorker(e);
        }

        @Override
        public Integer visit(FAdd e) {
            return visitOpBinaireWorker(e);
        }

        @Override
        public Integer visit(FSub e) {
            return visitOpBinaireWorker(e);
        }

        @Override
        public Integer visit(FMul e) {
            return visitOpBinaireWorker(e);
        }

        @Override
        public Integer visit(FDiv e) {
            return visitOpBinaireWorker(e);
        }

        @Override
        public Integer visit(Eq e) {
            return visitOpBinaireWorker(e);
        }

        @Override
        public Integer visit(LE e) {
            return visitOpBinaireWorker(e);
        }

        @Override
        public Integer visit(If e) {
            return 1 + e.getE1().accept(this) + e.getE2().accept(this) + e.getE3().accept(this);
        }

        @Override
        public Integer visit(Let e) {
            return 1 + e.getE1().accept(this) + e.getE2().accept(this);
        }

        @Override
        public Integer visit(Var e) {
            return 1;
        }

        @Override
        public Integer visit(LetRec e) {
            return 1 + e.getE().accept(this) + e.getFd().getE().accept(this);
        }

        @Override
        public Integer visit(App e) {
            return 1 + e.getEs().stream().mapToInt(x->x.accept(this)).sum();
        }

        @Override
        public Integer visit(Tuple e) {
            return 1 + e.getEs().stream().mapToInt(x->x.accept(this)).sum();
        }

        @Override
        public Integer visit(LetTuple e) {
            return 1 + e.getE1().accept(this) + e.getE2().accept(this);
        }

        @Override
        public Integer visit(Array e) {
            return 1 + e.getE1().accept(this) + e.getE2().accept(this);
        }

        private Integer visitAccesTableauWorker(AccesTableau e)
        {
            return 1 + e.getE1().accept(this) + e.getE2().accept(this);
        }
        
        @Override
        public Integer visit(Get e) {
            return visitAccesTableauWorker(e);
        }

        @Override
        public Integer visit(Put e) {
            return visitAccesTableauWorker(e) + e.getE3().accept(this);
        }
        
    }
    
    private class VisiteurCopieArbre extends ObjVisitorExp
    {
        @Override
        public Exp visit(Var e)
        {
            return new Var(new Id(e.getId().getIdString()));
        }
        
        @Override
        public Exp visit(Let e) {
          Exp e1 = e.getE1().accept(this);
          Exp e2 = e.getE2().accept(this);
          return new Let(new Id(e.getId().getIdString()), Type.gen(), e1 , e2);
        }

        @Override
        public Exp visit(LetRec e){
           Exp exp = e.getE().accept(this);
           FunDef funDef = e.getFd();
           List<Id> nouveauxArgs = new ArrayList<>();
           for(Id idArg : funDef.getArgs())
           {
               nouveauxArgs.add(new Id(idArg.getIdString()));
           }
           FunDef nouvelleFunDef = new FunDef(new Id(funDef.getId().getIdString()), funDef.getType(), nouveauxArgs, funDef.getE().accept(this));
          return new LetRec(nouvelleFunDef, exp);
        }
        
        @Override
        public Exp visit(LetTuple e){
           Exp e1 = e.getE1().accept(this);
           Exp e2 = e.getE2().accept(this);
           List<Id> nouveauxIds = new ArrayList<>();
           for(Id id : e.getIds())
           {
               nouveauxIds.add(new Id(id.getIdString()));
           }
          return new LetTuple(nouveauxIds, e.getTs(), e1, e2);
        }
    }
}
