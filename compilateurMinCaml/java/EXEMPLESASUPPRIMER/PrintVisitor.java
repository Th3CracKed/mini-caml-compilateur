package EXEMPLESASUPPRIMER;

import arbresyntaxique.Add;
import arbresyntaxique.App;
import arbresyntaxique.Array;
import arbresyntaxique.Bool;
import arbresyntaxique.Eq;
import arbresyntaxique.Exp;
import arbresyntaxique.FAdd;
import arbresyntaxique.FDiv;
import arbresyntaxique.FMul;
import arbresyntaxique.FNeg;
import arbresyntaxique.FSub;
import arbresyntaxique.Float;
import arbresyntaxique.Get;
import arbresyntaxique.If;
import arbresyntaxique.Int;
import arbresyntaxique.LE;
import arbresyntaxique.Let;
import arbresyntaxique.LetRec;
import arbresyntaxique.LetTuple;
import arbresyntaxique.Neg;
import arbresyntaxique.Not;
import arbresyntaxique.Put;
import arbresyntaxique.Sub;
import arbresyntaxique.Tuple;
import arbresyntaxique.Unit;
import arbresyntaxique.Var;
import visiteur.Visitor;
import java.util.*;
import visiteur.Visitor;

public class PrintVisitor implements Visitor {
    @Override
    public void visit(Unit e) {
        System.out.print("()");
    }

    @Override
    public void visit(Bool e) {
        System.out.print(e.getValeur());
    }

    @Override
    public void visit(Int e) {
        System.out.print(e.getValeur());
    }

    @Override
    public void visit(Float e) {
        String s = String.format("%.2f", e.getValeur());
        System.out.print(s);
    }

    @Override
    public void visit(Not e) {
        System.out.print("(not ");
        e.getE().accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(Neg e) {
        System.out.print("(- ");
        e.getE().accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(Add e) {
        System.out.print("(");
        e.getE1().accept(this);
        System.out.print(" + ");
        e.getE2().accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(Sub e) {
        System.out.print("(");
        e.getE1().accept(this);
        System.out.print(" - ");
        e.getE2().accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(FNeg e){
        System.out.print("(-. ");
        e.getE().accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(FAdd e){
        System.out.print("(");
        e.getE1().accept(this);
        System.out.print(" +. ");
        e.getE2().accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(FSub e){
        System.out.print("(");
        e.getE1().accept(this);
        System.out.print(" -. ");
        e.getE2().accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(FMul e) {
        System.out.print("(");
        e.getE1().accept(this);
        System.out.print(" *. ");
        e.getE2().accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(FDiv e){
        System.out.print("(");
        e.getE1().accept(this);
        System.out.print(" /. ");
        e.getE2().accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(Eq e){
        System.out.print("(");
        e.getE1().accept(this);
        System.out.print(" = ");
        e.getE2().accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(LE e){
        System.out.print("(");
        e.getE1().accept(this);
        System.out.print(" <= ");
        e.getE2().accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(If e){
        System.out.print("(if ");
        e.getE1().accept(this);
        System.out.print(" then ");
        e.getE2().accept(this);
        System.out.print(" else ");
        e.getE3().accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(Let e) {
        System.out.print("(let ");
        System.out.print(e.getId());
        System.out.print(" = ");
        e.getE1().accept(this);
        System.out.print(" in ");
        e.getE2().accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(Var e){
        System.out.print(e.getId());
    }


    // print sequence of identifiers 
    public static <E> void printInfix(List<E> l, String op) {
        if (l.isEmpty()) {
            return;
        }
        Iterator<E> it = l.iterator();
        System.out.print(it.next());
        while (it.hasNext()) {
            System.out.print(op + it.next());
        }
    }

    // print sequence of Exp
    void printInfix2(List<Exp> l, String op) {
        if (l.isEmpty()) {
            return;
        }
        Iterator<Exp> it = l.iterator();
        it.next().accept(this);
        while (it.hasNext()) {
            System.out.print(op);
            it.next().accept(this);
        }
    }

    @Override
    public void visit(LetRec e){
        System.out.print("(let rec " + e.getFd().getId() + " ");
        printInfix(e.getFd().getArgs(), " ");
        System.out.print(" = ");
        e.getFd().getE().accept(this);
        System.out.print(" in ");
        e.getE().accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(App e){
        System.out.print("(");
        e.getE().accept(this);
        System.out.print(" ");
        printInfix2(e.getEs(), " ");
        System.out.print(")");
    }

    @Override
    public void visit(Tuple e){
        System.out.print("(");
        printInfix2(e.getEs(), ", ");
        System.out.print(")");
    }

    @Override
    public void visit(LetTuple e){
        System.out.print("(let (");
        printInfix(e.getIds(), ", ");
        System.out.print(") = ");
        e.getE1().accept(this);
        System.out.print(" in ");
        e.getE2().accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(Array e){
        System.out.print("(Array.create ");
        e.getE1().accept(this);
        System.out.print(" ");
        e.getE2().accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(Get e){
        e.getE1().accept(this);
        System.out.print(".(");
        e.getE2().accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(Put e){
        System.out.print("(");
        e.getE1().accept(this);
        System.out.print(".(");
        e.getE2().accept(this);
        System.out.print(") <- ");
        e.getE3().accept(this);
        System.out.print(")");
    }
}


