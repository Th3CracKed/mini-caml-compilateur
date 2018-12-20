package backend;

public class Registre extends EmplacementMemoire {
    private final int numeroRegistre;
    public Registre(int numeroRegistre)
    {
        this.numeroRegistre = numeroRegistre;
    }
    
    @Override
    public String toString()
    {
        return "R"+this.getNumeroRegistre();
    }

    public int getNumeroRegistre() {
        return numeroRegistre;
    }
}
