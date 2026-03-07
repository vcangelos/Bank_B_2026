/*
* If you have to deal with CSV files, just use this.
* Its much easier and simplifed to use, and I documented how a lot of it worked
* here is some example usage on how you would use it:
*
* To initialize it, just get the path of wherever the CSV is located, and instantiate it
*
*       Path csvPath = Path.of("fileName.csv");
*       csvFile file = new csvFile(csvPath);
*
* If we want to pull any specific person's records, we can do so by calling any specific type of record
* (name, ssn, username, email, etc.) and passing in that, alongside the actual value
*
*       Map<String,String> record = file.getRecord("user_id", "32292");
*       if(record != null)
*       {
*           System.out.println(record.get("name");
*           System.out.println(record.get("email");
*       }
*
* So this code just gets the records of any customer, and we can access their specific name or wtv
* NOTE: You can use ANY identifier/key to grab a customer's records.
*
*/


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//this class just impelments and uses teh csvParsing class.
public class csvFile {
    private final Map<String,Integer> headerIndex = new HashMap<>(); // pretty much a dictionary in python. We have strings that map to integers. This will map the column names to int
    private final Path path; //csv's path

    public csvFile(Path path) throws IOException //throws just means that we might get a file error, and if we do, we just have to use the try-catch block later on whenever we call this method. error handling is not dealt here.
    {
        this.path = path;
        loadHeader();
    }

    //this function is static, and allows a bit more of a flexible constructor, allowing you to also create a file
    //the ... allows us to have as many headers as we want.
    public static csvFile openOrCreate(Path path, String... headers) throws IOException
    {
        if(Files.notExists(path))
        {
            if (headers.length==0)
            {
                throw new IllegalArgumentException("cant have a csv without headers silly!");
            }
            //gets all the parent directories in case we need to make the directory in order to
            Path parent = path.getParent();
            if (parent!=null)
            {
                Files.createDirectories(parent);
            }

            //now just write the headervline
            try(BufferedWriter writer = Files.newBufferedWriter(path))
            {
                writer.write(String.join(",",headers));
                writer.newLine();
            }
        }
        return new csvFile(path);
    }

    //we keep it private because we dont need the user of this class to be dealing with how to load the header
    private void loadHeader() throws IOException
    {
        //just having a try block means that the reader will automatically close the file after we are done, even if we get weird errors
        try(BufferedReader reader = Files.newBufferedReader(path))
        {
            String headerLine = reader.readLine(); //just reads the first line of the file
            if(headerLine==null)
            {
                throw new IllegalArgumentException("CSV file is empty"); //give an error if our file is empty
            }

            List<String> headers = csvParsing.parseLine(headerLine);
            for (int i = 0; i < headers.size(); i++) {
                headerIndex.put(headers.get(i),i); //we just populate the headerIndex dictionary with the parser, getting the csv keys as indices
            }

        }
    }

    //so if we want to get any value (such as name given any id), we just pass in the id column's name, and the id value
    public Map<String,String> getRecord(String recordCol, String recordValue) throws IOException 
    {
        //Integer can be null compared to int - weird
        Integer recordIndex = headerIndex.get(recordCol);
        if(recordIndex==null)
        {
            throw new IllegalArgumentException("Unknown column");
        }

        try(BufferedReader reader = Files.newBufferedReader(path))
        {
            reader.readLine();//because we already read the header, it will skip that and read the next line

            String line;
            while((line= reader.readLine())!=null)
            {
                List<String> fields = csvParsing.parseLine(line);

                //so if we found the row that has the value we are looking for, then
                if (recordIndex < fields.size() && fields.get(recordIndex).equals(recordValue))
                {
                    return mapRecord(fields);
                }
            }
        }
        return null;
    }

    //ok so this is just going to return a neat dictionary, mapping the column values to whatever values we got in that row
    private Map<String,String> mapRecord(List<String> fields)
    {
        Map<String,String> record = new HashMap<>();

        // map.entry is a single key-value pair. we go through all key value pairs
        for (Map.Entry<String,Integer> entry : headerIndex.entrySet())
        {
            int index = entry.getValue();
            //if the field doesnt exist, just give an empty string, otherwise we pull value from the fields we got earlier
            String value = index< fields.size() ? fields.get(index) : "";
            record.put(entry.getKey(), value);
        }
        return record;
    }




}
