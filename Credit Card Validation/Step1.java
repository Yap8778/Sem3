public class Step1
{

    // Method to process the card number after reversing it
    public static void processCardNumber(String cardNumber)
    {
        // 'replace' is to substitue a symbol to other things
        String cleanCardNumber = cardNumber.replace("/", "");

        // Reverse the card number to easily calculate
        // 'StringBuilder()' is a changeable value, therefore we need to let the card number become changeable then we can do the reverse
        // 'toString()' is to change the changeable value to a fixed value
        String reversedCardNumber = new StringBuilder(cleanCardNumber).reverse().toString();
        int sum = 0;

        // From left to right to do the calculation
        for (int i = 0; i < reversedCardNumber.length(); i++)
        {
            // https://www.tutorialspoint.com/java/lang/character_getnumericvalue.htm
            // 'getNumericValue' will only get the number between 0-9, if got the symbol or character it will transfer 10 or above to computer
            int digit = Character.getNumericValue(reversedCardNumber.charAt(i));

            // if the index cannot divide by 2 means that position is even digit
            if (i % 2 != 0)
            {
                // if find the even digit which means the index is 1,3,5,7...
                digit = digit * 2;

                // if the value*2 is bigger than 10
                if (digit > 9)
                {
                    // using the '/' to get the tens digit value and '%' to get the single digits
                    digit = digit / 10 + digit % 10;
                }
            }

            // Add all the number
            sum = sum + digit;
        }

        if (sum % 10 == 0)
        {
            System.out.println(cardNumber + " is a valid card");
        }
        else
        {
            System.out.println(cardNumber + " is a invalid card");
        }
    }
}
