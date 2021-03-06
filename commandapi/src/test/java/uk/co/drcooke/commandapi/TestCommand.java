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

package uk.co.drcooke.commandapi;

import uk.co.drcooke.commandapi.annotations.argument.numeric.ClampMethod;
import uk.co.drcooke.commandapi.annotations.argument.numeric.IntClamp;
import uk.co.drcooke.commandapi.annotations.command.Command;
import uk.co.drcooke.commandapi.annotations.command.DefaultHandler;
import uk.co.drcooke.commandapi.annotations.command.Subcommand;
import uk.co.drcooke.commandapi.execution.ExitCode;

@Command("test")
public class TestCommand {

    @DefaultHandler
    public ExitCode test(@IntClamp(min = 0, max = 10) int int1, boolean bool, char c) {
        System.out.println(int1);
        System.out.println(bool);
        System.out.println(c);
        return ExitCode.SUCCESS;
    }

    @Subcommand("test test")
    public ExitCode test(String argument) {
        return ExitCode.SUCCESS;
    }

}
