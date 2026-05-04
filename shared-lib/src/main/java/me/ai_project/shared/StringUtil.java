package me.ai_project.shared;

public class StringUtil {

    private StringUtil() {
    }

    public static String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public static boolean isBlank(String input) {
        return input == null || input.trim().isEmpty();
    }
}
