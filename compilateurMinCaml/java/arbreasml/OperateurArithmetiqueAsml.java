package arbreasml;

public abstract class OperateurArithmetiqueAsml implements ExpAsml{
    private final VarAsml e1;

    public OperateurArithmetiqueAsml(VarAsml e1) {
        this.e1 = e1;
    }

    public VarAsml getE1() {
        return e1;
    }
}
