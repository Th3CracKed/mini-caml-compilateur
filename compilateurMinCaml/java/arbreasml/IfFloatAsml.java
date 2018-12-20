package arbreasml;

public abstract class IfFloatAsml extends IfAsml {
    private final VarAsml e2;

    public IfFloatAsml(VarAsml e1, VarAsml e2, AsmtAsml eIf, AsmtAsml eElse) {
        super(e1, eIf, eElse);
        this.e2 = e2;
    }

    public VarAsml getE2() {
        return e2;
    }  
}
