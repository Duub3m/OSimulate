import java.util.Random;

//a new class, RandomDevice which implements Device
public class RandomDevice implements Device {
    //keeps an array (10 items) of java.util.Random
    private final Random[] randoms = new Random[10];

    @Override
    //Open: Creates a new Random device and put it in an empty spot in the array
    public int Open(String s) {

        ////If the supplied string for Open is not null or empty
        //assume that it is the seed for the Random class (convert the string to an integer)
     int seed = s != null && !s.isEmpty() ? Integer.parseInt(s) : 0; 
       for (int i = 0; i < randoms.length; i++) {
            if (randoms[i] == null) {
             randoms[i] = new Random(seed);
            return i; // The index is used as the device ID.
        }
     }
     return -1; // Indicate that no slot is available.
    }

    @Override
    //Close: nulls the device entry using an if staemtn 
    public void Close(int id) {
        if (id >= 0 && id < randoms.length) {
            randoms[id] = null;
        }
    }

    @Override
    //Read: create/fills an array with random values
    public byte[] Read(int id, int size) {
        if (id >= 0 && id < randoms.length && randoms[id] != null) {
            byte[] buffer = new byte[size];
            randoms[id].nextBytes(buffer);
            return buffer;
        }
        return new byte[0]; // return and empty array 
    }

    @Override
    //Seek will read random bytes but not return them.
    public void Seek(int id, int to) {
    }

    @Override
    //Write: returns 0 length and do nothing 
    public int Write(int id, byte[] data) {
        return 0;
    }
}
