package arbreasml;

import java.util.ArrayList;
import java.util.List;
import util.Constantes;
import visiteur.ObjVisiteurAsml;
import visiteur.VisiteurAsml;

/**
 * Noeud ASML correspondant à une déclaration de fonction
 */
public class FunDefConcreteAsml extends FunDefAsml {
    
    private final AsmtAsml asmt;    
    private final List<VarAsml> arguments;
    
    /**
     * Créé un noeud ASML FunDefConcrete avec pour label label, pour corps asmt et pour argument arguments
     * @param label le label de la fonction
     * @param asmt le corps de la fonction
     * @param arguments les arguments de la fonction
     */
    public FunDefConcreteAsml(String label, AsmtAsml asmt, List<VarAsml> arguments)
    {
        super(label);
        this.asmt = asmt;
        this.arguments = arguments;
    }

    /**
     * Méthode créant une instance de FunDefConcreteAsml correspondant à la fonction point d'entrée (de label _) d'un programme ASML
     * @param asmt le corps de la fonction principale
     * @return le noeud correspondant à la fonction principale
     */
    public static FunDefConcreteAsml creerMainFunDef(AsmtAsml asmt)
    {
        return new FunDefConcreteAsml(Constantes.NOM_FONCTION_MAIN_ASML, asmt, new ArrayList<>());
    }
    
    /**
     * Renvoie un booléen vrai si la fonction est la fonction point d'entrée du programme ASML (c'est-à-dire si son label est _) et faux sinon
     * @return un booléen vrai si la fonction est la fonction point d'entrée du programme ASML (c'est-à-dire si son label est _) et faux sinon
     */
    public boolean estMainFunDef()
    {
        return this.getLabel().equals(Constantes.NOM_FONCTION_MAIN_ASML);
    }
    
    /**
     * Renvoie le corps de la fonction
     * @return le corps de la fonction
     */
    public AsmtAsml getAsmt() {
        return asmt;
    }
        
    /**
     * Renvoie la liste des arguments de la fonction
     * @return la liste des arguments de la fonction
     */
    public List<VarAsml> getArguments() {
        return arguments;
    }
    

    @Override
    public void accept(VisiteurAsml v) {
        v.visit(this);
    }

    @Override
    public <E> E accept(ObjVisiteurAsml<E> v) {
        return v.visit(this);
    }

        
}
