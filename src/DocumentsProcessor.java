import java.io.*;
import java.util.*;

public class DocumentsProcessor implements IDocumentsProcessor{

    @Override
    public Map<String, List<String>> processDocuments(String directoryPath, int n) {

        // get list of all papers in the directory
        File paperDirectory = new File(directoryPath);
        String[] allPapers = paperDirectory.list();

        // nullpointer exception if directory is empty
        assert allPapers != null;

        // map to be populated and returned
        Map<String, List<String>> nMap = new HashMap<>();

        for (String f : allPapers) {
            try (
                // opening and making streams
                InputStream bufFile = new FileInputStream(directoryPath + "/" + f);
                Reader paperReader = new BufferedReader(new InputStreamReader(bufFile))

            ) {

                // setup map key
                nMap.put(f, new ArrayList<>());

                // populate list with iterator
                DocumentIterator diter = new DocumentIterator(paperReader, n);
                while (diter.hasNext()){
                    nMap.get(f).add(diter.next());
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return nMap;
    }

    @Override
    public List<Tuple<String, Integer>> storeNGrams(Map<String, List<String>> docs, String nwordFilePath) {
        try (
        Writer w = new FileWriter(nwordFilePath)
        ) {
            // generate keySet to iterate over
            Set<String> files = docs.keySet();

            // create list to store tuples
            ArrayList<Tuple<String, Integer>> tList = new ArrayList<>();

            for (String fileKey : files) {
                // write out ngrams and calculate size of data
                int sizeof = 0;
                for (int i = 0; i < docs.get(fileKey).size() - 1; i++) {
                    String writeString = docs.get(fileKey).get(i) + " ";

                    // use length of the getBytes array to find size of ngram and increase doc size
                    sizeof += writeString.getBytes().length;
                    w.write(writeString);
                }

            // create tuple and add to list
            tList.add(new Tuple<>(fileKey, sizeof));
        }
            return tList;

        } catch (Exception e){
            System.out.println("storeNGrams: " + e.getMessage());
        }
        // return empty list if error opening writer
        return List.of();
    }

    @Override
    public TreeSet<Similarities> computeSimilarities(String nwordFilePath, List<Tuple<String, Integer>> fileindex) {
        TreeSet<Similarities> similaritiesSet = new TreeSet<>();
        // open file for reading
        try (RandomAccessFile ngramFile = new RandomAccessFile(nwordFilePath, "r")
        )
        {
            // method outline:
            // iterate over tuples with the nested loop
            // i loop file : put every ngram into a hash set
            // j file : check whether i file hash set contains every ngram in j file
                // if matches > 0
                    // add to new Similarities with the two file names and counted matches


            // iterate over fileIndex to test and add all relevant pairs of files to the set
            int iByteCounter = 0;
            int jByteCounter;

            for (int i = 0; i < fileindex.size() - 1; i++) {
                // seek to position of ith file
                ngramFile.seek(iByteCounter);
                // get ith tuple
                Tuple<String, Integer> iFile = fileindex.get(i);
                // get the byte length of the ith file
                int iByteLength = iFile.getRight();

                // make a hashset of the ith file with helper method
                Set<String> iSet = nGramSet(ngramFile, iByteLength);

                // increment byte counter for every byte read by the readGram method
                iByteCounter += iByteLength;
                // set up the first j file for the inner loop
                jByteCounter = iByteCounter;

                for (int j = i + 1; j < fileindex.size(); j++){
                    // seek to position for j file
                    ngramFile.seek(jByteCounter);
                    // get tuple
                    Tuple<String, Integer> jFile = fileindex.get(j);
                    // get the byte length of the jth file
                    int jByteLength = jFile.getRight();

                    // count matches with other file using helper method
                    int matchCount = nGramMatches(ngramFile, jByteLength, iSet);

                    // if count > 0, create similarity and add it to set
                    if (matchCount > 0){
                        Similarities sim = new Similarities(iFile.getLeft(), jFile.getLeft());
                        sim.setCount(matchCount);
                        similaritiesSet.add(sim);
                    }

                    // increment counters to the byte representing the start of the next doc
                    jByteCounter += jByteLength + 1;
                }

            }

        } catch (Exception e){
            System.out.println("computeSimilarities: " + e.getMessage());
        }
        return similaritiesSet;
    }

    // helper method to turn ngrams from file into set
    public Set<String> nGramSet(RandomAccessFile raf, int byteSize) {
        StringBuilder ngram = new StringBuilder();
        Set<String> gramSet = new HashSet<>();

        try {
            // read all the bytes
            for (int i = 0; i < byteSize; i++){
                char c = (char) raf.read();
                if (c != ' ') {
                    // assemble the ngram character by character
                    ngram.append(c);
                } else{
                    // when file pointer hits a space, add the finished ngram to the set, reset the StringBuilder
                    gramSet.add(ngram.toString());
                    ngram.setLength(0);
                }
            }

        } catch (IOException e) {
            System.out.println("readNGrams: " + e.getMessage());
        }
        return gramSet;
    }

    // helper method to check every ngram in a file against a set
    public int nGramMatches(RandomAccessFile raf, int byteSize, Set<String> set){
        StringBuilder ngram = new StringBuilder();
        int matches = 0;
        try {
            for (int i = 0; i < byteSize; i++){
                char c = (char) raf.read();
                if (c != ' ') {
                    ngram.append(c);
                } else{
                    // exact same logic as the set building helper function, except we increment matches if the
                    // previously made set contains the ngram
                    if (set.contains(ngram.toString())){
                        matches++;
                    }
                    ngram.setLength(0);
                }
            }

        } catch (IOException e) {
            System.out.println("readNGrams: " + e.getMessage());
        }
    return matches;}

    @Override
    public void printSimilarities(TreeSet<Similarities> sims, int threshold) {
        // dummy similarity object with the threshold as its count to be used as fodder for the compareto method
        Similarities thresholdSim = new Similarities("null", "null");
        thresholdSim.setCount(threshold);

        // make a new subset with only elements greater than threshhold
        TreeSet<Similarities> toPrint = new TreeSet<>(sims.tailSet(thresholdSim));
        for (Similarities s : toPrint){
            System.out.println(s);
        }
    }


    public List<Tuple<String, Integer>> processAndStore(String directoryPath, String sequenceFile, int n){


        return List.of();
    }

}
