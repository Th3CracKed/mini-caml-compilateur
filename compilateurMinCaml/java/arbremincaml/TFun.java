package arbremincaml;

/**
 * Classe représentant le type d'une fonction.
 */
public class TFun extends Type {

    private Type t1;
    private Type t2;

    /**
     * Créé une instance de TFun représentant le type d'une fonction prenant en paramètre un élément de type t1 et renvoyant un élément de type t2
     * @param t1 le type du paramètre de la fonction
     * @param t2 le type du résultat de la fonction
     */
    public TFun(Type t1, Type t2) {
        setT1(t1);
        setT2(t2);
    }

    /**
     * Renvoie le type du paramètre de la fonction
     * @return le type du paramètre de la fonction
     */
    public Type getT1() {
        return t1;
    }

    /**
     * Définit t1 comme le nouveau type du paramètre de la fonction
     * @param t1 le nouveau type du paramètre de la fonction
     */
    public final void setT1(Type t1) {
        this.t1 = t1;
    }

    /**
     * Renvoie le type du résultat de la fonction
     * @return le type du résultat de la fonction
     */
    public Type getT2() {
        return t2;
    }

    /**
     * Définit t2 comme le nouveau type du résultat de la fonction
     * @param t2 le nouveau type du résultat de la fonction
     */
    public final void setT2(Type t2) {
        this.t2 = t2;
    }

    /**
     * Renvoie la représentation du type sous forme de chaîne
     * @return la représentation du type sous forme de chaîne
     */
    @Override
    public String toString() {
        return t1 + " -> " + t2;
    }

}
