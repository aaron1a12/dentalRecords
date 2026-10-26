import java.util.Scanner;

//=================================================================================================
/**
 * Records and manages simplified dental records (Incisors, Bicuspids, and Missing teeth)
 * for the members of a Florida family. Provides a menu to print records, extract teeth,
 * and calculate the family's root canal indices.
 *
 * @author Aaron Escobar
 * @version 1.0
 */
public class DentalRecords {
    //---------------------------------------------------------------------------------------------
    private static final Scanner keyboard = new Scanner(System.in);

    // Constants
    private static final int MIN_PEOPLE = 1;
    private static final int MAX_PEOPLE = 6;
    private static final int NUMBER_OF_LAYERS = 2;
    private static final int UPPER_LAYER = 0;
    private static final int LOWER_LAYER = 1;
    private static final int MAX_TEETH = 8;

    // Teeth validation status codes
    private static final int STATUS_SUCCESS = 0;
    private static final int STATUS_INVALID_TEETH_TYPES = 1;
    private static final int STATUS_INVALID_TEETH_MIN = 2;
    private static final int STATUS_INVALID_TEETH_MAX = 3;

    // Tooth extraction validation status codes
    private static final int STATUS_INVALID_TOOTH_NUMBER = 1;
    private static final int STATUS_MISSING_TOOTH = 2;

    //---------------------------------------------------------------------------------------------
    /**
     * Safely reads an integer from the keyboard scanner. It handles input
     * exceptions and clearing the invalid line from the scanner buffer.
     *
     * @return The integer entered by the user, or -1 if the input was invalid.
     */
    public static int getInputIntegerSafely() {
        int integer = -1;
        try {
            integer = keyboard.nextInt();
        } catch (Exception exception) {
            keyboard.nextLine();
            return -1;
        }

        return integer;
    } // end of getInputIntegerSafely

    /**
     * Checks whether a given family member name is valid (at least two non-space
     * characters and containing only letters, hyphens, or apostrophes).
     *
     * @param name The name string to validate.
     * @return true if the name is valid; false otherwise.
     */
    public static boolean isNameValid(String name) {
        String temporaryString;
        int charIndex;
        char currentChar;
        boolean isLetter;
        boolean isHyphen;
        boolean isApostrophe;

        if (name == null) {
            return false;
        }

        temporaryString = name.replace(" ", "");

        //----Must be at least 2 characters
        if (temporaryString.length() < 2) {
            return false;
        }

        //----Check for valid characters (letters, hyphens, apostrophes)
        for (charIndex = 0; charIndex < temporaryString.length(); charIndex++) {
            currentChar = temporaryString.charAt(charIndex);

            isLetter = (currentChar >= 'a' && currentChar <= 'z')
                    || (currentChar >= 'A' && currentChar <= 'Z');
            isHyphen = (currentChar == '-');
            isApostrophe = (currentChar == '\'');

            if (!isLetter && !isHyphen && !isApostrophe) {
                return false;
            }
        }

        return true;
    } // end of isNameValid

    /**
     * Validates a string of tooth characters to ensure it is non-empty, contains
     * only valid tooth types ('I', 'B', or 'M', case-insensitive), and does not
     * exceed the maximum allowed number of teeth.
     *
     * @param teeth The string of tooth characters entered by the user.
     * @return A status code indicating success or the specific type of validation error.
     */
    public static int validateTeeth(String teeth) {
        int toothIndex;
        char currentTooth;

        if (teeth.length() == 0) {
            return STATUS_INVALID_TEETH_MIN;
        }

        teeth = teeth.toUpperCase();

        for (toothIndex = 0; toothIndex < teeth.length(); toothIndex++) {
            currentTooth = teeth.charAt(toothIndex);

            if (currentTooth != 'I' && currentTooth != 'B' && currentTooth != 'M') {
                return STATUS_INVALID_TEETH_TYPES;
            }
        }

        if (teeth.length() > MAX_TEETH) {
            return STATUS_INVALID_TEETH_MAX;
        }

        return STATUS_SUCCESS;
    } // end of validateTeeth

