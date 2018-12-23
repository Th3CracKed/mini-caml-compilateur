package frontend;

import arbremincaml.*;
import java.util.HashSet;
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
            return new Let(id, e.getT(), e1, e2);
        } else {
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
