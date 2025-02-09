import java.util.Arrays;

/**
 * @author ericfouh
 */
public class Similarities implements Comparable<Similarities> {
    /**
     * 
     */
    private String file1;
    private String file2;
    private int    count;


    /**
     * @param file1
     * @param file2
     */
    public Similarities(String file1, String file2) {
        String[] fileOrder = {file1, file2};
        Arrays.sort(fileOrder);

        this.file1 = fileOrder[0];
        this.file2 = fileOrder[1];
        this.setCount(0);
    }


    /**
     * @return the file1
     */
    public String getFile1() {
        return file1;
    }


    /**
     * @return the file2
     */
    public String getFile2() {
        return file2;
    }


    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }


    /**
     * @param count the count to set
     */
    public void setCount(int count) {
        this.count = count;
    }


    @Override
    public int compareTo(Similarities o) {
        int countDif = this.count - o.count;
        int fileDif = this.file1.compareTo(o.file1);
        int file2Dif = this.file2.compareTo(o.file2);

        if (countDif == 0){
            if (fileDif == 0){
                return file2Dif;
            } else {return fileDif;}
        } else {return countDif;}
    }

    @Override
    public String toString() {
        String file1 =  getFile1().substring(0, getFile1().length() - 4);
        String file2 = getFile2().substring(0, getFile2().length() - 4);
        return (String.format("%s and %s have %d n-grams in common", file1, file2, count));
    }
}
