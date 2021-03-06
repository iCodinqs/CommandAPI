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

package uk.co.drcooke.commandapi.bukkit.security;

import uk.co.drcooke.commandapi.bukkit.annotations.user.ConsoleOnly;
import uk.co.drcooke.commandapi.bukkit.annotations.user.PlayerOnly;
import uk.co.drcooke.commandapi.execution.ExitCode;
import uk.co.drcooke.commandapi.execution.executable.CommandExecutable;
import uk.co.drcooke.commandapi.execution.executor.CommandExecutor;
import uk.co.drcooke.commandapi.security.User;

import java.util.Deque;

public class CommandSenderTypeCheckingCommandExcecutorDecorator implements CommandExecutor{

    private CommandExecutor commandExecutor;

    public CommandSenderTypeCheckingCommandExcecutorDecorator(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    @Override
    public ExitCode execute(CommandExecutable executable, Deque<String> arguments, User user) {
        if(executable.isAnnotationPresent(PlayerOnly.class)){
            if(!"CONSOLE".equals(user.getName())){
                return commandExecutor.execute(executable, arguments, user);
            }
        }else if(executable.isAnnotationPresent(ConsoleOnly.class)){
            if("CONSOLE".equals(user.getName())){
                return commandExecutor.execute(executable, arguments, user);
            }
        }
        return ExitCode.NO_PERMISSION;
    }
}
