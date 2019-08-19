package com.wootion.protocols.robot.operation;


public enum OP_STATUS {
    OP_CREATED(0),
    OP_SENDING(1),
    OP_SEND_FAIL(2),
    OP_SEND_SUCC(3),
    OP_RESPONDED(4),
    OP_SEND_TIMEOUT(5);


    private final int value;

    OP_STATUS(int status) {
        this.value = status;
    }

    public final int getValue() {
        return value;
    }

    public static OP_STATUS fromInt(int i) {
        for (OP_STATUS b : OP_STATUS.values()) {
            if (b.getValue() == i) {
                return b;
            }
        }
        return OP_CREATED;
    }

 

}