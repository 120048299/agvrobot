package com.wootion.protocols.robot.msg;

public class PublishStatus {
    // {"op":"advertise","topic":"\/chatter","id":"0","type":"std_msgs\/String"}
    private String op;
    private String topic;
    //        private String id;
    private RobotCommandAck msg;

    public PublishStatus() {
        this.setOp("publish");
        this.setTopic("/robot_status");
//            this.setId("1");
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

//        public String getId() {
//            return this.id;
//        }
//
//        public void setId(String id) {
//            this.id = id;
//        }

    public RobotCommandAck getMsg() {
        return msg;
    }

    public void setMsg(RobotCommandAck msg) {
        this.msg = msg;
    }

}
