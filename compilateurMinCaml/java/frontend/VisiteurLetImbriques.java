package frontend;

import arbremincaml.Id;
import arbremincaml.Exp;
import arbremincaml.Let;
import arbremincaml.Type;
import visiteur.ObjVisitorExp;

public class VisiteurLetImbriques extends ObjVisitorExp {
    
    private static Let inserer(Id id, Type t, Exp e1, Exp e2)
    {
        if(e1 instanceof Let)
        {
            Let e1Let = (Let)e1;
            return new Let(e1Let.getId(), e1Let.getT(), e1Let.getE1(), inserer(id, t, e1Let.getE2(), e2));
        }
        else
        {
            return new Let(id, t, e1, e2);
        }
    }
    
    @Override
    public Let visit(Let e) {
        Exp e1 = e.getE1().accept(this);
        Exp e2 = e.getE2().accept(this);
        return inserer(e.getId(), e.getT(), e1, e2); 
    }
}
