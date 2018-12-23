package typage;

import arbremincaml.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;
import util.*;
import visiteur.ObjVisitor;

public class VisiteurGenererEquationType implements ObjVisitor<LinkedList<EquationType>>{

    private final HashMap<String, Type> environnement;
    private Type type;
    private final Stack<Type> anciensTypes;
    
    public VisiteurGenererEquationType()
    {
        environnement = new HashMap<>();
        environnement.put(Constantes.PRINT_INT_CAML, new TFun(new TInt(), new TUnit()));
        environnement.put(Constantes.PRINT_NEWLINE_CAML, new TFun(new TUnit(), new TUnit()));
        /*environnement.put(Constantes.SIN_CAML, new TFun(new TFloat(), new TFloat()));
        environnement.put(Constantes.COS_CAML, new TFun(new TFloat(), new TFloat()));
        environnement.put(Constantes.SQRT_CAML, new TFun(new TFloat(), new TFloat()));
        environnement.put(Constantes.ABS_FLOAT_CAML, new TFun(new TFloat(), new TFloat()));
        environnement.put(Constantes.INT_OF_FLOAT_CAML, new TFun(new TFloat(), new TInt()));
        environnement.put(Constantes.FLOAT_OF_INT_CAML, new TFun(new TInt(), new TFloat()));
        environnement.put(Constantes.TRUNCATE_CAML, new TFun(new TFloat(), new TFloat()));*/
        type = new TUnit();
        anciensTypes = new Stack<>();
    }
    
    private LinkedList<EquationType> creerSingleton(Type t1, Type t2)
    {
        return new LinkedList<>(Arrays.asList(new EquationType(t1, t2)));
    }
    
    @Override
    public LinkedList<EquationType> visit(Unit e) {
        return creerSingleton(type, new TUnit());
    }

    @Override
    public LinkedList<EquationType> visit(Bool e) {
        return creerSingleton(type, new TBool());
    }

    @Override
    public LinkedList<EquationType> visit(Int e) {
        return creerSingleton(type, new TInt());
    }

    @Override
    public LinkedList<EquationType> visit(FloatMinCaml e) {
        throw new NotYetImplementedException();
    }
    
    private void changerType(Type type)
    {
        anciensTypes.push(this.type);
        this.type = type;
    }
    
    private void restaurerType()
    {
        type = anciensTypes.pop();
    }
    
    private LinkedList<EquationType> visitOpUnaireWorker(OperateurUnaire e, Type t)
    {
        changerType(t);
        LinkedList<EquationType> equationsE = e.getE().accept(this);
        restaurerType();
        equationsE.addFirst(new EquationType(type, t));
        return equationsE;
    }
    
    @Override
    public LinkedList<EquationType> visit(Not e) {
        return visitOpUnaireWorker(e, new TBool());
    }

    @Override
    public LinkedList<EquationType> visit(Neg e) {
        return visitOpUnaireWorker(e, new TInt());
    }

    private LinkedList<EquationType> visitOpBinaireWorker(OperateurBinaire e, Type typeOperandes, Type typeOperateur)
    {
        changerType(typeOperandes);
        LinkedList<EquationType> equationsE1 = e.getE1().accept(this);
        LinkedList<EquationType> equationsE2 = e.getE2().accept(this);
        restaurerType();
        equationsE1.addAll(equationsE2);
        equationsE1.addFirst(new EquationType(typeOperateur, type));
        return equationsE1;
    }
    
    private LinkedList<EquationType> visitOpArithmetiqueIntWorker(OperateurArithmetiqueInt e)
    {
        return visitOpBinaireWorker(e, new TInt(), new TInt());
    }
    
    private LinkedList<EquationType> visitOpRelationnelWorker(OperateurRelationnel e, Type typeOperandes)
    {
        return visitOpBinaireWorker(e, typeOperandes, new TBool());
    }
    
