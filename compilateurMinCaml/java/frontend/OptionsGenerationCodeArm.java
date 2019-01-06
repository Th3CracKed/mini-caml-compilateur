package frontend;

public class OptionsGenerationCodeArm {    
    private boolean utiliseSinOuCos;
    private boolean utiliseNewOuCreateArray;

    public OptionsGenerationCodeArm()
    {
        setUtiliseSinOuCos(false);
        setUtiliseNewOuCreateArray(false);
    }
    
    public boolean getUtiliseSinOuCos() {
        return utiliseSinOuCos;
    }

    public final void setUtiliseSinOuCos(boolean utiliseSinOuCos) {
        this.utiliseSinOuCos = utiliseSinOuCos;
    }

    public boolean getUtiliseNewOuCreateArray() {
        return utiliseNewOuCreateArray;
    }

    public final void setUtiliseNewOuCreateArray(boolean utiliseNewOuCreateArray) {
        this.utiliseNewOuCreateArray = utiliseNewOuCreateArray;
    }
}