    /**
     * Validates that a requested tooth number is within the bounds of the specified
     * tooth layer and is not already missing ('M').
     *
     * @param toothLayer The array of characters representing a single tooth row.
     * @param toothNumber The 1-based tooth number selected for extraction.
     * @return A status code indicating success, out-of-range tooth number, or missing tooth.
     */
    public static int validateExtraction(char[] toothLayer, int toothNumber) {
        if (toothNumber < 1 || toothNumber > toothLayer.length) {
            return STATUS_INVALID_TOOTH_NUMBER;
        }

        if (toothLayer[toothNumber - 1] == 'M') {
            return STATUS_MISSING_TOOTH;
        }

        return STATUS_SUCCESS;
    } // end of validateExtraction

    /**
     * Generates a string consisting of a specified number of spaces.
     *
     * @param size The number of space characters to generate.
     * @return A string of spaces of length size (or empty string if size is 0 or less).
     */
    public static String padSpaces(int size) {
        String string = "";
        int times;

        for (times = 0; times < size; times++) {
            string += " ";
        }

        return string;
    } // end of padSpaces

    /**
     * Returns the display name ("uppers" or "lowers") corresponding to a tooth layer index.
     *
     * @param layerIndex The index of the tooth layer (UPPER_LAYER or LOWER_LAYER).
     * @return The lowercase name of the tooth layer.
     */
    public static String getLayerName(int layerIndex) {
        String name;
        switch (layerIndex) {
            case UPPER_LAYER:
                name = "uppers";
                break;
            case LOWER_LAYER:
                name = "lowers";
                break;
            default:
                name = "[unknown teeth layer]";
                break;
        }

        return name;
    } // end of getLayerName

    /**
     * Prompts the user for the number of people in the family and validates that
     * the input falls between MIN_PEOPLE and MAX_PEOPLE inclusive.
     *
     * @return The validated number of family members.
     */
    public static int getFamilySize() {
        int familySize;

        System.out.print("Please enter number of people in the family : ");

        familySize = getInputIntegerSafely();

        while (familySize < MIN_PEOPLE || familySize > MAX_PEOPLE) {
            System.out.print("Invalid number of people, try again         : ");
            familySize = getInputIntegerSafely();
        }

        //----Consume the leftover newline character
        keyboard.nextLine();

        return familySize;
    } // end of getFamilySize

    /**
     * Prompts the user to enter the name, upper teeth row, and lower teeth row
     * for each member of the family, validating all inputs and storing them in
     * the provided arrays.
     *
     * @param familyNames The array of strings to store each family member's name.
     * @param familyTeeth The 3D character array to store each member's upper and lower teeth.
     */
    public static void getFamilyData(String[] familyNames, char[][][] familyTeeth) {
        int layer;
        int memberIndex;
        String name;
        String teeth;
        int teethStatusCode;
        int toothIndex;

        //----Loop through the family
        for (memberIndex = 0; memberIndex < familyNames.length; memberIndex++) {

            //----Get name
            System.out.print("Please enter the name for family member "
                    + (memberIndex + 1) + "   : ");

            do {
                name = keyboard.nextLine();
                if (!isNameValid(name)) {
                    System.out.print("Invalid name for family member "
                            + (memberIndex + 1) + ", try again : ");
                }
            } while (!isNameValid(name));

            familyNames[memberIndex] = name;

            //---Get teeth for each layer (uppers, lowers)
            for (layer = 0; layer < NUMBER_OF_LAYERS; layer++) {
                System.out.print("Please enter the " + getLayerName(layer) + " for " + name
                        + padSpaces(22 - getLayerName(layer).length() - name.length()) + ": ");

                do {
                    teeth = keyboard.nextLine().toUpperCase();
                    teethStatusCode = validateTeeth(teeth);

                    switch (teethStatusCode) {
                        case STATUS_INVALID_TEETH_MIN:
                            System.out.print("Too few teeth, try again                    : ");
                            break;
                        case STATUS_INVALID_TEETH_MAX:
                            System.out.print("Too many teeth, try again                   : ");
                            break;
                        case STATUS_INVALID_TEETH_TYPES:
                            System.out.print("Invalid teeth types, try again              : ");
                            break;
                        default:
                            break;
                    }
                } while (teethStatusCode != STATUS_SUCCESS);

                //----Save the validated teeth into the 3D array
                familyTeeth[memberIndex][layer] = new char[teeth.length()];

                for (toothIndex = 0; toothIndex < teeth.length(); toothIndex++) {
                    familyTeeth[memberIndex][layer][toothIndex] = teeth.charAt(toothIndex);
                }
            }

        }

    } // end of getFamilyData method