    @Override
    public LinkedList<EquationType> visit(Add e) {        
        return visitOpArithmetiqueIntWorker(e);
    }

    @Override
    public LinkedList<EquationType> visit(Sub e) {
        return visitOpArithmetiqueIntWorker(e);
    }

    @Override
    public LinkedList<EquationType> visit(FNeg e) {
        throw new NotYetImplementedException();
    }

    @Override
    public LinkedList<EquationType> visit(FAdd e) {
        throw new NotYetImplementedException();
    }

    @Override
    public LinkedList<EquationType> visit(FSub e) {
        throw new NotYetImplementedException();
    }

    @Override
    public LinkedList<EquationType> visit(FMul e) {
        throw new NotYetImplementedException();
    }

    @Override
    public LinkedList<EquationType> visit(FDiv e) {
        throw new NotYetImplementedException();
    }
    
    @Override
    public LinkedList<EquationType> visit(Eq e) {
        return visitOpRelationnelWorker(e, Type.gen());
    }

    @Override
    public LinkedList<EquationType> visit(LE e) {
        return visitOpRelationnelWorker(e, new TInt());
    }

    @Override
    public LinkedList<EquationType> visit(If e) {        
        changerType(new TBool());
        LinkedList<EquationType> equations = e.getE1().accept(this);
        restaurerType();
        Type typeIf = Type.gen();
        changerType(typeIf);
        LinkedList<EquationType> equationsE2 = e.getE2().accept(this);
        LinkedList<EquationType> equationsE3 = e.getE3().accept(this);
        restaurerType();
        equations.addAll(equationsE2);
        equations.addAll(equationsE3);
        equations.addFirst(new EquationType(typeIf, type));
        return equations;
    }

    @Override
    public LinkedList<EquationType> visit(Let e) {
        Type typeVariable = e.getT();
        String idString = e.getId().getIdString();
        changerType(typeVariable);
        LinkedList<EquationType> equationsE1 = e.getE1().accept(this);
        restaurerType();
        environnement.put(idString, typeVariable);
        LinkedList<EquationType> equationsE2 = e.getE2().accept(this);
        environnement.remove(idString);
        equationsE1.addAll(equationsE2);
        return equationsE1;
    }

    @Override
    public LinkedList<EquationType> visit(Var e) {
        String idString = e.getId().getIdString();
        Type typeE = environnement.get(idString);
        if(typeE == null)
        {
            throw new MyCompilationException("La variable "+idString+" n'a pas été déclarée");
        }
        return creerSingleton(typeE, type);
    }

    @Override
    public LinkedList<EquationType> visit(LetRec e) {
        throw new NotYetImplementedException();
    }

    @Override
    public LinkedList<EquationType> visit(App e) {
        Type typeVarFonction = Type.gen();
        changerType(typeVarFonction);
        LinkedList<EquationType> equations = e.getE().accept(this);
        restaurerType();
        Type typeResultat = Type.gen();
        TFun typeFun = null;
        for(Exp argument : e.getEs())
        {
            Type typeArgument = Type.gen();
            changerType(typeArgument);
            equations.addAll(argument.accept(this));
            restaurerType();
            typeFun = new TFun(typeArgument, (typeFun == null)?typeResultat:typeFun);
        }
        equations.add(new EquationType(typeVarFonction, typeFun));
        equations.add(new EquationType(type, typeResultat));
        return equations;
    }

    @Override
    public LinkedList<EquationType> visit(Tuple e) {
        throw new NotYetImplementedException();
    }

    @Override
    public LinkedList<EquationType> visit(LetTuple e) {
        throw new NotYetImplementedException();
    }

    @Override
    public LinkedList<EquationType> visit(Array e) {
        throw new NotYetImplementedException();
    }

    @Override
    public LinkedList<EquationType> visit(Get e) {
        throw new NotYetImplementedException();
    }

    @Override
    public LinkedList<EquationType> visit(Put e) {
        throw new NotYetImplementedException();
    }
    
}
