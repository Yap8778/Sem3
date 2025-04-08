import java.util.Scanner;

public class CreditCardTesting
{
    public static void main (String[] args)
    {
        Scanner sc = new Scanner(System.in);
        String cardNumber;
        /*
        https://www.programiz.com/java-programming/library/string/matches
        ^ means start,
        [0-9/] means the data of cardNumber will only accept number 0,1,2..9 and '/' .
        + means it allow one and above that kind of data appear.
        $ means end
        */
        String premise = "^[0-9/]+$";
        System.out.println("");
        System.out.println("*Please put '/' after enter four digit number*  ");
        System.out.print("Please enter your card Number (Exp: 1234/7890): ");
        cardNumber = sc.nextLine();

        // '!' means not, help the computer to check the user input is follow the format
        if (!cardNumber.contains("/"))
        {
            System.out.println("You did not follow the format. Please Try again.");
        }
        // The function of 'matches' to check the input is matches the given regular expression or not.
        else if (cardNumber.matches(premise))
        {
            if (cardNumber.length() >= 16)
            {
                System.out.println("You have succeed add the card number " + cardNumber);
                Step1.processCardNumber(cardNumber);
                Step2.CheckPattern(cardNumber);
                System.out.println("");
            }
            else
            {
                System.out.println("The card number must contain at least 16 digits.");
            }
        }
    }
}
