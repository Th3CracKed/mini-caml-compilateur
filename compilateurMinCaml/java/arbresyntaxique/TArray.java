package arbresyntaxique;

public class TArray extends Type{
    private final Type t;
    
    public TArray(Type t)
    {
        this.t = t;
    }
    
    public Type getT()
    {
        return t;
    }
}
