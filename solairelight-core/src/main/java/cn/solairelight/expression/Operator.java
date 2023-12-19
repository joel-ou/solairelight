package cn.solairelight.expression;

/**
 * @author Joel Ou
 */
public enum Operator {
    AND,
    OR;


    public static Operator cast(String operator){
        return Operator.valueOf(operator.toUpperCase());
    }
}
