package backend;

import arbreasml.*;
import frontend.EnvironnementClosure;
import java.util.HashMap;
import java.util.Stack;
import util.Constantes;

/**
 * Visiteur réalisant l'allocation de registres en plaçant toutes les variables locales sur la pile
 */
public class VisiteurAllocationRegistreSpill extends VisiteurAllocationRegistre {

    private int decalage;
    private final Stack<EmplacementMemoire> anciensDecalages;
    
    /**
     * Créé un visiteur réalisant l'allocation de registres en plaçant toutes les variables locales sur la pile
     * @param closures les closures du programme
     */
    public VisiteurAllocationRegistreSpill(HashMap<String, EnvironnementClosure> closures)
    {
        super(closures);
        remettreDecalageAZero();
        anciensDecalages = new Stack<>();
    }
    
    /**
     * Assigne 0 au décalage par rapport à FP de la prochaine adresse qui sera allouée à une variable locale
     */
    private void remettreDecalageAZero()
    {
        decalage = 0;
    }
    
    /**
     * sauvegarde le décalage par rapport à FP de la prochaine adresse qui sera allouée à une variable locale
     */
    private void sauvegarderDecalage()
    {
        anciensDecalages.push(new AdresseMemoire(decalage));
    }
    
    /**
     * Restaure la valeur du décalage par rapport à FP de la prochaine adresse qui sera allouée à une variable locale sauvegardée par le dernier appel à sauvegarderDecalage
     */
    private void restaurerDecalage()
    {
        decalage = ((AdresseMemoire)anciensDecalages.pop()).getDecalage();
    }
    
    /**
     * Renvoie la valeur du décalage par rapport à FP de la prochaine adresse qui sera allouée à une variable locale
     * @return la valeur du décalage par rapport à FP de la prochaine adresse qui sera allouée à une variable locale
     */
    private int decalageSuivant()
    {
        int dernierDecalage = decalage;
        decalage -= Constantes.TAILLE_MOT_MEMOIRE;
        return dernierDecalage;
    }

    /**
     * Visite le noeud e. Dans ce cas, visite e1 en sauvegardant le décalage par rapport à FP de la prochaine adresse qui sera allouée à une variable locale avant en le restaurant après, le sauvegarde à nouveau
     * alloue une adresse mémoire à la variable déclarée, visite e2 puis restaure le décalage
     * @param e le noeud à visiter 
     */
    @Override
    public void visit(LetAsml e) {
        sauvegarderDecalage(); 
        e.getE1().accept(this);
        restaurerDecalage();        
        sauvegarderDecalage(); 
        getEmplacementsVar().put(e.getIdString(), new AdresseMemoire(decalageSuivant())); 
        e.getE2().accept(this);
        restaurerDecalage();
    }

   /**
     * Visite le noeud e. Dans ce cas, appelle remettreDecalageAZero puis alloue pour le paramètre de numéro i 
     * (en numérotant les paramètres à partir de 0) le registre Ri si i est inférieur ou égal 3 et l'adresse FP+(n-i)*4 sinon (n est le nombre de paramètres). Les fonctions 
     * appelées avec call_closure ont un paramètre supplémentaire (implicite en ASML mais qu'il faut explicitement passer à la fonction en ARM) %self qu'il faut prendre en compte
     * @param e le noeud à visiter 
     */
    @Override
    public void visit(FunDefConcreteAsml e)
    {
        remettreDecalageAZero();
        super.visit(e);
    }
    
    /**
     * Visite le noeud e. Dans ce cas, appelle visitIfWorker en lui passant e comme paramètre
     * @param e le noeud à visiter 
     */
    @Override
    public void visit(IfEqIntAsml e) {
        visitIfWorker(e);
    }

    /**
     * Visite le noeud e. Dans ce cas, appelle visitIfWorker en lui passant e comme paramètre
     * @param e le noeud à visiter 
     */
    @Override
    public void visit(IfLEIntAsml e) {
        visitIfWorker(e);
    }
    
    /**
     * Visite le noeud e. Dans ce cas, appelle visitIfWorker en lui passant e comme paramètre
     * @param e le noeud à visiter 
     */
    @Override
    public void visit(IfGEIntAsml e) {
        visitIfWorker(e);
    }
    
    /**
     * Méthode factorisant les méthodes visit s'appliquant à des noeuds héritant de IfAsml. Cette méthode a pour effet de visiter les 2 branches du if
     * @param e le noeud à visiter 
     */
    private void visitIfWorker(IfAsml e)
    {
        e.getESiVrai().accept(this);
        e.getESiFaux().accept(this);
    } 
    
    /**
     * Visite le noeud e. Dans ce cas, appelle visitIfWorker en lui passant e comme paramètre
     * @param e le noeud à visiter 
     */
    @Override
    public void visit(IfEqFloatAsml e) {
        visitIfWorker(e);
    }

    /**
     * Visite le noeud e. Dans ce cas, appelle visitIfWorker en lui passant e comme paramètre
     * @param e le noeud à visiter 
     */
    @Override
    public void visit(IfLEFloatAsml e) {
        visitIfWorker(e);
    }
}
