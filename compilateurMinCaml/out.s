.text
.global _start
_min_caml_sin:
    FMSR S0, R0
    LDR R0, =_piSur2
    FLDS S1, [R0]
    FSUBS S0, S0, S1
    FMRS R0, S0
_min_caml_cos:
    FMSR S0, R0
    LDR R0, =_2pi
    FLDS S1, [R0]
    FDIVS S2, S0, S1
    FTOSIZS S2, S2
    FSITOS S2, S2
    FMULS S2, S2, S1
    FSUBS S0, S0, S2
    MOV R1, #0
    FCMPZS S0
    FMSTAT
    BHI sinFinSi1
    FNEGS S0, S0
sinFinSi1:
    LDR R0, =_piSur2
    FLDS S2, [R0]
    FCMPS S0, S2
    FMSTAT
    BLT sinFinSi2
    LDR R0, =_3PiSur2
    FLDS S2, [R0]
    FCMPS S0, S2
    FMSTAT
    BLT sinSinon
    FSUBS S0, S0, S1
    B sinFinSi2
sinSinon:
    LDR R0, =_pi
    FLDS S1, [R0]
    FSUBS S0, S1, S0
    MOV R1, #1          @ R1 = 1
sinFinSi2:
    LDR R3, =_coefficientsPolynomeSin
    MOV R2, R3
    ADD R2, R2, #24
    FLDMIAS R2, {S1}
    SUB R2, R2, #4
    FMULS S0, S0, S0
sinTantQue:
    CMP R2, R3
    BLT sinFinTantQue
    FMULS S1, S1, S0
    FLDMIAS R2, {S2}
    SUB R2, R2, #4
    FADDS S1, S1, S2
    B sinTantQue
sinFinTantQue:
    CMP R1, #0
    BEQ sinFinSi3
    FNEGS S1, S1
sinFinSi3:
    FMRS R0, S1
    BX LR            @aller a l'adresse dans lr (instruction return)
.ltorg

_start:
    PUSH {R4, R5, R6, R7, R8, R9, R10, FP}
    SUB FP, SP, #4
    SUB SP, SP, #32
    LDR R4, =_float19
    MOV R4, R4
    STR R4, [FP, #0]
    LDR R4, [FP, #0]
    LDR R4, [R4, #0]
    STR R4, [FP, #-4]
    PUSH {R0, R1, R2, R3, R12, LR}
    LDR R4, [FP, #-4]
    MOV R0, R4
    BL _min_caml_sin    
    MOV R7, R0
    POP {R0, R1, R2, R3, R12, LR}
    MOV R4, R7
    STR R4, [FP, #-8]
    LDR R4, =_float17
    MOV R4, R4
    STR R4, [FP, #-12]
    LDR R4, [FP, #-12]
    LDR R4, [R4, #0]
    STR R4, [FP, #-16]
    FLDS S0, [FP, #-16]
    FLDS S1, [FP, #-8]
    FMULS S0, S0, S1
    FSTS S0, [FP, #-20]
    FLDS S0, [FP, #-20]
    FTOSIZS S0, S0
    FSTS S0, [FP, #-24]
    PUSH {R0, R1, R2, R3, R12, LR}
    LDR R4, [FP, #-24]
    MOV R0, R4
    BL min_caml_print_int    
    MOV R7, R0
    POP {R0, R1, R2, R3, R12, LR}
    MOV R4, R7
    STR R4, [FP, #-28]
    LDR R0, =0
    ADD SP, SP, #32
    POP {R4, R5, R6, R7, R8, R9, R10, FP}
    B min_caml_exit
.ltorg

.data
.balign 4
_float19: .single 1.04
_float17: .single 10.0
_pi: .single 3.1415927
_piSur2: .single 1.5707964
_3PiSur2: .single 4.712389
_2pi: .single 6.2831855
_coefficientsPolynomeSin:
    .single 1.0
    .single -0.5
    .single 0.041666668
    .single -0.0013888889
    .single 2.4801588E-5
    .single -2.755732E-7
    .single 2.0876758E-9
