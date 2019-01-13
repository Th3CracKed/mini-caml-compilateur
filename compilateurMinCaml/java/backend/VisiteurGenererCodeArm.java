package backend;

import arbreasml.*;
import arbremincaml.*;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import util.*;
import visiteur.*;

/**
 * Visiteur générant du code ARM
 */
public class VisiteurGenererCodeArm extends GenerateurDeCode implements VisiteurAsml {
    private static final int TAILLE_ZONE_ALLOCATION_DYNAMIQUE = 100000*Constantes.TAILLE_MOT_MEMOIRE;
    private static final int NUM_REGISTRE_DESTINATION = 4;
    private static final int NUM_REGISTRE_OPERANDE1 = 4;
    private static final int NUM_REGISTRE_OPERANDE2 = 5;
    private static final int NUM_REGISTRE_IMMEDIAT_INVALIDE = 7;
    private static final int NUM_REGISTRE_SAUVEGARDE_VALEUR_RETOUR = 7;
    
    private static final int NUM_REGISTRE_DESTINATION_FLOAT = 0;
    private static final int NUM_REGISTRE_OPERANDE1_FLOAT = 0;
    private static final int NUM_REGISTRE_OPERANDE2_FLOAT = 1;
    
    private static final String FP = "FP";
    private static final String SP = "SP";
    private static final String LR = "LR";
    private static final String PC = "PC";
    private static final String LDR = "LDR";
    private static final String STR = "STR";
    private static final String FLDS = "FLDS";
    private static final String FSUBS = "FSUBS";
    private static final String LTORG = ".ltorg"; // les directives ltorg peuvent être placées après un branchement inconditionnel pour indiquer au compilateur de placer 
                                                  //les constantes (les flottants par exemple) à une adresse proche du code. Cela permet de réduire le risque, pour un 
                                                  // programme ARM comportant beaucoup d'instruction, de ne pas pouvoir compiler car certains labels sont des adresses
                                                  // trop loin du code pour pouvoir être chargées (le codage des instructions ARM ne contient pas directement des adresses
                                                  // mais un décalage par rapport à PC pour obtenir cette adresse, et toutes les valeurs des adresses ne sont pas disponibles

    private static final int MAX_DECALAGE_LOAD_STORE = (int) Math.pow(2, 12) - 1;
    private static final int MIN_DECALAGE_LOAD_STORE = -MAX_DECALAGE_LOAD_STORE; // un bit est reserve pour le signe et la valeur absolue est codee sur 12 bit pour les decalages immédiats utilisés comme ceci : [Rn, #decalage]
    
    private static final int MAX_DECALAGE_INDICE_LOAD_STORE_FLOAT = (int) Math.pow(2, 8) - 1;
    private static final int MIN_DECALAGE_INDICE_LOAD_STORE_FLOAT = -MAX_DECALAGE_LOAD_STORE; // un bit est reserve pour le signe et la valeur absolue est codee sur 8 bit pour les decalages immédiats utilisés comme ceci : [Rn, #decalage]

    private final HashMap<String, EmplacementMemoire> emplacementsMemoire;
    private final Stack<EmplacementMemoire> emplacementDestination;
    private int numRegistre;
    private final Stack<Boolean> pileEstInstructionMov;   
    private final OptionsGenerationCodeArm optionsGenCodeArm;
    
    private static final HashMap<String, String> FONCTIONS_ASML_VERS_ARM = new HashMap<>();
    private static final HashMap<Integer, String> REGISTRE_VERS_CHAINE = new HashMap<>();
    public static final HashMap<String, String> FONCTIONS_ASML_VERS_INSTRUCTIONS_FLOAT = new HashMap<>();
    static
    {
        final String FTOSIZS = "FTOSIZS";
        FONCTIONS_ASML_VERS_INSTRUCTIONS_FLOAT.put(Constantes.SQRT_ASML, "FSQRTS");
        FONCTIONS_ASML_VERS_INSTRUCTIONS_FLOAT.put(Constantes.ABS_FLOAT_ASML, "FABSS");
        FONCTIONS_ASML_VERS_INSTRUCTIONS_FLOAT.put(Constantes.TRUNCATE_ASML, FTOSIZS);
        FONCTIONS_ASML_VERS_INSTRUCTIONS_FLOAT.put(Constantes.INT_OF_FLOAT_ASML, FTOSIZS);
        FONCTIONS_ASML_VERS_INSTRUCTIONS_FLOAT.put(Constantes.FLOAT_OF_INT_ASML, "FSITOS");
        REGISTRE_VERS_CHAINE.put(Constantes.FP, FP);
        REGISTRE_VERS_CHAINE.put(Constantes.SP, SP);
        REGISTRE_VERS_CHAINE.put(Constantes.LR, LR);
        REGISTRE_VERS_CHAINE.put(Constantes.PC, PC);
        FONCTIONS_ASML_VERS_ARM.put(Constantes.PRINT_INT_ASML, Constantes.PRINT_INT_ARM);
        FONCTIONS_ASML_VERS_ARM.put(Constantes.PRINT_NEWLINE_ASML, Constantes.PRINT_NEWLINE_ARM);
        FONCTIONS_ASML_VERS_ARM.put(Constantes.CREATE_ARRAY_ASML, Constantes.CREATE_ARRAY_ARM);
        FONCTIONS_ASML_VERS_ARM.put(Constantes.CREATE_FLOAT_ARRAY_ASML, Constantes.CREATE_FLOAT_ARRAY_ARM);
        FONCTIONS_ASML_VERS_ARM.put(Constantes.SIN_ASML, Constantes.SIN_ARM);
        FONCTIONS_ASML_VERS_ARM.put(Constantes.COS_ASML, Constantes.COS_ARM);
    }

    /**
     * Créé un visiteur générant du code ARM
     * @param emplacementsMemoire les emplacement des variables déterminé à l'allocation de registre
     * @param fichierSortie le fichier dans lequel générer du code ARM
     * @param optionsGenCodeArm les options de génération de code
     */
    public VisiteurGenererCodeArm(HashMap<String, EmplacementMemoire> emplacementsMemoire, PrintStream fichierSortie, OptionsGenerationCodeArm optionsGenCodeArm) {
        super(fichierSortie);
        this.optionsGenCodeArm = optionsGenCodeArm;
        this.emplacementsMemoire = emplacementsMemoire;        
        this.emplacementDestination = new Stack();
        changerDestination(new Registre(0));
        pileEstInstructionMov = new Stack<>();
    }

    /**
     * Renvoie la chaîne correspondant au numéro de registre numReg (R0 pour 0, FP pour 11,...)
     * @param numReg le numéro de registre
     * @return la chaîne correspondant au numéro de registre numReg
     */
    private String strReg(int numReg) {
        String nomRegSpecial = REGISTRE_VERS_CHAINE.get(numReg);
        return (nomRegSpecial == null) ? "R" + numReg : nomRegSpecial;
    }

    /**
     * Définit si le prochain appel à visit sur un entier ou une variable doit afficher une instruction mov ou cet entier ou cette variable seule
     * @param estInstReturn vrai si l'instruction courante est une instruction return (dans let f x = let x = 1 in x, l'affectation de 1 à x n'est pas une
     * instruction return mais le x à la fin de la fonction en est une)
     */
    private void changerEstInstructionMov(boolean estInstReturn) {
        pileEstInstructionMov.push(estInstReturn && (pileEstInstructionMov.isEmpty() || estInstructionMov()));
    }

    /**
     * restaure la précédente valeur booléenne avant le dernier appel à changerEstInstructionMov indiquant si le prochain appel à visit sur un entier ou une 
     * variable doit afficher une instruction mov ou cet entier ou cette variable seule
     */
    private void restaurerEstInstructionMov() {
        pileEstInstructionMov.pop();
    }

    /**
     * Renvoie vrai si le prochain appel à visit sur un entier ou une variable doit afficher une instruction mov ou cet entier ou cette variable seule et faux sinon
     * @return vrai si le prochain appel à visit sur un entier ou une variable doit afficher une instruction mov ou cet entier ou cette variable seule et faux sinon
     */
    private boolean estInstructionMov() {
        return pileEstInstructionMov.peek();
    }

