package backend;

import arbreasml.*;
import frontend.EnvironnementClosure;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

/**
 * Visiteur réalisant l'allocation de registre avec l'algorithme linear scan
 */
public class VisiteurAllocationRegistreLinearScan extends VisiteurAllocationRegistre {
    private Environnement environnement;
    private final Stack<Environnement> anciensEnvironnements;
    private HashSet<String> idStringVarPouvantEtreLiberees;
    private final Stack<HashSet<String>> anciensIdStringVarPouvantEtreLiberees;
    
    /**
     * Créé un visiteur réalisant l'allocation de registre avec l'algorithme linear scan
     * @param closures les closures du programme ASML
     */
    public VisiteurAllocationRegistreLinearScan(HashMap<String, EnvironnementClosure> closures)
    {
        super(closures);
        anciensEnvironnements = new Stack<>();
        anciensIdStringVarPouvantEtreLiberees = new Stack<>();
        idStringVarPouvantEtreLiberees = new HashSet<>();
        viderEnvironnement();
    }
    
    /**
     * Remplace l'environnement courant par un nouvel environnement vide
     */
    private void viderEnvironnement()
    {
        environnement = new Environnement();
    }
    
    /**
     * Sauvegarde l'environnement courant (une copie de cette environnement est créé en appelant sa méthode clone)
     */
    private void sauvegarderEnvironnement()
    {
        anciensEnvironnements.push((Environnement)environnement.clone());
    }
    
    /**
     * Restaure le dernier environnement sauvegardé en appelant la méthode sauvegarderEnvironnement
     */
    private void restaurerEnvironnement()
    {
        environnement = anciensEnvironnements.pop();
    }
    
    /**
     * Sauvegarde puis vide la liste des variables déclarées à l'intérieur de la branche du if dans lequel est immédiatement contenu le noeud courant
     * (ou à l'intérieur de la fonction du noeud courant si il n'est pas dans la branche d'un if). Le linear scan considére que les durée de vie des variables
     * sont des intervalles et ne libère pas dans les branches d'un if les variables déclarées ailleurs que dans cette branche (il considère que si une variable est
     * utilisé dans au moins une des branches d'un if, on ne peut pas libérer son emplacement mémoire dans toutes les branches ce if.
     */
    private void sauvegarderEtViderIdStringVarPouvantEtreLiberees()
    {
        anciensIdStringVarPouvantEtreLiberees.push((HashSet<String>)idStringVarPouvantEtreLiberees.clone());
        idStringVarPouvantEtreLiberees.clear();
    }
    
    /**
     * Restaure la dernière liste des variables déclarées à l'intérieur de la branche du if dans lequel est immédiatement contenu le noeud courant
     * (ou à l'intérieur de la fonction du noeud courant si il n'est pas dans la branche d'un if) sauvegardée avec la méthode sauvegarderEtViderIdStringVarInterieurIf
     */
    private void restaurerIdStringVarPouvantEtreLiberees()
    {
        idStringVarPouvantEtreLiberees = anciensIdStringVarPouvantEtreLiberees.pop();
    }
    
    /**
     * Alloue un emplacement non alloué à la variable d'identifiant idString
     * @param idString l'identifiant de la variable pour laquelle on veut allouer un emplacement
     */
    private void allouerEmplacemment(String idString)
    {
        getEmplacementsVar().put(idString, environnement.emplacementSuivant(idString)); 
    }
    
