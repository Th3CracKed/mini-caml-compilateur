.text
.global _start
_start:
    PUSH {R4, R5, R6, R7, R8, R9, R10, FP}
    SUB FP, SP, #4
    LDR R8, =10000000
    LDR R9, =1
    LDR R10, =10000000
    CMP R8, R10
    BNE sinon17
    LDR R12, =1
    B finSi18
.ltorg
sinon17:
    LDR R12, =0
finSi18:
    CMP R12, R9
    BNE sinon19
    LDR R8, =42
    PUSH {R0, R1, R2, R3, R12, LR}
    MOV R0, R8
    BL min_caml_print_int    
    MOV R7, R0
    POP {R0, R1, R2, R3, R12, LR}
    MOV R0, R7
    B finSi20
.ltorg
sinon19:
    LDR R8, =0
    PUSH {R0, R1, R2, R3, R12, LR}
    MOV R0, R8
    BL min_caml_print_int    
    MOV R7, R0
    POP {R0, R1, R2, R3, R12, LR}
    MOV R0, R7
finSi20:
    POP {R4, R5, R6, R7, R8, R9, R10, FP}
    B min_caml_exit
.ltorg

