package EXEMPLESASUPPRIMER;

import arbremincaml.Add;
import arbremincaml.App;
import arbremincaml.Array;
import arbremincaml.Bool;
import arbremincaml.Eq;
import arbremincaml.Exp;
import arbremincaml.FAdd;
import arbremincaml.FDiv;
import arbremincaml.FMul;
import arbremincaml.FNeg;
import arbremincaml.FSub;
import arbremincaml.FloatMinCaml;
import arbremincaml.Get;
import arbremincaml.Id;
import arbremincaml.If;
import arbremincaml.Int;
import arbremincaml.LE;
import arbremincaml.Let;
import arbremincaml.LetRec;
import arbremincaml.LetTuple;
import arbremincaml.Neg;
import arbremincaml.Not;
import arbremincaml.Put;
import arbremincaml.Sub;
import arbremincaml.Tuple;
import arbremincaml.Unit;
import arbremincaml.Var;
import java.util.*;
import visiteur.ObjVisitor;

class VarVisitor implements ObjVisitor<Set<String>> {

    @Override
    public Set<String> visit(Unit e) {
        return new HashSet<>();
    }

    @Override
    public Set<String> visit(Bool e) {
        return new HashSet<>();
    }

    @Override
    public Set<String> visit(Int e) {
        return new HashSet<>();
    }

    @Override
    public Set<String> visit(FloatMinCaml e) { 
        return new HashSet<>();
    }

    @Override
    public Set<String> visit(Not e) {
        Set<String> fv = e.getE().accept(this);
        return e.getE().accept(this);
    }

    @Override
    public Set<String> visit(Neg e) {
        Set<String> fv = e.getE().accept(this);
        return fv;
    }

    @Override
    public Set<String> visit(Add e) {
        Set<String> fv1 = e.getE1().accept(this);
        Set<String> fv2 = e.getE2().accept(this);
        fv1.addAll(fv2);
        return fv1;
    }

    @Override
    public Set<String> visit(Sub e) {
        Set<String> fv1 = e.getE1().accept(this);
        Set<String> fv2 = e.getE2().accept(this);
        fv1.addAll(fv2);
        return fv1;
    }

    @Override
    public Set<String> visit(FNeg e){
        Set<String> fv = e.getE().accept(this);
        return fv;
    }

    @Override
    public Set<String> visit(FAdd e){
        Set<String> fv1 = e.getE1().accept(this);
        Set<String> fv2 = e.getE2().accept(this);
        fv1.addAll(fv2);
        return fv1;
    }

    @Override
    public Set<String> visit(FSub e){
        Set<String> fv1 = e.getE1().accept(this);
        Set<String> fv2 = e.getE2().accept(this);
        fv1.addAll(fv2);
        return fv1;
    }

    @Override
    public Set<String> visit(FMul e) {
        Set<String> fv1 = e.getE1().accept(this);
        Set<String> fv2 = e.getE2().accept(this);
        fv1.addAll(fv2);
        return fv1;
    }

    @Override
    public Set<String> visit(FDiv e){
        Set<String> fv1 = e.getE1().accept(this);
        Set<String> fv2 = e.getE2().accept(this);
        fv1.addAll(fv2);
        return fv1;
    }

    @Override
    public Set<String> visit(Eq e){
        Set<String> fv1 = e.getE1().accept(this);
        Set<String> fv2 = e.getE2().accept(this);
        fv1.addAll(fv2);
        return fv1;
    }

    @Override
    public Set<String> visit(LE e){
        Set<String> fv1 = e.getE1().accept(this);
        Set<String> fv2 = e.getE2().accept(this);
        fv1.addAll(fv2);
        return fv1;
    }

    @Override
    public Set<String> visit(If e){
        Set<String> fv1 = e.getE1().accept(this);
        Set<String> fv2 = e.getE2().accept(this);
        Set<String> fv3 = e.getE3().accept(this);
        fv1.addAll(fv2);
        fv1.addAll(fv3);
        return fv1;
    }

    @Override
    public Set<String> visit(Let e) {
        Set<String> res = new HashSet<>();
        Set<String> fv1 = e.getE1().accept(this);
        Set<String> fv2 = e.getE2().accept(this);
        fv2.remove(e.getId().toString());
        res.addAll(fv1);
        res.addAll(fv2);
        return res;
    }

    @Override
    public Set<String> visit(Var e){
        Set<String> res = new HashSet<>();
        res.add(e.getId().toString());
        return res;
    }

    @Override
    public Set<String> visit(LetRec e){
        Set<String> res = new HashSet<>();
        Set<String> fv = e.getE().accept(this);
        Set<String> fv_fun = e.getFd().getE().accept(this);
        for (Id id : e.getFd().getArgs()) {
            fv_fun.remove(id.toString());
        }
        fv.remove(e.getFd().getId().toString());
        fv_fun.remove(e.getFd().getId().toString());
        res.addAll(fv);
        res.addAll(fv_fun);
        return res;
    }

    @Override
    public Set<String> visit(App e){
        Set<String> res = new HashSet<>();
        res.addAll(e.getE().accept(this));
        for (Exp exp : e.getEs()) {
            res.addAll(exp.accept(this));
        }
        return res;
    }

    @Override
    public Set<String> visit(Tuple e){
        Set<String> res = new HashSet<>();
        for (Exp exp : e.getEs()) {
            res.addAll(exp.accept(this));
        }
        return res;
    }

    @Override
    public Set<String> visit(LetTuple e){
        Set<String> res = new HashSet<>();
        Set<String> fv1 = e.getE1().accept(this);
        Set<String> fv2 = e.getE2().accept(this);
        for (Id id : e.getIds()) {
            fv2.remove(id.toString());
        }
        res.addAll(fv1);
        res.addAll(fv2);
        return res;
    }

    @Override
    public Set<String> visit(Array e){
        Set<String> fv1 = e.getE1().accept(this);
        Set<String> fv2 = e.getE2().accept(this);
        fv1.addAll(fv2);
        return fv1;
    }

    @Override
    public Set<String> visit(Get e){
        Set<String> fv1 = e.getE1().accept(this);
        Set<String> fv2 = e.getE2().accept(this);
        fv1.addAll(fv2);
        return fv1;
    }

    @Override
    public Set<String> visit(Put e){
        Set<String> fv1 = e.getE1().accept(this);
        Set<String> fv2 = e.getE2().accept(this);
        Set<String> fv3 = e.getE3().accept(this);
        fv1.addAll(fv2);
        fv1.addAll(fv3);
        return fv1;
    }
}


