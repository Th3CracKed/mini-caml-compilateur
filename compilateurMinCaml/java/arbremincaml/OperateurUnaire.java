package arbremincaml;

public abstract class OperateurUnaire extends Exp {
    private final Exp e;

    OperateurUnaire(Exp e) {
        this.e = e;
    }
    
    public Exp getE()
    {
        return e;
    }
}
