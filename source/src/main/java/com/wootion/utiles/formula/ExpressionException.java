package com.wootion.utiles.formula;


/**
 * 表达式异常类
 *
 * @项目名称: sunson_pams
 * @类名称: ExpressionException
 * @类描述:
 * @创建人: 唐泽齐
 * @创建时间: 2017年12月15日 上午9:52:13
 * @修改人: 唐泽齐
 * @修改时间: 2017年12月15日 上午9:52:13
 * @修改备注:
 * @version: 1.0
 */
public class ExpressionException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 3136681292988750961L;

    public ExpressionException() {
        super();
    }

    public ExpressionException(String msg) {
        super(msg);
    }

    public ExpressionException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public ExpressionException(Throwable cause) {
        super(cause);
    }
}
