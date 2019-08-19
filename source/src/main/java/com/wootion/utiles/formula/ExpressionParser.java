package com.wootion.utiles.formula;

/**
 * 负责读取表达式生成ExpressionNode对象的类
 *
 * @项目名称: sunson_pams
 * @类名称: ExpressionParser
 * @类描述:
 * @创建人: 唐泽齐
 * @创建时间: 2017年12月15日 上午9:52:59
 * @修改人: 唐泽齐
 * @修改时间: 2017年12月15日 上午9:52:59
 * @修改备注:
 * @version: 1.0
 */
public class ExpressionParser {

    // 当前分析的表达式
    private String expression;

    // 当前读取的位置
    private int position;

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ExpressionParser(String expression) {
        this.expression = expression;
        this.position = 0;
    }

    /**
     * 读取下一个表达式节点,如果读取失败则返回null
     *
     * @return
     */
    public ExpressionNode readNode() {
        ExpressionNode s = new ExpressionNode(null);
        // 空格的位置
        int whileSpacePos = -1;
        boolean flag = false;
        StringBuffer buffer = new StringBuffer(10);
        while (this.position < this.expression.length()) {
            char c = this.expression.charAt(this.position);
            if (c == '"') {
                flag = !flag;
                if (!flag) {
                    this.position++;
                    buffer.append(c);
                    break;
                }
                if (buffer.length() != 0) {
                    break;
                }
            }
            if (flag) {
                this.position++;
                buffer.append(c);
            } else {
                if (s.IsWhileSpace(c)) {
                    if ((whileSpacePos >= 0) && ((this.position - whileSpacePos) > 1)) {
                        throw new ExpressionException(
                                String.format("表达式\"%s\"在位置(%s)上的字符非法!", this.getExpression(), this.getPosition()));
                    }
                    if (buffer.length() == 0) {
                        whileSpacePos = -1;
                    } else {
                        whileSpacePos = this.position;
                    }
                    this.position++;
                    continue;
                }
                if ((buffer.length() == 0) || s.IsCongener(c, buffer.charAt(buffer.length() - 1))) {
                    this.position++;
                    buffer.append(c);
                } else {
                    break;
                }
                if (!s.needMoreOperator(c)) {
                    break;
                }
            }
        }
        if (buffer.length() == 0) {
            return null;
        }
        ExpressionNode node = new ExpressionNode(buffer.toString());
        if (node.getType() == ExpressionNodeType.Unknown) {
            throw new ExpressionException(String.format("表达式\"%s\"在位置%s上的字符\"%s\"非法!", this.getExpression(),
                    this.getPosition() - node.getValue().length(), node.getValue()));
        }
        return node;
    }

}
