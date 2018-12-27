.text
.global _start
_v10:
    STMFD SP!, {LR}
    MOV FP, SP
    ADD R0, R0, R1
    MOV SP, FP
    LDMFD SP!, {LR}
    BX LR

_v9:
    STMFD SP!, {LR}
    MOV FP, SP
    SUB SP, SP, #4
    LDR R4, =2
    STR R4, [FP, #0]
    STMFD SP!, {R0, R1, R2, R3, FP, LR}
    MOV R0, R0
    LDR R4, [FP, #0]
    MOV R1, R4
    BL _v10    
    MOV R7, R0
    LDMFD SP!, {R0, R1, R2, R3, FP, LR}
    MOV R0, R7
    MOV SP, FP
    LDMFD SP!, {LR}
    BX LR

_start:
    STMFD SP!, {LR}
    MOV FP, SP
    SUB SP, SP, #8
    LDR R4, =40
    STR R4, [FP, #0]
    STMFD SP!, {R0, R1, R2, R3, FP, LR}
    LDR R4, [FP, #0]
    MOV R0, R4
    BL _v9    
    MOV R7, R0
    LDMFD SP!, {R0, R1, R2, R3, FP, LR}
    MOV R4, R7
    STR R4, [FP, #-4]
    STMFD SP!, {R0, R1, R2, R3, FP, LR}
    LDR R4, [FP, #-4]
    MOV R0, R4
    BL min_caml_print_int    
    MOV R7, R0
    LDMFD SP!, {R0, R1, R2, R3, FP, LR}
    MOV R0, R7
    MOV SP, FP
    LDMFD SP!, {LR}

