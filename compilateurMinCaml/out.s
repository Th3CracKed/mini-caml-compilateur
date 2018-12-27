.text
.global _start
_v58:
    STMFD SP!, {LR}
    SUB FP, SP, #4
    SUB SP, SP, #20
    ADD R4, R0, R1
    STR R4, [FP, #0]
    LDR R4, [FP, #0]
    ADD R4, R4, R2
    STR R4, [FP, #-4]
    LDR R4, [FP, #-4]
    ADD R4, R4, R3
    STR R4, [FP, #-8]
    LDR R4, [FP, #-8]
    LDR R5, [FP, #16]
    ADD R4, R4, R5
    STR R4, [FP, #-12]
    LDR R4, [FP, #-12]
    LDR R5, [FP, #12]
    ADD R4, R4, R5
    STR R4, [FP, #-16]
    LDR R4, [FP, #-16]
    LDR R5, [FP, #8]
    ADD R0, R4, R5
    ADD SP, SP, #20
    LDMFD SP!, {LR}
    BX LR

_start:
    STMFD SP!, {LR}
    SUB FP, SP, #4
    SUB SP, SP, #32
    LDR R4, =42
    STR R4, [FP, #0]
    LDR R4, =0
    STR R4, [FP, #-4]
    LDR R4, =0
    STR R4, [FP, #-8]
    LDR R4, =0
    STR R4, [FP, #-12]
    LDR R4, =0
    STR R4, [FP, #-16]
    LDR R4, =0
    STR R4, [FP, #-20]
    LDR R4, =0
    STR R4, [FP, #-24]
    SUB SP, SP, #12
    STMFD SP!, {R0, R1, R2, R3, FP, LR}
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
    BL _v58    
    MOV R7, R0
    LDMFD SP!, {R0, R1, R2, R3, FP, LR}
    MOV R4, R7
    STR R4, [FP, #-28]
    ADD SP, SP, #12
    STMFD SP!, {R0, R1, R2, R3, FP, LR}
    LDR R4, [FP, #-28]
    MOV R0, R4
    BL min_caml_print_int    
    MOV R7, R0
    LDMFD SP!, {R0, R1, R2, R3, FP, LR}
    MOV R0, R7
    ADD SP, SP, #32
    LDMFD SP!, {LR}

