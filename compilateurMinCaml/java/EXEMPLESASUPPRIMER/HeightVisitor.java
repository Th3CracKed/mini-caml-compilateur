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

public class HeightVisitor implements ObjVisitor<Integer> {

    @Override
    public Integer visit(Unit e) {
        // This tree is of height 0
        return 0;
    }

    @Override
    public Integer visit(Bool e) {
        return 0;
    }

    @Override
    public Integer visit(Int e) {
        return 0;
    }

    @Override
    public Integer visit(FloatMinCaml e) { 
        return 0;
    }

    @Override
    public Integer visit(Not e) {
        return e.getE().accept(this) + 1;
    }

    @Override
    public Integer visit(Neg e) {
        return e.getE().accept(this) + 1;
    }

    @Override
    public Integer visit(Add e) {
        int res1 = e.getE1().accept(this);
        int res2 = e.getE2().accept(this);
        return Math.max(res1, res2) + 1 ;
    }

    @Override
    public Integer visit(Sub e) {
        int res1 = e.getE1().accept(this);
        int res2 = e.getE2().accept(this);
        return Math.max(res1, res2) + 1 ;
   }

    @Override
    public Integer visit(FNeg e){
        return e.getE().accept(this) + 1;
    }

    @Override
    public Integer visit(FAdd e){
        int res1 = e.getE1().accept(this);
        int res2 = e.getE2().accept(this);
        return Math.max(res1, res2) + 1 ;
    }

    @Override
    public Integer visit(FSub e){
        int res1 = e.getE1().accept(this);
        int res2 = e.getE2().accept(this);
        return Math.max(res1, res2) + 1 ;
    }

    @Override
    public Integer visit(FMul e) {
        int res1 = e.getE1().accept(this);
        int res2 = e.getE2().accept(this);
        return Math.max(res1, res2) + 1 ;
     }

    @Override
    public Integer visit(FDiv e){
        int res1 = e.getE1().accept(this);
        int res2 = e.getE2().accept(this);
        return Math.max(res1, res2) + 1 ;
    }

    @Override
    public Integer visit(Eq e){
        int res1 = e.getE1().accept(this);
        int res2 = e.getE2().accept(this);
        return Math.max(res1, res2) + 1 ;
    }

    @Override
    public Integer visit(LE e){
        int res1 = e.getE1().accept(this);
        int res2 = e.getE2().accept(this);
        return Math.max(res1, res2) + 1 ;
    }

    @Override
    public Integer visit(If e){
        int res1 = e.getE1().accept(this);
        int res2 = e.getE2().accept(this);
        int res3 = e.getE3().accept(this);
        return Math.max(res1, Math.max(res2, res3)) + 1 ;
    }

    @Override
    public Integer visit(Let e) {
        int res1 = e.getE1().accept(this);
        int res2 = e.getE2().accept(this);
        return Math.max(res1, res2) + 1 ;
    }

    @Override
    public Integer visit(Var e){
        return 0;
    }

    @Override
    public Integer visit(LetRec e){
        int res1 = e.getE().accept(this);
        int res2 = e.getFd().getE().accept(this);
        return Math.max(res1, res2) + 1 ;
    }

    @Override
    public Integer visit(App e){
        int res1 = e.getE().accept(this);
        for (Exp exp : e.getEs()) {
            res1 = Math.max(res1, exp.accept(this));
        }
        return res1 + 1;
    }

    @Override
    public Integer visit(Tuple e){
        int res1 = 0;
        for (Exp exp : e.getEs()) {
            int res = exp.accept(this);
            res1 = Math.max(res, res1);
        }
        return res1 + 1;
    }

    @Override
    public Integer visit(LetTuple e){
        int res1 = e.getE1().accept(this);
        int res2 = e.getE2().accept(this);
        return Math.max(res1, res2);
    }

    @Override
    public Integer visit(Array e){
        int res1 = e.getE1().accept(this);
        int res2 = e.getE2().accept(this);
        return Math.max(res1, res2);
    }

    @Override
    public Integer visit(Get e){
        int res1 = e.getE1().accept(this);
        int res2 = e.getE2().accept(this);
        return Math.max(res1, res2);
    }

    @Override
    public Integer visit(Put e){
        int res1 = e.getE1().accept(this);
        int res2 = e.getE2().accept(this);
        int res3 = e.getE3().accept(this);
        return Math.max(res1, Math.max(res2, res3)) + 1 ;
    }
}


