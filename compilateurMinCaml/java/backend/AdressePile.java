package backend;

public class AdressePile extends EmplacementMemoire {
    
    private final int decalageFP;
    
    public AdressePile(int decalageFP)
    {
        this.decalageFP = decalageFP;
    }
    
    @Override
    public String toString()
    {
        return "[FP"+((this.getDecalage() == 0)?"":", #"+this.getDecalage())+"]";
    }

    public int getDecalage() {
        return decalageFP;
    }
}
