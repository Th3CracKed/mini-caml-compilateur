package backend;

import arbreasml.*;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import util.Constantes;
import util.GenerateurDeCode;
import util.NotYetImplementedException;
import visiteur.ObjVisiteurAsml;
import visiteur.VisiteurAsml;

public class VisiteurGenererCodeArm extends GenerateurDeCode implements VisiteurAsml {

    private static final int NUM_REGISTRE_DESTINATION = 4;
    private static final int NUM_REGISTRE_OPERANDE1 = 4;
    private static final int NUM_REGISTRE_OPERANDE2 = 5;
    private static final int NUM_REGISTRE_IMMEDIAT_INVALIDE = 6;
    private static final int NUM_REGISTRE_SAUVEGARDE_VALEUR_RETOUR = 7;
    private static final String FP = "FP";
    private static final String SP = "SP";
    private static final String LR = "LR";
    private static final String PC = "PC";
    private static final String LDR = "LDR";
    private static final String STR = "STR";

    private static final int MAX_DECALAGE_LOAD_STORE = (int) Math.pow(2, 12) - 1;
    private static final int MIN_DECALAGE_LOAD_STORE = -MAX_DECALAGE_LOAD_STORE; // un bit est reserve pour le signe et la valeur absolue est codee sur 12 bit pour les decalages immédiats utilisés comme ceci : [Rn, decalage]

    //private static final int MIN_IMMEDIAT_SHIFTER_OPERAND = -(int) Math.pow(2, 7);
    //private static final int MAX_IMMEDIAT_SHIFTER_OPERAND = -MIN_IMMEDIAT_SHIFTER_OPERAND-1; // toutes les valeurs sur 8 bits sont valides pour le shifter operand (entre -2^7 et 2^7-1)
    
    private static final int[] REGISTRE_SAUVEGARDES_APPELANT = new int[]{0, 1, 2, 3, Constantes.FP, Constantes.LR};

    private final HashMap<String, EmplacementMemoire> emplacementsMemoire;
    private final HashMap<Integer, String> registreVersChaine;
    private final HashSet<String> labels;
    private final Stack<EmplacementMemoire> emplacementDestination;
    private int numRegistre;
    private BigInteger numLabelSuivant;
    private final Stack<Boolean> pileEstInstructionMov;

    public VisiteurGenererCodeArm(HashMap<String, EmplacementMemoire> emplacementsMemoire, PrintStream fichierSortie, HashSet<String> labels) {
        super(fichierSortie);
        this.emplacementsMemoire = emplacementsMemoire;
        this.registreVersChaine = new HashMap<>();
        registreVersChaine.put(Constantes.FP, FP);
        registreVersChaine.put(Constantes.SP, SP);
        registreVersChaine.put(Constantes.LR, LR);
        registreVersChaine.put(Constantes.PC, PC);
        this.emplacementDestination = new Stack();
        changerDestination(new Registre(0));
        this.labels = labels;
        this.numLabelSuivant = BigInteger.ZERO;
        pileEstInstructionMov = new Stack<>();
    }

