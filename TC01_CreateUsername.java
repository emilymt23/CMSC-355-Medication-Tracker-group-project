public class TC01_CreateUsername {

    public static boolean createUsername(String username) {
        // Simulate username validation: between 5-50 chars and starts with uppercase
        return username.length() >= 5 && username.length() <= 50 && Character.isUpperCase(username.charAt(0));
    }

    public static void main(String[] args) {
        String input = "TestUser";
        boolean expected = true;
        boolean actual = createUsername(input);

        System.out.println("TC01: Create Username (valid input)");
        System.out.println("Input: " + input);
        System.out.println("Expected: " + expected);
        System.out.println("Actual: " + actual);
        System.out.println(actual == expected ? "? PASS" : "? FAIL");
    }
}