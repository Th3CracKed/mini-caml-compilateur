package util;

import java.util.Arrays;
import java.util.List;

public class Constantes {
    // divers
    public static int TAILLE_MOT_MEMOIRE = 4;
    
    // numéros de registre
    public static final int REGISTRE_VALEUR_RETOUR = 0;
    public static final int[] REGISTRES_PARAMETRES = new int[] {REGISTRE_VALEUR_RETOUR,1,2,3};
    public static final int[] REGISTRES_VAR_LOCALES = new int[] {4,5,6,7,8,9,10,12};
    public static final int FP = 11;
    public static final int SP = 13;
    public static final int LR = 14;
    public static final int PC = 15;

    // fonction externes mincaml
    public static final String PRINT_INT_CAML = "print_int";
    public static final String PRINT_NEWLINE_CAML = "print_newline";
    /*public static final String SIN_CAML = "sin";
    public static final String COS_CAML = "cos";
    public static final String SQRT_CAML = "sqrt";
    public static final String ABS_FLOAT_CAML = "abs_float";
    public static final String INT_OF_FLOAT_CAML = "int_of_float";
    public static final String FLOAT_OF_INT_CAML = "float_of_int";
    public static final String TRUNCATE_CAML = "truncate"; */   
    public static final List<String> FONCTION_EXTERNES_MINCAML = Arrays.asList(PRINT_INT_CAML, PRINT_NEWLINE_CAML/*, SIN_CAML, COS_CAML, SQRT_CAML, ABS_FLOAT_CAML, INT_OF_FLOAT_CAML, FLOAT_OF_INT_CAML, TRUNCATE_CAML*/);
    
    // fonction externes asml
    public static final String PRINT_INT_ASML = "_min_caml_print_int";
    public static final String PRINT_NEWLINE_ASML = "_min_caml_print_newline";
    /*public static final String CREATE_ARRAY_ASML = "_min_caml_create_array";
    public static final String CREATE_FLOAT_ARRAY_ASML = "_min_caml_create_float_array";
    public static final String SIN_ASML = "_min_caml_sin";
    public static final String COS_ASML = "_min_caml_cos";
    public static final String SQRT_ASML = "_min_caml_sqrt";
    public static final String ABS_FLOAT_ASML = "_min_caml_abs_float";
    public static final String INT_OF_FLOAT_ASML = "_min_caml_int_of_float";
    public static final String FLOAT_OF_INT_ASML = "_min_caml_float_of_int";
    public static final String TRUNCATE_ASML = "_min_caml_truncate";*/
    public static final List<String> FONCTION_EXTERNES_ASML = Arrays.asList(PRINT_INT_ASML, PRINT_NEWLINE_ASML/*, CREATE_ARRAY_ASML, CREATE_FLOAT_ARRAY_ASML, SIN_ASML, COS_ASML, SQRT_ASML, ABS_FLOAT_ASML, INT_OF_FLOAT_ASML, FLOAT_OF_INT_ASML, TRUNCATE_ASML*/);
}
