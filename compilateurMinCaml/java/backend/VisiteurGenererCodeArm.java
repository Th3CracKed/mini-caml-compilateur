package backend;

import arbreasml.*;
import arbremincaml.*;
import frontend.OptionsGenerationCodeArm;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import util.*;
import visiteur.*;

public class VisiteurGenererCodeArm extends GenerateurDeCode implements VisiteurAsml {
    private static final int TAILLE_ZONE_ALLOCATION_DYNAMIQUE = 100000*Constantes.TAILLE_MOT_MEMOIRE;
    private static final int NUM_REGISTRE_DESTINATION = 4;
    private static final int NUM_REGISTRE_OPERANDE1 = 4;
    private static final int NUM_REGISTRE_OPERANDE2 = 5;
    private static final int NUM_REGISTRE_IMMEDIAT_INVALIDE = 6;
    private static final int NUM_REGISTRE_SAUVEGARDE_VALEUR_RETOUR = 7;
    //private static final int NUM_REGISTRE_PROCHAINE_ADRESSE_ALLOUEE = 8;
    
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
    private static final String LTORG = ".ltorg";

    private static final int MAX_DECALAGE_LOAD_STORE = (int) Math.pow(2, 12) - 1;
    private static final int MIN_DECALAGE_LOAD_STORE = -MAX_DECALAGE_LOAD_STORE; // un bit est reserve pour le signe et la valeur absolue est codee sur 12 bit pour les decalages immédiats utilisés comme ceci : [Rn, #decalage]
    
    private static final int MAX_DECALAGE_INDICE_LOAD_STORE_FLOAT = (int) Math.pow(2, 8) - 1;
    private static final int MIN_DECALAGE_INDICE_LOAD_STORE_FLOAT = -MAX_DECALAGE_LOAD_STORE; // un bit est reserve pour le signe et la valeur absolue est codee sur 8 bit pour les decalages immédiats utilisés comme ceci : [Rn, #decalage]

    //private static final int MIN_IMMEDIAT_SHIFTER_OPERAND = -(int) Math.pow(2, 7);
    //private static final int MAX_IMMEDIAT_SHIFTER_OPERAND = -MIN_IMMEDIAT_SHIFTER_OPERAND-1; // toutes les valeurs sur 8 bits sont valides pour le shifter operand (entre -2^7 et 2^7-1)
    
    
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

    public VisiteurGenererCodeArm(HashMap<String, EmplacementMemoire> emplacementsMemoire, PrintStream fichierSortie, OptionsGenerationCodeArm optionsGenCodeArm) {
        super(fichierSortie);
        this.optionsGenCodeArm = optionsGenCodeArm;
        this.emplacementsMemoire = emplacementsMemoire;        
        this.emplacementDestination = new Stack();
        changerDestination(new Registre(0));
        pileEstInstructionMov = new Stack<>();
    }

    private String strReg(int numReg) {
        String nomRegSpecial = REGISTRE_VERS_CHAINE.get(numReg);
        return (nomRegSpecial == null) ? "R" + numReg : nomRegSpecial;
    }

    private void changerEstInstructionMov(boolean estInstReturn) {
        pileEstInstructionMov.push(estInstReturn && (pileEstInstructionMov.isEmpty() || estInstructionMov()));
    }

    private void restaurerEstInstructionMov() {
        pileEstInstructionMov.pop();
    }

    private boolean estInstructionMov() {
        return pileEstInstructionMov.peek();
    }

    public void setNumRegistre(int numRegistre) {
        this.numRegistre = numRegistre;
    }

    private void visitEmplacementVarWorker(EmplacementMemoire emplacementVar) {
        int numReg = (emplacementVar instanceof Registre) ? ((Registre) emplacementVar).getNumeroRegistre() : numRegistre;
        ecrire(strReg(numReg));
    }

    private void changerDestination(EmplacementMemoire emplacement) {
        this.emplacementDestination.push(emplacement);
    }

    private EmplacementMemoire destination() {
        return emplacementDestination.peek();
    }

    private EmplacementMemoire restaurerDestination() {
        return emplacementDestination.pop();
    }

    private void visitDestinationWorker() {
        changerEstInstructionMov(false);
        setNumRegistre(NUM_REGISTRE_DESTINATION);
        visitEmplacementVarWorker(destination());
        restaurerEstInstructionMov();
    }

