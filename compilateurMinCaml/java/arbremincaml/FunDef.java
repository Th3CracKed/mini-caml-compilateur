package arbremincaml;

import java.util.List;

/**
 * Définition de fonction en MinCaml (partie avant le mot clé in de l'instruction let rec)
 */
public class FunDef {
    private final Id id;
    private final Type type;
    private final List<Id> args;
    private final Exp e;

    /**
     * Créé une définition de fonction d'id id et de type t avec les arguments dans args et le corps e
     * @param id l'id de la fonction
     * @param t le type de la fonction
     * @param args les arguments de la fonction
     * @param e le corps de la fonction
     */
    public FunDef(Id id, Type t, List<Id> args, Exp e) {
        this.id = id;
        this.type = t;
        this.args = args;
        this.e = e;
    }

    /**
     * Renvoie l'id de la fonction
     * @return l'id de la fonction
     */
    public Id getId() {
        return id;
    }

    /**
     * Renvoie le type de la fonction
     * @return le type de la fonction
     */
    public Type getType() {
        return type;
    }

    /**
     * Renvoie les arguments de la fonction
     * @return les arguments de la fonction
     */
    public List<Id> getArgs() {
        return args;
    }

    /**
     * Renvoie le corps de la fonction
     * @return le corps de la fonction
     */
    public Exp getE() {
        return e;
    }
 
}
 
