package frontend;

import arbremincaml.*;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import util.MyCompilationException;
import visiteur.DonneesOperateurBinaire;
import visiteur.ObjVisitor;
import visiteur.ObjVisitorExp;

/**
 * Visiteur réalisant l'étape du constant folding du compilateur
 */
public class VisiteurConstantFolding extends ObjVisitorExp {

    private final HashMap<String, Valeur> varConstante;

    /**
     * Créé un visiteur réalisant l'étape du constant folding du compilateur
     */
    public VisiteurConstantFolding() {
        varConstante = new HashMap<>();
    }
    
    /**
     * Renvoie vrai si l'expression valeur est une valeur constante pouvant être propagée et faux sinon
     * @param valeur l'expression 
     * @return vrai si l'expression valeur est une valeur constante pouvant être propagée et faux sinon
     */
    private static boolean estValeurConstante(Exp valeur)
    {
        return (valeur instanceof Valeur && !(valeur instanceof Tuple));
    }
    
    /**
     * Détermine si l'expression e peut être remplacé par sa valeur : si c'est le cas renvoie cette valeur, sinon renvoie noeudSiValeurInvalide
     * @param e l'expression
     * @param noeudSiValeurInvalide expression renvoyée si e peut être remplacé par sa valeur
     * @return la valeur de e si e peut être remplacé par sa valeur et noeudSiValeurInvalide sinon
     */
    private Exp creerNoeudResultat(Exp e, Exp noeudSiValeurInvalide) {
        Valeur valeurMinCaml = noeudSiValeurInvalide.accept(new VisiteurCalculValeur());
        if (estValeurConstante(valeurMinCaml)) {
            Object val = valeurMinCaml.getValeur();
            VisiteurEffetDeBord vEffetDeBord = new VisiteurEffetDeBord();
            e.accept(vEffetDeBord);
            if(!vEffetDeBord.getAUnEffetDeBord() && (!(val instanceof Integer)))
            {
                return valeurMinCaml;
            }
        }
        return noeudSiValeurInvalide;
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie la valeur du noeud si le noeud peut être emplacé par sa
     * valeur. Sinon, renvoie un nouveau noeud du même type que e avec pour fils le résultat de l'application du visiteur à ses fils.
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Exp visit(LE e) {
        return creerNoeudResultat(e, super.visit(e));
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie la valeur du noeud si le noeud peut être emplacé par sa
     * valeur. Sinon, renvoie un nouveau noeud du même type que e avec pour fils le résultat de l'application du visiteur à ses fils.
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Exp visit(Eq e) {
        return creerNoeudResultat(e, super.visit(e));
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie la valeur du noeud si le noeud peut être emplacé par sa
     * valeur. Sinon, renvoie un nouveau noeud du même type que e avec pour fils le résultat de l'application du visiteur à ses fils.
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Exp visit(Add e) {
        return creerNoeudResultat(e, super.visit(e));
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie la valeur du noeud si le noeud peut être emplacé par sa
     * valeur. Sinon, renvoie un nouveau noeud du même type que e avec pour fils e1 et le résultat de l'application du visiteur à e2 (il ne faut pas remplacer le premier
     * opérande d'un sub par une valeur, sinon cela posera problème pour la conversion en ASML car cette opérande doit être une variable et on ne peut pas l'échanger avec le
     * premier comme pour le Add car la soustraction n'est pas commutative.
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Exp visit(Sub e) {
        Exp valeur = creerNoeudResultat(e, super.visit(e));
        if (valeur instanceof Valeur) {
            return valeur;
        } else {
            return new Sub(e.getE1(), ((Sub)valeur).getE2());
        }
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie la valeur du noeud si le noeud peut être emplacé par sa
     * valeur. Sinon, renvoie e (il ne faut pas pas remplacer son opérande par une valeur car il doit être une variable pour que la conversion en ASMl fonctionne).
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Exp visit(Not e) {
        return creerNoeudResultat(e, e);
    }

   /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie la valeur du noeud si le noeud peut être emplacé par sa
     * valeur. Sinon, renvoie e (il ne faut pas pas remplacer son opérande par une valeur car il doit être une variable pour que la conversion en ASMl fonctionne).
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Exp visit(Neg e) {
        return creerNoeudResultat(e, e);
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie la valeur du noeud si le noeud peut être emplacé par sa
     * valeur. Sinon, renvoie e (il ne faut pas pas remplacer son opérande par une valeur car il doit être une variable pour que la conversion en ASMl fonctionne).
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Exp visit(FNeg e){
      return creerNoeudResultat(e, e);
    }

   /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie la valeur du noeud si le noeud peut être emplacé par sa
     * valeur. Sinon, renvoie e (il ne faut pas pas remplacer ses opérandes par leur valeur car il doivent être des variables pour que la conversion en ASMl fonctionne).
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Exp visit(FAdd e){
       return creerNoeudResultat(e, e);
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie la valeur du noeud si le noeud peut être emplacé par sa
     * valeur. Sinon, renvoie e (il ne faut pas pas remplacer ses opérandes par leur valeur car il doivent être des variables pour que la conversion en ASMl fonctionne).
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Exp visit(FSub e){
        return creerNoeudResultat(e, e);
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie la valeur du noeud si le noeud peut être emplacé par sa
     * valeur. Sinon, renvoie e (il ne faut pas pas remplacer ses opérandes par leur valeur car il doivent être des variables pour que la conversion en ASMl fonctionne).
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Exp visit(FMul e) {
       return creerNoeudResultat(e, e);
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie la valeur du noeud si le noeud peut être emplacé par sa
     * valeur. Sinon, renvoie e (il ne faut pas pas remplacer ses opérandes par leur valeur car il doivent être des variables pour que la conversion en ASMl fonctionne).
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Exp visit(FDiv e){
        return creerNoeudResultat(e, e);
    }
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie la valeur du noeud si le noeud peut être emplacé par sa
     * valeur. Sinon, renvoie e.
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Exp visit(Var e) {
        return creerNoeudResultat(e, super.visit(e));
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie un nouveau noeud du même type que e avec pour fils 
     * le résultat de l'application du visiteur à ses fils et si e1 est une valeur pouvant être propagée, associe l'identifiant de la variable déclarée à la valeur
     * de e1
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Exp visit(Let e) {
        Let resultat = (Let)super.visit(e);
        Exp nouvelE1 = resultat.getE1();
        if(nouvelE1 instanceof Valeur)
        {
            varConstante.put(e.getId().getIdString(), (Valeur)nouvelE1);
        }
        return resultat; 
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie e
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Exp visit(App e) {
        return e;
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie la valeur du noeud if si il peut être remplacé
     * par sa valeur et si e1 n'a pas d'effet de bord. Sinon, renvoie un nouveau noeud du même type que e avec pour fils e1 et
     * le résultat de l'application du visiteur à e2 et e3
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    
    @Override
    public Exp visit(If e) {
        Exp e1 = e.getE1();
        Exp e2Accepte = e.getE2().accept(this);
        Exp e3Accepte = e.getE3().accept(this);
        If nouveauIf = new If(e1.accept(this), e2Accepte, e3Accepte);
        Valeur valeur = nouveauIf.accept(new VisiteurCalculValeur());
        VisiteurEffetDeBord v = new VisiteurEffetDeBord();
        e1.accept(v);
        if (valeur == null || v.getAUnEffetDeBord()) {
            return new If(e1, e2Accepte, e3Accepte);
        } else {
            return valeur;
        }
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie e
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Exp visit(Array e) {
        return e;
    }

    /**
     * Renvoie vrai si valeur est une valeur valide pour un indice de tableau et lève une exception sinon
     * @param valeur la valeur
     * @throws MyCompilationException si la valeur est constante et strictement négative
     */
    private void verifierValeurIndice(Valeur valeur)
    {
        if(valeur instanceof Int && ((Int)valeur).getValeur() < 0)
        {
            throw new MyCompilationException("Les indices de tableaux doivent être positifs ou nuls");
        }
    }
    
    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie un noeud Get
     * avec les même fils que e sauf pour e2, qui est remplacé par sa valeur si elle est constante
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     * @throws MyCompilationException si la valeur est constante et strictement négative
     */
    @Override
    public Exp visit(Get e) {
        Exp e2 = e.getE2();
        Valeur valeurE2 = e2.accept(new VisiteurCalculValeur());
        verifierValeurIndice(valeurE2);
        return new Get(e.getE1(), (valeurE2 == null) ? e2 : valeurE2);
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie un noeud Put
     * avec les même fils que e sauf pour e2, qui est remplacé par sa valeur si elle est constante
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     * @throws MyCompilationException si la valeur est constante et strictement négative
     */
    @Override
    public Exp visit(Put e) {
        Exp e2 = e.getE2();
        Valeur valeurE2 = e2.accept(new VisiteurCalculValeur());
        verifierValeurIndice(valeurE2);
        return new Put(e.getE1(), (valeurE2 == null) ? e2 : valeurE2, e.getE3());
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, renvoie un noeud Tuple
     * avec pour composantes le résultat de l'application du visiteur sur les composantes de e
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Exp visit(Tuple e) {
        Exp eAccepte = super.visit(e);
        return creerNoeudResultat(e, eAccepte);
    }

    /**
     * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. Dans ce cas, effectue le même traitement que pour un noeud
     * Let avec plusieurs variables au lieu d'une seule
     * @param e le noeud à visiter
     * @return le résultat de l'application du visiteur courant (this) au noeud e
     */
    @Override
    public Exp visit(LetTuple e) {
        Tuple valeurAffecte = (Tuple)e.getE1().accept(new VisiteurCalculValeur());             
        if (valeurAffecte != null) {
            List<Id> ids = e.getIds();
            List<Exp> esValeur = ((Tuple)valeurAffecte).getEs();  
            for (int i = 0; i < ids.size() ; i++) {
                if(esValeur.get(i) != null)
                {
                    varConstante.put(ids.get(i).getIdString(), (Valeur) valeurAffecte.getEs().get(i));
                }
            }
        }
        return super.visit(e);
    }

    /**
     * Visiteur renvoyant la valeur d'un noeud ou null si elle n'est pas constante (pour un tuple, le tuple est renvoyé si au moins une de ses composantes est constantes)
     */
    private class VisiteurCalculValeur implements ObjVisitor<Valeur> {

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Valeur visit(Not e) {
            Valeur valeur = e.getE().accept(this);
            if (valeur == null) {
                return null;
            }
            Boolean bool = (Boolean) valeur.getValeur();
            return new Bool(!bool);
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Valeur visit(Neg e) {
            Valeur valeur = e.getE().accept(this);
            if (valeur == null) {
                return null;
            }
            Integer valeurInteger = (Integer) valeur.getValeur();
            valeurInteger *= -1;
            return new Int(valeurInteger);
        }
        
        /**
         * Méthode factorisant les méthodes visit s'appliquant à un noeud héritant de OperateurBinaire
         * @param <T> le type des arguments de operateur
         * @param <U> le type du résultat de operateur
         * @param e le noeud à visiter
         * @param operateur l'opérateur binaire permettant de calculer la valeur de l'operateur à partir de la valeur de ses opérandes (fonction prennant deux paramètre de même type et renvoyant une valeur)
         * @param constructeur une référence sur le constructeur du noeud de la valeur constante
         * @return le résultat de l'application du visiteur courant (this) au noeud e
         */
        private <T, U> Valeur visitOpBinaireWorker(OperateurBinaire e, BiFunction<U, U, T> operateur, Function<T,Valeur> constructeur) {
            DonneesOperateurBinaire<Valeur> donneesOpBinaire = new DonneesOperateurBinaire<>(e, this);
            Valeur<U> valeur1 = (Valeur<U>)donneesOpBinaire.getE1();
            Valeur<U> valeur2 = (Valeur<U>)donneesOpBinaire.getE2();
            if (valeur1 == null || valeur2 == null) {
                return null;
            }
            return constructeur.apply(operateur.apply(valeur1.getValeur(), valeur2.getValeur()));
        }
        
        /**
         * Méthode factorisant les méthodes visit s'appliquant à un noeud héritant de OperateurArithmetiqueInt
         * @param e le noeud à visiter
         * @param operateur l'opérateur binaire permettant de calculer la valeur de l'operateur à partir de la valeur de ses opérandes prennant deux paramètre de type Integer et renvoyant une valeur de type Integer)
         * @return le résultat de l'application du visiteur courant (this) au noeud e
         */
        private Valeur visitOpArithmetiqueIntWorker(OperateurArithmetiqueInt e, BinaryOperator<Integer> operateur) {
            return visitOpBinaireWorker(e, operateur, Int::new);
        }

        /**
         * Méthode factorisant les méthodes visit s'appliquant à un noeud héritant de visitOpArithmetiqueFloatWorker
         * @param e le noeud à visiter
         * @param operateur l'opérateur binaire permettant de calculer la valeur de l'operateur à partir de la valeur de ses opérandes prennant deux paramètre de type Float et renvoyant une valeur de type Float)
         * @return le résultat de l'application du visiteur courant (this) au noeud e
         */
        private Valeur visitOpArithmetiqueFloatWorker(OperateurArithmetiqueFloat e, BinaryOperator<Float> operateur) {
            return visitOpBinaireWorker(e, operateur, FloatMinCaml::new);
        }
        
        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Valeur visit(Add e) {
            return visitOpArithmetiqueIntWorker(e, (a, b) ->(a + b));
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Valeur visit(Sub e) {
            return visitOpArithmetiqueIntWorker(e, (a, b) -> (a - b));
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Valeur visit(FNeg e) {
            Valeur valeur = e.getE().accept(this);
            if (valeur == null) {
                return null;
            }
            Float valeurFloat = (Float) valeur.getValeur();
            valeurFloat *= -1;
            return new FloatMinCaml(valeurFloat);
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Valeur visit(FAdd e) {
            return visitOpArithmetiqueFloatWorker(e, (a, b) -> (a + b));
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Valeur visit(FSub e) {
            return visitOpArithmetiqueFloatWorker(e, (a, b) -> (a - b));
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Valeur visit(FMul e) {
            return visitOpArithmetiqueFloatWorker(e, (a, b) -> (a * b));
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Valeur visit(FDiv e) {
            return visitOpArithmetiqueFloatWorker(e, (a, b) -> (a / b));
        }

        /**
         * Méthode factorisant les méthodes visit s'appliquant à un noeud héritant de OperateurRelationnel
         * @param e le noeud à visiter
         * @param operateur la fonction permettant de calculer la valeur de l'operateur à partir de la valeur de ses opérandes prennant deux paramètre et renvoyant une valeur)
         * @return le résultat de l'application du visiteur courant (this) au noeud e
         */
        private Valeur visitOpRelationnelWorker(OperateurRelationnel e, BiFunction operateur) {
            return visitOpBinaireWorker(e, operateur, Bool::new);
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Valeur visit(Eq e) {
            return visitOpRelationnelWorker(e, (a, b) -> a.equals(b)); // on peut utiliser la méthode equals car les valeurs sont soit des Boolean, soit des Integer, soit des Tuple (la classe tuple redefinit la methode equals)
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Valeur visit(LE e) {
            return visitOpRelationnelWorker(e, (a, b) -> (Integer) a <= (Integer) b);
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Valeur visit(If e) {
            Valeur valeurE1 = e.getE1().accept(this);
            Valeur valeurE2 = e.getE2().accept(this);
            Valeur valeurE3 = e.getE3().accept(this);
            if (valeurE1 != null) {
                boolean valE1 = (Boolean) valeurE1.getValeur();
                if (valE1 && valeurE2 != null) {
                    return valeurE2;
                } else if (!valE1 && valeurE3 != null) {
                    return valeurE3;
                }
            }
            if (valeurE2 != null && valeurE3 != null && valeurE2.getValeur().equals(valeurE3.getValeur())) {
                return valeurE2;
            }
            return null;
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Valeur visit(Let e) {
            Valeur valeurAffecte = e.getE1().accept(this);
            varConstante.put(e.getId().getIdString(), valeurAffecte);
            return e.getE2().accept(this);
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Valeur visit(Var e) {
            return varConstante.get(e.getId().getIdString());
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Valeur visit(LetRec e) {
            return e.getE().accept(this);
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Valeur visit(App e) {
            return null;
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Valeur visit(Tuple e) {
            List<Exp> composantesAcceptes = new ArrayList<>();
            for (Exp composante : e.getEs()) {
                composantesAcceptes.add(composante.accept(this));
            }
            if (composantesAcceptes.stream().anyMatch(x -> x != null)) // si au moins une composante du tuple est constante, on a besoin de le renvoyer
            {
                return new Tuple(composantesAcceptes);
            } else {
                return null;
            }
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Valeur visit(LetTuple e) {
            Valeur valeurAffecte = e.getE1().accept(this);
            if (valeurAffecte != null) {
                Tuple valeurAffecteeTuple = (Tuple) valeurAffecte;
                List<Id> ids = e.getIds();
                for (int i = 0; i < ids.size(); i++) {
                    varConstante.put(ids.get(i).getIdString(), (Valeur) valeurAffecteeTuple.getEs().get(i));
                }
            }
            return e.getE2().accept(this);
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Valeur visit(Array e) {
            return null;
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Valeur visit(Get e) {
            return null;
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Valeur visit(Put e) {
            return null;
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Valeur visit(FloatMinCaml e) {
            return e;
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Valeur visit(Unit e) {
            return e;
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Valeur visit(Bool e) {
            return e;
        }

        /**
        * Visite le noeud e et renvoie le résultat de l'application du visiteur à ce noeud. 
        * @param e le noeud à visiter
        * @return le résultat de l'application du visiteur courant (this) au noeud e
        */
        @Override
        public Valeur visit(Int e) {
            return e;
        }
    }
    
}
