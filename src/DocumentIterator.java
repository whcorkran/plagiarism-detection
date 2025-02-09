import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class DocumentIterator implements Iterator<String> {

    private Reader r;
    private int n;
    private int c;
    

    public DocumentIterator(Reader r, int n) throws IOException{
        this.r = r;
        this.n = n;
    }


    private void skipNonLetters() {
        try {
            this.c = this.r.read();
            while (!Character.isLetter(this.c) && this.c != -1) {
                this.c = this.r.read();
            }
        } catch (IOException e) {
            this.c = -1;
        }
    }


    @Override
    public boolean hasNext() {
        return (c != -1);
    }


    @Override
    public String next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        StringBuilder answer = new StringBuilder();
        try {
            // read first word
            skipNonLetters();
            while (Character.isLetter(this.c)) {
                answer.append((char) this.c);
                this.c = this.r.read();
            }
            // mark position in the reader after reading first word
            r.mark(n * 20);
            skipNonLetters();

            // for loop to read the rest of the words without marking
            for (int i = 0 ; i < (n-1); i++){
            while (Character.isLetter(this.c) && hasNext()) {
                answer.append((char) this.c);
                this.c = this.r.read();
            }
            skipNonLetters();

            }
            // return to the reader position after the first word
            r.reset();
        } catch (IOException e) {
            throw new NoSuchElementException();
        }

        // make a new stringbuilder with lowercase letters
        answer = new StringBuilder(answer.toString().toLowerCase());
        // return final string
        return answer.toString();
    }

}
