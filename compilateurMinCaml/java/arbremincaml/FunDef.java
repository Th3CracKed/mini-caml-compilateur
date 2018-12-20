package arbremincaml;


import java.util.List;

public class FunDef {
    private final Id id;
    private final Type type;
    private final List<Id> args;
    private final Exp e;

    public FunDef(Id id, Type t, List<Id> args, Exp e) {
        this.id = id;
        this.type = t;
        this.args = args;
        this.e = e;
    }

    public Id getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public List<Id> getArgs() {
        return args;
    }

    public Exp getE() {
        return e;
    }
 
}
 
