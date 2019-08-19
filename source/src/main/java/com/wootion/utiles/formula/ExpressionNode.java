package com.wootion.utiles.formula;


import com.github.pagehelper.util.StringUtil;

/**
 * 存储表达式运算符或操作数的各个节点的类
 *
 * @项目名称: sunson_pams
 * @类名称: ExpressionNode
 * @类描述:
 * @创建人: 唐泽齐
 * @创建时间: 2017年12月15日 上午9:50:50
 * @修改人: 唐泽齐
 * @修改时间: 2017年12月15日 上午9:50:50
 * @修改备注:
 * @version: 1.0
 */
public class ExpressionNode {

    private String value;

    private ExpressionNodeType type;

    private int pri;

    private ExpressionNode unitaryNode;

    private Object numeric;

    /**
     *
     * @param value
     *            操作数或运算符
     */
    public ExpressionNode(String value) {
        this.value = value;
        this.type = parseNodeType(value);
        this.pri = getNodeTypePRI(this.type);
        this.numeric = null;
    }

    public Object getNumeric() {
        if (this.numeric == null) {

            if ((this.type == ExpressionNodeType.String) || (this.type == ExpressionNodeType.Date)) {
                return this.value;
            }

            if (this.type != ExpressionNodeType.Numeric) {
                return 0;
            }
            Double num = new Double(this.value);
            if (this.unitaryNode != null && this.unitaryNode.type == ExpressionNodeType.Subtract) {
                num = 0 - num;
            }
            this.numeric = num;
        }
        return numeric;
    }

    public void setNumeric(Object numeric) {
        this.numeric = numeric;
        this.value = this.numeric.toString();
    }

    /**
     * 设置或返回与当前节点相关联的一元操作符节点
     *
     * @param unitaryNode
     */
    public void setUnitaryNode(ExpressionNode unitaryNode) {
        this.unitaryNode = unitaryNode;
    }

    /**
     * 解析节点类型
     *
     * @param value
     * @return
     */
    private ExpressionNodeType parseNodeType(String value) {
        if (StringUtil.isEmpty(value)) {
            return ExpressionNodeType.Unknown;
        }
        switch (value) {
            case "+":
                return ExpressionNodeType.Plus;
            case "-":
                return ExpressionNodeType.Subtract;
            case "*":
                return ExpressionNodeType.MultiPly;
            case "/":
                return ExpressionNodeType.Divide;
            case "%":
                return ExpressionNodeType.Mod;
            case "^":
                return ExpressionNodeType.Power;
            case "(":
                return ExpressionNodeType.LParentheses;
            case ")":
                return ExpressionNodeType.RParentheses;
            case "&":
                return ExpressionNodeType.BitwiseAnd;
            case "|":
                return ExpressionNodeType.BitwiseOr;
            case "&&":
            case "<并且>":
            case "并且":
            case "and":
                return ExpressionNodeType.And;
            case "||":
            case "<或者>":
            case "或者":
            case "or":
                return ExpressionNodeType.Or;
            case "!":
                return ExpressionNodeType.Not;
            case "==":
            case "=":
                return ExpressionNodeType.Equal;
            case "!=":
            case "<>":
            case "≠":
                return ExpressionNodeType.Unequal;
            case ">":
                return ExpressionNodeType.GT;
            case "<":
                return ExpressionNodeType.LT;
            case ">=":
            case "≥":
                return ExpressionNodeType.GTOrEqual;
            case "<=":
            case "≤":
                return ExpressionNodeType.LTOrEqual;
            case "<<":
                return ExpressionNodeType.LShift;
            case ">>":
                return ExpressionNodeType.RShift;
            case "@":
            case "<包含>":
            case "包含":
                return ExpressionNodeType.Like;
            case "!@":
            case "<不包含>":
            case "不包含":
                return ExpressionNodeType.NotLike;
            case "!!$":
                return ExpressionNodeType.StartWith;
            case "!!@":
                return ExpressionNodeType.EndWith;

        }
        if (isNumerics(value)) {
            return ExpressionNodeType.Numeric;
        }
        if (isDatetime(value)) {
            return ExpressionNodeType.Date;
        }
        if (value.contains("\"")) {
            return ExpressionNodeType.String;
        }
        return ExpressionNodeType.Unknown;
    }

