package com.tommy.urlshortener.lock

import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext

object SpELParser {

    fun getDynamicValue(parameterNames: Array<String>, args: Array<Any>, key: String): Any? {
        val parser: ExpressionParser = SpelExpressionParser()
        val context = StandardEvaluationContext()

        parameterNames.indices.forEach {
            context.setVariable(parameterNames[it], args[it])
        }

        return parser.parseExpression(key).getValue(context, Any::class.java)
    }
}
