import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter JSON test cases, separated by a blank line.");
        System.out.println("Enter two blank lines consecutively to finish input.");

        List<String> allLines = new ArrayList<>();
        int blankLineCount = 0;

        // Read until two consecutive blank lines
        while (true) {
            String line = scanner.nextLine();
            if (line.trim().isEmpty()) {
                blankLineCount++;
                if (blankLineCount == 2) {
                    break;
                }
            } else {
                blankLineCount = 0;
                allLines.add(line);
            }
        }

        // Join all lines into a single string, then split into test cases by single blank line
        String allInput = String.join("\n", allLines);
        String[] testCases = allInput.split("\\n\\s*\\n");  // split by blank line

        for (int i = 0; i < testCases.length; i++) {
            System.out.println("Test case " + (i + 1) + ":");
            processTestCase(testCases[i]);
            System.out.println(); // Blank line between test case outputs
        }
    }

    private static void processTestCase(String jsonInput) {
        int n = extractIntValue(jsonInput, "\"n\"");
        int k = extractIntValue(jsonInput, "\"k\"");

        if (n == -1 || k == -1) {
            System.out.println("Invalid input JSON: missing n or k.");
            return;
        }

        List<BigInteger> roots = new ArrayList<>();

        for (int i = 1; i <= n + 20; i++) {
            String keyStr = "\"" + i + "\":";
            if (!jsonInput.contains(keyStr)) continue;

            String base = extractStringValue(jsonInput, i, "base");
            String value = extractStringValue(jsonInput, i, "value");

            if (base == null || value == null) continue;

            int baseNum;
            try {
                baseNum = Integer.parseInt(base);
            } catch (NumberFormatException e) {
                System.out.println("Invalid base for root " + i);
                return;
            }

            try {
                BigInteger root = new BigInteger(value, baseNum);
                roots.add(root);
            } catch (Exception e) {
                System.out.println("Invalid root value for root " + i);
                return;
            }
        }

        if (roots.size() < k) {
            System.out.println("Not enough roots provided.");
            return;
        }

        BigInteger constantTerm = calculateConstantTerm(roots, k);
        System.out.println("Constant term c = " + constantTerm.toString());
    }

    private static int extractIntValue(String json, String key) {
        int idx = json.indexOf(key);
        if (idx == -1) return -1;
        int colonIdx = json.indexOf(":", idx);
        if (colonIdx == -1) return -1;

        int commaIdx = json.indexOf(",", colonIdx);
        int braceIdx = json.indexOf("}", colonIdx);
        int endIdx = (commaIdx != -1 && commaIdx < braceIdx) ? commaIdx : braceIdx;
        if (endIdx == -1) return -1;

        String valStr = json.substring(colonIdx + 1, endIdx).trim();
        try {
            return Integer.parseInt(valStr);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static String extractStringValue(String json, int keyNum, String field) {
        String keyStr = "\"" + keyNum + "\":";
        int idx = json.indexOf(keyStr);
        if (idx == -1) return null;
        int startIdx = json.indexOf("{", idx);
        int endIdx = json.indexOf("}", startIdx);
        if (startIdx == -1 || endIdx == -1) return null;
        String block = json.substring(startIdx, endIdx + 1);

        String fieldStr = "\"" + field + "\":";
        int fIdx = block.indexOf(fieldStr);
        if (fIdx == -1) return null;
        int colonIdx = block.indexOf(":", fIdx);
        int commaIdx = block.indexOf(",", colonIdx);
        int braceIdx = block.indexOf("}", colonIdx);
        int endFieldIdx = (commaIdx != -1 && commaIdx < braceIdx) ? commaIdx : braceIdx;
        if (endFieldIdx == -1) return null;

        String val = block.substring(colonIdx + 1, endFieldIdx).trim();
        val = val.replace("\"", "");
        return val;
    }

    private static BigInteger calculateConstantTerm(List<BigInteger> roots, int k) {
        BigInteger product = BigInteger.ONE;
        for (int i = 0; i < k; i++) {
            product = product.multiply(roots.get(i));
        }
        if (k % 2 == 1) {
            product = product.negate();
        }
        return product;
    }
}
