public class Step2
{
    public static void CheckPattern (String cardNumber)
    {
        String cleanCardNumber = cardNumber.replace("/", "");
        // https://www.geeksforgeeks.org/string-startswith-method-in-java-with-examples/
        // 'startsWith' can help us to check the value is true or false
        if (cleanCardNumber.startsWith("4"))
        {
            System.out.println("Type of credit card: Visa Cards");
        }
        else if (cleanCardNumber.startsWith("5"))
        {
            System.out.println("Type of credit card: Master Cards");
        }
        else if (cleanCardNumber.startsWith("37"))
        {
            System.out.println("Type of credit card: American Express cards");
        }
        else
        {
            System.out.println("Sorry, this card cannot be defined.");
        }
    }
}