    /**
     * Prints the complete dental records (uppers and lowers)
     * for every member of the family.
     *
     * @param familyNames The array of family member names.
     * @param familyTeeth The 3D array of family member teeth records.
     */
    public static void printRecords(String[] familyNames, char[][][] familyTeeth) {
        int memberIndex;
        int layerIndex;
        int toothIndex;
        String layerName;

        System.out.println();

        //----Loop through the family
        for (memberIndex = 0; memberIndex < familyNames.length; memberIndex++) {
            System.out.println(familyNames[memberIndex]);

            //----Loop through the layers
            for (layerIndex = 0; layerIndex < NUMBER_OF_LAYERS; layerIndex++) {
                layerName = getLayerName(layerIndex);
                layerName = layerName.substring(0, 1).toUpperCase()
                        + layerName.substring(1, layerName.length());

                System.out.print("  " + layerName + ": ");

                //----Loop through the teeth
                for (toothIndex = 0;
                     toothIndex < familyTeeth[memberIndex][layerIndex].length; toothIndex++) {
                    System.out.print(" " + (toothIndex + 1) + ":"
                            + familyTeeth[memberIndex][layerIndex][toothIndex] + " ");
                }
                System.out.println();
            }
        }
        System.out.println();
    } // end of printRecords

    /**
     * Searches for a family member by name (case-insensitive) and returns their index.
     *
     * @param familyNames The array of family member names.
     * @param targetName The name of the family member to find.
     * @return The 0-based index of the family member, or -1 if not found.
     */
    public static int getFamilyMemberByName(String[] familyNames, String targetName) {
        int memberIndex;

        targetName = targetName.toUpperCase();

        for (memberIndex = 0; memberIndex < familyNames.length; memberIndex++) {
            if (familyNames[memberIndex].toUpperCase().equals(targetName)) {
                return memberIndex;
            }
        }

        return -1;
    } // end of getFamilyMemberByName

    /**
     * Prompts the user for a family member, tooth layer, and tooth number,
     * validates each selection, and marks the specified tooth as missing ('M').
     *
     * @param familyNames The array of family member names.
     * @param familyTeeth The 3D array of family member teeth records.
     */
    public static void extractTooth(String[] familyNames, char[][][] familyTeeth) {
        String memberName;
        int memberIndex = -1;
        String layerInput;
        char layerSelection = ' ';
        int layerIndex = 0;
        int toothNumber;
        int extractionStatusCode;

        System.out.print("Which family member                         : ");

        do {
            memberName = keyboard.nextLine();
            memberIndex = getFamilyMemberByName(familyNames, memberName);

            if (memberIndex == -1) {
                System.out.print("Invalid family member, try again            : ");
            }

        } while (memberIndex == -1);

        System.out.print("Which tooth layer (U)pper or (L)ower        : ");

        do {
            layerInput = keyboard.nextLine();
            layerInput = layerInput.replaceAll(" ", "");
            layerInput = layerInput.toUpperCase();

            if (layerInput.length() > 0) {
                layerSelection = layerInput.charAt(0);
            } else {
                layerSelection = 0;
            }

            if (layerInput.length() != 1 || (layerSelection != 'U' && layerSelection != 'L')) {
                System.out.print("Invalid layer, try again                    : ");
            }

        } while (layerInput.length() != 1 || (layerSelection != 'U' && layerSelection != 'L'));

        switch (layerSelection) {
            case 'U':
                layerIndex = UPPER_LAYER;
                break;
            case 'L':
                layerIndex = LOWER_LAYER;
                break;
            default:
                break;
        }

        System.out.print("Which tooth number                          : ");

        do {
            toothNumber = getInputIntegerSafely();
            extractionStatusCode = validateExtraction(
                    familyTeeth[memberIndex][layerIndex], toothNumber);

            switch (extractionStatusCode) {
                case STATUS_INVALID_TOOTH_NUMBER:
                    System.out.print("Invalid tooth number, try again             : ");
                    break;
                case STATUS_MISSING_TOOTH:
                    System.out.print("Missing tooth, try again                    : ");
                    break;
                default:
                    break;
            }

        } while (extractionStatusCode != STATUS_SUCCESS);

        //----Consume the leftover newline character
        keyboard.nextLine();

        familyTeeth[memberIndex][layerIndex][toothNumber - 1] = 'M';
        System.out.println();
    } // end of extractTooth

