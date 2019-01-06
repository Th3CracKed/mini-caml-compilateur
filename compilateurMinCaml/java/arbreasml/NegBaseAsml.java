package arbreasml;

public abstract class NegBaseAsml implements ExpAsml {
    private final VarAsml e;
    public NegBaseAsml(VarAsml e)
    {
        this.e = e;
    }    

    public VarAsml getE() {
        return e;
    }
}
