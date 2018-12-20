package EXEMPLESASUPPRIMER;
// the height of the AST) but uses the simple visitor for which
// every method returns void. Not recommended.

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
import visiteur.Visitor;
import visiteur.Visitor;

// 
// To use it, add the following lines in your main(),
// where "result" is the Exp produced by the parser.
//
//      HeightVisitor v = new HeightVisitor();
//      result.accept(v);
//      System.out.println("Height = " + v.getRes());
//
// Similarly, you can compute more complex values based
// on this scheme. For instance, the set of free variables,
// or generate a new tree.
 
class HeightVisitorSimple implements Visitor {

    // this variable is used to store the result of 
    // each visit method. In particular, we can consider
    // that after each call to "accept", res stores the
    // result produced by the visitor
    private int res;

    // Used to get the final result from the Main method 
    public int getRes() {
        return res;
    }

    @Override
    public void visit(Unit e) {
        // This tree is of height 0
        res = 0;
    }

    @Override
    public void visit(Bool e) {
        res = 0;
    }

    @Override
    public void visit(Int e) {
        res = 0;
    }

    @Override
    public void visit(FloatMinCaml e) { 
        res = 0;
    }

    @Override
    public void visit(Not e) {
        // we compute the height of e's only subtree
        // we know that after this call, the height of the
        // tree will be stored in res
        e.getE().accept(this);
        // we increment res, as e is one unit higher then e.e
        res++;
    }

    @Override
    public void visit(Neg e) {
        e.getE().accept(this);
        res++;
    }

    @Override
    public void visit(Add e) {
        // this not has two subtrees
        // we retrieve e1 height with accept call on left subtree
        e.getE1().accept(this);
        // we need to copy res as it will be updated by the next
        // accept call
        int res1 = res;
        // same thing on right subtree
        e.getE2().accept(this);
        int res2 = res;
        // finally, we store e's height in res
        res = Math.max(res1, res2) + 1 ;
    }

    @Override
    public void visit(Sub e) {
        e.getE1().accept(this);
        int res1 = res;
        e.getE2().accept(this);
        int res2 = res;
        res = Math.max(res1, res2) + 1 ;
   }

    @Override
    public void visit(FNeg e){
        e.getE().accept(this);
        res++;
    }

    @Override
    public void visit(FAdd e){
        e.getE1().accept(this);
        int res1 = res;
        e.getE2().accept(this);
        int res2 = res;
        res = Math.max(res1, res2) + 1 ;
    }

    @Override
    public void visit(FSub e){
        e.getE1().accept(this);
        int res1 = res;
        e.getE2().accept(this);
        int res2 = res;
        res = Math.max(res1, res2) + 1 ;
    }

    @Override
    public void visit(FMul e) {
        e.getE1().accept(this);
        int res1 = res;
        e.getE2().accept(this);
        int res2 = res;
        res = Math.max(res1, res2) + 1 ;
     }

    @Override
    public void visit(FDiv e){
        e.getE1().accept(this);
        int res1 = res;
        e.getE2().accept(this);
        int res2 = res;
        res = Math.max(res1, res2) + 1 ;
    }

    @Override
    public void visit(Eq e){
        e.getE1().accept(this);
        int res1 = res;
        e.getE2().accept(this);
        int res2 = res;
        res = Math.max(res1, res2) + 1 ;
    }

    @Override
    public void visit(LE e){
        e.getE1().accept(this);
        int res1 = res;
        e.getE2().accept(this);
        int res2 = res;
        res = Math.max(res1, res2) + 1 ;
    }

    @Override
    public void visit(If e){
        e.getE1().accept(this);
        int res1 = res;
        e.getE2().accept(this);
        int res2 = res;
        e.getE3().accept(this);
        int res3 = res;
        res = Math.max(res1, Math.max(res2, res3)) + 1 ;
    }

    @Override
    public void visit(Let e) {
        e.getE1().accept(this);
        int res1 = res;
        e.getE2().accept(this);
        int res2 = res;
        res = Math.max(res1, res2) + 1 ;
    }

    @Override
    public void visit(Var e){
        res = 0;
    }

    @Override
    public void visit(LetRec e){
        e.getE().accept(this);
        int res1 = res;
        e.getFd().getE().accept(this);
        int res2 = res;
        res = Math.max(res1, res2) + 1 ;
    }

    @Override
    public void visit(App e){
        e.getE().accept(this);
        int res1 = res;
        for (Exp exp : e.getEs()) {
            exp.accept(this);
            res1 = Math.max(res1, res);
        }
        res = res1 + 1;
    }

    @Override
    public void visit(Tuple e){
        int res1 = 0;
        for (Exp exp : e.getEs()) {
            exp.accept(this);
            res1 = Math.max(res, res1);
        }
        res = res1 + 1;
    }

    @Override
    public void visit(LetTuple e){
        e.getE1().accept(this);
        int res1 = res;
        e.getE2().accept(this);
        int res2 = res;
        res = Math.max(res1, res2) + 1 ;
    }

    @Override
    public void visit(Array e){
        e.getE1().accept(this);
        int res1 = res;
        e.getE2().accept(this);
        int res2 = res;
        res = Math.max(res1, res2) + 1 ;
    }

    @Override
    public void visit(Get e){
        e.getE1().accept(this);
        int res1 = res;
        e.getE2().accept(this);
        int res2 = res;
        res = Math.max(res1, res2) + 1 ;
    }

    @Override
    public void visit(Put e){
        e.getE1().accept(this);
        int res1 = res;
        e.getE2().accept(this);
        int res2 = res;
        e.getE3().accept(this);
        int res3 = res;
        res = Math.max(res1, Math.max(res2, res3)) + 1 ;
    }
}


