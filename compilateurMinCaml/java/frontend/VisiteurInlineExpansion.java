package frontend;

import arbremincaml.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import util.Constantes;
import visiteur.*;

/**
 * Visiteur réalisant l'étape de l'inline expansion (remplaçant les appels aux fonction dont le nombre de noeuds est inférieur à un seuil par le corps de la fonction)
 */
public class VisiteurInlineExpansion extends ObjVisitorExp {
    
    private static final int TAILLE_MAX_INLINE_EXPANSION = 15;    
    private final HashMap<String, FunDef> fonctionAEtendre;
    
    /**
     * Créé un visiteur réalisant l'étape de l'inline expansion
     */
    public VisiteurInlineExpansion()
    {
        fonctionAEtendre = new HashMap<>();
    }
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, ajoute la fonction déclarée dans e à la liste des fonctions
     * pour lesquelles réaliser l'inline expansion si son nombre de noeuds est inférieur à TAILLE_MAX_INLINE_EXPANSION et renvoie un noeud du même type que e avec pour
     * fils le résultat de l'application du visiteur courant aux fils de e
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Exp visit(LetRec e)
    {
        FunDef funDef = e.getFd();
        int tailleCorpsFonction = funDef.getE().accept(new VisiteurNbNoeudsArbre());
        if(tailleCorpsFonction <= TAILLE_MAX_INLINE_EXPANSION)
        {
            fonctionAEtendre.put(funDef.getId().getIdString(), funDef);
        }
        return super.visit(e);
    }
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, si la fonction appelée est dans la liste fonctionAEtendre des fonctions
     * pour lesquelles réaliser l'inline expansion, renvoie le corps de la fonction en remplaçant les paramètres par les valeurs passée à la fonction dans eSinon, renvoie
     * le résultat de l'application du visiteur courant aux fils de e
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Exp visit(App e)
    {
        FunDef funDef = null;
        Exp fun = e.getE();
        if(fun instanceof Var)
        {
            funDef = fonctionAEtendre.get(((Var)fun).getId().getIdString());
        }
        if(funDef != null && !Constantes.FONCTION_EXTERNES_MINCAML.contains(((Var)e.getE()).getId().getIdString()))
        {            
            HashMap<String, String> renommage = new HashMap<>();
            List<Id> args = funDef.getArgs();
            for(int i = 0 ; i < args.size() ; i++)
            {
                renommage.put(args.get(i).getIdString(), ((Var)e.getEs().get(i)).getId().getIdString());
            }
            Exp copieCorpsFonction = funDef.getE().accept(new VisiteurCopieArbre());
            copieCorpsFonction.accept(new VisiteurAlphaConversion(renommage));
            return copieCorpsFonction;
        }
        else
        {
            return super.visit(e);
        }
    }   
    
