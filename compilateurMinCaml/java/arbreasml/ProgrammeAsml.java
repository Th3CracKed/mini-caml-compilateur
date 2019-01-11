package arbreasml;

import java.util.List;
import visiteur.ObjVisiteurAsml;
import visiteur.VisiteurAsml;

/**
 * NoeudAsml correspondant à un programme ASML complet
 */
public class ProgrammeAsml implements NoeudAsml {
    private final FunDefConcreteAsml mainFunDef;
    private final List<FunDefAsml> funDefs;
    
    /**
     * Créé un programme ASML avec la fonction point d'entrée mainFunDef et les autres fonctions et les nombres flottants dans la liste funDefs
     * @param mainFunDef la fonction point d'entrée du programme
     * @param funDefs la liste des autres fonctions et des nombres flottants du programme
     */
    public ProgrammeAsml(FunDefConcreteAsml mainFunDef, List<FunDefAsml> funDefs)
    {
        this.mainFunDef = mainFunDef;
        this.funDefs = funDefs;
    }

    /**
     * Renvoie la fonction point d'entrée du programme
     * @return la fonction point d'entrée du programme
     */
    public FunDefConcreteAsml getMainFunDef() {
        return mainFunDef;
    }

    /**
     * Renvoie la liste des autres fonctions et des nombres flottants du programme
     * @return la liste des autres fonctions et des nombres flottants du programme
     */
    public List<FunDefAsml> getFunDefs() {
        return funDefs;
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