    /**
     * Counts the total number of incisors ('I'), bicuspids ('B'), and missing teeth ('M')
     * across the family and calculates and prints the roots of Ix^2 + Bx - M = 0.
     *
     * @param familyTeeth The 3D array of family member teeth records.
     */
    public static void reportRootCanals(char[][][] familyTeeth) {
        int memberIndex;
        int layer;
        int toothIndex;
        int incisorsCount = 0;
        int bicuspidsCount = 0;
        int missingCount = 0;
        char currentTooth;
        double discriminant;
        double root1;
        double root2;

        for (memberIndex = 0; memberIndex < familyTeeth.length; memberIndex++) {

            for (layer = 0; layer < NUMBER_OF_LAYERS; layer++) {
                for (toothIndex = 0;
                     toothIndex < familyTeeth[memberIndex][layer].length; toothIndex++) {
                    currentTooth = familyTeeth[memberIndex][layer][toothIndex];

                    if (currentTooth == 'I') {
                        incisorsCount++;
                    } else if (currentTooth == 'B') {
                        bicuspidsCount++;
                    } else if (currentTooth == 'M') {
                        missingCount++;
                    }
                }
            }

        }

        discriminant = (bicuspidsCount * bicuspidsCount)
                + (4.0 * incisorsCount * missingCount);
        root1 = (-bicuspidsCount + Math.sqrt(discriminant)) / (2.0 * incisorsCount);
        root2 = (-bicuspidsCount - Math.sqrt(discriminant)) / (2.0 * incisorsCount);

        System.out.printf("One root canal at     %.2f", root1);
        System.out.println();
        System.out.printf("Another root canal at %.2f", root2);
        System.out.println();
        System.out.println();
    } // end of reportRootCanals

    /**
     * Displays the interactive main menu loop and dispatches user selections
     * to print records, extract a tooth, report root canal indices, or exit.
     *
     * @param familyNames The array of family member names.
     * @param familyTeeth The 3D array of family member teeth records.
     */
    public static void displayMenu(String[] familyNames, char[][][] familyTeeth) {
        String userInput;
        char selection = ' ';

        do {
            System.out.print("(P)rint, (E)xtract, (R)oot, e(X)it          : ");

            do {
                userInput = keyboard.nextLine();
                userInput = userInput.replaceAll(" ", "");
                userInput = userInput.toUpperCase();

                if (userInput.length() > 0) {
                    selection = userInput.charAt(0);
                } else {
                    selection = 0;
                }

                if (userInput.length() != 1
                        || (selection != 'P' && selection != 'E' && selection != 'R' && selection != 'X')) {
                    System.out.print("Invalid menu option, try again              : ");
                }
            } while (userInput.length() != 1
                    || (selection != 'P' && selection != 'E' && selection != 'R' && selection != 'X'));

            switch (selection) {
                case 'P':
                    printRecords(familyNames, familyTeeth);
                    break;
                case 'E':
                    extractTooth(familyNames, familyTeeth);
                    break;
                case 'R':
                    reportRootCanals(familyTeeth);
                    break;
                case 'X':
                    System.out.println("\nExiting the Floridian Tooth Records :-)");
                    break;
                default:
                    break;
            }

        } while (selection != 'X');
    } // end of displayMenu

    //-----------------------------------------------------------------------------
    /**
     * Main entry point for the Floridian Tooth Records program. Initializes arrays,
     * collects family dental data, and launches the interactive menu.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        int familySize;
        String[] familyNames;
        char[][][] familyTeeth;

        System.out.println("Welcome to the Floridian Tooth Records");
        System.out.println("--------------------------------------");

        //----Get the family info and set up our array
        familySize = getFamilySize();
        familyNames = new String[familySize];
        familyTeeth = new char[familySize][NUMBER_OF_LAYERS][];

        //----Populate the familyTeeth records array
        getFamilyData(familyNames, familyTeeth);
        System.out.println();

        //----Go to the menu
        displayMenu(familyNames, familyTeeth);

    } // end of main method

    //-----------------------------------------------------------------------------

} // end of DentalRecords class
//=============================================================================