    /**
     * Définit numRegistre comme le numéro de registre dans lequel charger la prochaine variable visitée si elle stockée en mémoire
     * @param numRegistre le numéro du registre
     */
    public void setNumRegistre(int numRegistre) {
        this.numRegistre = numRegistre;
    }

    /**
     * Ecrit dans le fichier de sortie la représentation du registre dans lequel est contenu (ou a étéchargé si la variables est en mémoire) la variable à l'emplacement
     * mémoire emplacementVar
     * @param emplacementVar l'emplacement mémoire
     */
    private void visitEmplacementVarWorker(EmplacementMemoire emplacementVar) {
        int numReg = (emplacementVar instanceof Registre) ? ((Registre) emplacementVar).getNumeroRegistre() : numRegistre;
        ecrire(strReg(numReg));
    }

    /**
     * Change la destination de la prochaine instruction a écrire
     * @param emplacement l'emplacement mémoire de la destination
     */
    private void changerDestination(EmplacementMemoire emplacement) {
        this.emplacementDestination.push(emplacement);
    }

    /**
     * Renvoie l'emplacement mémoire de la destination de la prochaine instruction a écrire
     * @return l'emplacement mémoire de la destination de la prochaine instruction a écrire
     */
    private EmplacementMemoire destination() {
        return emplacementDestination.peek();
    }

    /**
     * Restaure l'emplacement mémoire destination précédent (celui avant le dernier appel à changerDestination)
     * @return l'emplacement mémoire destination précédent (celui avant le dernier appel à changerDestination)
     */
    private EmplacementMemoire restaurerDestination() {
        return emplacementDestination.pop();
    }

    /**
     * Ecrit dans le fichier de sortie le registre qui contient la destination (ou, si la variable est sur la pile, un registre dont le contenu sera ensuite réécri
     * à l'adresse de cette variable)
     */
    private void visitDestinationWorker() {
        changerEstInstructionMov(false);
        setNumRegistre(NUM_REGISTRE_DESTINATION);
        visitEmplacementVarWorker(destination());
        restaurerEstInstructionMov();
    }

    /**
     * Ecrit dans le fichier de sortie le registre qui contient ou dans lequel a été chargé le premier opérande de l'instruction courante
     */
    private void visitOperande1Worker(VarAsml e) {
        changerEstInstructionMov(false);
        setNumRegistre(NUM_REGISTRE_OPERANDE1);
        e.accept(this);
        restaurerEstInstructionMov();
    }

