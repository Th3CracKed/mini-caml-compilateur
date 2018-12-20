package arbremincaml;

public class TFun extends Type {
    private final Type t1;
    private final Type t2;
    
    public TFun(Type t1 , Type t2)
    {
        this.t1 = t1;
        this.t2 = t2;
    }
    
    public Type getT1()
    {
        return t1;
    }
    
    public Type getT2()
    {
        return t2;
    }
    
    @Override
    public String toString()
    {
        return "("+t1+") -> ("+t2+")";
    }
}
