package frontend;

import arbremincaml.Id;
import arbremincaml.Exp;
import arbremincaml.Let;
import arbremincaml.*;
import arbremincaml.Type;
import java.util.List;
import visiteur.ObjVisitorExp;

public class VisiteurLetImbriques extends ObjVisitorExp {
    
    private static Exp insererLet(Id id, Type t, Exp e1, Exp e2)
    {
        if(e1 instanceof Let)
        {
            Let e1Let = (Let)e1;
            return new Let(e1Let.getId(), e1Let.getT(), e1Let.getE1(), insererLet(id, t, e1Let.getE2(), e2));
        }
        /*else if(e1 instanceof LetRec)
        {
            LetRec e1LetRec = (LetRec)e1;
            FunDef funDef = e1LetRec.getFd();
            return new LetRec(new FunDef(funDef.getId(), funDef.getType(), funDef.getArgs(), funDef.getE()), insererLet(id, t, e1LetRec.getE(), e2));
        }*/
        else
        {
            return new Let(id, t, e1, e2);
        }
    }
    
    private static Exp insererLetRec(Id id, Type t, List<Id> args, Exp eFunDef, Exp exp)
    {
        /*if(eFunDef instanceof Let)
        {
            Let eFunDefLet = (Let)eFunDef;
            return new Let(eFunDefLet.getId(), eFunDefLet.getT(), eFunDefLet.getE1(), insererLetRec(id, t, args, eFunDefLet.getE2(), exp));
        }
        else */if(eFunDef instanceof LetRec)
        {
            LetRec eFunDefLetRec = (LetRec)eFunDef;
            FunDef funDef = eFunDefLetRec.getFd();
            return new LetRec(new FunDef(funDef.getId(), funDef.getType(), funDef.getArgs(), funDef.getE()), insererLetRec(id, t, args, eFunDefLetRec.getE(), exp));
        }
        else
        {
            return new LetRec(new FunDef(id, t, args, eFunDef), exp);
        }
    }
            
    @Override
    public Exp visit(Let e) {
        Exp e1 = e.getE1().accept(this);
        Exp e2 = e.getE2().accept(this);
        return insererLet(e.getId(), e.getT(), e1, e2); 
    }
    
    @Override
    public Exp visit(LetRec e) {
        FunDef funDef = e.getFd();
        Exp eFunDef = funDef.getE().accept(this);
        Exp exp = e.getE().accept(this);
        return insererLetRec(funDef.getId(), funDef.getType(), funDef.getArgs(), eFunDef, exp); 
    }
}
