package frontend;

import arbremincaml.Id;
import arbremincaml.Exp;
import arbremincaml.Let;
import arbremincaml.Type;
import visiteur.ObjVisitorExp;

public class VisiteurLetImbriques extends ObjVisitorExp {
    @Override
    public Let visit(Let e) {
        Exp e1 = e.getE1().accept(this);
        Exp e2 = e.getE2().accept(this);
        Type t = e.getT();
        Id id = e.getId();
        if(e1 instanceof Let)
        {
            Let letImbrique = (Let)e1;
            e1 = letImbrique.getE1();
            e2 = new Let(id, t, letImbrique.getE2(), e2);
            t = letImbrique.getT();
            id = letImbrique.getId();
        }        
        return new Let(id, t, e1, e2);
    }
}
