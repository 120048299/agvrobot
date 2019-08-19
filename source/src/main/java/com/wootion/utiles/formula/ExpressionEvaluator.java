package com.wootion.utiles.formula;

import com.github.pagehelper.util.StringUtil;
import com.wootion.agvrobot.utils.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

/**
 * 解析公式并返回结果的类
 *
 * @项目名称: sunson_pams
 * @类名称: ExpressionEvaluator
 * @类描述:
 * @创建人: 唐泽齐
 * @创建时间: 2017年12月15日 上午9:56:08
 * @修改人: 唐泽齐
 * @修改时间: 2017年12月15日 上午9:56:08
 * @修改备注:
 * @version: 1.0
 */
public class ExpressionEvaluator {

    ExpressionEvaluator() {

    }

    /**
     * 将算术表达式转换为逆波兰表达式
     *
     * @param expression
     *            要计算的表达式,如"1+2+3+4"
     * @return
     */
    private static List<ExpressionNode> parseExpression(String expression) {
        if (StringUtil.isEmpty(expression)) {
            return new ArrayList<ExpressionNode>();
        }

        List<ExpressionNode> listOperator = new ArrayList<ExpressionNode>(10);
        Stack<ExpressionNode> stackOperator = new Stack<ExpressionNode>();

        ExpressionParser expParser = new ExpressionParser(expression);
        ExpressionNode beforeExpNode = null; // 前一个节点
        ExpressionNode unitaryNode = null; // 一元操作符
        ExpressionNode expNode;
        // 是否需要操作数
        boolean requireOperand = false;

        while ((expNode = expParser.readNode()) != null) {
            if ((expNode.getType() == ExpressionNodeType.Numeric)
                    || (expNode.getType() == ExpressionNodeType.String)
                    || (expNode.getType() == ExpressionNodeType.Date)) {
                // 操作数， 直接加入后缀表达式中
                if (unitaryNode != null) {
                    // 设置一元操作符节点
                    expNode.setUnitaryNode(unitaryNode);
                    unitaryNode = null;
                }

                listOperator.add(expNode);
                requireOperand = false;
                continue;
            } else if (expNode.getType() == ExpressionNodeType.LParentheses) {
                // 左括号， 直接加入操作符栈
                stackOperator.push(expNode);
                continue;
            } else if (expNode.getType() == ExpressionNodeType.RParentheses) {
                // 右括号则在操作符栈中反向搜索，直到遇到匹配的左括号为止，将中间的操作符依次加到后缀表达式中。
                ExpressionNode lpNode = null;
                while (stackOperator.size() > 0) {
                    lpNode = stackOperator.pop();
                    if (lpNode.getType() == ExpressionNodeType.LParentheses)
                        break;
                    listOperator.add(lpNode);
                }
                if (lpNode == null || lpNode.getType() != ExpressionNodeType.LParentheses) {
                    throw new ExpressionException(String.format("在表达式\"%s\"中没有与在位置(%s)上\")\"匹配的\"(%s)\"字符!",expParser.getExpression(), expParser.getPosition()));
                }
            } else {
                if (stackOperator.size() == 0) {
                    // 第一个节点则判断此节点是否是一元操作符"+,-,!,("中的一个,否则其它都非法
                    if (listOperator.size() == 0 && !(expNode.getType() == ExpressionNodeType.LParentheses
                            || expNode.getType() == ExpressionNodeType.Not)) {
                        // 后缀表达式没有任何数据则判断是否是一元操作数
                        if (ExpressionNode.IsUnitaryNode(expNode.getType())) {
                            unitaryNode = expNode;
                        } else {
                            // 丢失操作数
                            throw new ExpressionException(String.format("表达式\"%s\"在位置(%s)上缺少操作数!",
                                    expParser.getExpression(), expParser.getPosition()));
                        }
                    } else {
                        // 直接压入操作符栈
                        stackOperator.push(expNode);
                    }
                    requireOperand = true; // 下一个节点需要操作数
                    continue;
                } else {
                    if (requireOperand) {
                        // 如果需要操作数则判断当前的是否是"+","-"号(一元操作符),如果是则继续
                        if (ExpressionNode.IsUnitaryNode(expNode.getType()) && unitaryNode == null) {
                            unitaryNode = expNode;
                        } else {
                            // 丢失操作数
                            throw new ExpressionException(String.format("表达式\"%s\"在位置({1})上缺少操作数!",
                                    expParser.getExpression(), expParser.getPosition()));
                        }
                    } else {
                        // 对前面的所有操作符进行优先级比较
                        do {
                            // 取得上一次的操作符
                            beforeExpNode = stackOperator.peek();

                            // 如果前一个操作符优先级较高，则将前一个操作符加入后缀表达式中
                            if (beforeExpNode.getType() != ExpressionNodeType.LParentheses
                                    && (beforeExpNode.getPri() - expNode.getPri()) >= 0) {
                                listOperator.add(stackOperator.pop());
                            } else {
                                break;
                            }

                        } while (stackOperator.size() > 0);

                        // 将操作符压入操作符栈
                        stackOperator.push(expNode);
                        requireOperand = true;
                    }
                }
            }
        }

        if (requireOperand) {
            // 丢失操作数
            throw new ExpressionException(
                    String.format("表达式\"%s\"在位置({1})上缺少操作数!", expParser.getExpression(), expParser.getPosition()));
        }
        // 清空堆栈
        while (stackOperator.size() > 0) {
            // 取得操作符
            beforeExpNode = stackOperator.pop();
            if (beforeExpNode.getType() == ExpressionNodeType.LParentheses) {
                throw new ExpressionException(String.format("表达式\"%s\"中括号不匹配,丢失右括号!", expParser.getExpression(),
                        expParser.getPosition()));
            }
            listOperator.add(beforeExpNode);
        }

        return listOperator;
    }

