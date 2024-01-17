import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.regex.*;

import static java.lang.Math.abs;

public class Lab5 {
    public static void main(String[] args) {
        try {
            String fileWithInstructionsAdress = "input.txt";
            if (args.length == 1) {
                fileWithInstructionsAdress = args[0];
            }
            File instuctionsFile = new File(fileWithInstructionsAdress);
            Scanner instructionsScanner = new Scanner(instuctionsFile);
            int result = doingCycle(instructionsScanner);
            System.out.println("Works was ended: " + String.valueOf(result));
            return;
        } catch (Exception e) {
            System.out.println("Some problems with instruction file, no work to do");
            return;
        }
    }

    private static int doingCycle(Scanner instuctions) {
        int res = 0;
        while (instuctions.hasNextLine()) {
            String newWork = instuctions.nextLine();
            if (newWork.isEmpty()) {
                continue;
            }
            int isOK = choseOneFunction(newWork);
            if (isOK >= 0) {
                res++;
            } else {
                System.out.println("After " + String.valueOf(res) + " good works we had some trouble with code "
                        + String.valueOf(isOK));
            }
        }
        return res;
    }

    private static int choseOneFunction(String newWork) {
        try {
            String[] args = newWork.split(" ");
            if (args.length < 3) {
                return -1;
            }
            int caseNumber = Integer.parseInt(args[0]);
            String inputFileName = args[1];
            String outputFileName = args[2];
            File inputFile = new File(inputFileName);
            Scanner input = new Scanner(inputFile);
            PrintWriter output = new PrintWriter(outputFileName);
            int errorCode = 0;
            switch (caseNumber) {
                case 1:
                    errorCode = numberFindWorker(input, output);
                    break;
                case 2:
                    errorCode = passwordCheckWorker(input, output);
                    break;
                case 3:
                    errorCode = linkReplaceWorker(input, output);
                    break;
                case 4:
                    errorCode = checkIPv4Worker(input, output);
                    break;
                case 5:
                    errorCode = allWordsWithStartWorker(input, output);
                    break;
                default:

                    output.printf("no such work\n");
                    output.close();
                    return -2;
            }
            output.close();
            if (errorCode < 0) {
                return -3;
            }
            return 1;
        } catch (Exception e) {
            System.out.println("Exception on work with task: " + newWork);
            return -4;
        }
    }

    private static int numberFindWorker(Scanner input, PrintWriter output) {
        while (input.hasNextLine()) {
            String inputStr = input.nextLine();
            String[] res = numberFind(inputStr);
            for (String oneRes : res) {
                output.print(oneRes);
                output.print(" ");
            }
            output.println();
        }
        return 0;
    }

    private static String[] numberFind(String str) {
        String regExp = "([-+]?([1-9][0-9]*)|0)(\\.\\d+)?";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        m = m.reset();
        int count = 0;
        while (m.find()) {
            count++;
        }
        String[] res = new String[count];
        m = m.reset();
        count = 0;
        while (m.find()) {
            res[count] = m.group();
            count++;
        }
        return res;
    }

    private static int passwordCheckWorker(Scanner input, PrintWriter output) {
        while (input.hasNextLine()) {
            String inputStr = input.nextLine();
            String[] res = passwordCheck(inputStr);
            for (String oneRes : res) {
                output.print(oneRes);
                output.print(" ");
            }
            output.println();
        }
        return 0;
    }

    private static String[] passwordCheck(String str) {
        String hasCapitalRegExp = "^.*[A-Z].*$";
        Pattern hasCapitalPattern = Pattern.compile(hasCapitalRegExp);
        Matcher hasCapitalMatcher = hasCapitalPattern.matcher(str);
        String hasNuRegExp = "^.*\\d.*$";
        Pattern hasNumPattern = Pattern.compile(hasNuRegExp);
        Matcher hasNumMatcher = hasNumPattern.matcher(str);
        String allSymregExp = "^[a-zA-Z0-9|\\[\\]?@!\\\\#$%\\^/\\&*\\-+=\'\":;\\(\\)\\{\\}]{8,16}$";
        Pattern allSymPattern = Pattern.compile(allSymregExp);
        Matcher allSymMatcher = allSymPattern.matcher(str);
        hasCapitalMatcher = hasCapitalMatcher.reset();
        hasNumMatcher = hasNumMatcher.reset();
        allSymMatcher = allSymMatcher.reset();
        int count = 0;
        while (hasCapitalMatcher.find() && hasNumMatcher.find() && allSymMatcher.find()) {
            count++;
        }
        String[] res = new String[count];
        hasCapitalMatcher = hasCapitalMatcher.reset();
        hasNumMatcher = hasNumMatcher.reset();
        allSymMatcher = allSymMatcher.reset();
        count = 0;
        while (hasCapitalMatcher.find() && hasNumMatcher.find() && allSymMatcher.find()) {
            res[count] = hasCapitalMatcher.group();
            count++;
        }
        return res;
    }

