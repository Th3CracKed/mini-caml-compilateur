package arbreasml;

public abstract class IfIntAsml extends IfAsml {
    private final VarOuIntAsml e2;

    public IfIntAsml(VarAsml e1, VarOuIntAsml e2, AsmtAsml eIf, AsmtAsml eElse) {
        super(e1, eIf, eElse);
        this.e2 = e2;
    }

    public VarOuIntAsml getE2() {
        return e2;
    }  
}
