import java.util.Scanner;

//=================================================================================================

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

    //---------------------------------------------------------------------------------------------

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
    }

    public static int validateTeeth(String teeth) {
        int errorCode = 0;
        //for () {

        //}

        return STATUS_SUCCESS;
    }

    public static String padSpaces(int size) {
        String string = "";
        int times;

        for (times = 0; times < size; times++) {
            string += " ";
        }

        return string;
    }

    public static String getLayerName(int layerIndex) {
        String name = "";
        switch (layerIndex) {
            case UPPER_LAYER:
                name = "uppers";
                break;
            case LOWER_LAYER:
                name = "lowers";
                break;
            default:
                name = "[unknown teeth layer]";
        }

        return name;
    }

    public static int getFamilySize() {
        int familySize;

        System.out.print("Please enter number of people in the family : ");
        familySize = keyboard.nextInt();

        while (familySize < MIN_PEOPLE || familySize > MAX_PEOPLE) {
            System.out.print("Invalid number of people, try again         : ");
            familySize = keyboard.nextInt();
        }

        //----Consume the leftover newline character
        keyboard.nextLine();

        return familySize;
    }

    public static void getFamilyData(String[] familyNames, char[][][] familyTeeth) {
        int memberIndex;
        String name;
        int layer;
        int teethStatusCode;
        int toothIndex;
        String teeth;

        //----Loop through the family
        for (memberIndex = 0; memberIndex < familyNames.length; memberIndex++) {

            //----Get name
            System.out.print("Please enter the name for family member " + (memberIndex + 1) + "   : ");

            do {
                name = keyboard.nextLine();
                if (!isNameValid(name)) {
                    System.out.print("Invalid name for family member " + (memberIndex + 1) + ", try again : ");
                }
            } while (!isNameValid(name));

            familyNames[memberIndex] = name;

            //---Get teeth for each layer (uppers, lowers)
            for (layer = 0; layer < NUMBER_OF_LAYERS; layer++) {
                System.out.print("Please enter the " + getLayerName(layer) + " for " + name
                        + padSpaces(22 - getLayerName(layer).length() - name.length()) + ": ");

                do {
                    teeth = keyboard.nextLine();
                    teethStatusCode = validateTeeth(teeth);

                    switch (teethStatusCode) {
                        case STATUS_INVALID_TEETH_MAX:
                            System.out.print("Too many teeth, try again                   : ");
                            break;
                        case STATUS_INVALID_TEETH_TYPES:
                        case STATUS_INVALID_TEETH_MIN:
                            System.out.print("Invalid teeth types, try again              : ");
                            break;
                        default:
                            break;
                    }
                } while (teethStatusCode != STATUS_SUCCESS);

                //----Save the validated teeth into the 3D array
                familyTeeth[memberIndex][layer] = new char[teeth.length()];
                for (toothIndex = 0; toothIndex < teeth.length(); toothIndex++) {
                    familyTeeth[memberIndex][layer][toothIndex] =
                            Character.toUpperCase(teeth.charAt(toothIndex));
                }
            }

        }

    } // end of getFamilyData method

    //-----------------------------------------------------------------------------

    public static void main(String[] args) {
        int familySize;
        String[] familyNames;
        char[][][] familyTeeth;
        int memberIndex;

        System.out.println("Welcome to the Floridian Tooth Records");
        System.out.println("--------------------------------------");

        //----Get the family info and set up our array
        familySize = getFamilySize();
        familyNames = new String[familySize];
        familyTeeth = new char[familySize][NUMBER_OF_LAYERS][];

        getFamilyData(familyNames, familyTeeth);

        //----Show the names for now
        for (memberIndex = 0; memberIndex < familyNames.length; memberIndex++) {
            System.out.println("[" + memberIndex + "] " + familyNames[memberIndex]);
        }
    } // end of main method

    //-----------------------------------------------------------------------------

} // end of DentalRecords class
//=============================================================================