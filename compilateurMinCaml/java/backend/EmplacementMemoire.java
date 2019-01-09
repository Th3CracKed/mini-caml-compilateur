package backend;

public abstract class EmplacementMemoire {
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        int valeurThis = (this instanceof Registre)?((Registre)this).getNumeroRegistre():((AdresseMemoire)this).getDecalage();
        int valeurAutre = (obj instanceof Registre)?((Registre)obj).getNumeroRegistre():((AdresseMemoire)obj).getDecalage();
        return valeurThis == valeurAutre;
    }

    @Override
    public int hashCode() {
        if(this instanceof Registre)
        {
            return 1000*Integer.hashCode(((Registre)this).getNumeroRegistre());
        }
        else
        {
            return Integer.hashCode(((AdresseMemoire)this).getDecalage());
        }
    }
}
