package frontend;

import arbremincaml.*;
import java.util.HashSet;
import java.util.List;
import visiteur.ObjVisitorExp;
import visiteur.Visitor;

public class VisiteurDefinitionsInutiles extends ObjVisitorExp {

    @Override
    public Exp visit(Let e) {
        Exp e2 = e.getE2().accept(this);
        Id id = e.getId();
        VisiteurVariablesUtilisees visVarUtilisees = new VisiteurVariablesUtilisees();
        e2.accept(visVarUtilisees);
        VisiteurEffetDeBord visEffetDeBord = new VisiteurEffetDeBord();
        e.getE1().accept(visEffetDeBord);
        if (visVarUtilisees.getVariablesUtilisees().contains(id.getIdString()) || visEffetDeBord.getAUnEffetDeBord()) {
            Exp e1 = e.getE1().accept(this);
            return new Let(id, Type.gen(), e1, e2);
        } else {
            return e2;
        }
    }
    
    @Override
    public Exp visit(LetRec e) {
        Exp exp = e.getE().accept(this);
        FunDef funDef = e.getFd();
        Id id = funDef.getId();
        VisiteurVariablesUtilisees visVarUtilisees = new VisiteurVariablesUtilisees();
        exp.accept(visVarUtilisees);
        if (visVarUtilisees.getVariablesUtilisees().contains(id.getIdString())) {
            Exp eFunDef = funDef.getE().accept(this);
            return new LetRec(new FunDef(id, funDef.getType(), funDef.getArgs(), eFunDef), exp);
        } else {
            return exp;
        }
    }
    
    @Override
    public Exp visit(LetTuple e)
    {
        Exp e2 = e.getE2().accept(this);
        List<Id> ids = e.getIds();
        VisiteurVariablesUtilisees visVarUtilisees = new VisiteurVariablesUtilisees();
        e2.accept(visVarUtilisees);        
        if (ids.stream().anyMatch(x->visVarUtilisees.getVariablesUtilisees().contains(x.getIdString())))
        {
            Exp e1 = e.getE1().accept(this);
            return new LetTuple(ids, e.getTs(), e1, e2);
        }
        else
        {
            return e2;
        }
    }
    
    private class VisiteurVariablesUtilisees implements Visitor {

        private final HashSet<String> variablesUtilisees;

        public VisiteurVariablesUtilisees() {
            variablesUtilisees = new HashSet<>();
        }

        public HashSet<String> getVariablesUtilisees() {
            return variablesUtilisees;
        }

        @Override
        public void visit(Var e) {
            variablesUtilisees.add(e.getId().getIdString());
        }
    }
}
