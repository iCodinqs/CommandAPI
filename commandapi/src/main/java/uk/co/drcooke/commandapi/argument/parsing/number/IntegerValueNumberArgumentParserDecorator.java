/*
 * Copyright 2018 David Cooke
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package uk.co.drcooke.commandapi.argument.parsing.number;

import uk.co.drcooke.commandapi.annotations.argument.numeric.IntClamp;
import uk.co.drcooke.commandapi.argument.parsing.ArgumentParser;
import uk.co.drcooke.commandapi.argument.parsing.IllegalInputException;
import uk.co.drcooke.commandapi.argument.parsing.CommandParameter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class IntegerValueNumberArgumentParserDecorator implements ArgumentParser<Number>{

    private final ArgumentParser<Number> parser;
    private static final Map<Class<? extends Number>, Function<IntClamp, Consumer<Number>>> INTEGER_CLAMP_VALIDATION_FUNCTIONS;
    private static final Map<Class<? extends Number>, Function<IntClamp, UnaryOperator<Number>>> INTEGER_CLAMP_LIMITING_FUNCTIONS;

    static {
        Map<Class<? extends Number>, Function<IntClamp, Consumer<Number>>> validators = new HashMap<>();
        validators.put(Byte.class, intClamp -> number -> ensureNumberInBounds(number.byteValue(), (byte)intClamp.min(), (byte)intClamp.max()));
        validators.put(Short.class, intClamp -> number -> ensureNumberInBounds(number.shortValue(), (short)intClamp.min(), (short)intClamp.max()));
        validators.put(Integer.class, intClamp -> number -> ensureNumberInBounds(number.intValue(), (int)intClamp.min(), (int)intClamp.max()));
        validators.put(Long.class, intClamp -> number -> ensureNumberInBounds(number.longValue(), intClamp.min(), intClamp.max()));
        validators.put(Double.class, intClamp -> number -> ensureNumberInBounds(number.doubleValue(), (double)intClamp.min(), (double)intClamp.max()));
        validators.put(Float.class, intClamp -> number -> ensureNumberInBounds(number.floatValue(), (float)intClamp.min(), (float)intClamp.max()));
        validators.put(BigInteger.class, intClamp -> number -> ensureNumberInBounds(BigInteger.valueOf(number.longValue()), BigInteger.valueOf(intClamp.min()), BigInteger.valueOf(intClamp.max())));
        validators.put(BigDecimal.class, intClamp -> number -> ensureNumberInBounds(BigDecimal.valueOf(number.doubleValue()), BigDecimal.valueOf(intClamp.min()), BigDecimal.valueOf(intClamp.max())));
        INTEGER_CLAMP_VALIDATION_FUNCTIONS = validators;
        Map<Class<? extends Number>, Function<IntClamp, UnaryOperator<Number>>> limiters = new HashMap<>();
        limiters.put(Byte.class, intClamp -> number -> Math.max(intClamp.min(), Math.min(intClamp.max(), (byte)number)));
        limiters.put(Short.class, intClamp -> number -> Math.max(intClamp.min(), Math.min(intClamp.max(), (short)number)));
        limiters.put(Integer.class, intClamp -> number -> Math.max(intClamp.min(), Math.min(intClamp.max(), (int)number)));
        limiters.put(Long.class, intClamp -> number -> Math.max(intClamp.min(), Math.min(intClamp.max(), (long)number)));
        limiters.put(Double.class, intClamp -> number -> Math.max(intClamp.min(), Math.min(intClamp.max(), (double)number)));
        limiters.put(Float.class, intClamp -> number -> Math.max(intClamp.min(), Math.min(intClamp.max(), (float)number)));
        limiters.put(BigInteger.class, intClamp -> number -> BigInteger.valueOf(number.longValue()).max(BigInteger.valueOf(intClamp.min())).min(BigInteger.valueOf(intClamp.max())));
        limiters.put(BigDecimal.class, intClamp -> number -> BigDecimal.valueOf(number.doubleValue()).max(BigDecimal.valueOf(intClamp.min()).min(BigDecimal.valueOf(intClamp.max()))));
        INTEGER_CLAMP_LIMITING_FUNCTIONS = limiters;
    }

    public static <T extends Number & Comparable<T>> void ensureNumberInBounds(T number, T min, T max){
        if(number.compareTo(max) > 0){
            throw new ArgumentOutOfBoundsException("Number " + number + " must be less than or equal to " + max);
        }
        if(number.compareTo(min) < 0){
            throw new ArgumentOutOfBoundsException("Number " + number + " must be more than or equal to " + min);
        }
    }

    public IntegerValueNumberArgumentParserDecorator(ArgumentParser<Number> parser) {
        this.parser = parser;
    }

    @Override
    public Number parse(Deque<String> arguments, CommandParameter commandParameter) {
        return clampIfNecessary(parser.parse(arguments, commandParameter), commandParameter);
    }

    @Override
    public boolean canParseParameter(CommandParameter commandParameter) {
        return parser.canParseParameter(commandParameter);
    }

    public static Number clampIfNecessary(Number number, CommandParameter commandParameter){
        if(commandParameter.isAnnotationPresent(IntClamp.class)){
            return clamp(number, commandParameter.getAnnotation(IntClamp.class));
        }else{
            return number;
        }
    }

    public static Number clamp(Number number, IntClamp clamp){
        switch(clamp.method()){
            case LIMIT:{
                return limitNumberToClamp(number, clamp);
            }
            case VALIDATE:{
                validateNumberWithinClamp(number, clamp);
            }
            default:{
                return number;
            }
        }
    }

    public static Number limitNumberToClamp(Number number, IntClamp clamp){
        Function<IntClamp, UnaryOperator<Number>> limiter = INTEGER_CLAMP_LIMITING_FUNCTIONS.get(number.getClass());
        if(limiter == null){
            throw new IllegalInputException("Unknown number type " + number.getClass().getCanonicalName());
        }else{
            return limiter.apply(clamp).apply(number);
        }
    }

    public static void validateNumberWithinClamp(Number number, IntClamp clamp){
        Function<IntClamp, Consumer<Number>> validator = INTEGER_CLAMP_VALIDATION_FUNCTIONS.get(number.getClass());
        if(validator == null){
            throw new IllegalInputException("Unknown number type " + number.getClass().getCanonicalName());
        }else{
            validator.apply(clamp).accept(number);
        }
    }
}
