package com.ilinklink.tg.utils;

import com.spc.posesdk.bean.ActionRules;

import java.util.ArrayList;

/**
 * ActionRulesUtils
 * Created By:Chuck
 * Des:
 * on 2022/6/22 10:03
 */
public  class ActionRulesUtils {



    /**
     * 双杠规则
     * rules = [['up','left_shoulder','left_elbow','left_wrist',">=",150,"上撑不到位"],
     *   ['down','left_shoulder','left_elbow','left_wrist',"<=",100,"下放不到位"]])
     * @return
     */
    public static ArrayList<ActionRules> crateParallelbarPullupActionRules() {
        ArrayList<ActionRules> actionRules = new ArrayList<>();

        ActionRules rules = new ActionRules();
        rules.setClassName("up");
        rules.setP1Name("LEFT_SHOULDER");
        rules.setP2Name("LEFT_ELBOW");
        rules.setP3Name("LEFT_WRIST");
        rules.setCompareBy(">=");
        rules.setDegree(150);
        rules.setDescription("上撑不到位");
        actionRules.add(rules);

        ActionRules rules2 = new ActionRules();
        rules2.setClassName("down");
        rules2.setP1Name("LEFT_SHOULDER");
        rules2.setP2Name("LEFT_ELBOW");
        rules2.setP3Name("LEFT_WRIST");
        rules2.setCompareBy("<=");
        rules2.setDegree(100);
        rules2.setDescription("下放不到位");
        actionRules.add(rules2);

        return actionRules;
    }

    /**单杠的规则
     * rules = [['up','left_shoulder','left_elbow','left_wrist',"<=",50,"上拉不到位"],
     * ['down','left_elbow','left_shoulder','left_hip',">=",150,"下放不直"]])
     *
     * @return
     */
    public static  ArrayList<ActionRules> crateSiglebarPullupActionRules() {
        ArrayList<ActionRules> actionRules = new ArrayList<>();

        ActionRules rules = new ActionRules();
        rules.setClassName("up");
        rules.setP1Name("LEFT_SHOULDER");
        rules.setP2Name("LEFT_ELBOW");
        rules.setP3Name("LEFT_WRIST");
        rules.setCompareBy("<=");
        rules.setDegree(50);
        rules.setDescription("上拉不到位");
        actionRules.add(rules);

        ActionRules rules2 = new ActionRules();
        rules2.setClassName("down");
        rules2.setP1Name("LEFT_ELBOW");
        rules2.setP2Name("LEFT_SHOULDER");
        rules2.setP3Name("LEFT_HIP");
        rules2.setCompareBy(">=");
        rules2.setDegree(150);
        rules2.setDescription("下放不直");
        actionRules.add(rules2);

        return actionRules;
    }

    /**
     * 仰卧起坐的规则
     * rules = [['up','left_shoulder','left_hip','left_knee',"<=",40,"身体前倾不够"]])
     *
     * @return
     */
    public static  ArrayList<ActionRules> crateSitUpActionRules() {
        ArrayList<ActionRules> actionRules = new ArrayList<>();

        ActionRules rules = new ActionRules();
        rules.setClassName("up");
        rules.setP1Name("LEFT_SHOULDER");
        rules.setP2Name("LEFT_HIP");
        rules.setP3Name("LEFT_KNEE");
        rules.setCompareBy("<=");
        rules.setDegree(40);
        rules.setDescription("身体前倾不够");
        actionRules.add(rules);
        return actionRules;
    }

    /**
     * 俯卧撑的规则
     * rules = [['up','left_shoulder','left_hip','left_knee',">=",160,"身体不直"],
     * ['down','left_shoulder','left_elbow','left_wrist',"<=",110,"下压不足"]])
     *
     * @return
     */
    public static  ArrayList<ActionRules> cratePushUpActionRules() {
        ArrayList<ActionRules> actionRules = new ArrayList<>();

        ActionRules rules = new ActionRules();
        rules.setClassName("up");
        rules.setP1Name("LEFT_SHOULDER");
        rules.setP2Name("LEFT_HIP");
        rules.setP3Name("LEFT_KNEE");
        rules.setCompareBy(">=");
        rules.setDegree(160);
        rules.setDescription("身体不直");
        actionRules.add(rules);

        ActionRules rules2 = new ActionRules();
        rules2.setClassName("down");
        rules2.setP1Name("LEFT_SHOULDER");
        rules2.setP2Name("LEFT_ELBOW");
        rules2.setP3Name("LEFT_WRIST");
        rules2.setCompareBy("<=");
        rules2.setDegree(110);
        rules2.setDescription("下压不足");
        actionRules.add(rules2);

        return actionRules;
    }
}