    /**
     * 对逆波兰表达式进行计算
     *
     * @param nodes
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked", "incomplete-switch" })
    private static  Object CalcExpression(List<ExpressionNode> nodes) {
        if (nodes == null || nodes.size() == 0)
            return null;

        if (nodes.size() > 1) {
            int index = 0;
            // 储存数据
            ArrayList values = new ArrayList();
            while (index < nodes.size()) {
                ExpressionNode node = nodes.get(index);

                switch (node.getType()) {
                    // 如果是数字，则将值存入 values 中
                    case Numeric:
                    case String:
                    case Date:
                        values.add(node.getNumeric());
                        index++;
                        break;
                    default:
                        // 二元表达式，需要二个参数， 如果是Not的话，则只要一个参数
                        int paramCount = 2;
                        if (node.getType() == ExpressionNodeType.Not)
                            paramCount = 1;
                        // 计算操作数的值
                        if (values.size() < paramCount) {
                            throw new ExpressionException("缺少操作数");
                        }
                        // 传入参数
                        Object[] data = new Object[paramCount];
                        for (int i = 0; i < paramCount; i++) {
                            data[i] = values.get(index - paramCount + i);
                        }
                        // 将计算结果再存入当前节点
                        node.setNumeric(calculate(node.getType(), data));
                        node.setType(ExpressionNodeType.Numeric);
                        // 将操作数节点删除
                        for (int i = 0; i < paramCount; i++) {
                            nodes.remove(index - i - 1);
                            values.remove(index - i - 1);
                        }
                        index -= paramCount;
                        break;
                }

            }
        }

        if (nodes.size() != 1) {
            throw new ExpressionException("缺少操作符或操作数");
        }
        switch (nodes.get(0).getType()) {
            case Numeric:
                return nodes.get(0).getNumeric();

            case String:
            case Date:
                return nodes.get(0).getNumeric().toString().replace("\"", "");
        }
        throw new ExpressionException("缺少操作数");
    }

    /**
     * 计算节点的值
     *
     * @param nodeType
     *            节点的类型
     * @param data
     *            要计算的值,有可能是两位或一位数
     * @return
     */
    @SuppressWarnings("incomplete-switch")
    private static  Object calculate(ExpressionNodeType nodeType, Object[] data) {
        double d1, d2;
        boolean b1, b2;
        Date time1, time2;
        Object obj1 = data[0];
        if(nodeType == ExpressionNodeType.Not ) { //zhou.fei 20180622  提前处理，因为只有一个操作数
            b1 = ConvertToBool(obj1);
            return !b1;
        }

        Object obj2 = data[1];
        String str1 = obj1.toString();
        String str2 = obj2.toString();

        boolean dateflag = ExpressionNode.isDatetime(str1) || ExpressionNode.isDatetime(str2);
        boolean strflag = str1.contains("\"") || str2.contains("\"");
        str1 = str1.replace("\"", "");
        str2 = str2.replace("\"", "");

        switch (nodeType) {
            case Plus:
                if (!strflag) {
                    d1 = ConvertToDecimal(obj1);
                    d2 = ConvertToDecimal(obj2);
                    return (d1 + d2);
                }
                return new StringBuffer(str1 + str2).toString();
            case Subtract:
                d1 = ConvertToDecimal(obj1);
                d2 = ConvertToDecimal(obj2);
                return d1 - d2;
            case MultiPly:
                d1 = ConvertToDecimal(obj1);
                d2 = ConvertToDecimal(obj2);
                return d1 * d2;
            case Divide:
                d1 = ConvertToDecimal(obj1);
                d2 = ConvertToDecimal(obj2);
                if (d2 == 0)
                    throw new RuntimeException();
                return d1 / d2;
            case Power:
                d1 = ConvertToDecimal(obj1);
                d2 = ConvertToDecimal(obj2);
                return Math.pow((double) d1, (double) d2);
            case Mod:
                d1 = ConvertToDecimal(obj1);
                d2 = ConvertToDecimal(obj2);
                if (d2 == 0)
                    throw new RuntimeException();
                return d1 % d2;
            case BitwiseAnd:
                d1 = ConvertToDecimal(obj1);
                d2 = ConvertToDecimal(obj2);
                return (int) d1 & (int) d2;
            case BitwiseOr:
                d1 = ConvertToDecimal(obj1);
                d2 = ConvertToDecimal(obj2);
                return (int) d1 | (int) d2;
            case And:
                b1 = ConvertToBool(obj1);
                b2 = ConvertToBool(obj2);
                return b1 && b2;
            case Or:
                b1 = ConvertToBool(obj1);
                b2 = ConvertToBool(obj2);
                return b1 || b2;
            /*case Not:
                b1 = ConvertToBool(obj1);
                return !b1;
            */case Equal:
                if (!dateflag) {
                    if (strflag) {
                        return str1.equals(str2);
                    }
                    d1 = ConvertToDecimal(obj1);
                    d2 = ConvertToDecimal(obj2);
                    return (d1 == d2);
                }
                time1 = DateUtil.parseDate(str1);
                time2 = DateUtil.parseDate(str2);

                return (time1.getTime() == time2.getTime());
            case Unequal:
                if (!dateflag) {
                    if (strflag) {
                        return (!str1.equals(str2));
                    }
                    d1 = ConvertToDecimal(obj1);
                    d2 = ConvertToDecimal(obj2);
                    return (d1 != d2);
                }
                time1 = DateUtil.parseDate(str1);
                time2 = DateUtil.parseDate(str2);

                return (time1.getTime() != time2.getTime());
            case GT:

                if (!dateflag) {
                    d1 = ConvertToDecimal(obj1);
                    d2 = ConvertToDecimal(obj2);
                    return (d1 > d2);
                }
                time1 = DateUtil.parseDate(str1);
                time2 = DateUtil.parseDate(str2);
                return (time1.getTime() > time2.getTime());

            case LT:

                if (!dateflag) {
                    d1 = ConvertToDecimal(obj1);
                    d2 = ConvertToDecimal(obj2);
                    return (d1 < d2);
                }
                time1 = DateUtil.parseDate(str1);
                time2 = DateUtil.parseDate(str2);
                return (time1.getTime() < time2.getTime());

            case GTOrEqual:

                if (!dateflag) {
                    d1 = ConvertToDecimal(obj1);
                    d2 = ConvertToDecimal(obj2);
                    return (d1 >= d2);
                }
                time1 = DateUtil.parseDate(str1);
                time2 = DateUtil.parseDate(str2);
                return (time1.getTime() >= time2.getTime());

            case LTOrEqual:
                if (!dateflag) {
                    d1 = ConvertToDecimal(obj1);
                    d2 = ConvertToDecimal(obj2);
                    return (d1 <= d2);
                }
                time1 = DateUtil.parseDate(str1);
                time2 = DateUtil.parseDate(str2);
                return (time1.getTime() <= time2.getTime());
            case LShift:
                d1 = ConvertToDecimal(obj1);
                d2 = ConvertToDecimal(obj2);
                return (long) d1 << (int) d2;

            case RShift:
                d1 = ConvertToDecimal(obj1);
                d2 = ConvertToDecimal(obj2);
                return (long) d1 >> (int) d2;
            case Like:
                if (!strflag) {
                    return false;
                }
                return str1.contains(str2);
            case NotLike:
                if (!strflag) {
                    return false;
                }
                return !str1.contains(str2);
            case StartWith:
                if (!strflag) {
                    return false;
                }
                return str1.startsWith(str2);
            case EndWith:
                if (!strflag) {
                    return false;
                }
                return str1.endsWith(str2);
        }

        return 0;
    }

    /**
     * 某个值转换为bool值
     *
     * @param value
     * @return
     */
    private static  Boolean ConvertToBool(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else {
            return value != null;
        }
    }

    /**
     * 将某个值转换为decimal值
     *
     * @param value
     * @return
     */
    private static  Double ConvertToDecimal(Object value) {
        if (value instanceof Boolean) {
            return ((Boolean) value ? 1d : 0d);
        } else {
            return Double.parseDouble(value.toString());
        }
    }

    /**
     *
     * @param expression
     *            要计算的表达式,如"1+2+3+4"
     * @return 返回计算结果,如果带有逻辑运算符则返回true/false,否则返回数值
     */
    public Object eval(String expression) {
        return CalcExpression(parseExpression(expression));
    }

    public  Object evalThreeOperand(String expression) {
        int index = expression.indexOf("?");
        if (index > -1) {
            String str = expression.substring(0, index);
            String str2 = expression.substring(index + 1);
            index = str2.indexOf(":");

            if (Boolean.parseBoolean((CalcExpression(parseExpression(str))).toString())) {
                return eval(str2.substring(0, index));
            }
            return eval(str2.substring(index + 1));
        }
        return CalcExpression(parseExpression(expression));
    }

}