    private static int linkReplaceWorker(Scanner input, PrintWriter output) {
        while (input.hasNextLine()) {
            String inputStr = input.nextLine();
            String res = linkReplace(inputStr);
            /*
             * for(String oneRes : res){
             * output.print(oneRes);
             * output.print(" ");
             * }
             */
            output.println(res);
        }
        return 0;
    }

    private static final String fullHostRegex = giveHostFullRegex();
    private static final String fullURLRegex = giveFullURLRegex(); // 1038 chars

    private static String linkReplace(String str) { // <a href="https://html5css.ru/html/">Посетите наш HTML
                                                    // Справочник</a>
        String urlFindRegex = "^" + fullURLRegex + "$";
        Pattern urlPattern = Pattern.compile(urlFindRegex);
        Matcher urlMatcher = urlPattern.matcher(str);
        String res = "";
        res = urlMatcher.replaceAll(match -> {
            String firstMatch = match.group();
            Matcher hostMatcher = Pattern.compile(fullHostRegex).matcher(firstMatch);
            String hostname = "";
            try {
                if (!hostMatcher.find(0)) {
                    hostname = "404: host not found";
                    System.out.println("host not found");
                } else {
                    hostname = hostMatcher.group();
                    if (hostname.equals(firstMatch)) {
                        firstMatch = "http://" + firstMatch + "/";
                    }
                }
            } catch (IllegalStateException e) {
                System.out.println(e.getMessage());
                e.printStackTrace(System.out);
                throw e;
            }
            return "<a href=\"" + firstMatch + "\">" + hostname + "</a><br>";
        });
        return res;
    }

    private static String giveHostFullRegex() {
        String IPv4word = "((25[0-5])|(2[0-4][0-9])|([01]?[0-9][0-9]?)|(0))";
        String hostIPv4Form = "((" + IPv4word + "\\.){3}" + IPv4word + ")"; // 255
        String IPv6word = "[0-9a-fA-F]{1,4}";
        String IPv6only = "(" + IPv6word + ":){7,7}" + IPv6word + "|("
                + IPv6word + ":){1,7}:|("
                + IPv6word + ":){1,6}:" + IPv6word +
                "|(" + IPv6word + ":){1,5}(:" + IPv6word + "){1,2}|("
                + IPv6word + ":){1,4}(:" + IPv6word + "){1,3}|("
                + IPv6word + ":){1,3}(:" + IPv6word + "){1,4}|("
                + IPv6word + ":){1,2}(:" + IPv6word + "){1,5}|"
                + IPv6word + ":((:" + IPv6word + "){1,6})|:((:"
                + IPv6word + "){1,7}|:)";
        String hostIPv6Local = "fe80:(:" + IPv6word + "){0,4}%[0-9a-zA-Z]{1,}";
        String hostIPv6asIPv4 = "::(ffff(:0{1,4}){0,1}:){0,1}" + hostIPv4Form + "|(" + IPv6word + ":){1,4}:"
                + hostIPv4Form;
        String hostIPv6Form = "(" + IPv6only + "|" + hostIPv6Local + "|" + hostIPv6asIPv4 + ")";// ::
        // IPv6 Regex from https://debugpointer.com/regex/regex-for-ipv6
        String hostTextForm = "(([a-zA-Z0-9_\\-]+\\.)*[a-zA-Z0-9_\\-]+)";
        String hostRegex = "(" + hostIPv4Form + "|(\\[" + hostIPv6Form + "\\])|" + hostTextForm + ")";
        /*
         * System.out.print("host ipv4 regex len: ");
         * System.out.println(hostIPv4Form.length());
         * System.out.print("host ipv6 regex len: ");
         * System.out.println(hostIPv6Form.length());
         * System.out.print("host text regex len: ");
         * System.out.println(hostTextForm.length());
         * System.out.print("host total regex len: ");
         * System.out.println(hostRegex.length());
         */
        return hostRegex;
    }

