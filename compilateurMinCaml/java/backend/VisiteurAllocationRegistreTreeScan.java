package backend;

import arbreasml.*;
import frontend.EnvironnementClosure;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

/**
 * Visiteur réalisant l'allocation de registre avec l'algorithme tree scan
 */
public class VisiteurAllocationRegistreTreeScan extends VisiteurAllocationRegistre {
    private Environnement environnement;
    private final Stack<Environnement> anciensEnvironnements;
    private HashSet<String> idStringVarANePasLiberer;
    private final Stack<HashSet<String>> anciensIdStringVarANePasLiberer;
    
    /**
     * Créé un visiteur réalisant l'allocation de registre avec l'algorithme tree scan
     * @param closures les closures du programme ASML
     */
    public VisiteurAllocationRegistreTreeScan(HashMap<String, EnvironnementClosure> closures)
    {
        super(closures);
        anciensEnvironnements = new Stack<>();
        anciensIdStringVarANePasLiberer = new Stack<>();
        idStringVarANePasLiberer = new HashSet<>(); 
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
     * Sauvegarde les variables pouvant être libérées dans certaines branches (par exemple pour un programme de la forme de let _ = let y = 1 in let x = e1 in e2, 
     * il est possible que y puisse être libéré dans certaines branches de e1 (après sa dernière utilisation) si elle n'est pas utilisée dans e2)
     */
    private void sauvegarderIdStringVarANePasLiberer()
    {
        anciensIdStringVarANePasLiberer.push((HashSet<String>)idStringVarANePasLiberer.clone());
    }
    
    /**
     * Sauvegarde les variables pouvant être libérées dans certaines branches
     */
    private void restaurerIdStringVarANePasLiberer()
    {
        idStringVarANePasLiberer = anciensIdStringVarANePasLiberer.pop();
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
     * Visite le noeud e. Dans ce cas appelle sauvegarderIdStringVarANePasLiberer, libére les variables non utilisées
     * dans e1 et dans e2 non présentes dans idStringVarANePasLiberer, visite e1, appelle restaurerIdStringVarANePasLiberer, libére les variables non 
     * utilisées dans e2 non présentes dans idStringVarANePasLiberer, alloue un emplacement mémoire à la variable déclarée puis visite e2
     * @param e le noeud à visiter 
     */
    @Override
    public void visit(LetAsml e) {   
        AsmtAsml e1 = e.getE1();  
        AsmtAsml e2 = e.getE2();             
        String idString = e.getIdString();
        VisiteurVariablesUtiliseesAsml visVarUtiliseesE1 = new VisiteurVariablesUtiliseesAsml();
        e1.accept(visVarUtiliseesE1);
        HashSet<String> variablesUtiliseesE1 = visVarUtiliseesE1.getVariablesUtilisees();        
        VisiteurVariablesUtiliseesAsml visVarUtilisees = new VisiteurVariablesUtiliseesAsml();
        e2.accept(visVarUtilisees);
        HashSet<String> variablesUtiliseesE2 = visVarUtilisees.getVariablesUtilisees();
        
        sauvegarderIdStringVarANePasLiberer();         
        idStringVarANePasLiberer.addAll(variablesUtiliseesE2);
        List<String> idStringVariables = new ArrayList<>(environnement.getVariablesAllouees().keySet());
        for(String idStringVarAllouee : idStringVariables)
        {
            if(!variablesUtiliseesE1.contains(idStringVarAllouee) && !variablesUtiliseesE2.contains(idStringVarAllouee) && !idStringVarANePasLiberer.contains(idStringVarAllouee))
            {
                environnement.libererEmplacement(idStringVarAllouee);
            }
        }
        
        e.getE1().accept(this);
        restaurerIdStringVarANePasLiberer();
        
        List<String> idStringVariablesE2 = new ArrayList<>(environnement.getVariablesAllouees().keySet());
        for(String idStringVarAllouee : idStringVariablesE2)
        {
            if(!variablesUtiliseesE2.contains(idStringVarAllouee) && !idStringVarANePasLiberer.contains(idStringVarAllouee))
            {
                environnement.libererEmplacement(idStringVarAllouee);
            }
        }
        allouerEmplacemment(idString);  
        e2.accept(this);
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
        sauvegarderEnvironnement(); 
        e.getESiVrai().accept(this);
        restaurerEnvironnement();
        sauvegarderEnvironnement(); 
        e.getESiFaux().accept(this);
        restaurerEnvironnement();
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
