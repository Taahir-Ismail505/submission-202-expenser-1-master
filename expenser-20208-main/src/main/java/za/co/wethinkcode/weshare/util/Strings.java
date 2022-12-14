package za.co.wethinkcode.weshare.util;

/**
 * Convenience functions for working with Strings.
 * It was quicker to write a simple one-liner than find a library to do the same.
 *
 * You are welcome to add other convenience functions for strings here.
 */
public class Strings {
    /**
     * Change the first letter of a string to upper case.
     * @param string The string to transform
     * @return the string with the first letter capitalized
     */
    public static String capitaliseFirstLetter(String string) {
        return string.substring(0, 1)
                .toUpperCase() + string.substring(1);
    }
}
