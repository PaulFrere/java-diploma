import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class LinksSuggester {

    private final File file;

    public LinksSuggester(File file) throws WrongLinksFormatException {
        this.file = file;
    }

    public List<Suggest> allSuggestFromConfig() throws FileNotFoundException {
        List<Suggest> suggestList = new ArrayList<>();
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            String[] conf = scanner.nextLine().split("\t");
            if (conf.length != 3) {
                throw new WrongLinksFormatException("Not 3 parameters are specified in the file.");
            }
            suggestList.add(new Suggest(conf[0], conf[1], conf[2]));
        }
        scanner.close();
        return suggestList;
    }


    public List<Suggest> suggest(List<Suggest> suggestList,String text) {
        return suggestList.stream()
                .filter(p -> StringUtils.containsIgnoreCase(text, p.getKeyWord()))
                .distinct()
                .collect(Collectors.toList());
    }
}
