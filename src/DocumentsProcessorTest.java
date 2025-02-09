import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DocumentsProcessorTest {
    private Map<String, List<String>> docMap;
    private DocumentsProcessor d;
    private final String ngramFile = "test_out/grams.txt";
    List<Tuple<String, Integer>> tList;
    TreeSet<Similarities> simSet;

    @BeforeEach
    void setup() {
        d = new DocumentsProcessor();

        docMap = d.processDocuments("test_doc/match_testing", 3);
        tList = d.storeNGrams(docMap, ngramFile);
        simSet = d.computeSimilarities(ngramFile, tList);
    }

    @Test
    //processDocuments minimum functionality
    void processDocumentsMin(){
        List<String> abfList = docMap.get("abf0704.txt");
        List<String> przList = docMap.get("prz100.txt");
        assertEquals(abfList.getFirst(), "nineteeneightyfour");
        assertEquals(przList.getFirst(), "shortlyafterthe");
    }

    @Test
    //storeNGrams minimum functionality
    void storeNgramsMin(){

        try ( RandomAccessFile raf = new RandomAccessFile(ngramFile, "r"))
        {
            raf.seek(0);
            char fileChar = (char) raf.read();
            StringBuilder comp = new StringBuilder();

            while (Character.isLetter(fileChar)){
                comp.append(fileChar);
                fileChar = (char) raf.read();
            }

            String result = comp.toString();

            assertEquals(result, docMap.get("prz100.txt").getFirst());
            assertEquals(result.getBytes().length, 15);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    //computeSimilarities minimum functionality
    void computeSimilaritiesMin(){
        Similarities similarity = simSet.getLast();
        assertEquals(3, similarity.getCount());
        assertEquals(3, simSet.size());
    }

    @Test
    void printSimilarities(){
        OutputStream out = System.out;
        d.printSimilarities(simSet, 2);
    }


}
