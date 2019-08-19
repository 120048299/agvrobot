package com.wootion.protocols.robot.msg;

public class MsgNames {

    public final static String node_server = "server";
    public final static String node_cmd_interface = "cmd_interface";
    public final static String node_robot_control = "robot_control";
    public final static String node_status_control = "status_control";
    public final static String node_task_manage = "task_manage";
    public final static String node_preset_rotate = "preset_rotate";
    public final static String node_camera = "camera_node";
    public final static String node_clound_terrace = "cloud_terrace";
    public final static String node_thermal_imager = "thermal_imager";
    public final static String node_move_base = "move_base";




    //心跳与状态
    public final static String topic_server_status = "/server_status";
    public final static String topic_server_status_type = "wootion_msgs/GeneralTopic";

    public final static String topic_robot_status = "/robot_status";
    public final static String topic_robot_status_type = "wootion_msgs/RobotStatus";

    public final static String topic_mode_command = "/mode_cmd";
    public final static String topic_mode_command_type = "/wootion_msgs/GeneralCmd";
    public final static String topic_move_command = "/move_cmd";
    public final static String topic_move_command_type = "/wootion_msgs/MoveCmd";
    public final static String topic_control_command = "/control_cmd";
    public final static String topic_control_command_type = "wootion_msgs/GeneralCmd";

    public final static String topic_control_status = "/control_status";
    public final static String topic_control_status_type = "wootion_msgs/ControlStatus";//ControlCommandAck
    public final static String topic_cmd_action_goal = "/cmd_action_goal";
    public final static String topic_cmd_action_goal_type = "wootion_msgs/GeneralCmd";

    public final static String SERVICE_ADD_REQUEST = "add_two_ints";
    public final static String SERVICE_ADD_REQUEST_TYPE = "wootion_msgs/AddTwoInts";
    public final static String SERVICE_ADD_RESPONSE_TYPE = "wootion_msgs/AddTwoInts";

    public final static String FIBONACCI_GOAL_NAME = "/fibonacci/goal";
    public final static String FIBONACCI_GOAL_TYPE = "wootion_msgs/FibonacciActionGoal";
    public final static String FIBONACCI_FEEDBACK_NAME = "/fibonacci/feedback";
    public final static String FIBONACCI_FEEDBACK_TYPE = "wootion_msgs/FibonacciActionFeedback";
    public final static String FIBONACCI_RESULT_NAME = "/fibonacci/result";
    public final static String FIBONACCI_RESULT_TYPE = "wootion_msgs/FibonacciActionResult";



/*

{"op":"advertise","id":"advertise:/fibonacci/goal:1","type":"beginner_tutorials/FibonacciActionGoal","topic":"/fibonacci/goal","latch":false,"queue_size":100}
{"op":"advertise","id":"advertise:/fibonacci/cancel:2","type":"actionlib_msgs/GoalID","topic":"/fibonacci/cancel","latch":false,"queue_size":100}	145
{"op":"subscribe","id":"subscribe:/fibonacci/status:3","type":"actionlib_msgs/GoalStatusArray","topic":"/fibonacci/status","compression":"none","throttle_rate":0,"queue_length":0}	179
{"op":"subscribe","id":"subscribe:/fibonacci/feedback:4","type":"beginner_tutorials/FibonacciActionFeedback","topic":"/fibonacci/feedback","compression":"none","throttle_rate":0,"queue_length":0}	{"op":"subscribe","id":"subscribe:/fibonacci/result:5","type":"beginner_tutorials/FibonacciActionResult","topic":"/fibonacci/result","compression":"none","throttle_rate":0,"queue_length":0}	189

{"op":"publish","id":"publish:/fibonacci/goal:6","topic":"/fibonacci/goal","msg":{"goal_id":{"stamp":{"secs":0,"nsecs":0},"id":"goal_0.09562925680091583_1560219432442"},"goal":{"order":7}},"latch":false}

{"topic": "/fibonacci/status", "msg": {"header": {"stamp": {"secs": 1560219432, "nsecs": 376792907}, "frame_id": "", "seq": 161}, "status_list": []}, "op": "publish"}	166

{"topic": "/fibonacci/status", "msg": {"header": {"stamp": {"secs": 1560219432, "nsecs": 485929012}, "frame_id": "", "seq": 162}, "status_list": [{"status": 1, "text": "This goal has been accepted by the simple action server", "goal_id": {"stamp": {"secs": 1560219432, "nsecs": 462405920}, "id": "goal_0.09562925680091583_1560219432442"}}]}, "op": "publish"}	358

{"topic": "/fibonacci/feedback", "msg": {"status": {"status": 1, "text": "This goal has been accepted by the simple action server", "goal_id": {"stamp": {"secs": 1560219432, "nsecs": 462405920}, "id": "goal_0.09562925680091583_1560219432442"}}, "header": {"stamp": {"secs": 1560219432, "nsecs": 486495018}, "frame_id": "", "seq": 1}, "feedback": {"sequence": [0, 1, 1]}}, "op": "publish"}

{"topic": "/fibonacci/result", "msg": {"status": {"status": 3, "text": "", "goal_id": {"stamp": {"secs": 1560219432, "nsecs": 462405920}, "id": "goal_0.09562925680091583_1560219432442"}}, "header": {"stamp": {"secs": 1560219438, "nsecs": 488420963}, "frame_id": "", "seq": 1}, "result": {"sequence": [0, 1, 1, 2, 3, 5, 8, 13]}}, "op": "publish"}

*/

