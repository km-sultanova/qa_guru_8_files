import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;


import java.io.File;

import static com.codeborne.selenide.Selenide.$;

public class SelenideDownloadTest {


    @Test
    void downloadTest() throws Exception{
        Selenide.open("https://github.com/junit-team/junit5/blob/main/README.md");
        File textFile = $("#raw-url").download();
        //System.out.println(href);

    }
}
