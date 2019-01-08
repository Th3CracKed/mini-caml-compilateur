package frontend;

import arbremincaml.Id;
import arbremincaml.Exp;
import arbremincaml.Let;
import arbremincaml.*;
import arbremincaml.Type;
import java.util.List;
import visiteur.ObjVisitorExp;

public class VisiteurLetImbriques extends ObjVisitorExp {
    
    private static Exp insererLet(Id id, Exp e1, Exp e2)
    {
        if(e1 instanceof Let)
        {
            Let e1Let = (Let)e1;
            return new Let(e1Let.getId(), Type.gen(), e1Let.getE1(), insererLet(id, e1Let.getE2(), e2));
        }
        else if(e1 instanceof LetRec)
        {
            LetRec e1LetRec = (LetRec)e1;
            FunDef funDef = e1LetRec.getFd();
            return new LetRec(new FunDef(funDef.getId(), funDef.getType(), funDef.getArgs(), funDef.getE()), insererLet(id, e1LetRec.getE(), e2));
        }
        else if(e1 instanceof LetTuple)
        {
            LetTuple e1LetTuple = (LetTuple)e1;
            return new LetTuple(e1LetTuple.getIds(), e1LetTuple.getTs(), e1LetTuple.getE1(), insererLet(id, e1LetTuple.getE2(), e2));
        }
        else
        {
            return new Let(id, Type.gen(), e1, e2);
        }
    }
    
    /*private static Exp insererLetRec(Id id, Type t, List<Id> args, Exp eFunDef, Exp exp)
    {
        if(eFunDef instanceof Let)
        {
            Let eFunDefLet = (Let)eFunDef;
            return new Let(eFunDefLet.getId(), eFunDefLet.getT(), eFunDefLet.getE1(), insererLetRec(id, t, args, eFunDefLet.getE2(), exp));
        }
        else if(eFunDef instanceof LetRec)
        {
            LetRec eFunDefLetRec = (LetRec)eFunDef;
            FunDef funDef = eFunDefLetRec.getFd();
            return new LetRec(new FunDef(funDef.getId(), funDef.getType(), funDef.getArgs(), funDef.getE()), insererLetRec(id, t, args, eFunDefLetRec.getE(), exp));
        }
        else if(eFunDef instanceof LetTuple)
        {
            LetTuple eFunDefLetTuple = (LetTuple)eFunDef;
            return new LetTuple(eFunDefLetTuple.getIds(), eFunDefLetTuple.getTs(), eFunDefLetTuple.getE1(), insererLetRec(id, t, args, eFunDefLetTuple.getE2(), exp));
        }
        else
        {
            return new LetRec(new FunDef(id, t, args, eFunDef), exp);
        }
    }*/        
    
    private static Exp insererLetTuple(List<Id> ids, List<Type> ts, Exp e1, Exp e2)
    {
        if(e1 instanceof Let)
        {
            Let e1Let = (Let)e1;
            return new Let(e1Let.getId(), e1Let.getT(), e1Let.getE1(), insererLetTuple(ids, ts, e1Let.getE2(), e2));
        }
        else if(e1 instanceof LetRec)
        {
            LetRec e1LetRec = (LetRec)e1;
            FunDef funDef = e1LetRec.getFd();
            return new LetRec(new FunDef(funDef.getId(), funDef.getType(), funDef.getArgs(), funDef.getE()), insererLetTuple(ids, ts, e1LetRec.getE(), e2));
        }
        else if(e1 instanceof LetTuple)
        {
            LetTuple e1LetTuple = (LetTuple)e1;
            return new LetTuple(e1LetTuple.getIds(), e1LetTuple.getTs(), e1LetTuple.getE1(), insererLetTuple(ids, ts, e1LetTuple.getE2(), e2));
        }
        else
        {
            return new LetTuple(ids, ts, e1, e2);
        }
    }
    
    @Override
    public Exp visit(Let e) {
        Exp e1 = e.getE1().accept(this);
        Exp e2 = e.getE2().accept(this);
        return insererLet(e.getId(), e1, e2); 
    }
    
    @Override
    public Exp visit(LetTuple e) {
        Exp e1 = e.getE1().accept(this);
        Exp e2 = e.getE2().accept(this);
        return insererLetTuple(e.getIds(), e.getTs(), e1, e2); 
    }
    
    /*@Override
    public Exp visit(LetRec e) {
        FunDef funDef = e.getFd();
        Exp eFunDef = funDef.getE().accept(this);
        Exp exp = e.getE().accept(this);
        return insererLetRec(funDef.getId(), funDef.getType(), funDef.getArgs(), eFunDef, exp); 
    }*/

}
