import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Link;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws Exception {
        // создаём конфиг
        LinksSuggester linksSuggester = new LinksSuggester(new File("data/config"));


        // перебираем пдфки в data/pdfs
            var dir = new File("data/pdfs");
            for (var fileIn : Objects.requireNonNull(dir.listFiles())) {
                List<Suggest> allSuggestFromConfig = linksSuggester.allSuggestFromConfig();

        // для каждой пдфки создаём новую в data/converted

                var doc = new PdfDocument(new PdfReader(fileIn), new PdfWriter("data/converted/" + fileIn.getName()));


                // перебираем страницы pdf

                for (int i = 1; i <= doc.getNumberOfPages(); i++) {
                    var text = PdfTextExtractor.getTextFromPage(doc.getPage(i));
                    List<Suggest> suggestList = linksSuggester.suggest(allSuggestFromConfig, text);

        // если в странице есть неиспользованные ключевые слова, создаём новую страницу за ней

                    if (!suggestList.isEmpty() && !allSuggestFromConfig.isEmpty()) {
                        for (Suggest sug : suggestList) {
                            allSuggestFromConfig.removeIf(n -> (n.equals(sug)));
                        }

                        var newPage = doc.addNewPage(i + 1);

        // вставляем туда рекомендуемые ссылки из конфига

                        var rect = new Rectangle(newPage.getPageSize()).moveRight(10).moveDown(10);
                        Canvas canvas = new Canvas(newPage, rect);
                        Paragraph paragraph = new Paragraph("Suggestions:\n");
                        paragraph.setFontSize(25);

                        for (Suggest sugg : suggestList) {
                            PdfLinkAnnotation annotation = new PdfLinkAnnotation(rect);
                            PdfAction action = PdfAction.createURI(sugg.getUrl());
                            annotation.setAction(action);
                            Link link = new Link(sugg.getTitle(), annotation);
                            paragraph.add(link.setUnderline());
                            paragraph.add("\n");
                        }
                        canvas.add(paragraph);
                        canvas.close();
                        i++;
                    }
                }
                doc.close();
            }
    }
}
