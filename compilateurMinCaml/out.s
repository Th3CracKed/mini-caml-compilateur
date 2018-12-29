.text
.global _start
_min_caml_create_array:
    CMP R0, #0
    BLE min_caml_exit
    MOV R2, R0
    MOV R0, R8
create_array_boucle:
    STR R1, [R8]
    SUB R2, R2, #1
    ADD R8, R8, #4
    CMP R2, #0
    BNE create_array_boucle
    BX LR

_f22:
    STMFD SP!, {R4, R5, R6, R7, R8, R9, R10, FP}
    SUB FP, SP, #4
    SUB SP, SP, #24
    CMP R1, #0
    BNE sinon48
    LDR R4, =1
    STR R4, [FP, #0]
    B finSi49
sinon48:
    LDR R4, =0
    STR R4, [FP, #0]
finSi49:
    LDR R4, =1
    STR R4, [FP, #-4]
    LDR R4, [FP, #0]
    LDR R5, [FP, #-4]
    CMP R4, R5
    BNE sinon50
    LDR R0, =0
    B finSi51
sinon50:
    SUB R4, R1, #1
    STR R4, [FP, #-8]
    LDR R6, [FP, #-8]
    LDR R4, [R0, R6]
    STR R4, [FP, #-12]
    SUB R4, R1, #1
    STR R4, [FP, #-16]
    STMFD SP!, {R0, R1, R2, R3, R8, R12, LR}
    MOV R0, R0
    LDR R4, [FP, #-16]
    MOV R1, R4
    BL _f22    
    MOV R7, R0
    LDMFD SP!, {R0, R1, R2, R3, R8, R12, LR}
    MOV R4, R7
    STR R4, [FP, #-20]
    LDR R4, [FP, #-12]
    LDR R5, [FP, #-20]
    ADD R0, R4, R5
finSi51:
    ADD SP, SP, #24
    LDMFD SP!, {R4, R5, R6, R7, R8, R9, R10, FP}
    BX LR

_start:
    LDR R6, =10000
    SUB SP, SP, R6
    MOV R8, SP
    STMFD SP!, {R4, R5, R6, R7, R8, R9, R10, FP}
    SUB FP, SP, #4
    SUB SP, SP, #20
    LDR R4, =42
    STR R4, [FP, #0]
    LDR R4, =1
    STR R4, [FP, #-4]
    STMFD SP!, {R0, R1, R2, R3, R8, R12, LR}
    LDR R4, [FP, #0]
    MOV R0, R4
    LDR R4, [FP, #-4]
    MOV R1, R4
    BL _min_caml_create_array    
    MOV R7, R0
    LDMFD SP!, {R0, R1, R2, R3, R8, R12, LR}
    MOV R4, R7
    STR R4, [FP, #-8]
    LDR R4, =42
    STR R4, [FP, #-12]
    STMFD SP!, {R0, R1, R2, R3, R8, R12, LR}
    LDR R4, [FP, #-8]
    MOV R0, R4
    LDR R4, [FP, #-12]
    MOV R1, R4
    BL _f22    
    MOV R7, R0
    LDMFD SP!, {R0, R1, R2, R3, R8, R12, LR}
    MOV R4, R7
    STR R4, [FP, #-16]
    STMFD SP!, {R0, R1, R2, R3, R8, R12, LR}
    LDR R4, [FP, #-16]
    MOV R0, R4
    BL min_caml_print_int    
    MOV R7, R0
    LDMFD SP!, {R0, R1, R2, R3, R8, R12, LR}
    MOV R0, R7
    ADD SP, SP, #20
    LDMFD SP!, {R4, R5, R6, R7, R8, R9, R10, FP}
    LDR R6, =10000
    ADD SP, SP, R6
    B min_caml_exit