    private void visitOperande1Worker(VarAsml e) {
        changerEstInstructionMov(false);
        setNumRegistre(NUM_REGISTRE_OPERANDE1);
        e.accept(this);
        restaurerEstInstructionMov();
    }

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
                /*System.out.println(String.format("i >> 24 : 0x%08X, i << 8 : 0x%08X", (valeur >> i), (valeur << (numDernierBit-i+1))));
                System.out.println(i+"tournee : "+String.format("0x%08X", valeurTournee));*/
                return true;
            }
        }
        return false;
        // les valeurs immediates valides pour le shifter operand (dernier parametre des instructions comme add, sub, mov, cmp...) sont toutes les valeurs
        // pouvant etre obtenues en faisant subit a une valeur sur 1 octet (entre -2^7 et 2^7-1) une rotation droite d'un nombre pair de rang
        // par exemple 0xFF000000 est valide car il devient apres une rotation droite de 8 rangs 0x000000FF, qui est une valeur qui tient sur un octet 
        // mais 0xFFFFFFFF est invalide (il ne peut pas tenir sur un octet) ni 0x1FE00000 (il ne pourrait tenir sur un octet qu'avec une rotation d'un nombre impair de rang)
        // pour savoir si le nombre est valide, il faut qu'il ait au moins 25 bit à zéros consécutif
        // ou 24 bits à zéro consécutifs avec en plus le numéro de bit du premier zéro (en partant de la gauche) pair (les bits sont numérotés de 0 à 31 de droites à gauche)
        // Par exemple, la valeur 0x0FFFFFF0 à 8 bits à zéro consécutifs (4 au début et 4 à la fin) et le premier zéro à l'indice 28
        /*int nbZerosConsecutifs = 0;
        int indicePremierZero = 0;
        int indicePremierZeroMax = 0;
        int nbMaxZerosConsecutifs = 0;     
        int nbZerosConsecutifsPoidsFaible = 0;
        int nbZerosConsecutifsPoidsFort = 0;
        int masque = 0b1;
        int nbBitsEntier = Constantes.TAILLE_MOT_MEMOIRE*8;
        for(int i = 0 ; i < nbBitsEntier ; i++)
        {
            if((valeur & (masque << i)) == 0)
            {
                nbZerosConsecutifs++;
                if(nbZerosConsecutifs > nbMaxZerosConsecutifs)
                {
                    nbMaxZerosConsecutifs = nbZerosConsecutifs;
                    indicePremierZeroMax = indicePremierZero;
                }
                if(nbMaxZerosConsecutifs == (i+1))
                {
                    nbZerosConsecutifsPoidsFaible = nbZerosConsecutifs;
                }
                else if(i == nbBitsEntier-1)
                {
                    nbZerosConsecutifsPoidsFort = nbZerosConsecutifs;
                }
            }
            else
            {
                nbZerosConsecutifs = 0;
                indicePremierZero = i+1;
            }
        }
        int nbZerosPoidsFaibleEtFort = nbZerosConsecutifsPoidsFaible + nbZerosConsecutifsPoidsFort;
        if(nbZerosPoidsFaibleEtFort > nbMaxZerosConsecutifs)
        {
            nbMaxZerosConsecutifs = nbZerosPoidsFaibleEtFort;
            indicePremierZeroMax = nbBitsEntier - nbZerosPoidsFaibleEtFort - 1;
        }
        int nbZeroPourEtreValide = nbBitsEntier - 8; // 8 correspond au nombre de bits utilises pour stocker la valeur du shifter operand (valeur à laquelle on peut faire subir une rotation droite d'un nombre pair de rangs)
        boolean resultat = (nbMaxZerosConsecutifs >= (nbZeroPourEtreValide+1) || (nbMaxZerosConsecutifs == nbZeroPourEtreValide && (indicePremierZeroMax%2==0)));
        //System.out.println(String.format("le nombre 0x%08X (%d) est il un shifter operand valide ?"+nbMaxZerosConsecutifs+" "+resultat, valeur, valeur));
        return resultat;*/
        //return (valeur >= MIN_IMMEDIAT_SHIFTER_OPERAND && valeur <= MAX_IMMEDIAT_SHIFTER_OPERAND);
    }
    
    private void chargerValeurImmediateLDR(int numRegistre, int valeurDOrigine)
    {
        ecrireAvecIndentation("LDR " + strReg(numRegistre) + ", =" + valeurDOrigine + "\n");
    }
    
    private void chargerValeurImmediateLDR(int valeurDOrigine) {
        chargerValeurImmediateLDR(NUM_REGISTRE_IMMEDIAT_INVALIDE, valeurDOrigine);
        /*int masque = 0x000000FF;
        boolean premiereInstruction = true;
        final int numDernierOctet = Constantes.TAILLE_MOT_MEMOIRE-1;
        String strRegistre = strReg(NUM_REGISTRE_IMMEDIAT_INVALIDE);
        for (int i = 0; i <= numDernierOctet; i++) {
            int valeurImm = (valeurDOrigine & (masque << i * 8));
            if (valeurImm != 0 || (i == numDernierOctet && premiereInstruction)) {
                if (premiereInstruction) {
                    ecrireAvecIndentation("MOV " + strRegistre + ", #" + valeurImm + "\n");
                    premiereInstruction = false;
                } else {
                    ecrireAvecIndentation("ADD " + strRegistre + ", " + strRegistre + ", #" + valeurImm + "\n");
                }
            }
        }*/
    }
        
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
    
    private void loadStoreWorker(EmplacementMemoire emplacement, int numReg, String nomInstruction, int numRegBase) {
        if (emplacement instanceof AdressePile) {
            int decalage = ((AdressePile) emplacement).getDecalage();
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
                //strDecalage = strReg(NUM_REGISTRE_IMMEDIAT_INVALIDE);
                chargerValeurImmediateLDR(valeur);
            }
        }
    }

    private void str(EmplacementMemoire emplacement, int numReg, int numRegBase) {
        loadStoreWorker(emplacement, numReg, STR, numRegBase);
    }

    private void strDestination() {
        str(destination(), NUM_REGISTRE_DESTINATION, Constantes.FP);
    }

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

    private EmplacementMemoire emplacementVariable(String idString) {
        return emplacementsMemoire.get(idString);
    }

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

    @Override
    public void visit(AddAsml e) {
        visitOpArithmetiqueIntWorker(e, "ADD");
    }

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
    
    @Override
    public void visit(FunDefConcreteAsml e) {
        if (e.estMainFunDef()) {
            ecrire(Constantes.NOM_FONCTION_MAIN_ARM);
        } else {
            ecrire(e.getLabel());
        }
        ecrire(":\n");
        augmenterNiveauIndentation();
        /*if (e.estMainFunDef()) {
            ajouterValeurASP(-TAILLE_ZONE_ALLOCATION_DYNAMIQUE);
            ecrireAvecIndentation("MOV "+strReg(NUM_REGISTRE_PROCHAINE_ADRESSE_ALLOUEE)+", "+SP+"\n");
        }*/
        changerEstInstructionMov(true);
        int tailleEnvironnement = e.accept(new VisiteurTailleEnvironnement())/* + 4 * REGISTRE_SAUVEGARDES_APPELE.length*/;
        if(Constantes.REGISTRE_SAUVEGARDES_APPELE.length >= 1)
        {
            ecrireAvecIndentation("PUSH {");
            for (int i = 0; i < Constantes.REGISTRE_SAUVEGARDES_APPELE.length; i++) {
                if(i >= 1)
                {
                    ecrire(", ");
                }
                ecrire(strReg(Constantes.REGISTRE_SAUVEGARDES_APPELE[i]));
                //loadStoreWorker(new AdressePile((i+1) * Constantes.TAILLE_MOT_MEMOIRE), REGISTRE_SAUVEGARDES_APPELE[i], STR, Constantes.SP);
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
                //loadStoreWorker(new AdressePile((i+1) * Constantes.TAILLE_MOT_MEMOIRE), REGISTRE_SAUVEGARDES_APPELE[i], STR, Constantes.SP);
            }
            ecrire("}\n");
        }
        restaurerEstInstructionMov();
        if (e.estMainFunDef()) {
            //ajouterValeurASP(TAILLE_ZONE_ALLOCATION_DYNAMIQUE);            
            ecrireAvecIndentation("B "+Constantes.EXIT_ARM+"\n");
        }
        else
        {            
            ecrireAvecIndentation("BX LR\n");
        }
        ecrire(LTORG+"\n\n");
        diminuerNiveauIndentation();
    }
    
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
                //loadStoreWorker(new AdressePile((tailleAEmpiler - i * Constantes.TAILLE_MOT_MEMOIRE)), REGISTRE_SAUVEGARDES_APPELANT[i], STR, Constantes.SP);
            }
            ecrire("}\n");
        }
        int nbParametres = e.getArguments().size();
        int tailleParametresAEmpiler = (/*REGISTRE_SAUVEGARDES_APPELANT.length + */Math.max(0, (nbParametres - Constantes.REGISTRES_PARAMETRES.length))) * Constantes.TAILLE_MOT_MEMOIRE;
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
                AdressePile adressePileParamPasseEnParam = null;
                if(emplacementParam instanceof Registre && Arrays.<Integer>asList(Constantes.REGISTRES_PARAMETRES).contains(((Registre)emplacementParam).getNumeroRegistre()))
                {
                    // pour le programme let rec f x y = let rec g z t = z - t in g y x in f 1 2, g passe ses parametres dans l'ordre inverse, et si on ne gere pas ce cas, 
                    // le code genere serait MOV R0, R1; MOV R1, R0 (qui est faux car cela correspond a l'appel g y y). Pour eviter cela lorsqu'une fonction passe ses
                    // parametres a une autre, les valeurs des parametres sont chargee depuis la pile (elles sont presentes pour la sauvegarde du contexte par l'appelant
                    int indRegSauvegardeAppelant = Arrays.<Integer>asList(Constantes.REGISTRE_SAUVEGARDES_APPELANT).indexOf(((Registre)emplacementParam).getNumeroRegistre());
                    adressePileParamPasseEnParam = new AdressePile(indRegSauvegardeAppelant * Constantes.TAILLE_MOT_MEMOIRE+tailleParametresAEmpiler);
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
                        loadStoreWorker(new AdressePile((nbParametres-1-i)*Constantes.TAILLE_MOT_MEMOIRE), NUM_REGISTRE_OPERANDE1, STR, Constantes.SP);
                    }
                    else
                    {
                        chargerValeur(e.getArguments().get(i), NUM_REGISTRE_OPERANDE1, Constantes.FP);
                        int numReg = (emplacementParam instanceof Registre)?((Registre)emplacementParam).getNumeroRegistre():NUM_REGISTRE_OPERANDE1;
                        loadStoreWorker(new AdressePile((nbParametres-1-i)*Constantes.TAILLE_MOT_MEMOIRE), numReg, STR, Constantes.SP);
                    }  
                    //loadStoreWorker(new AdressePile((e.getArguments().size()-i-1) * Constantes.TAILLE_MOT_MEMOIRE), NUM_REGISTRE_OPERANDE1, STR, Constantes.SP);
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
        //strDestination();        
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
                //loadStoreWorker(new AdressePile((tailleAEmpiler - i * Constantes.TAILLE_MOT_MEMOIRE)), REGISTRE_SAUVEGARDES_APPELANT[i], STR, Constantes.SP);
            }
            ecrire("}\n");
        }
        ecrireAvecIndentation("MOV ");
        visitDestinationWorker();
        ecrire(", " + strReg(NUM_REGISTRE_SAUVEGARDE_VALEUR_RETOUR) + "\n");
        strDestination();
    }
    
    private void visitInstructionFloatVersFloatWorker(VarAsml param0, String nomInstruction)
    {
        transfertVersRegFloat(NUM_REGISTRE_OPERANDE1_FLOAT, param0);
        ecrireAvecIndentation(nomInstruction+" "+strRegFloat(NUM_REGISTRE_DESTINATION_FLOAT)+", "+strRegFloat(NUM_REGISTRE_OPERANDE1_FLOAT)+"\n");
        transfertDestinationFloat();   
    }
    
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

    @Override
    public void visit(IntAsml e) {
        //chargerValeur(e, -1, -1);
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

    @Override
    public void visit(NopAsml e) {
        IntAsml.nil().accept(this);
    }

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
            //ecrireAvecIndentation("STR R1, [R3]            @ initialise le prochain mot memoire avec la valeur du deuxieme parametre de la fonction\n");
            ecrireAvecIndentation("SUB R2, R2, #"+Constantes.TAILLE_MOT_MEMOIRE+"            @ decremente la taille restante a initialiser de 4\n");
            //ecrireAvecIndentation("ADD R3, R3, #"+Constantes.TAILLE_MOT_MEMOIRE+"            @ stocke l'adresse du prochain mot memoire a initialiser dans r3 \n");
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
        float[] coefficientsPolynome = new float[]{1.0f, -0.5f, 0.041666668f, -0.0013888889f, 2.4801588E-5f, -2.755732E-7f, 2.0876758E-9f}; // abs(cos(x)-(x^0*1.0+x^2*-0.5+x^4*0.041666668+x^6*-0.0013888889+x^8*2.4801588E-5+x^10*-2.755732E-7+x^12*2.0876758E-9)) <= 2^-23 si -pi/2 <= x <= pi/2 (2^-23 est la précision (plus petite valeur strictement positive) des flottants simple précision). Ce polynôme est la somme des monomes de degre inférieur ou égal à 12 du développement en série entière de cos(x)
        if(optionsGenCodeArm.getUtiliseSinOuCos())
        {
            int decalageDernierElement = (coefficientsPolynome.length - 1)*Constantes.TAILLE_MOT_MEMOIRE;
            ecrire(Constantes.SIN_ARM+":\n");
            augmenterNiveauIndentation();
            ecrireAvecIndentation("FMSR S0, R0\n");
            ecrireAvecIndentation("LDR R0, ="+Constantes.PI_SUR_2_ARM+"\n");
            ecrireAvecIndentation("FLDS S1, [R0]\n");
            ecrireAvecIndentation("FSUBS S0, S0, S1\n");
            ecrireAvecIndentation("FMRS R0, S0\n");  
            diminuerNiveauIndentation();
            ecrire(Constantes.COS_ARM+":\n");
            augmenterNiveauIndentation();
            ecrireAvecIndentation("FMSR S0, R0\n");
            ecrireAvecIndentation("LDR R0, ="+Constantes.DEUX_PI_ARM+"\n");
            ecrireAvecIndentation("FLDS S1, [R0]\n");
            ecrireAvecIndentation("FDIVS S2, S0, S1\n");
            ecrireAvecIndentation("FTOSIS S2, S2\n");
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
            ecrireAvecIndentation("BLT "+Constantes.SIN_FIN_SI_2_ARM+"\n"); // sinFinSi2        
            ecrireAvecIndentation("LDR R0, ="+Constantes.TROIS_PI_SUR_2_ARM+"\n");
            ecrireAvecIndentation("FLDS S2, [R0]\n");
            ecrireAvecIndentation("FCMPS S0, S2\n");    
            ecrireAvecIndentation("FMSTAT\n");
            ecrireAvecIndentation("BLT "+Constantes.SIN_SINON_ARM+"\n"); // sinSinon1
            ecrireAvecIndentation("FSUBS S0, S0, S1\n");
            ecrireAvecIndentation("B "+Constantes.SIN_FIN_SI_2_ARM+"\n");
            ecrire(Constantes.SIN_SINON_ARM+":\n");
            ecrireAvecIndentation("LDR R0, ="+Constantes.PI_ARM+"\n");
            ecrireAvecIndentation("FLDS S1, [R0]\n");
            ecrireAvecIndentation("FSUBS S0, S1, S0\n");
            ecrireAvecIndentation("MOV R1, #1          @ R1 = 1\n");            
            ecrire(Constantes.SIN_FIN_SI_2_ARM+":\n");
            ecrireAvecIndentation("LDR R3, ="+Constantes.COEFFICIENTS_POLYNOME_SIN_ARM+"\n");
            ecrireAvecIndentation("MOV R2, R3\n");
            ecrireAvecIndentation("ADD R2, R2, #"+decalageDernierElement+"\n");
            ecrireAvecIndentation("FLDMIAS R2, {S1}\n");
            ecrireAvecIndentation("SUB R2, R2, #"+Constantes.TAILLE_MOT_MEMOIRE+"\n");
            ecrireAvecIndentation("FMULS S0, S0, S0\n");
            ecrire(Constantes.SIN_TANT_QUE_ARM+":\n");
            ecrireAvecIndentation("CMP R2, R3\n");
            ecrireAvecIndentation("BLT "+Constantes.SIN_FIN_TANT_QUE_ARM+"\n"); // sinFinTantQue
            ecrireAvecIndentation("FMULS S1, S1, S0\n");
            ecrireAvecIndentation("FLDMIAS R2, {S2}\n");
            ecrireAvecIndentation("SUB R2, R2, #"+Constantes.TAILLE_MOT_MEMOIRE+"\n");
            ecrireAvecIndentation("FADDS S1, S1, S2\n");
            ecrireAvecIndentation("B "+Constantes.SIN_TANT_QUE_ARM+"\n"); // sinTantQue        
            ecrire(Constantes.SIN_FIN_TANT_QUE_ARM+":\n");
            ecrireAvecIndentation("CMP R1, #0\n");
            ecrireAvecIndentation("BEQ "+Constantes.SIN_FIN_SI_3_ARM+"\n");  // sinFinSi3  
            ecrireAvecIndentation("FNEGS S1, S1\n");
            ecrire(Constantes.SIN_FIN_SI_3_ARM+":\n");
            ecrireAvecIndentation("FMRS R0, S1\n");
            ecrireAvecIndentation("BX LR            @aller a l'adresse dans lr (instruction return)\n");
            ecrire(LTORG+"\n\n");
            /*
                FMSR S0, R0 @ S0 = R0
    LDR R0, =_2pi          @ R0 = _2pi
    FLDS S1, [R0]          @ S1 = *R0
    FDIVS S2, S0, S1          @ S2 = S0/S1
    FTOSIZS S2, S2          @ S2 = (int)S2
    FSITOS S2, S2          @ S2 = (float)S2
    FMULS S2, S2, S1          @ S2 *= S1
    FSUBS S0, S0, S2          @ S0 -= S2
    MOV R1, #0          @ R1 = 0
    FCMPZS S0          @ comparer S0 a 0
    BGT sinFinSi1          @ si S0 > 0 aller a sinFinSi1
    FNEGS S0, S0          @ S0 *= -1
sinFinSi1:
    LDR R0, =_piSur2          @ R0 = _piSur2
    FLDS S2, [R0]          @ S2 = *R0
    FCMPS S0, S2          @ comparer S0 a S2
    BMI sinFinSi2          @ si S0 < S2 aller a sinFinSi2
    LDR R0, =_3PiSur2          @ R0 = _3piSur2
    FLDS S2, [R0]          @ S2 = *R0
    FCMPS S0, S2          @ comparer S0 a S2
    BMI sinSinon          @ si S0 < S2 aller a sinSinon
    FSUBS S0, S0, S1          @ S0 -= S1
    B sinFinSi2          @ aller a sinFinSi2
sinSinon:
    LDR R0, =_pi          @ R0 = _pi
    FLDS S1, [R0]          @ S1 = *R0
    FSUBS S0, S1, S0          @ S0 = S1 - S0
    MOV R1, #1          @ R1 = 1
            */
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
        if(optionsGenCodeArm.getUtiliseSinOuCos() || optionsGenCodeArm.getUtiliseNewOuCreateArray())
        {
            ecrire(".data\n");
            ecrire(".balign "+Constantes.TAILLE_MOT_MEMOIRE+"\n");
        }
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

    @Override
    public void visit(SubAsml e) {
        visitOpArithmetiqueIntWorker(e, "SUB");
    }
    
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
            loadStoreFloat((AdressePile)emplacementDest, NUM_REGISTRE_DESTINATION_FLOAT, false);
        } 
    }
          
        
    @Override
    public void visit(FNegAsml e) {
        visitInstructionFloatVersFloatWorker(e.getE(), "FNEGS");     
    }

    private void visitOperateurArithmetiqueFloatWorker(OperateurArithmetiqueFloatAsml e, String nomInstruction)
    {
        transfertVersRegFloat(NUM_REGISTRE_OPERANDE1_FLOAT, e.getE1());
        transfertVersRegFloat(NUM_REGISTRE_OPERANDE2_FLOAT, e.getE2());
        ecrireAvecIndentation(nomInstruction+" "+strRegFloat(NUM_REGISTRE_DESTINATION_FLOAT)+", "+strRegFloat(NUM_REGISTRE_OPERANDE1_FLOAT)+", "+strRegFloat(NUM_REGISTRE_OPERANDE2_FLOAT)+"\n");
        transfertDestinationFloat();      
    }
    
    @Override
    public void visit(FAddAsml e) {
        visitOperateurArithmetiqueFloatWorker(e, "FADDS");
    }

    @Override
    public void visit(FSubAsml e) {
        visitOperateurArithmetiqueFloatWorker(e, FSUBS);
    }

    @Override
    public void visit(FMulAsml e) {
        visitOperateurArithmetiqueFloatWorker(e, "FMULS");
    }

    @Override
    public void visit(FDivAsml e) {
        visitOperateurArithmetiqueFloatWorker(e, "FDIVS");
    }

    @Override
    public void visit(CallClosureAsml e) {
        List<VarAsml> arguments = e.getArguments();
        VarAsml var = e.getVar();
        arguments.add(0, var);
        CallAsml call = new CallAsml(var.getIdString(), arguments);
        call.accept(this);
    }
    
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

    private boolean estDecalageImmediatLoadStoreValide(int decalage)
    {
        return (decalage >= MIN_DECALAGE_LOAD_STORE && decalage <= MAX_DECALAGE_LOAD_STORE);
    }
    
    private String strVariable(VarAsml var, int registreChargement)
    {
        EmplacementMemoire emplacementVar = emplacementVariable(var.getIdString());
            return strReg((emplacementVar instanceof Registre)?((Registre)emplacementVar).getNumeroRegistre():registreChargement);
    }
    
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
    
    @Override
    public void visit(MemLectureAsml e) {
        String finInstruction = visitMemWorker(e);
        ecrireAvecIndentation(LDR+" ");
        visitDestinationWorker();
        ecrire(finInstruction);
        strDestination();
    }

    @Override
    public void visit(MemEcritureAsml e) {        
        String finInstruction = visitMemWorker(e);    
        //System.out.println("valeur ecrite : "+emplacementVariable(e.getValeurEcrite().getIdString()));
        chargerValeur(e.getValeurEcrite(), NUM_REGISTRE_OPERANDE2, Constantes.FP);
        ecrireAvecIndentation(STR+" "+strVariable(e.getValeurEcrite(), NUM_REGISTRE_OPERANDE2)+finInstruction);        
        IntAsml.nil().accept(this); // stocke nil dans la destination (nil est represente par l'entier 0)
    }

    @Override
    public void visit(LetFloatAsml e) {
        ecrire(e.getLabel()+": .single "+e.getValeur()+"\n"); // e.getValeur() ne peut pas avoir comme valeur Nan, +infini ou -infini (le constructeur de FloatMinCaml le vérifie)
    }
    
    private void visitIfWorker(IfAsml e, String instBranchement)
    {       
        String labelElse = Id.genIdStringAvecPrefixe("sinon");
        String labelEndIf = Id.genIdStringAvecPrefixe("finSi");
        ecrireAvecIndentation(instBranchement + " " + labelElse + "\n");
        /*if(instBranchement.length == 1)
        {
            ecrireAvecIndentation(instBranchement[0] + " " + labelElse + "\n");
        }
        else
        {
            String labelIf = Id.genIdStringAvecPrefixe("si");
            for(int i = 0 ; i < instBranchement.length ; i++)
            {
                ecrireAvecIndentation(instBranchement[i] + " " + labelIf + "\n");
            }
            ecrireAvecIndentation("B " + labelElse + "\n");
        }*/
        e.getESiVrai().accept(this);
        ecrireAvecIndentation("B " + labelEndIf + "\n");
        ecrire(labelElse + ":\n");
        e.getESiFaux().accept(this);
        ecrire(labelEndIf + ":\n");
    }
    
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
    
    private String strRegFloat(int numRegFloat)
    {
        return "S"+numRegFloat;
    }
    
    private boolean estDecalageLoadStoreFloatValide(int decalageEnOctet)
    {
        int decalageIndice = decalageEnOctet/4;
        return(decalageIndice >= MIN_DECALAGE_INDICE_LOAD_STORE_FLOAT && decalageIndice <= MAX_DECALAGE_INDICE_LOAD_STORE_FLOAT);
    }
    
    private void loadStoreFloat(AdressePile emplacement, int numReg, boolean estInstructionLoad) {
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
    
    private void transfertVersRegFloat(int numRegDestination, VarAsml varSource)
    {
        EmplacementMemoire emplacementSource = emplacementVariable(varSource.getIdString());
        if(emplacementSource instanceof Registre)
        {
            ecrireAvecIndentation("FMSR "+strRegFloat(numRegDestination)+", "+strReg(((Registre)emplacementSource).getNumeroRegistre())+"\n");
        }
        else // if(emplacementSource instanceof AdressePile)
        {
            loadStoreFloat((AdressePile)emplacementSource, numRegDestination, true);
        }        
    }
    
    private void visitIfFloatWorker(IfFloatAsml e, String instBranchement) {
        transfertVersRegFloat(NUM_REGISTRE_OPERANDE1_FLOAT, e.getE1());
        transfertVersRegFloat(NUM_REGISTRE_OPERANDE2_FLOAT, e.getE2());        
        ecrireAvecIndentation("FCMPS "+strRegFloat(NUM_REGISTRE_OPERANDE1_FLOAT)+", "+strRegFloat(NUM_REGISTRE_OPERANDE2_FLOAT)+"\n");
        ecrireAvecIndentation("FMSTAT\n");
        visitIfWorker(e, instBranchement);
    }

    @Override
    public void visit(IfEqIntAsml e) {
        visitIfIntWorker(e, "BNE");
    }

    @Override
    public void visit(IfLEIntAsml e) {
        visitIfIntWorker(e, "BGT");
    }

    @Override
    public void visit(IfGEIntAsml e) {
        visitIfIntWorker(e, "BLT");
    }

    @Override
    public void visit(IfEqFloatAsml e) {
        visitIfFloatWorker(e, "BNE");
    }

    @Override
    public void visit(IfLEFloatAsml e) {
        visitIfFloatWorker(e, "BHI");
    }

    private class VisiteurTailleEnvironnement implements ObjVisiteurAsml<Integer> {

        @Override
        public Integer visit(AddAsml e) {
            return 0;
        }

        @Override
        public Integer visit(FunDefConcreteAsml e) {
            return e.getAsmt().accept(this);
        }

        @Override
        public Integer visit(IntAsml e) {
            return 0;
        }

        @Override
        public Integer visit(LetAsml e) {
            return Constantes.TAILLE_MOT_MEMOIRE + Math.max(e.getE1().accept(this), e.getE2().accept(this));
        }

        @Override
        public Integer visit(NegAsml e) {
            return 0;
        }

        @Override
        public Integer visit(NopAsml e) {
            return 0;
        }

        @Override
        public Integer visit(ProgrammeAsml e) {
            int resultat = e.getMainFunDef().accept(this);
            for (FunDefAsml funDef : e.getFunDefs()) {
                resultat += funDef.accept(this);
            }
            return resultat;
        }

        @Override
        public Integer visit(SubAsml e) {
            return 0;
        }

        @Override
        public Integer visit(VarAsml e) {
            return 0;
        }

        @Override
        public Integer visit(NewAsml e) {
            return 0;
        }

        @Override
        public Integer visit(FNegAsml e) {
            return 0;
        }

        @Override
        public Integer visit(FAddAsml e) {
            return 0;
        }

        @Override
        public Integer visit(FSubAsml e) {
            return 0;
        }

        @Override
        public Integer visit(FMulAsml e) {
            return 0;
        }

        @Override
        public Integer visit(FDivAsml e) {
            return 0;
        }

        @Override
        public Integer visit(CallAsml e) {
            return 0;
        }

        @Override
        public Integer visit(CallClosureAsml e) {
            return 0;
        }

        @Override
        public Integer visit(MemLectureAsml e) {
            return 0;
        }

        @Override
        public Integer visit(MemEcritureAsml e) {
            return 0;
        }

        @Override
        public Integer visit(LetFloatAsml e) {
            return Constantes.TAILLE_MOT_MEMOIRE;
        }

        private Integer visitIfWorker(IfAsml e) {
            return Math.max(e.getESiVrai().accept(this), e.getESiFaux().accept(this));
        }

        @Override
        public Integer visit(IfEqIntAsml e) {
            return visitIfWorker(e);
        }

        @Override
        public Integer visit(IfLEIntAsml e) {
            return visitIfWorker(e);
        }

        @Override
        public Integer visit(IfGEIntAsml e) {
            return visitIfWorker(e);
        }

        @Override
        public Integer visit(IfEqFloatAsml e) {
            return visitIfWorker(e);
        }

        @Override
        public Integer visit(IfLEFloatAsml e) {
            return visitIfWorker(e);
        }

    }
}
