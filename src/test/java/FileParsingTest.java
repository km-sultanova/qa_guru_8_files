import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.io.Zip;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class FileParsingTest {

    ClassLoader classLoader = FileParsingTest.class.getClassLoader();

    @Test
    void pdfParsingTest() throws Exception {
        InputStream stream = classLoader.getResourceAsStream("cats.pdf");
        PDF pdf = new PDF(stream);
        Assertions.assertEquals(1, pdf.numberOfPages);
        stream.close();
    }

    @Test
    void xlsParsingTest() throws Exception {
        InputStream stream = classLoader.getResourceAsStream("Colors.xlsx");
        XLS xls = new XLS(stream);
        String stringCellValue = xls.excel.getSheetAt(0).getRow(1).getCell(1).getStringCellValue();
        System.out.println(stringCellValue);
        stream.close();
    }

    @Test
    void csvParsingTest() throws Exception {
        try (InputStream stream = classLoader.getResourceAsStream("Colors.csv");
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
             ZipInputStream zis = new ZipInputStream(stream)){
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null){
                org.assertj.core.api.Assertions.assertThat(entry.getName()).isEqualTo("Colors.xlsx");

            }
        }
    }
}