    private String strReg(int numReg) {
        String nomRegSpecial = registreVersChaine.get(numReg);
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
            valeurTournee = Integer.rotateRight(valeur, i); // fait subit a veleur tournee une rotation droit de i rangs
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
            if (decalage >= MIN_DECALAGE_LOAD_STORE && decalage <= MAX_DECALAGE_LOAD_STORE) {
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
            loadStoreWorker(emplacementVariable(((VarAsml) e).getIdString()), numReg, LDR, numRegBase);
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
        changerEstInstructionMov(true);
        int tailleEnvironnement = e.accept(new VisiteurTailleEnvironnement())/* + 4 * REGISTRE_SAUVEGARDES_APPELE.length*/;
        if(Constantes.REGISTRE_SAUVEGARDES_APPELE.length >= 1)
        {
            ecrireAvecIndentation("STMFD SP!, {");
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
            ecrireAvecIndentation("LDMFD SP!, {");
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
        if (!e.estMainFunDef()) {
            ecrireAvecIndentation("BX LR\n");
        }
        diminuerNiveauIndentation();
        ecrire("\n");
    }
    
        @Override
    public void visit(CallAsml e) {
        int tailleAEmpiler = (/*REGISTRE_SAUVEGARDES_APPELANT.length + */Math.max(0, (e.getArguments().size() - Constantes.REGISTRES_PARAMETRES.length))) * Constantes.TAILLE_MOT_MEMOIRE;
        ajouterValeurASP(-tailleAEmpiler);
        if(REGISTRE_SAUVEGARDES_APPELANT.length >= 1)
        {
            ecrireAvecIndentation("STMFD SP!, {");
            for (int i = 0; i < REGISTRE_SAUVEGARDES_APPELANT.length; i++) {
                if(i >= 1)
                {
                    ecrire(", ");
                }
                ecrire(strReg(REGISTRE_SAUVEGARDES_APPELANT[i]));
                //loadStoreWorker(new AdressePile((tailleAEmpiler - i * Constantes.TAILLE_MOT_MEMOIRE)), REGISTRE_SAUVEGARDES_APPELANT[i], STR, Constantes.SP);
            }
            ecrire("}\n");
        }  
        for (int i = 0; i < e.getArguments().size(); i++) {
            chargerValeur(e.getArguments().get(i), NUM_REGISTRE_OPERANDE1, Constantes.FP);
            if (i <= Constantes.REGISTRES_PARAMETRES.length - 1) {
                ecrireAvecIndentation("MOV R" + Constantes.REGISTRES_PARAMETRES[i] + ", ");
                visitOperande1Worker(e.getArguments().get(i));
                ecrire("\n");
            } else {
                loadStoreWorker(new AdressePile((e.getArguments().size()-i-1) * Constantes.TAILLE_MOT_MEMOIRE), NUM_REGISTRE_OPERANDE1, STR, Constantes.SP);
            }
        }
        ecrireAvecIndentation("BL ");
        if (e.getIdString().equals(Constantes.PRINT_INT_ASML)) {
            ecrire(Constantes.PRINT_INT_ARM);
        } else if (e.getIdString().equals(Constantes.PRINT_NEWLINE_ASML)) {
            ecrire(Constantes.PRINT_NEWLINE_ARM);
        }  /*else if (e.getIdString().equals(Constantes.CREATE_ARRAY_ASML)) {
            throw new NotYetImplementedException();
        } else if (e.getIdString().equals(Constantes.CREATE_FLOAT_ARRAY_ASML)) {
            throw new NotYetImplementedException();
        } else if (e.getIdString().equals(Constantes.SIN_ASML)) {
            ecrire(Constantes.SIN_ARM);throw new NotYetImplementedException();
        } else if (e.getIdString().equals(Constantes.COS_ASML)) {
            ecrire(Constantes.COS_ARM);throw new NotYetImplementedException();
        } else if (e.getIdString().equals(Constantes.SQRT_ASML)) {
            throw new NotYetImplementedException();
        } else if (e.getIdString().equals(Constantes.ABS_FLOAT_ASML)) {
            throw new NotYetImplementedException();
        } else if (e.getIdString().equals(Constantes.INT_OF_FLOAT_ASML)) {
            throw new NotYetImplementedException();
        } else if (e.getIdString().equals(Constantes.FLOAT_OF_INT_ASML)) {
            throw new NotYetImplementedException();
        } else if (e.getIdString().equals(Constantes.TRUNCATE_ASML)) {
            throw new NotYetImplementedException();
        } */else {
            ecrire(e.getIdString());
        }
        /*
         = "_min_caml_create_array";
    public static final String  = "_min_caml_create_float_array";
    public static final String  = "_min_caml_sin";
    public static final String  = "_min_caml_cos";
    public static final String  = "_min_caml_sqrt";
    public static final String  = "_min_caml_abs_float";
    public static final String  = "_min_caml_int_of_float";
    public static final String  = "_min_caml_float_of_int";
    public static final String 
        */
        ecrireAvecIndentation("\n");
        ecrireAvecIndentation("MOV " + strReg(NUM_REGISTRE_SAUVEGARDE_VALEUR_RETOUR) + ", " + strReg(Constantes.REGISTRE_VALEUR_RETOUR) + "\n");
        //strDestination();
        if(REGISTRE_SAUVEGARDES_APPELANT.length >= 1)
        {
            ecrireAvecIndentation("LDMFD SP!, {");
            for (int i = 0; i < REGISTRE_SAUVEGARDES_APPELANT.length; i++) {
                if(i >= 1)
                {
                    ecrire(", ");
                }
                ecrire(strReg(REGISTRE_SAUVEGARDES_APPELANT[i]));
                //loadStoreWorker(new AdressePile((tailleAEmpiler - i * Constantes.TAILLE_MOT_MEMOIRE)), REGISTRE_SAUVEGARDES_APPELANT[i], STR, Constantes.SP);
            }
            ecrire("}\n");
        }
        ecrireAvecIndentation("MOV ");
        visitDestinationWorker();
        ecrire(", " + strReg(NUM_REGISTRE_SAUVEGARDE_VALEUR_RETOUR) + "\n");
        strDestination();
        ajouterValeurASP(tailleAEmpiler);

    }

    @Override
    public void visit(IntAsml e) {
        //chargerValeur(e, -1, -1);
        if (estInstructionMov()) {
            ecrireAvecIndentation("LDR ");
            visitDestinationWorker();
            ecrire(", =" + e.getValeur()+"\n");
            strDestination();
        }
        else
        {
            ecrire("#" + e.getValeur());            
        }
        int valeur = e.getValeur();
    }

    @Override
    public void visit(NopAsml e) {
        IntAsml.nil().accept(this);
    }

    @Override
    public void visit(ProgrammeAsml e) {
        ecrire(".text\n");
        ecrire(".global " + Constantes.NOM_FONCTION_MAIN_ARM + "\n");
        for (FunDefAsml funDef : e.getFunDefs()) {
            funDef.accept(this);
        }
        e.getMainFunDef().accept(this);
    }

    @Override
    public void visit(SubAsml e) {
        visitOpArithmetiqueIntWorker(e, "SUB");
    }

    @Override
    public void visit(NewAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public void visit(FNegAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public void visit(FAddAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public void visit(FSubAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public void visit(FMulAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public void visit(FDivAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public void visit(CallClosureAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public void visit(MemLectureAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public void visit(MemEcritureAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public void visit(LetFloatAsml e) {
        throw new NotYetImplementedException();
    }

    private String genererLabel(String prefixe) {
        String label = null;
        do {
            label = prefixe + numLabelSuivant;
            // on utilise BigInteger au lieu de int pour eviter d'avoir des labels de meme nom si on en cree au moins Integer.MAX_VALUE (même si c'est peu probable d'en créer autant)
            numLabelSuivant = numLabelSuivant.add(BigInteger.ONE);
        } while (labels.contains(label));
        labels.add(label);
        return label;
    }

    private void visitIfEqIntWorker(IfIntAsml e, String instBranchementElse) {
        String labelElse = genererLabel("sinon");
        String labelEndIf = genererLabel("finSi");
        VarAsml op1 = e.getE1();
        VarOuIntAsml op2 = e.getE2();
        chargerValeur(op1, NUM_REGISTRE_OPERANDE1, Constantes.FP);
        chargerValeur(op2, NUM_REGISTRE_OPERANDE2, Constantes.FP);
        ecrireAvecIndentation("CMP ");
        visitOperande1Worker(op1);
        ecrire(", ");
        visitOperande2IntWorker(op2);
        ecrire("\n");
        ecrireAvecIndentation(instBranchementElse + " " + labelElse + "\n");
        e.getESiVrai().accept(this);
        ecrireAvecIndentation("B " + labelEndIf + "\n");
        ecrire(labelElse + ":\n");
        e.getESiFaux().accept(this);
        ecrire(labelEndIf + ":\n");
    }

    @Override
    public void visit(IfEqIntAsml e) {
        visitIfEqIntWorker(e, "BNE");
    }

    @Override
    public void visit(IfLEIntAsml e) {
        visitIfEqIntWorker(e, "BGT");
    }

    @Override
    public void visit(IfGEIntAsml e) {
        visitIfEqIntWorker(e, "BLT");
    }

    @Override
    public void visit(IfEquFloatAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public void visit(IfLEFloatAsml e) {
        throw new NotYetImplementedException();
    }

    @Override
    public void visit(LabelFloatAsml e) {
        throw new NotYetImplementedException();
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
            return Constantes.TAILLE_MOT_MEMOIRE + e.getE2().accept(this);
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
            throw new NotYetImplementedException();
        }

        @Override
        public Integer visit(FNegAsml e) {
            throw new NotYetImplementedException();
        }

        @Override
        public Integer visit(FAddAsml e) {
            throw new NotYetImplementedException();
        }

        @Override
        public Integer visit(FSubAsml e) {
            throw new NotYetImplementedException();
        }

        @Override
        public Integer visit(FMulAsml e) {
            throw new NotYetImplementedException();
        }

        @Override
        public Integer visit(FDivAsml e) {
            throw new NotYetImplementedException();
        }

        @Override
        public Integer visit(CallAsml e) {
            return 0;
        }

        @Override
        public Integer visit(CallClosureAsml e) {
            throw new NotYetImplementedException();
        }

        @Override
        public Integer visit(MemLectureAsml e) {
            throw new NotYetImplementedException();
        }

        @Override
        public Integer visit(MemEcritureAsml e) {
            throw new NotYetImplementedException();
        }

        @Override
        public Integer visit(LetFloatAsml e) {
            throw new NotYetImplementedException();
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
        public Integer visit(IfEquFloatAsml e) {
            throw new NotYetImplementedException();
        }

        @Override
        public Integer visit(IfLEFloatAsml e) {
            throw new NotYetImplementedException();
        }

        @Override
        public Integer visit(LabelFloatAsml e) {
            throw new NotYetImplementedException();
        }

    }
}
