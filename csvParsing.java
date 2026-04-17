import java.lang.reflect.Array;
import java.util.ArrayList; //arraylists are just normal arrays but you can add/append to them. their size is dynamic

public class csvParsing {
    public static ArrayList<String> parseLine(String Line)
    {
        //ok so we are given a line as input, any line frm that csv file.
        ArrayList<String> result = new ArrayList<>(); // a list of every single data component for any given person. So in order, it would be something like ID, First name, Last name, etc.
        //string builders are like strings but you can add and manipulate them, unlike String.
        StringBuilder field = new StringBuilder(); //so any given SPECIFIC singular piece of data. This would be each object in the array list

        boolean inQuotes = false;// going to have like a toggling state machine for whenever theres like encapsulating commas inside a quotation. Chatgpt recommended this so that like a csv entry like :   [ "Smith, John", 21 ] does not get parsed as 3 fields, ""Smith,", "John"", and "21"

        for (int i = 0; i < Line.length(); i++) {
            char c = Line.charAt(i);

            if (c=='"')
            {
                //so in this case, if we are in a set of quotes already, and we come across a ", then well
                if(inQuotes && i+1 < Line.length() && Line.charAt(i+1)=='"' )
                {
                    field.append('"');
                    i++;
                }
                else
                {
                    inQuotes=!inQuotes;
                }
            }
            else if (c ==',' && inQuotes){ //if we are at a comma and not in quotes, lets just add that field and clear the current field
                result.add(field.toString());
                field.setLength(0);
            }
            else //if we are in quotes, then the field doesnt matter and just add the characters on and on
            {
                field.append(c);
            }
        }


        result.add(field.toString());
        return result;
    }

}
