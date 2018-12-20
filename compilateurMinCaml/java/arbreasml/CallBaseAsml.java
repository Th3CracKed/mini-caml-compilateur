package arbreasml;

import java.util.List;

public abstract class CallBaseAsml implements ExpAsml {
    private final List<VarAsml> arguments;
    public CallBaseAsml(List<VarAsml> arguments)
    {
        this.arguments = arguments;
    }  

    public List<VarAsml> getArguments() {
        return arguments;
    }
}