    /**
     * Visite le noeud e. Dans ce cas, visite e1 en sauvegardant l'environnement avant et en le restaurant après, sauvegarde à nouveau
     * l'environnement puis alloue à la variable déclarée un nouvel emplacement mémoire. Ensuite, pour chaque variable pour lesquels un emplacement est alloué, 
     * si la variable est dans idStringVarPouvantEtreLiberees (la liste des variables déclarées à l'intérieur de la branche du if dans lequel est immédiatement contenu le noeud courant
     * (ou à l'intérieur de la fonction du noeud courant si il n'est pas dans la branche d'un if)) et n'est pas utilisée dans e2, cet emplacement est libéré. Enfin e2 est visité
     * puis l'environnement est restauré
     * @param e le noeud à visiter 
     */
    @Override
    public void visit(LetAsml e) {   
        sauvegarderEnvironnement(); 
        e.getE1().accept(this);
        restaurerEnvironnement();          
        sauvegarderEnvironnement(); 
        String idString = e.getIdString();
        allouerEmplacemment(idString);
        idStringVarPouvantEtreLiberees.add(idString);
        AsmtAsml e2 = e.getE2();       
        VisiteurVariablesUtiliseesAsml visVarUtilisees = new VisiteurVariablesUtiliseesAsml();
        e2.accept(visVarUtilisees);
        List<String> idStringVariables = new ArrayList<>(environnement.getVariablesAllouees().keySet());
        for(String idStringVarAllouee : idStringVariables)
        {
            if(!visVarUtilisees.getVariablesUtilisees().contains(idStringVarAllouee) && idStringVarPouvantEtreLiberees.contains(idStringVarAllouee))
            {
                environnement.libererEmplacement(idStringVarAllouee);
            }
        }
        e2.accept(this);
        restaurerEnvironnement();
    }

    /**
     * Visite le noeud e. Dans ce cas, remplace l'environnement courant par un environnement vide et alloue pour le paramètre de numéro i 
     * (en numérotant les paramètres à partir de 0) le registre Ri si i est inférieur ou égal 3 et l'adresse FP+(n-i)*4 sinon (n est le nombre de paramètres). Les fonctions 
     * appelées avec call_closure ont un paramètre supplémentaire (implicite en ASML mais qu'il faut explicitement passer à la fonction en ARM) %self qu'il faut prendre en compte
     * @param e le noeud à visiter 
     */
    @Override
    public void visit(FunDefConcreteAsml e)
    {
        viderEnvironnement();
        super.visit(e);
    }
    
    /**
     * Visite le noeud e. Dans ce cas, appele la méthode visitIfWorker en lui passant e en paramètre
     * @param e le noeud à visiter 
     */
    @Override
    public void visit(IfEqIntAsml e) {
        visitIfWorker(e);
    }

    /**
     * Visite le noeud e. Dans ce cas, appele la méthode visitIfWorker en lui passant e en paramètre
     * @param e le noeud à visiter 
     */
    @Override
    public void visit(IfLEIntAsml e) {
        visitIfWorker(e);
    }
    
    /**
     * Visite le noeud e. Dans ce cas, appele la méthode visitIfWorker en lui passant e en paramètre
     * @param e le noeud à visiter 
     */
    @Override
    public void visit(IfGEIntAsml e) {
        visitIfWorker(e);
    }
    
    /**
     * Méthode factorisant le code des méthodes visit s'appliquant à un noeud héritant de IfAsml. Visite chaque branche du if en appelant sauvegarderEtViderIdStringVarPouvantEtreLiberees
     * avant et restaurerIdStringVarPouvantEtreLiberees après.
     * @param e le noeud à visiter 
     */
    private void visitIfWorker(IfAsml e)
    {
        sauvegarderEtViderIdStringVarPouvantEtreLiberees();
        e.getESiVrai().accept(this);
        restaurerIdStringVarPouvantEtreLiberees();
        sauvegarderEtViderIdStringVarPouvantEtreLiberees();
        e.getESiFaux().accept(this);
        restaurerIdStringVarPouvantEtreLiberees();
    }  
    
    /**
     * Visite le noeud e. Dans ce cas, appele la méthode visitIfWorker en lui passant e en paramètre
     * @param e le noeud à visiter 
     */
    @Override
    public void visit(IfEqFloatAsml e) {
        visitIfWorker(e);
    }

    /**
     * Visite le noeud e. Dans ce cas, appele la méthode visitIfWorker en lui passant e en paramètre
     * @param e le noeud à visiter 
     */
    @Override
    public void visit(IfLEFloatAsml e) {
        visitIfWorker(e);
    } 
}
