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
    public static final int[] REGISTRE_SAUVEGARDES_APPELE = new int[]{4,5,6,7,8,9,10,FP};

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
    
    // fonction principale asml
    public static final String NOM_FONCTION_MAIN_ASML = "_";
        
    // mots reserves asml (seulement ceux qui pourraient être des identificateurs valides)
    public static final List<String> MOTS_RESERVES_ASML = Arrays.asList("if", "then", "else", "let", "in", "neg", /*"fneg", "mem", "fmul", "fdiv", "fsub", "fadd",*/ "add", "sub", "call", /*"new",*/ "nop"/*, "apply_closure"*/);

    
    // fonction externes asml
    public static final String PRINT_INT_ASML = "_min_caml_print_int";
    public static final String PRINT_NEWLINE_ASML = "_min_caml_print_newline";
    public static final String CREATE_ARRAY_ASML = "_min_caml_create_array";
    /*public static final String CREATE_FLOAT_ARRAY_ASML = "_min_caml_create_float_array";
    public static final String SIN_ASML = "_min_caml_sin";
    public static final String COS_ASML = "_min_caml_cos";
    public static final String SQRT_ASML = "_min_caml_sqrt";
    public static final String ABS_FLOAT_ASML = "_min_caml_abs_float";
    public static final String INT_OF_FLOAT_ASML = "_min_caml_int_of_float";
    public static final String FLOAT_OF_INT_ASML = "_min_caml_float_of_int";
    public static final String TRUNCATE_ASML = "_min_caml_truncate";*/
    public static final List<String> FONCTION_EXTERNES_ASML = Arrays.asList(PRINT_INT_ASML, PRINT_NEWLINE_ASML/*, CREATE_ARRAY_ASML, CREATE_FLOAT_ARRAY_ASML, SIN_ASML, COS_ASML, SQRT_ASML, ABS_FLOAT_ASML, INT_OF_FLOAT_ASML, FLOAT_OF_INT_ASML, TRUNCATE_ASML*/);

    
    // fonction principale arm
    public static final String NOM_FONCTION_MAIN_ARM = "_start";
    
    // fonction externes arm
    public static final String PRINT_INT_ARM = "min_caml_print_int";
    public static final String PRINT_NEWLINE_ARM = "min_caml_print_newline";
    public static final String CREATE_ARRAY_ARM = CREATE_ARRAY_ASML;
    /*public static final String CREATE_FLOAT_ARRAY_ARM = CREATE_FLOAT_ARRAY_ASML;
    /*public static final String SIN_ARM = SIN_ASML;
    public static final String COS_ARM = COS_ASML;*/
    public static final List<String> FONCTION_EXTERNES_ARM = Arrays.asList(PRINT_INT_ARM, PRINT_NEWLINE_ARM, CREATE_ARRAY_ARM/*, CREATE_FLOAT_ARRAY_ARM, SIN_ARM, COS_ARM */);
    
    public static final String EXIT_ARM = "min_caml_exit";
    public static final String CREATE_ARRAY_BOUCLE_ARM = "create_array_boucle";
    // fonctions presentes dans libMinCaml.S mais que l'utilisateur ne peut pas appeler comme les fonctions externes. Par exemple,
    // min_caml_print_int appelle min_caml_print_string qui elle meme appelle stringlength mais le programmeur MinCaml ne peut pas appeler stringlength directement.
    // min_caml_exit ne peut pas non plus etre appele explicitement mais est appele automatique a la fin de chaque programme
    public static final List<String> FONCTION_EXTERNES_PRIVEES_ARM = Arrays.asList(EXIT_ARM, CREATE_ARRAY_BOUCLE_ARM, "stringlength", "stringlength_loop", "stringlength_first", 
                                                                                   "min_caml_print_string", "hello_world_string", "min_caml_hello_world", 
                                                                                   "min_caml_print_char", ".string_for_int", "stringofint", ".i2s_non_zero", 
                                                                                   ".i2s_positive", ".i2s_exit", ".i2s_constants");
}
