.text
.global _start
_f664:
    STMFD SP!, {LR}
    SUB FP, SP, #4
    SUB SP, SP, #32
    CMP R0, #0
    BNE sinon700
    LDR R4, =1
    STR R4, [FP, #0]
    B finSi701
sinon700:
    LDR R4, =0
    STR R4, [FP, #0]
finSi701:
    LDR R4, =1
    STR R4, [FP, #-4]
    LDR R4, [FP, #0]
    LDR R5, [FP, #-4]
    CMP R4, R5
    BNE sinon702
    LDR R0, =0
    B finSi703
sinon702:
    SUB R4, R0, #1
    STR R4, [FP, #-8]
    LDR R4, [FP, #-8]
    CMP R4, #0
    BNE sinon704
    LDR R4, =1
    STR R4, [FP, #-12]
    B finSi705
sinon704:
    LDR R4, =0
    STR R4, [FP, #-12]
finSi705:
    LDR R4, =1
    STR R4, [FP, #-16]
    LDR R4, [FP, #-12]
    LDR R5, [FP, #-16]
    CMP R4, R5
    BNE sinon706
    LDR R4, =0
    STR R4, [FP, #-20]
    B finSi707
sinon706:
    LDR R4, [FP, #-8]
    SUB R4, R4, #1
    STR R4, [FP, #-24]
    STMFD SP!, {R0, R1, R2, R3, FP, LR}
    LDR R4, [FP, #-24]
    MOV R0, R4
    BL _f664    
    MOV R7, R0
    LDMFD SP!, {R0, R1, R2, R3, FP, LR}
    MOV R4, R7
    STR R4, [FP, #-28]
    LDR R4, [FP, #-28]
    ADD R4, R4, #1
    STR R4, [FP, #-20]
finSi707:
    LDR R4, [FP, #-20]
    ADD R0, R4, #1
finSi703:
    ADD SP, SP, #32
    LDMFD SP!, {LR}
    BX LR

_start:
    STMFD SP!, {LR}
    SUB FP, SP, #4
    SUB SP, SP, #20
    LDR R4, =0
    STR R4, [FP, #0]
    LDR R4, =1
    STR R4, [FP, #-4]
    LDR R4, [FP, #0]
    LDR R5, [FP, #-4]
    CMP R4, R5
    BNE sinon708
    LDR R4, =0
    STR R4, [FP, #-8]
    B finSi709
sinon708:
    LDR R4, =41
    STR R4, [FP, #-12]
    STMFD SP!, {R0, R1, R2, R3, FP, LR}
    LDR R4, [FP, #-12]
    MOV R0, R4
    BL _f664    
    MOV R7, R0
    LDMFD SP!, {R0, R1, R2, R3, FP, LR}
    MOV R4, R7
    STR R4, [FP, #-16]
    LDR R4, [FP, #-16]
    ADD R4, R4, #1
    STR R4, [FP, #-8]
finSi709:
    STMFD SP!, {R0, R1, R2, R3, FP, LR}
    LDR R4, [FP, #-8]
    MOV R0, R4
    BL min_caml_print_int    
    MOV R7, R0
    LDMFD SP!, {R0, R1, R2, R3, FP, LR}
    MOV R0, R7
    ADD SP, SP, #20
    LDMFD SP!, {LR}

