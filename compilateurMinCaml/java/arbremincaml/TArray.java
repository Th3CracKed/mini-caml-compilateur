package arbremincaml;

public class TArray extends Type{
    private Type t;
    
    public TArray(Type t)
    {
        setT(t);
    }    
    
    public Type getT()
    {
        return t;
    }
    
    public final void setT(Type t)
    {
        this.t = t;
    }
    
    @Override
    public String toString()
    {
        return "["+t+"]";
    }
}