    /**
     * 获取各节点类型的优先级
     *
     * @param nodeType
     * @return
     */
    private int getNodeTypePRI(ExpressionNodeType nodeType) {
        switch (nodeType) {
            case LParentheses:
            case RParentheses:
                return 9;
            // 逻辑非是一元操作符,所以其优先级较高
            case Not:
                return 8;
            case Mod:
                return 7;
            case MultiPly:
            case Divide:
            case Power:
                return 6;
            case Plus:
            case Subtract:
                return 5;
            case LShift:
            case RShift:
                return 4;
            case BitwiseAnd:
            case BitwiseOr:
                return 3;
            case Equal:
            case Unequal:
            case GT:
            case LT:
            case GTOrEqual:
            case LTOrEqual:
            case Like:
            case NotLike:
            case StartWith:
            case EndWith:
                return 2;
            case And:
            case Or:
                return 1;
            default:
                return 0;
        }

    }

    /**
     * 判断是否为数值
     *
     * @param op
     * @return
     */
    public boolean isNumerics(String op) {
        return op.matches("^[\\+\\-]?(0|[1-9]\\d*|[1-9]\\d*\\.\\d+|0\\.\\d+)");
    }

    /**
     * 判断是否为日期
     *
     * @param op
     * @return
     */
    public static boolean isDatetime(String op) {
        op = op.replace("\"", "").trim();
        return op.matches("\\d{4}\\-\\d{2}\\-\\d{2}(\\s\\d{2}\\:\\d{2}\\:\\d{2})?");
    }

    /**
     * 判断某个字符后是否需要更多的操作符
     *
     * @param c
     * @return
     */
    public boolean needMoreOperator(char c) {
        switch (c) {
            case '&':
            case '|':
            case '=':
            case '!':
            case '>':
            case '<':
            case '.': // 小数点
            case 'o': ////or
            case 'a': ////and
            case 'n': ////and
            case '并': ////
            case '或': ////
                return true;
        }
        // //数字则需要更多
        return Character.isDigit(c);
    }

    /**
     * 判断两个字符是否是同一类
     *
     * @param c1
     * @param c2
     * @return
     */
    public boolean IsCongener(char c1, char c2) {
        if ((c1 == '(') || (c2 == '(')) {
            return false;
        }
        if ((c1 == ')') || (c2 == ')')) {
            return false;
        }
        if ((c1 == '"') || (c2 == '"')) {
            return false;
        }
        if (Character.isDigit(c1) || (c1 == '.')) {
            // c1为数字,则c2也为数字
            return (Character.isDigit(c2) || (c2 == '.'));
        }
        return (!Character.isDigit(c2) && (c2 != '.'));
    }

    /**
     * 判断某个字符是否是空白字符
     *
     * @param c
     * @return
     */
    public boolean IsWhileSpace(char c) {
        return c == ' ' || c == '\t';
    }

    /**
     * 判断是否是一元操作符节点
     *
     * @param nodeType
     * @return
     */
    public static boolean IsUnitaryNode(ExpressionNodeType nodeType) {
        return (nodeType == ExpressionNodeType.Plus || nodeType == ExpressionNodeType.Subtract);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ExpressionNodeType getType() {
        return type;
    }

    public void setType(ExpressionNodeType type) {
        this.type = type;
    }

    public int getPri() {
        return pri;
    }

    public void setPri(int pri) {
        this.pri = pri;
    }

    public ExpressionNode getUnitaryNode() {
        return unitaryNode;
    }
}