    /**
     * Créé un visiteur permettant de calculer le nombre de noeuds d'un arbre MinCaml. Les méthodes visit s'appliquant à un noeud terminal renvoie 1 et celles s'appliquant à un noeud
     * non terminal renvoie 1 plus le nombre des noeuds de leurs fils.
     */
    private class VisiteurNbNoeudsArbre implements ObjVisitor<Integer>
    {

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Integer visit(Unit e) {
            return 1;
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Integer visit(Bool e) {
            return 1;
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Integer visit(Int e) {
            return 1;
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Integer visit(FloatMinCaml e) {
            return 1;
        }

        /**
         * Méthode factorisant le code des noeuds héritant de OperateurUnaire
         * @param e le noeud à visiter
         * @return le résultat de l'application du visiteur courant (this) au noeud e
         */
        private Integer visitOpUnaireWorker(OperateurUnaire e)
        {
            return 1 + e.getE().accept(this);
        }
        
        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Integer visit(Not e) {
            return visitOpUnaireWorker(e);
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Integer visit(Neg e) {
            return visitOpUnaireWorker(e);
        }
        
        /**
         * Méthode factorisant le code des noeuds héritant de OperateurBinaire
         * @param e le noeud à visiter
         * @return le résultat de l'application du visiteur courant (this) au noeud e
         */
        private Integer visitOpBinaireWorker(OperateurBinaire e)
        {
            return 1 + e.getE1().accept(this) + e.getE2().accept(this);
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Integer visit(Add e) {
            return visitOpBinaireWorker(e);
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Integer visit(Sub e) {
            return visitOpBinaireWorker(e);
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Integer visit(FNeg e) {
            return visitOpUnaireWorker(e);
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Integer visit(FAdd e) {
            return visitOpBinaireWorker(e);
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Integer visit(FSub e) {
            return visitOpBinaireWorker(e);
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Integer visit(FMul e) {
            return visitOpBinaireWorker(e);
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Integer visit(FDiv e) {
            return visitOpBinaireWorker(e);
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Integer visit(Eq e) {
            return visitOpBinaireWorker(e);
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Integer visit(LE e) {
            return visitOpBinaireWorker(e);
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Integer visit(If e) {
            return 1 + e.getE1().accept(this) + e.getE2().accept(this) + e.getE3().accept(this);
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Integer visit(Let e) {
            return 1 + e.getE1().accept(this) + e.getE2().accept(this);
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Integer visit(Var e) {
            return 1;
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Integer visit(LetRec e) {
            return 1 + e.getE().accept(this) + e.getFd().getE().accept(this);
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Integer visit(App e) {
            return 1 + e.getEs().stream().mapToInt(x->x.accept(this)).sum();
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Integer visit(Tuple e) {
            return 1 + e.getEs().stream().mapToInt(x->x.accept(this)).sum();
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Integer visit(LetTuple e) {
            return 1 + e.getE1().accept(this) + e.getE2().accept(this);
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Integer visit(Array e) {
            return 1 + e.getE1().accept(this) + e.getE2().accept(this);
        }

        /**
         * Méthode factorisant le code des noeuds héritant de AccesTableau
         * @param e le noeud à visiter
         * @return le résultat de l'application du visiteur courant (this) au noeud e
         */
        private Integer visitAccesTableauWorker(AccesTableau e)
        {
            return 1 + e.getE1().accept(this) + e.getE2().accept(this);
        }
        
        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Integer visit(Get e) {
            return visitAccesTableauWorker(e);
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Integer visit(Put e) {
            return visitAccesTableauWorker(e) + e.getE3().accept(this);
        }
        
    }
    
    /**
     * Visiteur réalisant une copie d'un arbre MinCaml
     */
    private class VisiteurCopieArbre extends ObjVisitorExp
    {
        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Exp visit(Var e)
        {
            return new Var(new Id(e.getId().getIdString()));
        }
        
        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Exp visit(Let e) {
          Exp e1 = e.getE1().accept(this);
          Exp e2 = e.getE2().accept(this);
          return new Let(new Id(e.getId().getIdString()), Type.gen(), e1 , e2);
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Exp visit(LetRec e){
           Exp exp = e.getE().accept(this);
           FunDef funDef = e.getFd();
           List<Id> nouveauxArgs = new ArrayList<>();
           for(Id idArg : funDef.getArgs())
           {
               nouveauxArgs.add(new Id(idArg.getIdString()));
           }
           FunDef nouvelleFunDef = new FunDef(new Id(funDef.getId().getIdString()), funDef.getType(), nouveauxArgs, funDef.getE().accept(this));
          return new LetRec(nouvelleFunDef, exp);
        }
        
        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Exp visit(LetTuple e){
           Exp e1 = e.getE1().accept(this);
           Exp e2 = e.getE2().accept(this);
           List<Id> nouveauxIds = new ArrayList<>();
           for(Id id : e.getIds())
           {
               nouveauxIds.add(new Id(id.getIdString()));
           }
          return new LetTuple(nouveauxIds, e.getTs(), e1, e2);
        }
    }
}
