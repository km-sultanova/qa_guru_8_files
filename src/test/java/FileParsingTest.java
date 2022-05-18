import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.opencsv.CSVReader;
import domain.Book;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileParsingTest {

    ClassLoader classLoader = FileParsingTest.class.getClassLoader();

    void pdfParsingTest(String name) throws Exception {
        InputStream stream = classLoader.getResourceAsStream(name);
        PDF pdf = new PDF(stream);
        Assertions.assertEquals(1, pdf.numberOfPages);
    }

    void xlsParsingTest(String name) throws Exception {
        InputStream stream = classLoader.getResourceAsStream(name);
        XLS xls = new XLS(stream);
        String stringCellValue = xls.excel.getSheetAt(0).getRow(1).getCell(1).getStringCellValue();
        Assertions.assertEquals(stringCellValue, "IndianRed");
    }

    void csvParsingTest(String name) throws Exception {
        try (InputStream stream = classLoader.getResourceAsStream(name);
             CSVReader reader = new CSVReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            List<String[]> content = reader.readAll();
            org.assertj.core.api.Assertions.assertThat(content).contains(
                    new String[]{"Color", "Hex"},
                    new String[]{"IndianRed", "CD5C5C"}
            );
        }
    }

    @Test
    void zipParsingTest() throws Exception {
        try (InputStream stream = classLoader.getResourceAsStream("Somedata.zip");
             ZipInputStream zis = new ZipInputStream(stream)) {
            ZipEntry entry;
            while((entry = zis.getNextEntry()) != null) {
                if (entry.getName().contains(".xlsx")) {
                    org.assertj.core.api.Assertions.assertThat(entry.getName()).isEqualTo("Colors.xlsx");
                    xlsParsingTest(entry.getName());
                } else if (entry.getName().contains(".csv")) {
                    csvParsingTest(entry.getName());
                } else if (entry.getName().contains(".pdf")) {
                    pdfParsingTest(entry.getName());
                }
            }
        }
    }

    @Test
    void jsonCommonParseTest() throws Exception {
        String bookName = "Harry Potter and the Philosopher's Stone";
        Gson gson = new Gson();
        try (InputStream stream = classLoader.getResourceAsStream("simple.json")){
            String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
            org.assertj.core.api.Assertions.assertThat(jsonObject.get("bookName").getAsString()).isEqualTo(bookName);
            org.assertj.core.api.Assertions.assertThat(jsonObject.get("housesColors")
                    .getAsJsonObject().get("Gryffindor").getAsString()).isEqualTo("red and gold");
            org.assertj.core.api.Assertions.assertThat(jsonObject.get("housesColors")
                    .getAsJsonObject().size()).isEqualTo(4);

        }
    }

    @Test
    void jsonTypeParseTest() throws Exception {
        String bookName = "Harry Potter and the Philosopher's Stone";
        Gson gson = new Gson();
        try (InputStream stream = classLoader.getResourceAsStream("simple.json")){
            String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            Book jsonObject = gson.fromJson(json, Book.class);
            org.assertj.core.api.Assertions.assertThat(jsonObject.bookName).isEqualTo(bookName);
            System.out.println(jsonObject.housesColors.gryffindor);
            org.assertj.core.api.Assertions.assertThat(jsonObject.housesColors.gryffindor).isEqualTo("red and gold");
            org.assertj.core.api.Assertions.assertThat(jsonObject.translateTo.size()).isEqualTo(5);
        }
    }
}