    //读表模块
    public final static String topic_read_scale_command = "/read_scale_cmd";
    public final static String topic_read_scale_command_type = "wootion_msgs/ReadScaleCmd";
    public final static String topic_read_scale_ack = "/read_scale_ack";
    public final static String topic_read_scale_ack_type = "wootion_msgs/ReadScaleAck";

    //巡检点设置模块
    public final static String topic_addptzset_command_type = "wootion_msgs/PresetScaleCmd";
    public final static String topic_addptzset_command = "/preset_scale_cmd";
    public final static String topic_addptzset_status_type = "wootion_msgs/PresetScaleAck";
    public final static String topic_addptzset_status = "/preset_scale_ack";

    //读红外模块
    public final static String topic_infrared_command_type = "wootion_msgs/GeneralCmd";
    public final static String topic_infrared_command = "/infrared_cmd";
    public final static String topic_infrared_ack_type = "wootion_msgs/GeneralAck";
    public final static String topic_infrared_ack = "/infrared_ack";

    public final static String topic_virtual_obstacle_command_type = "wootion_msgs/GeneralCmd";
    public final static String topic_virtual_obstacle_command = "/virtual_obstacle_cmd";
    public final static String topic_virtual_obstacle_ack_type = "wootion_msgs/GeneralAck";
    public final static String topic_virtual_obstacle_ack = "/virtual_obstacle_ack";


    public final static String topic_terrace_command_type = "wootion_msgs/GeneralCmd";
    public final static String topic_terrace_command = "/terrace_cmd";
    public final static String topic_terrace_ack_type = "wootion_msgs/GeneralAck";
    public final static String topic_terrace_ack = "/terrace_ack";

    public final static String topic_camera_command = "/camera_cmd";
    public final static String topic_camera_command_type = "wootion_msgs/GeneralCmd";
    public final static String topic_camera_ack = "/camera_ack";
    public final static String topic_camera_ack_type = "wootion_msgs/GeneralAck";


    public final static String topic_preset_rotate_command_type = "wootion_msgs/PresetRotate";
    public final static String topic_preset_rotate_command = "/preset_rotate";


    //业务相关的接口名称
    public final static String service_data_syn = "data_sync";

    public final static String topic_task_status = "/task_status";
    public final static String topic_ptz_status = "/ptz_status";
    public final static String topic_general_type = "wootion_msgs/GeneralTopic";
    public final static String topic_task_event = "/task_event";
    public final static String topic_task_event_type = "wootion_msgs/GeneralTopic";
    public final static String service_result_query = "result_query";
    public final static String service_task_control = "task_control";

    public final static String topic_charge_room_command = "/charge_room";



}
