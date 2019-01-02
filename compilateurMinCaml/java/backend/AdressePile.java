package backend;

public class AdressePile extends EmplacementMemoire {
    
    private final int decalage;
    
    public AdressePile(int decalageFP)
    {
        this.decalage = decalageFP;
    }
    
    @Override
    public String toString()
    {
        return "[FP"+((this.getDecalage() == 0)?"":", #"+this.getDecalage())+"]";
    }

    public int getDecalage() {
        return decalage;
    }
}
