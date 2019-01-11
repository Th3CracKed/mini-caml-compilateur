package typage;

import arbremincaml.Type;

/**
 * Classe représentant une équation de type (un type est égal à un type)
 */
public class EquationType {

    private Type t1;
    private Type t2;
    
    /**
     * Créé une instance de EquationType représentant l'équation t1 = t2
     * @param t1 le premier type de l'équation
     * @param t2 le second type de l'équation
     */
    public EquationType(Type t1, Type t2) {
        this.setT1(t1);
        this.setT2(t2);
    }

    /**
     * Renvoie le premier type de l'équation
     * @return le premier type de l'équation
     */
    public Type getT1() {
        return t1;
    }

    /**
     * Définit t1 comme le premier type de l'équation
     * @param t1 la nouvelle valeur du premier type de l'équation
     */
    public final void setT1(Type t1) {
        this.t1 = t1;
    }
    
    /**
     * Renvoie le second type de l'équation
     * @return le second type de l'équation
     */
    public Type getT2() {
        return t2;
    }
    
    /**
     * Définit t2 comme le premier type de l'équation
     * @param t2 la nouvelle valeur du premier type de l'équation
     */
    public final void setT2(Type t2) {
        this.t2 = t2;
    }

    /**
     * Echange le premier et le second type de l'équation
     */
    public void echange() {
        Type temp = t1;
        setT1(t2);
        setT2(temp);
    }
    
    /**
     * Renvoie la représentation sous forme de chaîne de l'équation
     * @return la représentation sous forme de chaîne de l'équation
     */
    @Override
    public String toString()
    {
        return t1+" = "+t2;
    }
}
