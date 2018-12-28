.text
.global _start
_f1786:
    STMFD SP!, {LR}
    SUB FP, SP, #4
    SUB SP, SP, #32
    CMP R0, #0
    BGT sinon1822
    LDR R4, =1
    STR R4, [FP, #0]
    B finSi1823
sinon1822:
    LDR R4, =0
    STR R4, [FP, #0]
finSi1823:
    LDR R4, =1
    STR R4, [FP, #-4]
    LDR R4, [FP, #0]
    LDR R5, [FP, #-4]
    CMP R4, R5
    BNE sinon1824
    LDR R0, =0
    B finSi1825
sinon1824:
    SUB R4, R0, #1
    STR R4, [FP, #-8]
    LDR R4, [FP, #-8]
    CMP R4, #0
    BGT sinon1826
    LDR R4, =1
    STR R4, [FP, #-12]
    B finSi1827
sinon1826:
    LDR R4, =0
    STR R4, [FP, #-12]
finSi1827:
    LDR R4, =1
    STR R4, [FP, #-16]
    LDR R4, [FP, #-12]
    LDR R5, [FP, #-16]
    CMP R4, R5
    BNE sinon1828
    LDR R4, =0
    STR R4, [FP, #-20]
    B finSi1829
sinon1828:
    LDR R4, [FP, #-8]
    SUB R4, R4, #1
    STR R4, [FP, #-24]
    STMFD SP!, {R0, R1, R2, R3, FP, LR}
    LDR R4, [FP, #-24]
    MOV R0, R4
    BL _f1786    
    MOV R7, R0
    LDMFD SP!, {R0, R1, R2, R3, FP, LR}
    MOV R4, R7
    STR R4, [FP, #-28]
    LDR R4, [FP, #-28]
    LDR R5, [FP, #-8]
    ADD R4, R4, R5
    STR R4, [FP, #-20]
finSi1829:
    LDR R4, [FP, #-20]
    ADD R0, R4, R0
finSi1825:
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
    BNE sinon1830
    LDR R4, =0
    STR R4, [FP, #-8]
    B finSi1831
sinon1830:
    LDR R4, =9999
    STR R4, [FP, #-12]
    STMFD SP!, {R0, R1, R2, R3, FP, LR}
    LDR R4, [FP, #-12]
    MOV R0, R4
    BL _f1786    
    MOV R7, R0
    LDMFD SP!, {R0, R1, R2, R3, FP, LR}
    MOV R4, R7
    STR R4, [FP, #-16]
    LDR R4, [FP, #-16]
    LDR R6, =10000
    ADD R4, R4, R6
    STR R4, [FP, #-8]
finSi1831:
    STMFD SP!, {R0, R1, R2, R3, FP, LR}
    LDR R4, [FP, #-8]
    MOV R0, R4
    BL min_caml_print_int    
    MOV R7, R0
    LDMFD SP!, {R0, R1, R2, R3, FP, LR}
    MOV R0, R7
    ADD SP, SP, #20
    LDMFD SP!, {LR}
    B min_caml_exit
