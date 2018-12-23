.text
.global _start
_start:
    MOV FP, SP
    SUB SP, SP, #24
    STR R7, [SP, #20]
    LDR R4, =1
    STR R4, [FP, #0]
    SUB SP, SP, #20
    STR R0, [SP, #16]
    STR R1, [SP, #12]
    STR R2, [SP, #8]
    STR R3, [SP, #4]
    STR LR, [SP, #0]
    LDR R4, [FP, #0]
    MOV R0, R4
    BL min_caml_print_int    
    MOV R7, R0
    STR R4, [FP, #-4]
    LDR R0, [SP, #16]
    LDR R1, [SP, #12]
    LDR R2, [SP, #8]
    LDR R3, [SP, #4]
    LDR LR, [SP, #0]
    MOV R4, R7
    STR R4, [FP, #-4]
    ADD SP, SP, #20
    SUB SP, SP, #20
    STR R0, [SP, #16]
    STR R1, [SP, #12]
    STR R2, [SP, #8]
    STR R3, [SP, #4]
    STR LR, [SP, #0]
    BL min_caml_print_newline    
    MOV R7, R0
    STR R4, [FP, #-8]
    LDR R0, [SP, #16]
    LDR R1, [SP, #12]
    LDR R2, [SP, #8]
    LDR R3, [SP, #4]
    LDR LR, [SP, #0]
    MOV R4, R7
    STR R4, [FP, #-8]
    ADD SP, SP, #20
    LDR R4, [FP, #-4]
    LDR R5, [FP, #-8]
    CMP R4, R5
    BNE sinon0
    LDR R4, =1
    STR R4, [FP, #-12]
    B finSi1
sinon0:
    LDR R4, =0
    STR R4, [FP, #-12]
finSi1:
    LDR R4, [FP, #-12]
    CMP R4, #0
    BNE sinon2
    LDR R4, =2
    STR R4, [FP, #-16]
    B finSi3
sinon2:
    LDR R4, =1
    STR R4, [FP, #-16]
finSi3:
    SUB SP, SP, #20
    STR R0, [SP, #16]
    STR R1, [SP, #12]
    STR R2, [SP, #8]
    STR R3, [SP, #4]
    STR LR, [SP, #0]
    LDR R4, [FP, #-16]
    MOV R0, R4
    BL min_caml_print_int    
    MOV R7, R0
    LDR R0, [SP, #16]
    LDR R1, [SP, #12]
    LDR R2, [SP, #8]
    LDR R3, [SP, #4]
    LDR LR, [SP, #0]
    MOV R0, R7
    ADD SP, SP, #20
    LDR R7, [SP, #20]
    MOV SP, FP

