.text
.global _start
_f10:
    PUSH {R4, R5, R6, R7, R8, R9, R10, FP}
    SUB FP, SP, #4
    ADD R0, R0, R1
    POP {R4, R5, R6, R7, R8, R9, R10, FP}
    BX LR
.ltorg

_start:
    PUSH {R4, R5, R6, R7, R8, R9, R10, FP}
    SUB FP, SP, #4
    SUB SP, SP, #16
    LDR R4, =12
    STR R4, [FP, #0]
    LDR R4, =0
    STR R4, [FP, #-4]
    PUSH {R0, R1, R2, R3, R12, LR}
    LDR R4, [FP, #-4]
    MOV R0, R4
    BL _f10    
    MOV R7, R0
    POP {R0, R1, R2, R3, R12, LR}
    MOV R4, R7
    STR R4, [FP, #-8]
    PUSH {R0, R1, R2, R3, R12, LR}
    LDR R4, [FP, #-8]
    MOV R0, R4
    LDR R4, [FP, #0]
    MOV R1, R4
    LDR R4, [FP, #-8]
    LDR R4, [R4]
    MOV LR, PC
    BX R4    
    MOV R7, R0
    POP {R0, R1, R2, R3, R12, LR}
    MOV R4, R7
    STR R4, [FP, #-12]
    PUSH {R0, R1, R2, R3, R12, LR}
    LDR R4, [FP, #-12]
    MOV R0, R4
    BL min_caml_print_int    
    MOV R7, R0
    POP {R0, R1, R2, R3, R12, LR}
    MOV R0, R7
    ADD SP, SP, #16
    POP {R4, R5, R6, R7, R8, R9, R10, FP}
    B min_caml_exit
.ltorg