    /**
     * Renvoie vrai si valeur est une valeur immédiate valide pour le shifter operand (deuxième opérande des instructions comme ADD)
     * @param valeur la valeur entière
     * @return vrai si valeur est une valeur immédiate valide pour le shifter operand (deuxième opérande des instructions comme ADD)
     */
    public static boolean estShifterOpImmediatValide(int valeur)
    {
        int numDernierBit = Constantes.TAILLE_MOT_MEMOIRE*8-1;
        int valeurMin1Octet = 0;
        int valeurMax1Octet = (int)Math.pow(2, 8)-1;
        int valeurTournee = 0;
        for(int i = 0 ; i <= numDernierBit ; i+=2)
        {         
            valeurTournee = Integer.rotateRight(valeur, i); // fait subit a veleur tournee une rotation droit de i rangs. 
            // ((valeur >> i) | (valeur << (numDernierBit+1-i))  ne fonctionne pas à cause de l'éventuelle extension de signe 
            // (l'opérateur >> fait un décalage arithmétique alors qu'il faudrait un décalage logique dans ce cas)
            if(valeurTournee >= valeurMin1Octet && valeurTournee <= valeurMax1Octet)
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Ecrit une instruction chargeant la valeur valeurDOrigine dans le registre numRegiste (l'instruction LDR de charger nimporte quelle valeur entière sur 4 octets 
     * dans un registre (seules certaines valeurs immédiates peuvent être utilisées avec l'instruction MOV)
     * @param numRegistre le numéro de registre destination
     * @param valeurDOrigine la valeur à charger dans le registre
     */
    private void chargerValeurImmediateLDR(int numRegistre, int valeurDOrigine)
    {
        ecrireAvecIndentation("LDR " + strReg(numRegistre) + ", =" + valeurDOrigine + "\n");
    }
    
    /**
     * Ecrit une instruction chargeant la valeur valeurDOrigine dans le registre NUM_REGISTRE_IMMEDIAT_INVALIDE (Ce registre est utilisé quand valeur n'est pas une valeur
     * immédiate invalide pour une instruction comme ADD)
     * @param valeurDOrigine la valeur à charger dans le registre
     */
    private void chargerValeurImmediateLDR(int valeurDOrigine) {
        chargerValeurImmediateLDR(NUM_REGISTRE_IMMEDIAT_INVALIDE, valeurDOrigine);
    }
        
    /**
     * Ecrit dans le fichier de sortie le registre qui contient ou dans lequel a été chargé le deuxième opérande de l'instruction courante
     */
    private void visitOperande2IntWorker(VarOuIntAsml e) {
        changerEstInstructionMov(false);
        if (e instanceof VarAsml) {
            setNumRegistre(NUM_REGISTRE_OPERANDE2);
        }
        else if (!estShifterOpImmediatValide(((IntAsml) e).getValeur()))
        {
            setNumRegistre(NUM_REGISTRE_IMMEDIAT_INVALIDE);
        }
        e.accept(this);
        restaurerEstInstructionMov();
    }
    
    /**
     * Si emplacement est une adresse mémoire, écrit dans le fichier de sortie l'instruction nomInstruction (LDR ou STR) permettant de faire un 
     * transfert entre le registre de numéro numReg et l'adresse ((AdresseMemoire) emplacement).getDecalage()+RnumRegBase
     * @param emplacement l'emplacement mémoire vers ou depuis lequel faire un transfert si cet emplacement est une adresse mémoire
     * @param numReg le numéro du registre vers ou depuis lequel un transfert va être fait avec la mémoire
     * @param nomInstruction le nom de l'instruction (LDR ou STR)
     * @param numRegBase le numéro de registre contenant l'adresse de base à utiliser pour l'éventuelle adresse mémoire dans emplacement
     */
    private void loadStoreWorker(EmplacementMemoire emplacement, int numReg, String nomInstruction, int numRegBase) {
        if (emplacement instanceof AdresseMemoire) {
            int decalage = ((AdresseMemoire) emplacement).getDecalage();
            String strDecalage = null;
            if (estDecalageImmediatLoadStoreValide(decalage)) {
                strDecalage = "#" + decalage;
            } else // si la valeur du decalage est trop grande pour être une valeur immediate, il faut la copier dans un registre
            {
                strDecalage = strReg(NUM_REGISTRE_IMMEDIAT_INVALIDE);
                chargerValeurImmediateLDR(decalage);

            }
            ecrireAvecIndentation(nomInstruction + " " + strReg(numReg) + ", [" + strReg(numRegBase) + ", " + strDecalage + "]\n");
        }
    }

    /**
     * Charge la valeur dans e dans le registre numReg (e peut être un entier, une variable ou un label)
     * @param e la variable, l'entier ou le label à charger
     * @param numReg le numéro de registre dans lequel charger la valeur de e
     * @param numRegBase le numéro de registre de base utilisé si e est une variable en mémoire
     */
    private void chargerValeur(VarOuIntAsml e, int numReg, int numRegBase) {
        if (e instanceof VarAsml) {
            String idString = ((VarAsml)e).getIdString();
            if(Id.estUnLabel(idString))
            {
                ecrireAvecIndentation(LDR+" "+strReg(numReg)+", ="+idString+"\n");
            }
            else
            {
                loadStoreWorker(emplacementVariable(idString), numReg, LDR, numRegBase);
            }
        } else // if(e instanceof IntAsml)
        {
            int valeur = ((IntAsml)e).getValeur();
            if (!estShifterOpImmediatValide(valeur)) { // si la valeur est trop grande pour être une valeur immediate, il faut la copier dans un registre
                chargerValeurImmediateLDR(valeur);
            }
        }
    }

    /**
     * Si emplacement est une adresse mémoire, écrit dans le fichier de sortie l'instruction STR permettant d'écrire le contenu du registre de numéro numReg 
     * à l'adresse ((AdresseMemoire) emplacement).getDecalage()+RnumRegBase
     * @param emplacement l'emplacement mémoire vers ou depuis lequel faire un transfert si cet emplacement est une adresse mémoire
     * @param numReg le numéro du registre vers ou depuis lequel un transfert va être fait avec la mémoire
     * @param numRegBase le numéro de registre contenant l'adresse de base à utiliser pour l'éventuelle adresse mémoire dans emplacement
     */
    private void str(EmplacementMemoire emplacement, int numReg, int numRegBase) {
        loadStoreWorker(emplacement, numReg, STR, numRegBase);
    }

    /**
     * Si l'emplacement de la destination de la dernière instruction est une adresse mémoire, écrit dans le fichier de sortie l'instruction STR permettant 
     * d'écrire le contenu du registre de numéro NUM_REGISTRE_DESTINATION à l'adresse de cette destination
     */
    private void strDestination() {
        str(destination(), NUM_REGISTRE_DESTINATION, Constantes.FP);
    }

    /**
     * Visite le noeud e. Dans ce cas, écrit dans le fichier l'instruction neg correspondant au noeud neg e.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(NegAsml e) {
        VarAsml op1 = e.getE();
        chargerValeur(op1, NUM_REGISTRE_OPERANDE1, Constantes.FP);
        ecrireAvecIndentation("NEG ");
        visitDestinationWorker();
        ecrire(", ");
        visitOperande1Worker(e.getE());
        ecrire("\n");
        strDestination();
    }

    /**
     * Renvoie l'emplacement mémoire de la variable d'identifiant idString
     * @param idString l'identifiant de la variable
     * @return 
     */
    private EmplacementMemoire emplacementVariable(String idString) {
        return emplacementsMemoire.get(idString);
    }

    /**
     * Visite le noeud e. Dans ce cas, écrit dans le fichier le registre contenant ou dans lequel a été chargé la variable e, ou (si estInstructionMov renvoie vrai)
     * une ou plusieurs instructions (un MOV et éventuellement un LDR pour écrire le contenu dans cette variable dans la destination courante)
     * @param e le noeud à visiter
     */
    @Override
    public void visit(VarAsml e) {
        if (estInstructionMov()) {
            chargerValeur(e, NUM_REGISTRE_OPERANDE1, Constantes.FP);
            ecrireAvecIndentation("MOV ");
            visitDestinationWorker();
            ecrire(", ");
        }
        visitEmplacementVarWorker(emplacementVariable(e.getIdString()));
        if (estInstructionMov()) {
            ecrire("\n");
            strDestination();
        }
    }

    /**
     * Visite le noeud e. Dans ce cas, visite e1 en indiquant qu'il s'agit d'une affectation avec pour destination la variable déclarée puis visit e2
     * @param e le noeud à visiter
     */
    @Override
    public void visit(LetAsml e) {
        changerEstInstructionMov(true);
        changerDestination(emplacementVariable(e.getIdString()));
        ExpAsml e1 = e.getE1();
        e1.accept(this);
        restaurerDestination();
        e.getE2().accept(this);
        restaurerEstInstructionMov();
    }

    /**
     * Méthode factorisant la génération de code pour les instructions héritant de OperateurArithmetiqueIntAsml (ADD ou SUB)
     * @param e le noeud à visiter
     * @param nomOperateur le nom de l'instruction (ADD ou SUB)
     */
    private void visitOpArithmetiqueIntWorker(OperateurArithmetiqueIntAsml e, String nomOperateur) {
        VarAsml op1 = e.getE1();
        VarOuIntAsml op2 = e.getE2();
        chargerValeur(op1, NUM_REGISTRE_OPERANDE1, Constantes.FP);
        chargerValeur(op2, NUM_REGISTRE_OPERANDE2, Constantes.FP);
        ecrireAvecIndentation(nomOperateur + " ");
        visitDestinationWorker();
        ecrire(", ");
        visitOperande1Worker(op1);
        ecrire(", ");
        visitOperande2IntWorker(op2);
        ecrire("\n");
        strDestination();
    }

    /**
     * Visite le noeud e. Dans ce cas, écrit dans le fichier l'instruction add correspondant au noeud add e.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(AddAsml e) {
        visitOpArithmetiqueIntWorker(e, "ADD");
    }

    /**
     * Si valeur est non nulle, écrit la ou les instructions pour ajouter la valeur valeur au registre SP
     * @param valeur la valeur à ajouter au registre SP
     */
    private void ajouterValeurASP(int valeur)
    {
        if(valeur != 0)
        {            
            int valAbs = Math.abs(valeur);
            String instruction = (valeur > 0)?"ADD":"SUB";
            String chaineOperande2 = null;
            if(estShifterOpImmediatValide(valAbs))
            {
                chaineOperande2 = "#"+valAbs;
            }
            else
            {            
                chargerValeurImmediateLDR(valAbs);
                chaineOperande2 = "R"+NUM_REGISTRE_IMMEDIAT_INVALIDE;
            }
            ecrireAvecIndentation(instruction+" SP, SP, "+chaineOperande2+"\n");
        }
    }
    
    /**
     * Visite le noeud e. Dans ce cas, écrit dans le fichier la fonction correspondant au noeud FunDefConcreteAsml e (avec la sauvegarde des registres devant l'être par 
     * l'appelé et l'ajout de l'instruction de fin de fonction (BX LR) si e n'est pas la fonction principale ou l'appel à min_caml_exit si e est la fonction principale
     * @param e le noeud à visiter
     */
    @Override
    public void visit(FunDefConcreteAsml e) {
        if (e.estMainFunDef()) {
            ecrire(Constantes.NOM_FONCTION_MAIN_ARM);
        } else {
            ecrire(e.getLabel());
        }
        ecrire(":\n");
        augmenterNiveauIndentation();
        changerEstInstructionMov(true);
        VisiteurTailleEnvironnement visTailleEnvironnement = new VisiteurTailleEnvironnement();
        e.accept(visTailleEnvironnement);
        int tailleEnvironnement = visTailleEnvironnement.getTailleEnvironnement();   
        if(Constantes.REGISTRE_SAUVEGARDES_APPELE.length >= 1)
        {
            ecrireAvecIndentation("PUSH {");
            for (int i = 0; i < Constantes.REGISTRE_SAUVEGARDES_APPELE.length; i++) {
                if(i >= 1)
                {
                    ecrire(", ");
                }
                ecrire(strReg(Constantes.REGISTRE_SAUVEGARDES_APPELE[i]));
            }
            ecrire("}\n");
        }           
        ecrireAvecIndentation("SUB FP, SP, #"+Constantes.TAILLE_MOT_MEMOIRE+"\n");
        ajouterValeurASP(-tailleEnvironnement);
        e.getAsmt().accept(this);
        ajouterValeurASP(tailleEnvironnement);
        if(Constantes.REGISTRE_SAUVEGARDES_APPELE.length >= 1)
        {
            ecrireAvecIndentation("POP {");
            for (int i = 0; i < Constantes.REGISTRE_SAUVEGARDES_APPELE.length; i++) {
                if(i >= 1)
                {
                    ecrire(", ");
                }
                ecrire(strReg(Constantes.REGISTRE_SAUVEGARDES_APPELE[i]));
            }
            ecrire("}\n");
        }
        restaurerEstInstructionMov();
        if (e.estMainFunDef()) {      
            ecrireAvecIndentation("B "+Constantes.EXIT_ARM+"\n");
        }
        else
        {            
            ecrireAvecIndentation("BX LR\n");
        }
        ecrire(LTORG+"\n\n");
        diminuerNiveauIndentation();
    }
    
    /**
     * Fonction permettant de factoriser la génération code pour les noeud CallAsml, CallClosureAsml et NewASML. Decrit dans le fichier les instructions correspondant 
     * au noeud CallAsml e (avec la sauvegarde des registres devant l'être par l'appelant et le passage des paramètres). Le paramètre param0 permet de traiter
     * l'instruction new comme un appel de fonction (la seul différence et que son paramètre peut être une variable ou un entier (et pas seulement une variable)
     * @param e le noeud à visiter
     * @param param0 le paramètre entier remplaçant le paramètre de la fonction si il n'est pas null
     */
    public void visitCallWorker(CallAsml e, IntAsml param0)
    {
        if(Constantes.REGISTRE_SAUVEGARDES_APPELANT.length >= 1)
        {
            ecrireAvecIndentation("PUSH {");
            for (int i = 0; i < Constantes.REGISTRE_SAUVEGARDES_APPELANT.length; i++) {
                if(i >= 1)
                {
                    ecrire(", ");
                }
                ecrire(strReg(Constantes.REGISTRE_SAUVEGARDES_APPELANT[i]));
            }
            ecrire("}\n");
        }
        int nbParametres = e.getArguments().size();
        int tailleParametresAEmpiler = Math.max(0, (nbParametres - Constantes.REGISTRES_PARAMETRES.length)) * Constantes.TAILLE_MOT_MEMOIRE;
        ajouterValeurASP(-tailleParametresAEmpiler);
        for (int i = 0; i < nbParametres; i++) {   
            if(i == 0 && param0 != null)
            {
                String strRegistre = strReg(Constantes.REGISTRES_PARAMETRES[i]);
                ecrireAvecIndentation("LDR " + strRegistre + ", ="+param0.getValeur()+"\n");
            }
            else 
            {
                EmplacementMemoire emplacementParam = emplacementVariable(e.getArguments().get(i).getIdString());
                AdresseMemoire adressePileParamPasseEnParam = null;
                if(emplacementParam instanceof Registre && Arrays.<Integer>asList(Constantes.REGISTRES_PARAMETRES).contains(((Registre)emplacementParam).getNumeroRegistre()))
                {
                    // pour le programme let rec f x y = let rec g z t = z - t in g y x in f 1 2, g passe ses parametres a f en echangeant l'ordre des parametres, et si on ne
                    // gere pas ce cas, le code genere serait MOV R0, R1; MOV R1, R0 (qui est faux car cela correspond a l'appel g y y). Pour eviter cela lorsqu'une fonction passe ses
                    // parametres a une autre, les valeurs des parametres sont chargee depuis la pile (elles sont presentes pour la sauvegarde du contexte par l'appelant)
                    int indRegSauvegardeAppelant = Arrays.<Integer>asList(Constantes.REGISTRE_SAUVEGARDES_APPELANT).indexOf(((Registre)emplacementParam).getNumeroRegistre());
                    adressePileParamPasseEnParam = new AdresseMemoire(indRegSauvegardeAppelant * Constantes.TAILLE_MOT_MEMOIRE+tailleParametresAEmpiler);
                }
                if (i <= Constantes.REGISTRES_PARAMETRES.length - 1)
                {
                    if(adressePileParamPasseEnParam != null)
                    {
                        loadStoreWorker(adressePileParamPasseEnParam, Constantes.REGISTRES_PARAMETRES[i], LDR, Constantes.SP);
                    }
                    else
                    {
                        chargerValeur(e.getArguments().get(i), NUM_REGISTRE_OPERANDE1, Constantes.FP);
                        ecrireAvecIndentation("MOV " + strReg(Constantes.REGISTRES_PARAMETRES[i]) + ", ");                    
                        visitOperande1Worker(e.getArguments().get(i));
                        ecrire("\n");
                    }
                }
                else
                {
                    if(adressePileParamPasseEnParam != null)
                    {
                        loadStoreWorker(adressePileParamPasseEnParam, NUM_REGISTRE_OPERANDE1, LDR, Constantes.SP);
                        loadStoreWorker(new AdresseMemoire((nbParametres-1-i)*Constantes.TAILLE_MOT_MEMOIRE), NUM_REGISTRE_OPERANDE1, STR, Constantes.SP);
                    }
                    else
                    {
                        chargerValeur(e.getArguments().get(i), NUM_REGISTRE_OPERANDE1, Constantes.FP);
                        int numReg = (emplacementParam instanceof Registre)?((Registre)emplacementParam).getNumeroRegistre():NUM_REGISTRE_OPERANDE1;
                        loadStoreWorker(new AdresseMemoire((nbParametres-1-i)*Constantes.TAILLE_MOT_MEMOIRE), numReg, STR, Constantes.SP);
                    }  
                }
            }                        
        }        
        String idString = e.getIdString();
        if (Id.estUnLabel(idString)) {               
            ecrireAvecIndentation("BL ");
            String nomFonctionTraduite = FONCTIONS_ASML_VERS_ARM.get(idString);
            ecrire((nomFonctionTraduite == null)?idString:nomFonctionTraduite);
        }
        else // le noeud visite est un CallClosureAsml transforme en CallAsml
        {                
            String strRegistre = null;
            int numReg = NUM_REGISTRE_OPERANDE1;
            strRegistre = strReg(numReg);      
            chargerValeur(e.getArguments().get(0), numReg, Constantes.FP);
            ecrireAvecIndentation("LDR "+strRegistre+", [");
            visitOperande1Worker(e.getArguments().get(0));
            ecrire("]\n");
            ecrireAvecIndentation("MOV "+LR+", "+PC+"\n");  
            ecrireAvecIndentation("BX "+strRegistre);
        }
        ecrireAvecIndentation("\n");
        ecrireAvecIndentation("MOV " + strReg(NUM_REGISTRE_SAUVEGARDE_VALEUR_RETOUR) + ", " + strReg(Constantes.REGISTRE_VALEUR_RETOUR) + "\n");
        ajouterValeurASP(tailleParametresAEmpiler);
        if(Constantes.REGISTRE_SAUVEGARDES_APPELANT.length >= 1)
        {
            ecrireAvecIndentation("POP {");
            for (int i = 0; i < Constantes.REGISTRE_SAUVEGARDES_APPELANT.length; i++) {
                if(i >= 1)
                {
                    ecrire(", ");
                }
                ecrire(strReg(Constantes.REGISTRE_SAUVEGARDES_APPELANT[i]));
            }
            ecrire("}\n");
        }
        ecrireAvecIndentation("MOV ");
        visitDestinationWorker();
        ecrire(", " + strReg(NUM_REGISTRE_SAUVEGARDE_VALEUR_RETOUR) + "\n");
        strDestination();
    }
    
    /**
     * Méthode pour factoriser la génération de code des instructions (ou des fonctions traduites par une instruction comme celle ci) avec un opérande et une destination
     * qui sont des register dédiés aux nombres flottant (fneg, sqrt, abs, int_of_float, float_of_int et truncate)
     * @param param0 l'opérande de l'instruction
     * @param nomInstruction le nom de l'instruction
     */
    private void visitInstructionFloatVersFloatWorker(VarAsml param0, String nomInstruction)
    {
        transfertVersRegFloat(NUM_REGISTRE_OPERANDE1_FLOAT, param0);
        ecrireAvecIndentation(nomInstruction+" "+strRegFloat(NUM_REGISTRE_DESTINATION_FLOAT)+", "+strRegFloat(NUM_REGISTRE_OPERANDE1_FLOAT)+"\n");
        transfertDestinationFloat();   
    }
    
    /**
     * Visite le noeud e. Dans ce cas, écrit dans le fichier le code correspondant à l'appel de la fonction du noeud CallAsml e ou, si il s'agit d'une fonction
     * ASML se traduisant en une instruction ARM (comme sqrt), écrit cette instruction dans le fichier.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(CallAsml e) {
        String idString = e.getIdString();
        VarAsml param0 = (e.getArguments().isEmpty())?null:e.getArguments().get(0);
        String traductionInstructionVFP = FONCTIONS_ASML_VERS_INSTRUCTIONS_FLOAT.get(idString);
        if(traductionInstructionVFP != null)
        {
            visitInstructionFloatVersFloatWorker(param0, traductionInstructionVFP);
        }
        else
        {
            visitCallWorker(e, null);
        }
    }

    /**
     * Visite le noeud e. Dans ce cas, si estInstructionMov est vrai, écrit dans le fichier le code correspondant au chargement de l'entier e à l'emplacement mémoire
     * destination. Sinon, si la valeur de e n'est pas une valeur immédiate valide pour un shifter operand (deuxième opérande pour des instructions comme ADD),
     * le chaine correspondante au registre ou sont chargées les valeur immédiates invalide (NUM_REGISTRE_IMMEDIAT_INVALIDE) est écrit. Sinon, la valeur de e est écrite
     * (précédée du caractère #)
     * @param e le noeud à visiter
     */
    @Override
    public void visit(IntAsml e) {
        int valeur = e.getValeur();
        if (estInstructionMov()) {
            ecrireAvecIndentation("LDR ");
            visitDestinationWorker();
            ecrire(", =" + e.getValeur()+"\n");
            strDestination();
        }
        else if(!estShifterOpImmediatValide(valeur))
        {
            ecrire(strReg(NUM_REGISTRE_IMMEDIAT_INVALIDE));   
        }
        else
        {
            ecrire("#" + valeur);            
        }
    }

    /**
     * Visite le noeud e. Dans ce cas, créé et visit un noeud IntAsml correspondant à la valeur nil (Nop est une expression en ASML et doit avoir une valeur)
     * @param e le noeud à visiter
     */
    @Override
    public void visit(NopAsml e) {
        IntAsml.nil().accept(this);
    }

    /**
     * Visite le noeud e. Dans ce cas, visite les fonctions et les déclarations de nombres flottants du programme ASML e après avoir écrit les fonctions 
     * sinus, cosinus, et celles pour allouer de la mémoire ou créer un tableau si elles sont utilisées dans le programme
     * @param e le noeud à visiter
     */
    @Override
    public void visit(ProgrammeAsml e) {          
        ecrire(".text\n");
        ecrire(".global " + Constantes.NOM_FONCTION_MAIN_ARM + "\n");
        if(optionsGenCodeArm.getUtiliseNewOuCreateArray())
        {
            ecrire(Constantes.CREATE_FLOAT_ARRAY_ARM+":\n");
            ecrire(Constantes.CREATE_ARRAY_ARM+":\n");
            augmenterNiveauIndentation();
            ecrireAvecIndentation("LSL R0, R0, #2          @ multiplier r0 par 4 pour que r0 ait pour valeur le nombre d'octet a allouer\n"); 
            ecrireAvecIndentation("MOV R2, R0            @ met la taille restante a initialiser dans r2 \n"); 
            ecrireAvecIndentation("LDR R3, ="+Constantes.DEBUT_ZONE_MEMOIRE_DYNAMIQUE_LIBRE_ARM+"            @ charge l'adresse du pointeur sur le debut de la prochaine zone a allouer dans r3 \n"); // 
            ecrireAvecIndentation("LDR R3, [R3]            @ charge le pointeur sur le debut de la prochaine zone a allouer dans r3 \n");
            ecrire(Constantes.CREATE_ARRAY_BOUCLE_ARM+":\n");
            ecrireAvecIndentation("STMIA R3!, {R1}            @ initialise le prochain mot memoire avec la valeur du deuxieme parametre de la fonction et ajoute 4 a r3 pour qu'il pointe sur le prochain mot memoire a initialiser\n");
            ecrireAvecIndentation("SUB R2, R2, #"+Constantes.TAILLE_MOT_MEMOIRE+"            @ decremente la taille restante a initialiser de 4\n");
            ecrireAvecIndentation("CMP R2, #0            @ compare la taille restante a initialiser a 0\n");
            ecrireAvecIndentation("BGT "+Constantes.CREATE_ARRAY_BOUCLE_ARM+"            @ si la taille restante a initialiser est strictement positive, aller a "+Constantes.CREATE_ARRAY_BOUCLE_ARM+"\n");           
            ecrire(Constantes.NEW_ARM+":\n");
            ecrireAvecIndentation("MOV R2, R0            @ met la taille restante a allouer dans r2 \n"); 
            ecrireAvecIndentation("LDR R0, ="+Constantes.DEBUT_ZONE_MEMOIRE_DYNAMIQUE_LIBRE_ARM+"            @ charge l'adresse du pointeur sur le debut de la prochaine zone a allouer dans r0 \n"); // 
            ecrireAvecIndentation("LDR R0, [R0]            @ charge le pointeur sur le debut de la prochaine zone a allouer dans r0 \n"); // 
            ecrireAvecIndentation("ADD R3, R0, R2          @ stocke la nouvelle valeur du pointeur sur le debut de la prochaine zone a allouer dans r3 (son ancienne valeur a laquelle on ajoute la taille allouee)\n");
            ecrireAvecIndentation("LDR R2, ="+Constantes.DEBUT_ZONE_MEMOIRE_DYNAMIQUE_LIBRE_ARM+"            @ charge l'adresse du pointeur sur le debut de la prochaine zone a allouer dans r2\n");
            ecrireAvecIndentation("STR R3, [R2]            @ecrit la nouvelle valeur du pointeur sur le debut de la prochaine zone a allouer\n");
            ecrireAvecIndentation("BX LR            @aller a l'adresse dans lr (instruction return)\n");
            ecrire(LTORG+"\n\n");
            diminuerNiveauIndentation();
        }    
        float[] coefficientsPolynome = new float[]{1.0f, -0.5f, 0.041666668f, -0.0013888889f, 2.4801588E-5f, -2.755732E-7f, 2.0876758E-9f}; // abs(cos(x)-(x^0*1.0+x^2*-0.5+x^4*0.041666668+x^6*-0.0013888889+x^8*2.4801588E-5+x^10*-2.755732E-7+x^12*2.0876758E-9)) <= 2^-23 si -pi/2 <= x <= pi/2 (2^-23 est la précision (plus petite valeur strictement positive) des flottants simple précision). Ce polynôme est la somme des monomes de degre inférieurs ou égaux à 12 du développement en série entière de cos(x)
        if(optionsGenCodeArm.getUtiliseSinOuCos())
        {
            int decalageDernierElement = (coefficientsPolynome.length - 1)*Constantes.TAILLE_MOT_MEMOIRE;
            ecrire(Constantes.SIN_ARM+":\n");
            augmenterNiveauIndentation();
            ecrireAvecIndentation("@la fonction sin(x) renvoie cos(x-pi/2)\n");
            ecrireAvecIndentation("FMSR S0, R0            @S0 = R0\n");
            ecrireAvecIndentation("LDR R0, ="+Constantes.PI_SUR_2_ARM+"            @R0 = _piSur2\n");
            ecrireAvecIndentation("FLDS S1, [R0]            @S1 = *R0 (met dans S1 la valeur pi/2)\n");
            ecrireAvecIndentation("FSUBS S0, S0, S1            @S0 -= S1\n");
            ecrireAvecIndentation("FMRS R0, S0            @R0 = S0\n");  
            diminuerNiveauIndentation();
            ecrire(Constantes.COS_ARM+":\n");
            augmenterNiveauIndentation();            
            ecrireAvecIndentation("@si le paramètre de cos n'est pas entre -pi/2 et pi/2, on utilise les propriétés de cette fonction\n");
            ecrireAvecIndentation("@pour se ramener à ce cas\n");
            ecrireAvecIndentation("FMSR S0, R0\n");
            ecrireAvecIndentation("LDR R0, ="+Constantes.DEUX_PI_ARM+"\n");
            ecrireAvecIndentation("FLDS S1, [R0]\n");
            ecrireAvecIndentation("FDIVS S2, S0, S1\n");
            ecrireAvecIndentation("FTOSIZS S2, S2\n");
            ecrireAvecIndentation("FSITOS S2, S2\n");
            ecrireAvecIndentation("FMULS S2, S2, S1\n");
            ecrireAvecIndentation("FSUBS S0, S0, S2\n");
            ecrireAvecIndentation("MOV R1, #0\n");
            ecrireAvecIndentation("FCMPZS S0\n");
            ecrireAvecIndentation("FMSTAT\n");
            ecrireAvecIndentation("BHI "+Constantes.SIN_FIN_SI_1_ARM+"\n");
            ecrireAvecIndentation("FNEGS S0, S0\n");
            ecrire(Constantes.SIN_FIN_SI_1_ARM+":\n");
            ecrireAvecIndentation("LDR R0, ="+Constantes.PI_SUR_2_ARM+"\n");
            ecrireAvecIndentation("FLDS S2, [R0]\n");
            ecrireAvecIndentation("FCMPS S0, S2\n"); 
            ecrireAvecIndentation("FMSTAT\n");
            ecrireAvecIndentation("BLT "+Constantes.SIN_FIN_SI_2_ARM+"\n");      
            ecrireAvecIndentation("LDR R0, ="+Constantes.TROIS_PI_SUR_2_ARM+"\n");
            ecrireAvecIndentation("FLDS S2, [R0]\n");
            ecrireAvecIndentation("FCMPS S0, S2\n");    
            ecrireAvecIndentation("FMSTAT\n");
            ecrireAvecIndentation("BLT "+Constantes.SIN_SINON_ARM+"\n"); 
            ecrireAvecIndentation("FSUBS S0, S0, S1\n");
            ecrireAvecIndentation("B "+Constantes.SIN_FIN_SI_2_ARM+"\n");
            ecrire(Constantes.SIN_SINON_ARM+":\n");
            ecrireAvecIndentation("LDR R0, ="+Constantes.PI_ARM+"\n");
            ecrireAvecIndentation("FLDS S1, [R0]\n");
            ecrireAvecIndentation("FSUBS S0, S1, S0\n");
            ecrireAvecIndentation("@on affecte 1 à R1 pour indiquer qu'il faut appliqué l'instruction FNEG au résultat avant de le renvoyer\n");
            ecrireAvecIndentation("@ (on a utilisé le fait que cos(pi-x)=-cos(x) )\n");
            ecrireAvecIndentation("MOV R1, #1          @ R1 = 1 indique qu'il faut appliqué l'instruction FNEG au résultat avant de le renvoyé\n");            
            ecrire(Constantes.SIN_FIN_SI_2_ARM+":\n");
            ecrireAvecIndentation("LDR R3, ="+Constantes.COEFFICIENTS_POLYNOME_SIN_ARM+"\n");
            ecrireAvecIndentation("MOV R2, R3\n");
            ecrireAvecIndentation("ADD R2, R2, #"+decalageDernierElement+"\n");
            ecrireAvecIndentation("FLDMIAS R2, {S1}\n");
            ecrireAvecIndentation("SUB R2, R2, #"+Constantes.TAILLE_MOT_MEMOIRE+"\n");
            ecrireAvecIndentation("FMULS S0, S0, S0\n");
            ecrireAvecIndentation("@Evaluation du polynome approchant la fonction cos avec la méthode de Horner. Les coefficients du polynome sont à \n");
            ecrireAvecIndentation("@l'adresse "+Constantes.COEFFICIENTS_POLYNOME_SIN_ARM+"\n");
            ecrire(Constantes.SIN_TANT_QUE_ARM+":\n");
            ecrireAvecIndentation("CMP R2, R3\n");
            ecrireAvecIndentation("BLT "+Constantes.SIN_FIN_TANT_QUE_ARM+"\n"); 
            ecrireAvecIndentation("FMULS S1, S1, S0\n");
            ecrireAvecIndentation("FLDMIAS R2, {S2}\n");
            ecrireAvecIndentation("SUB R2, R2, #"+Constantes.TAILLE_MOT_MEMOIRE+"\n");
            ecrireAvecIndentation("FADDS S1, S1, S2\n");
            ecrireAvecIndentation("B "+Constantes.SIN_TANT_QUE_ARM+"\n");    
            ecrire(Constantes.SIN_FIN_TANT_QUE_ARM+":\n");
            ecrireAvecIndentation("CMP R1, #0\n");
            ecrireAvecIndentation("BEQ "+Constantes.SIN_FIN_SI_3_ARM+"\n");  
            ecrireAvecIndentation("FNEGS S1, S1\n");
            ecrire(Constantes.SIN_FIN_SI_3_ARM+":\n");
            ecrireAvecIndentation("FMRS R0, S1\n");
            ecrireAvecIndentation("BX LR            @aller a l'adresse dans lr (instruction return)\n");
            ecrire(LTORG+"\n\n");
            diminuerNiveauIndentation();
        }        
        for (FunDefAsml funDef : e.getFunDefs()) {
            if(funDef instanceof FunDefConcreteAsml)
            {
                funDef.accept(this);
            }
        }
        e.getMainFunDef().accept(this);
        augmenterNiveauIndentation();
        ecrire(".data\n");
        ecrire(".balign "+Constantes.TAILLE_MOT_MEMOIRE+"\n");
        if(optionsGenCodeArm.getUtiliseNewOuCreateArray())
        {
            ecrire(Constantes.ZONE_MEMOIRE_DYNAMIQUE_ARM+": .skip "+TAILLE_ZONE_ALLOCATION_DYNAMIQUE+"\n");
            ecrire(Constantes.DEBUT_ZONE_MEMOIRE_DYNAMIQUE_LIBRE_ARM+": .word "+Constantes.ZONE_MEMOIRE_DYNAMIQUE_ARM+"\n");
        }
        List<List<FunDefAsml>> listesFunDefs = new ArrayList<>(Arrays.asList(e.getFunDefs()));
        if(optionsGenCodeArm.getUtiliseSinOuCos())
        {
            Float piFloat = (float)Math.PI;
            listesFunDefs.add(Arrays.asList(new LetFloatAsml(Constantes.PI_ARM, piFloat), new LetFloatAsml(Constantes.PI_SUR_2_ARM, piFloat/2.0f), new LetFloatAsml(Constantes.TROIS_PI_SUR_2_ARM, 3.0f*(piFloat/2.0f)), new LetFloatAsml(Constantes.DEUX_PI_ARM, 2.0f*piFloat)));
        }        
        for(List<FunDefAsml> listeFunDef : listesFunDefs)
        {
            for (FunDefAsml funDef : listeFunDef) {
                if(funDef instanceof LetFloatAsml)
                {
                    funDef.accept(this);
                }
            }
        }     
        if(optionsGenCodeArm.getUtiliseSinOuCos())
        {
            ecrire(Constantes.COEFFICIENTS_POLYNOME_SIN_ARM+":\n");
            for(float coefficient : coefficientsPolynome)
            {
                ecrire("    .single "+coefficient+"\n");
            }    
        }          
        diminuerNiveauIndentation();
    }

    /**
     * Visite le noeud e. Dans ce cas, écrit dans le fichier l'instruction sub correspondant au noeud sub e.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(SubAsml e) {
        visitOpArithmetiqueIntWorker(e, "SUB");
    }
    
    /**
     * Place le résultat d'une instruction pour les flottants dans le registre ou à l'adresse mémoire qui contient la destination de cette opération
     * (les instructions pour les flottants utilisent des registres dédiés (S0, S1) mais les variables sont toujours placées par l'allocation de registre
     * sur la pile ou dans les registre généraux (R8, R9...), il faut donc réaliser des transfert entre ces emplacements mémoire
     */
    public void transfertDestinationFloat()
    {
        EmplacementMemoire emplacementDest = destination();
        String strRegDestinationFloat = strRegFloat(NUM_REGISTRE_DESTINATION_FLOAT);
        if(emplacementDest instanceof Registre)
        {
            ecrireAvecIndentation("FMRS "+strReg(((Registre)emplacementDest).getNumeroRegistre())+", "+strRegDestinationFloat+"\n");
        }
        else // if(emplacementSource instanceof AdressePile)
        {
            loadStoreFloat((AdresseMemoire)emplacementDest, NUM_REGISTRE_DESTINATION_FLOAT, false);
        } 
    }
          
       
    /**
     * Visite le noeud e. Dans ce cas, écrit dans le fichier l'instruction fneg correspondant au noeud fneg e.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(FNegAsml e) {
        visitInstructionFloatVersFloatWorker(e.getE(), "FNEGS");     
    }

    /**
     * Méthode factorisant la génération de code des noeuds ASML héritant de OperateurArithmetiqueFloatAsml (fadd, fsub, fmul et fdiv)
     * @param e le noeud à visiter
     * @param nomInstruction le nom de l'instruction correspondant à l'opérateur arithmétique e
     */
    private void visitOperateurArithmetiqueFloatWorker(OperateurArithmetiqueFloatAsml e, String nomInstruction)
    {
        transfertVersRegFloat(NUM_REGISTRE_OPERANDE1_FLOAT, e.getE1());
        transfertVersRegFloat(NUM_REGISTRE_OPERANDE2_FLOAT, e.getE2());
        ecrireAvecIndentation(nomInstruction+" "+strRegFloat(NUM_REGISTRE_DESTINATION_FLOAT)+", "+strRegFloat(NUM_REGISTRE_OPERANDE1_FLOAT)+", "+strRegFloat(NUM_REGISTRE_OPERANDE2_FLOAT)+"\n");
        transfertDestinationFloat();      
    }
    
    /**
     * Visite le noeud e. Dans ce cas, écrit dans le fichier l'instruction fadd correspondant au noeud fadd e.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(FAddAsml e) {
        visitOperateurArithmetiqueFloatWorker(e, "FADDS");
    }

    /**
     * Visite le noeud e. Dans ce cas, écrit dans le fichier l'instruction fsub correspondant au noeud fsub e.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(FSubAsml e) {
        visitOperateurArithmetiqueFloatWorker(e, FSUBS);
    }

    /**
     * Visite le noeud e. Dans ce cas, écrit dans le fichier l'instruction fmul correspondant au noeud fmul e.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(FMulAsml e) {
        visitOperateurArithmetiqueFloatWorker(e, "FMULS");
    }

    /**
     * Visite le noeud e. Dans ce cas, écrit dans le fichier l'instruction fdiv correspondant au noeud fdiv e.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(FDivAsml e) {
        visitOperateurArithmetiqueFloatWorker(e, "FDIVS");
    }

    /**
     * Visite le noeud e. Dans ce cas, transforme le noeud CallClosureAsml en un noeud call avec %self comme premier paramètre supplémentaire
     * @param e le noeud à visiter
     */
    @Override
    public void visit(CallClosureAsml e) {
        List<VarAsml> arguments = e.getArguments();
        VarAsml var = e.getVar();
        arguments.add(0, var);
        CallAsml call = new CallAsml(var.getIdString(), arguments);
        call.accept(this);
    }
    
    /**
     * Visite le noeud e. Dans ce cas, appelle la méthode visitCallWorker en lui passant en paramètre un noeud CallAsml appelant la fonction new avec en paramètre
     * le résultat de la méthode getE de e. Le paramètre est passé de la méthode façon que pour les appels de fonctions si c'est une variable et en utilisant le
     * deuxième paramètre de visitCallWorker si c'est un entier.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(NewAsml e) {
        List<VarAsml> parametres = new ArrayList<>();
        VarOuIntAsml parametre = e.getE();
        if(parametre instanceof IntAsml)
        {
            parametres.add(null);
        }
        else // if(parametre instanceof VarAsml)
        {
            parametres.add((VarAsml)parametre);
            parametre = null;
        }
        visitCallWorker(new CallAsml(Constantes.NEW_ARM, parametres), (IntAsml)parametre);
    }

    /**
     * Renvoie vrai si l'entier decalage est une valeur immédiate valide pour le décalage d'une instruction LDR ou STR (par exemple dans LDR R0, [FP, #-4], le décalage
     * est -4) et faux sinon
     * @param decalage le décalage de l'adresse accédée par l'instruction LDR ou STR
     * @return vrai si l'entier decalage est une valeur immédiate valide pour le décalage d'une instruction LDR ou STR (par exemple dans LDR R0, [FP, #-4], le décalage
     * est -4) et faux sinon
     */
    private boolean estDecalageImmediatLoadStoreValide(int decalage)
    {
        return (decalage >= MIN_DECALAGE_LOAD_STORE && decalage <= MAX_DECALAGE_LOAD_STORE);
    }
    
    /**
     * Renvoie la représentation sous forme de chaîne du registre dans lequel est stocké var (ou du registre de numéro registreChargement si la variable est sur la pile)
     * (par exemple, R0 est renvoyé si var est dans R0)
     * @param var la variable
     * @param registreChargement
     * @return la représentation sous forme de chaîne du registre dans lequel est stocké var (ou du registre de numéro registreChargement si la variable est sur la pile)
     */
    private String strVariable(VarAsml var, int registreChargement)
    {
        EmplacementMemoire emplacementVar = emplacementVariable(var.getIdString());
        return strReg((emplacementVar instanceof Registre)?((Registre)emplacementVar).getNumeroRegistre():registreChargement);
    }
    
    /**
     * Méthode factorisant la génération de code des noeud correspondant à un accès en mémoire (par exemple, si l'instruction à écrire
     * est LDR R1, [FP], elle renvoie la chaîne ", [FP")
     * @param e
     * @return une chaîne contenant la fin de l'instruction LDR ou STR pour effectuer l'accès mémoire correspondant à e
     */
    private String visitMemWorker(MemAsml e)
    {
        chargerValeur(e.getTableau(), NUM_REGISTRE_OPERANDE1, Constantes.FP);
        VarOuIntAsml indice = e.getIndice();
        String strDecalage = null;
        if(indice instanceof IntAsml)
        {
            int decalage = ((IntAsml)indice).getValeur()*Constantes.TAILLE_MOT_MEMOIRE;
            if(estDecalageImmediatLoadStoreValide(decalage))
            {
                strDecalage = "#"+decalage;
            }
            else
            {
                ecrireAvecIndentation(LDR+" "+strReg(NUM_REGISTRE_IMMEDIAT_INVALIDE)+", ="+decalage);
                strDecalage = strReg(NUM_REGISTRE_IMMEDIAT_INVALIDE);
            }
        }
        else // if(indice instanceof VarAsml)
        {
            chargerValeur(indice, NUM_REGISTRE_IMMEDIAT_INVALIDE, Constantes.FP);            
            strDecalage = strVariable((VarAsml)e.getIndice(), NUM_REGISTRE_IMMEDIAT_INVALIDE)+", LSL #"+2;
        }
        return", ["+strVariable(e.getTableau(), NUM_REGISTRE_OPERANDE1)+", "+strDecalage+"]\n";
    }
    
    /**
     * Visite le noeud e. Dans ce cas, écrit dans le fichier la ou les instructions correspondant à l'accès mémoire en lecture décrit dans e.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(MemLectureAsml e) {
        String finInstruction = visitMemWorker(e);
        ecrireAvecIndentation(LDR+" ");
        visitDestinationWorker();
        ecrire(finInstruction);
        strDestination();
    }

    /**
     * Visite le noeud e. Dans ce cas, écrit dans le fichier la ou les instructions correspondant à l'accès mémoire en écriture décrit dans e.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(MemEcritureAsml e) {        
        String finInstruction = visitMemWorker(e);    
        chargerValeur(e.getValeurEcrite(), NUM_REGISTRE_OPERANDE2, Constantes.FP);
        ecrireAvecIndentation(STR+" "+strVariable(e.getValeurEcrite(), NUM_REGISTRE_OPERANDE2)+finInstruction);        
        IntAsml.nil().accept(this); // stocke nil dans la destination (nil est represente par l'entier 0)
    }

    /**
     * Visite le noeud e. Dans ce cas, écrit dans le fichier une constante (nombre flottant) dont le nom est renvoyé par la méthode getLabel de e et dont la valeur
     * est renvoyé par la méthode getValeur de e
     * @param e le noeud à visiter
     */
    @Override
    public void visit(LetFloatAsml e) {
        ecrire(e.getLabel()+": .single "+e.getValeur()+"\n"); // e.getValeur() ne peut pas avoir comme valeur Nan, +infini ou -infini (le constructeur de LetFloatAsml le vérifie)
    }
    
    /**
     * Méthode factorisant la génération de code des noeuds héritant de IfAsml
     * @param e le noeud à visiter
     * @param instBranchement l'instruction de branchement conditionnnelle pour aller à la branche else après avoir effectué la comparaison
     */
    private void visitIfWorker(IfAsml e, String instBranchement)
    {       
        String labelElse = Id.genIdStringAvecPrefixe("sinon");
        String labelEndIf = Id.genIdStringAvecPrefixe("finSi");
        ecrireAvecIndentation(instBranchement + " " + labelElse + "\n");
        e.getESiVrai().accept(this);
        ecrireAvecIndentation("B " + labelEndIf + "\n");
        ecrire(LTORG+"\n");
        ecrire(labelElse + ":\n");
        e.getESiFaux().accept(this);
        ecrire(labelEndIf + ":\n");
    }
    
    /**
     * Méthode factorisant la génération de code des noeuds héritant de IfIntAsml
     * @param e le noeud à visiter
     * @param instBranchement l'instruction de branchement conditionnnelle pour aller à la branche else après avoir effectué la comparaison
     */
    private void visitIfIntWorker(IfIntAsml e, String instBranchement) {
        VarAsml op1 = e.getE1();
        VarOuIntAsml op2 = e.getE2();
        chargerValeur(op1, NUM_REGISTRE_OPERANDE1, Constantes.FP);
        chargerValeur(op2, NUM_REGISTRE_OPERANDE2, Constantes.FP);
        ecrireAvecIndentation("CMP ");
        visitOperande1Worker(op1);
        ecrire(", ");
        visitOperande2IntWorker(op2);
        ecrire("\n");
        visitIfWorker(e, instBranchement);
    }
    
    /**
     * Renvoie la représentation sous forme de chaîne du registre dédié aux nombres flottants de numéro numRegFloat (par exemple, renvoie S0 pour 0)
     * @param numRegFloat le numéro du registre
     * @return la représentation sous forme de chaîne du registre dédié aux nombres flottants de numéro numRegFloat (par exemple, renvoie S0 pour 0)
     */
    private String strRegFloat(int numRegFloat)
    {
        return "S"+numRegFloat;
    }
    
    /**
     * Renvoie vrai si l'entier decalage est une valeur immédiate valide pour le décalage d'une instruction FLDS ou FSTS et faux sinon
     * @param decalage le décalage de l'adresse accédée par l'instruction FLDS ou FSTS
     * @return vrai si l'entier decalage est une valeur immédiate valide pour le décalage d'une instruction FLDS ou FSTS et faux sinon
     */
    private boolean estDecalageLoadStoreFloatValide(int decalageEnOctet)
    {
        int decalageIndice = decalageEnOctet/4;
        return(decalageIndice >= MIN_DECALAGE_INDICE_LOAD_STORE_FLOAT && decalageIndice <= MAX_DECALAGE_INDICE_LOAD_STORE_FLOAT);
    }
    
    /**
     * Ecrit dans le fichier de sortie l'instruction (FLDS si estInstructionLoad est vrai ou FSTS sinon) permettant de faire un 
     * transfert entre le registre dédié aux flottants de numéro numReg et l'adresse emplacement.getDecalage()+FP
     * @param emplacement l'adresse mémoire vers ou depuis laquelle faire un transfert
     * @param numReg le numéro du registre vers ou depuis lequel un transfert va être fait avec la mémoire
     * @param estInstructionLoad vrai si l'instruction a écrire est FLDS, faux sinon
     */
    private void loadStoreFloat(AdresseMemoire emplacement, int numReg, boolean estInstructionLoad) {
            String strRegistre = strRegFloat(numReg);
            int decalageEnOctet = emplacement.getDecalage();                     
            if (estDecalageLoadStoreFloatValide(decalageEnOctet))
            {                
                ecrireAvecIndentation((estInstructionLoad?FLDS:"FSTS")+" "+strRegistre+", ["+FP+", #"+decalageEnOctet+"]\n");
            }
            else // si la valeur du decalage est trop grande pour être une valeur immediate, il faut la copier dans un registre
            {
                String strRegDecalage = strReg(NUM_REGISTRE_IMMEDIAT_INVALIDE);
                ecrireAvecIndentation("LDR "+strRegDecalage+", ="+decalageEnOctet+"\n");
                ecrireAvecIndentation("ADD "+strRegDecalage+", "+strRegDecalage+", "+FP+"\n");
                ecrireAvecIndentation((estInstructionLoad?"FLDMIAS":"FSTMIAS")+" "+strRegDecalage+", {"+strRegistre+"}\n");
            }
    }
    
    /**
     * Transfert la valeur de varSource vers le registre dédié aux flottants de numéro numRegDestination
     * @param numRegDestination le numéro du registre destination
     * @param varSource la source (une variable)
     */
    private void transfertVersRegFloat(int numRegDestination, VarAsml varSource)
    {
        EmplacementMemoire emplacementSource = emplacementVariable(varSource.getIdString());
        if(emplacementSource instanceof Registre)
        {
            ecrireAvecIndentation("FMSR "+strRegFloat(numRegDestination)+", "+strReg(((Registre)emplacementSource).getNumeroRegistre())+"\n");
        }
        else // if(emplacementSource instanceof AdressePile)
        {
            loadStoreFloat((AdresseMemoire)emplacementSource, numRegDestination, true);
        }        
    }
    
    /**
     * Méthode factorisant la génération de code des noeuds héritant de IfIntAsml
     * @param e le noeud à visiter
     * @param instBranchement l'instruction de branchement conditionnnelle pour aller à la branche else après avoir effectué la comparaison
     */
    private void visitIfFloatWorker(IfFloatAsml e, String instBranchement) {
        transfertVersRegFloat(NUM_REGISTRE_OPERANDE1_FLOAT, e.getE1());
        transfertVersRegFloat(NUM_REGISTRE_OPERANDE2_FLOAT, e.getE2());        
        ecrireAvecIndentation("FCMPS "+strRegFloat(NUM_REGISTRE_OPERANDE1_FLOAT)+", "+strRegFloat(NUM_REGISTRE_OPERANDE2_FLOAT)+"\n");
        ecrireAvecIndentation("FMSTAT\n");
        visitIfWorker(e, instBranchement);
    }

    /**
     * Visite le noeud e. Dans ce cas, écrit dans le fichier la ou les instructions correspondant au noeud IfEqIntAsml.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(IfEqIntAsml e) {
        visitIfIntWorker(e, "BNE");
    }

    /**
     * Visite le noeud e. Dans ce cas, écrit dans le fichier la ou les instructions correspondant au noeud IfLEIntAsml.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(IfLEIntAsml e) {
        visitIfIntWorker(e, "BGT");
    }

    /**
     * Visite le noeud e. Dans ce cas, écrit dans le fichier la ou les instructions correspondant au noeud IfGEIntAsml.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(IfGEIntAsml e) {
        visitIfIntWorker(e, "BLT");
    }

    /**
     * Visite le noeud e. Dans ce cas, écrit dans le fichier la ou les instructions correspondant au noeud IfEqFloatAsml.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(IfEqFloatAsml e) {
        visitIfFloatWorker(e, "BNE");
    }

    /**
     * Visite le noeud e. Dans ce cas, écrit dans le fichier la ou les instructions correspondant au noeud IfLEFloatAsml.
     * @param e le noeud à visiter
     */
    @Override
    public void visit(IfLEFloatAsml e) {
        visitIfFloatWorker(e, "BHI");
    }

    /**
     * Visiteur permettant de calculer la taille de l'environnement d'une fonction
     */
    private class VisiteurTailleEnvironnement implements VisiteurAsml {        
        private int tailleEnvironnement;
        
        /**
         * Créé un visiteur permettant de calculer la taille de l'environnement d'une fonction
         */
        public VisiteurTailleEnvironnement()
        {
            setTailleEnvironnement(0);
        }
        
        /**
        * Visite le noeud e. Dans ce cas, si l'emplacement de la variable déclarée est une adresse mémoire, définit comme nouvelle taille de l'environnement
        * le maximum entre l'ancienne taille de l'environnement et la valeur absolue du décalage entre l'adresse et FP (la taille de l'environnement d'une fonction
        * est la valeur absolue du décalage minimal de l'adresse de ses variables locales par rapport FP auquel on ajoute 4).
        * @param e le noeud à visiter
        */
        @Override
        public void visit(LetAsml e) {
            EmplacementMemoire emplacementVar = emplacementVariable(e.getIdString());
            if(emplacementVar instanceof AdresseMemoire)
            {
                setTailleEnvironnement(Math.max(tailleEnvironnement, Math.abs(((AdresseMemoire)emplacementVar).getDecalage())+Constantes.TAILLE_MOT_MEMOIRE));
            }
            e.getE1().accept(this);
            e.getE2().accept(this);
        }

        /**
         * Renvoie la taille de l'environnement de la fonction visitée
         * @return la taille de l'environnement de la fonction visitée
         */
        public int getTailleEnvironnement() {
            return tailleEnvironnement;
        }

        /**
         * Définit tailleEnvironnement comme la nouvelle taille de l'environnement de la fonction visitée
         * @param tailleEnvironnement la nouvelle taille de l'environnement de la fonction visitée
         */
        private void setTailleEnvironnement(int tailleEnvironnement) {
            this.tailleEnvironnement = tailleEnvironnement;
        }
    }
}
