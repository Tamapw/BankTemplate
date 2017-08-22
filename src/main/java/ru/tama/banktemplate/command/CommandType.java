package ru.tama.banktemplate.command;

/**
 * Перечисление команд, которые могут быть получены у пользователя.
 * <li>{@link #COMMAND_CREATE}</li>
 * <li>{@link #COMMAND_START}</li>
 * <li>{@link #COMMAND_STOP}</li>
 * <li>{@link #COMMAND_TRANSFER}</li>
 * <li>{@link #COMMAND_ADD_ACCOUNTS}</li>
 * <li>{@link #COMMAND_ADD_ACCOUNT_WITH_MONEY}</li>
 * <li>{@link #COMMAND_PRINT_ACCOUNTS}</li>
 * <li>{@link #COMMAND_PRINT_BANKS}</li>
 * <li>{@link #COMMAND_EXIT}</li>
 *
 * @author tama
 */
public enum CommandType {
    /**
     * Запуск банка в системе. <br>
     * Формат команды: "start <i>name</i>", где <br>
     * &emsp;  <i>name</i> - имя банка в системе.<br>
     * example: start sbt<br>
     */
    COMMAND_START("start"),

    /**
     * Остановка банка в системе, сохранение всех совершённых операций на диск.<br>
     * Формат команды: "stop", т.е. без аргументов.<br>
     * <br>
     * example: stop<br>
     */
    COMMAND_STOP("stop"),

    /**
     * Перевод между счетами. <br>
     * Счет являет собой: уникальный id в банке-номер банка. "1234-18", где 1234 - номер счёта в банке 18.<br>
     * Формат команды: "transfer <i>from</i> <i>to</i> <i>money</i>", где <br>
     * &emsp;  <i>from</i> - счёт, с которого совершается перевод, <br>
     * &emsp;  <i>to</i> - счёт, куда совершается перевод,<br>
     * &emsp;  <i>money</i> - количество денег, которые нужно перевести.<br>
     * example: transfer 13563-12 23623-1 500<br>
     */
    COMMAND_TRANSFER("transfer"),

    /**
     * Вывод всех лицевых счетов и их баланс на экран. Вначале выводятся лицевые счета пользователей, <br>
     * затем лицевые счета банков(корреспондетские счета).<br>
     * Формат команды "print -a", т.е. с аргументом -a.<br>
     * <br>
     * example: print -a<br>
     */
    COMMAND_PRINT_ACCOUNTS("print -a"),

    /**
     * Вывод список существующих банков в системе и их номера. <br>
     * Формат команды: "print -b", т.е. с аргументом -b. <br>
     * <br>
     * example: print -b<br>
     */
    COMMAND_PRINT_BANKS("print -b"),

    /**
     * Создаёт банк в системе.<br>
     * Формат команды: "create <i>name</i>", где
     * &emsp;  <i>name</i> - имя банка, который необходимо создать.<br>
     * example: create sbt<br>
     */
    COMMAND_CREATE("create"),

    /**
     * Выходит из программы. Останавливает банк, если таковой был запущен и сохраняет данные. <br>
     * Формат команды: "exit", т.е. без аргументов. <br>
     * <br>
     * example: exit
     */
    COMMAND_EXIT("exit"),

    /**
     * Добавляет новые пользовательские лицевые счета в банк. <br>
     * Формат команды: "add -a <i>count</i>", где <br>
     * &emsp;  <i>count</i> - количество счетов, которые нужно добавить. <br>
     * example: add -a 10
     */
    COMMAND_ADD_ACCOUNTS("add -a"),

    /**
     * Добавляет новый пользовательский лицевой счет в банк, с указанной суммой на счету. <br>
     * Формат команды: "add -p <i>money</i>", где <br>
     * &emsp;  <i>money</i> - сумма денег, которая должна быть на новом счёте.. <br>
     * example: add -p 1231
     */
    COMMAND_ADD_ACCOUNT_WITH_MONEY("add -p");


    private String description;

    CommandType(String description) {
        this.description = description;
    }

    public static CommandType parse(String... args) {
        String command = args[0];
        CommandType commandType = null;

        switch (command) {
            case "start": {
                commandType = COMMAND_START;
            } break;

            case "stop": {
                commandType = COMMAND_STOP;
            } break;

            case "create": {
                commandType = COMMAND_CREATE;
            } break;

            case "transfer": {
                commandType = COMMAND_TRANSFER;
            } break;

            case "print": {
                if (args.length < 2) {
                    throw new IllegalArgumentException("Не передан аргумент команды print");
                }

                String argument = args[1];
                switch (argument) {
                    case "-a": {
                        commandType = COMMAND_PRINT_ACCOUNTS;
                    } break;

                    case "-b": {
                        commandType = COMMAND_PRINT_BANKS;
                    } break;

                    default: {
                        throw new IllegalArgumentException("Передан не существующий аргумент команды print: " + argument);
                    }
                }
            } break;

            case "exit": {
                commandType = COMMAND_EXIT;
            } break;

            case "add": {
                if (args.length < 2) {
                    throw new IllegalArgumentException("Не передана аргумент команды add");
                }

                String argument = args[1];
                switch (argument) {
                    case "-a": {
                        commandType = COMMAND_ADD_ACCOUNTS;
                    } break;

                    case "-p": {
                        commandType = COMMAND_ADD_ACCOUNT_WITH_MONEY;
                    } break;

                    default: {
                        throw new IllegalArgumentException("Передан несуществующий аргумент команды add");
                    }
                }
            } break;

            default: {
                throw new IllegalArgumentException("Передана не существующая команда.");
            }
        }

        return commandType;
    }

    @Override
    public String toString() {
        return description;
    }

}