    private static String giveFullURLRegex() { // scheme://username:password@host:port/path?query#fragment

        // ((25[0-5])|(2[0-4][1-9])|(1[0-9]{0,2})|0\\.){4}) // ipv4
        // (:((6553[0-5])|(655[0-2][0-9])|(65[0-4][0-9]{2})|(6[0-4][0-9]{3})|([1-5][0-9]{4})|([1-9][0-9]{0,3})|0))?
        // //port
        String schemeRegex = "([a-zA-Z]{3,}://)?";
        String usernameRegex = "[a-zA-Z]+";
        String passwordRegex = "[a-zA-Z0-9|!#$%\\&*\\-+\\_]+";
        String authRegex = "(" + usernameRegex + ":" + passwordRegex + "@)?";
        String portRegex = "(:((6553[0-5])|(655[0-2][0-9])|(65[0-4][0-9]{2})|(6[0-4][0-9]{3})|([1-5][0-9]{4})|([1-9][0-9]{0,3})|0))?"; // 65535
        String pathRegex = "([a-zA-Z0-9]+/)*([a-zA-Z0-9_\\-\\(\\)\\[\\]]+(\\.[a-zA-Z]+)*)?";
        String extraData = "(\\?.+)?(#.+)?";
        String fullURLRegex = schemeRegex + authRegex + fullHostRegex + portRegex + "(/?" + pathRegex + extraData
                + ")?";
        /*
         * //stats
         * System.out.print("scheme regex len: ");
         * System.out.println(schemeRegex.length());
         * System.out.print("user regex len: ");
         * System.out.println(usernameRegex.length());
         * System.out.print("pass regex len: ");
         * System.out.println(passwordRegex.length());
         * System.out.print("host regex len: ");
         * System.out.println(fullHostRegex.length());
         * System.out.print("port regex len: ");
         * System.out.println(portRegex.length());
         * System.out.print("path regex len: ");
         * System.out.println(pathRegex.length());
         * System.out.print("extra regex len: ");
         * System.out.println(extraData.length());
         * System.out.print("total regex len: ");
         * System.out.println(fullURLRegex.length());
         */

        //System.out.println(fullURLRegex);

        return fullURLRegex;
    }

    private static int checkIPv4Worker(Scanner input, PrintWriter output) {
        while (input.hasNextLine()) {
            String inputStr = input.nextLine();
            String res[] = checkIPv4(inputStr);
            for (String oneRes : res) {
                output.print(oneRes);
                output.print(" ");
            }
            output.println();
        }
        return 0;
    }

    private static String[] checkIPv4(String str) {
        String IPv4word = "((25[0-5])|(2[0-4][0-9])|([01]?[0-9][0-9]?)|(0))";
        String hostIPv4Form = "\\b((" + IPv4word + "\\.){3}" + IPv4word + ")\\b"; // 255
        Pattern ipv4Pattern = Pattern.compile(hostIPv4Form);
        Matcher ipv4Matcher = ipv4Pattern.matcher(str);
        int count = 0;
        while (ipv4Matcher.find()) {
            count++;
        }
        String[] res = new String[count];
        ipv4Matcher = ipv4Matcher.reset();
        count = 0;
        while (ipv4Matcher.find()) {
            res[count] = ipv4Matcher.group();
            count++;
        }
        return res;
    }

    private static int allWordsWithStartWorker(Scanner input, PrintWriter output) {
        while (input.hasNextLine()) {
            String inputStr = input.nextLine();
            String[] res = allWordsWithStart(inputStr, 'a');
            for (String oneRes : res) {
                output.print(oneRes);
                output.print(" ");
            }
            output.println();
        }
        return 0;
    }

    private static String[] allWordsWithStart(String str, char chr) {
        String startCharRegex = "\\b[" + String.valueOf(chr).toUpperCase() + String.valueOf(chr).toLowerCase()
                + "][a-zA-Z0-9]*\\b";
        Pattern startCharPattern = Pattern.compile(startCharRegex);
        Matcher startCharMatcher = startCharPattern.matcher(str);
        int count = 0;
        while (startCharMatcher.find()) {
            count++;
        }
        String[] res = new String[count];
        startCharMatcher = startCharMatcher.reset();
        count = 0;
        while (startCharMatcher.find()) {
            res[count] = startCharMatcher.group();
            count++;
        }
        return res;
    }
}
