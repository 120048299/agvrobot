package com.wootion.protocols.robot.msg;

import lombok.Data;

@Data
public class ActionGoalMsg {
   GoalID goal_id;
   Goal goal;

   public void setId(int id){
      goal_id=new GoalID(String.valueOf(id));
   }
}
