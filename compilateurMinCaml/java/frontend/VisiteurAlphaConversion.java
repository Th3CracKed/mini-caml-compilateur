package frontend;

import arbremincaml.*;
import java.util.HashMap;
import util.Constantes;
import visiteur.Visitor;

/**
 * Visiteur réalisant l'alpha-conversion d'un programme MinCaml.
 */
public class VisiteurAlphaConversion implements Visitor {

    private final HashMap<String, String> idsVariable;
    
    /**
    * Créé un visiteur réalisant l'alpha-conversion d'un programme MinCaml.
    */
    public VisiteurAlphaConversion()
    {
        idsVariable = new HashMap<>();
        for(String nomFonction : Constantes.FONCTION_EXTERNES_MINCAML)
        {
            idsVariable.put(nomFonction, nomFonction);
        }
        idsVariable.put(Constantes.NOM_FONCTION_MAIN_ASML, Constantes.NOM_FONCTION_MAIN_ASML);
        idsVariable.put(Constantes.NOM_FONCTION_MAIN_ARM, Constantes.NOM_FONCTION_MAIN_ARM);
    }
    
    /**
     * Créé un visiteur réalisant l'alpha-conversion d'un programme MinCaml en renommant les variables dont les identifiants sont les clé de la table de hachage
     * renommage par la valeur associée à cet identifiant dans la table
     * @param renommage la table de hachage associant les anciens noms de variables aux nouveaux
     */
    public VisiteurAlphaConversion(HashMap<String, String> renommage)
    {
        this();
        idsVariable.putAll(renommage);
    }
    
    /**
     * Visite le noeud e. Dans ce cas, visite e1, créé et associe un nouveau nom par lequel il faut remplacer celui de la variable déclarée, visite e2 puis restaure
     * l'ancien nom (qui peut être null si il n'avait pas encore été rencontré) par lequel remplacer celui de la variable déclarée
     * @param e le noeud à visiter
     */
    @Override
    public void visit(Let e) {   
      Id id = e.getId();
      String ancienIdString = id.getIdString();
      String nouvelIdString = Id.genIdString();
      String ancienRenommage = idsVariable.get(ancienIdString);
      id.setIdString(nouvelIdString);  
      e.getE1().accept(this);
      idsVariable.put(ancienIdString, nouvelIdString); 
      e.getE2().accept(this);
      idsVariable.put(ancienIdString, ancienRenommage);
    }
    
    /**
     * Visite le noeud e. Dans ce cas, réalise le même traitement que la méthode visit s'appliquant à des instances de Let mais en renommant plusieurs variables déclarées 
     * (les paramètres de la fonction dans e1 et le nom de la fonction dans e1 et dans e2) au lieu d'une seule
     * @param e le noeud à visiter
     */
    @Override
    public void visit(LetRec e) {   
      FunDef funDef = e.getFd();
      Id id = funDef.getId();
      String ancienIdString = id.getIdString();
      String nouvelIdString = Id.genLabelFonction();
      String ancienRenommage = idsVariable.get(ancienIdString);
      id.setIdString(nouvelIdString);
      idsVariable.put(ancienIdString, nouvelIdString);   
      HashMap<String, String> anciensIdsStringArgs = new HashMap<>();
      for(Id argument : funDef.getArgs())
      {
            String ancienIdStringArg = argument.getIdString();            
            String nouvelIdStringArg = Id.genIdString();
            String ancienRenommageArg = idsVariable.get(ancienIdStringArg);
            argument.setIdString(nouvelIdStringArg);
            idsVariable.put(ancienIdStringArg, nouvelIdStringArg);
            anciensIdsStringArgs.put(ancienIdStringArg, ancienRenommageArg);
      }
      funDef.getE().accept(this);         
      idsVariable.putAll(anciensIdsStringArgs);
      e.getE().accept(this);
      idsVariable.put(ancienIdString, ancienRenommage);
    }
    
    /**
     * Visite le noeud e. Dans ce cas, réalise le même traitement que la méthode visit s'appliquant à des instances de Let mais en renommant plusieurs variables déclarées au lieu
     * d'une seule
     * @param e le noeud à visiter
     */
    @Override
    public void visit(LetTuple e) {   
      HashMap<String, String> anciensIdsStringComposante = new HashMap<>();
      e.getE1().accept(this);
      for(Id idComposante : e.getIds())
      {
            String ancienIdStringComposante = idComposante.getIdString();
            String nouvelIdStringComposante = Id.genIdString();
            String ancienRenommageComposante = idsVariable.get(ancienIdStringComposante);
            idComposante.setIdString(nouvelIdStringComposante);
            idsVariable.put(ancienIdStringComposante, nouvelIdStringComposante);
            anciensIdsStringComposante.put(ancienIdStringComposante, ancienRenommageComposante);
      }
      e.getE2().accept(this);         
      idsVariable.putAll(anciensIdsStringComposante);
    }

    /**
     * Visite le noeud e. Change l'identifiant de la variable par le nouvel identifiant associé à l'ancien dans la table de hachage des renommage (si l'ancien est présent
     * dans cette table)
     * @param e le noeud à visiter
     */
    @Override
    public void visit(Var e){
        String nouvelId = idsVariable.get(e.getId().getIdString());
        if(nouvelId != null)
        {
            e.getId().setIdString(nouvelId);
        }
    }  
}


