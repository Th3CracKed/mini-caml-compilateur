.text
.global _start
_f10:
    PUSH {R4, R5, R6, R7, R8, R9, R10, FP}
    SUB FP, SP, #4
    LDR R4, [FP, #36]
    MOV R0, R4
    POP {R4, R5, R6, R7, R8, R9, R10, FP}
    BX LR
.ltorg

_start:
    PUSH {R4, R5, R6, R7, R8, R9, R10, FP}
    SUB FP, SP, #4
    SUB SP, SP, #32
    LDR R4, =42
    STR R4, [FP, #0]
    LDR R4, =6
    STR R4, [FP, #-4]
    LDR R4, =5
    STR R4, [FP, #-8]
    LDR R4, =4
    STR R4, [FP, #-12]
    LDR R4, =3
    STR R4, [FP, #-16]
    LDR R4, =2
    STR R4, [FP, #-20]
    LDR R4, =1
    STR R4, [FP, #-24]
    PUSH {R0, R1, R2, R3, R12, LR}
    SUB SP, SP, #12
    LDR R4, [FP, #-24]
    MOV R0, R4
    LDR R4, [FP, #-20]
    MOV R1, R4
    LDR R4, [FP, #-16]
    MOV R2, R4
    LDR R4, [FP, #-12]
    MOV R3, R4
    LDR R4, [FP, #-8]
    STR R4, [SP, #8]
    LDR R4, [FP, #-4]
    STR R4, [SP, #4]
    LDR R4, [FP, #0]
    STR R4, [SP, #0]
    BL _f10    
    MOV R7, R0
    ADD SP, SP, #12
    POP {R0, R1, R2, R3, R12, LR}
    MOV R4, R7
    STR R4, [FP, #-28]
    PUSH {R0, R1, R2, R3, R12, LR}
    LDR R4, [FP, #-28]
    MOV R0, R4
    BL min_caml_print_int    
    MOV R7, R0
    POP {R0, R1, R2, R3, R12, LR}
    MOV R0, R7
    ADD SP, SP, #32
    POP {R4, R5, R6, R7, R8, R9, R10, FP}
    B min_caml_exit
.ltorg

