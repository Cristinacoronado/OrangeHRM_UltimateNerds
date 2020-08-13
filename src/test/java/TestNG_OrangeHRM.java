import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Wait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.Assertion;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class TestNG_OrangeHRM {

    int initialsize;
    int updatedsize;
    int difference = 0;
    WebDriver driver = null;

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        driver.get("https://orangehrm-demo-6x.orangehrmlive.com/");
    }

    @Test(priority = 0)
    public void addMapNews() throws InterruptedException {
        logIn("admin");
        Thread.sleep(3000);
        driver.switchTo().frame("noncoreIframe");
        List<WebElement> key = driver.findElements(By.xpath("//td/a[@class='newsTopic']"));

        Map<String, List<String>> data = new HashMap<String, List<String>>();

        //putting keys to the map
        for (int i = 0; i < key.size(); i++) {
            data.put(key.get(i).getText(), null);
        }

        for (String each : data.keySet()) {
            List<String> list = new ArrayList<String>();
            list.add(driver.findElement(By.xpath("(//table[@id='resultTable']//tbody//a[text()='" + each + "']/../../td)[3]")).getText());
            list.add(driver.findElement(By.xpath("(//table[@id='resultTable']//tbody//a[text()='" + each + "']/../../td)[6]")).getText());

            if (driver.findElement(By.xpath("(//table[@id='resultTable']//tbody//a[text()='" + each + "']/../../td)[7]/i")).getAttribute("class")
                    .equals("material-icons attachment disabled handCurser")) {
                list.add("Attachment - NO");
            } else {
                list.add("Attachment - YES");
            }

            data.put(each, list);
        }
        for (String each : data.keySet()) {
            System.out.print(each);
            for (int i = 0; i < data.get(each).size(); i++) {
                System.out.print(" | " + data.get(each).get(i));
            }
            System.out.println();
        }

        initialsize = data.size();
        System.out.println("count of news : " + initialsize);

    }

    @Test (priority = 1)
    public void addNewsitems() {

        logIn("admin");
        driver.switchTo().frame("noncoreIframe");
        driver.findElement(By.cssSelector(".large.material-icons")).click();
        driver.findElement(By.name("news[topic]")).sendKeys("Congratulations UltimateNerds");
        driver.switchTo().frame("news_description_ifr");
        driver.findElement(By.id("tinymce")).sendKeys("Promotion was awarded to UltimateNerds 08/10/2020");
        driver.switchTo().parentFrame();
        driver.findElement(By.id("nextBtn")).click();
        driver.findElement(By.xpath("//*[contains(text(), 'All User Roles')]")).click();
        driver.findElement(By.xpath("//*[text()='Publish']")).click();
        difference++;

        List<WebElement> key = driver.findElements(By.xpath("//td/a[@class='newsTopic']"));
        System.out.println(key.size());
        updatedsize = key.size();
        //verify if size of table increased after added
        Assert.assertEquals(initialsize+difference, updatedsize, "Size of the table is not updated");
        String expectedtopic = "Congratulations UltimateNerds";
       // Thread.sleep(2000);
        //verify if new news is displayed on table
        String actualtopic = driver.findElement(By.xpath("//td/a[text()='Congratulations UltimateNerds']")).getText();
        System.out.println("actualtopic: " + actualtopic);
        Assert.assertEquals(actualtopic,expectedtopic);
    }

    @Test (priority = 2)
    public void verifyNewlyAdd() throws InterruptedException{
        logIn("1st Level Supervisor");
        Assert.assertEquals(driver.findElement(By.xpath("//*[contains(text(), 'UltimateNerds')]")).getText(), "Congratulations UltimateNerds");
        //Verifytopic
        WebElement topic = driver.findElement(By.xpath("//div[contains(text(),'Congratulations UltimateNerds')]"));
        String expectedtopic = "Congratulations UltimateNerds";
        Assert.assertEquals(topic.getText(),expectedtopic);

        //VerifyDes.
        topic.click();
       // Thread.sleep(2000);
        WebElement description = driver.findElement(By.xpath("//div[@class='tinymce-saved-content']//p[contains(text(),'UltimateNerds')]"));
        String expectedDescription = "Promotion was awarded to UltimateNerds 08/10/2020";
        System.out.println(description.getText());
        Assert.assertEquals(description.getText(),expectedDescription);

    }

    @Test(priority = 3)
    public void verifyDeleteNews() throws InterruptedException{
            logIn("admin");
            driver.switchTo().frame("noncoreIframe");
            if (driver.findElement(By.xpath("//*[contains(text(), 'UltimateNerds')]")).isDisplayed()) {
                driver.findElement(By.xpath("//*[contains(@for, 'checkbox')]")).click();
                driver.findElement(By.xpath("//*[text()='more_horiz']")).click();
                driver.findElement(By.id("newsDelete")).click();
                Thread.sleep(2000);
                driver.findElement(By.xpath("//*[text()='yes, delete']")).click();
                Thread.sleep(2000);

                List<WebElement> topics = driver.findElements(By.cssSelector(".newsTopic"));
                for(WebElement el : topics) {
                    Assert.assertFalse(el.getText().contains("UltimateNerds"));
                }
                int difference = updatedsize-topics.size();
                Assert.assertEquals(difference, 1);
            }
        }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }

    //login method
    public void logIn(String user){
        final String admin = "admin";
        final String supervisor = "1st Level Supervisor";
        switch (user) {
            case admin:
                driver.findElement(By.id("btnLogin")).click();
                driver.findElement(By.xpath("//*[text()='Admin']")).click();
                driver.findElement(By.xpath("//*[text()='Announcements']")).click();
                driver.findElement(By.xpath("//*[text()='News']")).click();
                break;
            case supervisor:
                driver.findElement(By.xpath("//*[@type='button']")).click();
                driver.findElement(By.xpath("//*[contains(text(),'"+supervisor+"')]")).click();
                driver.findElement(By.xpath("//*[text()='Announcements']")).click();
                driver.findElement(By.xpath("//*[text()='News']")).click();
                break;
            default:
                System.out.println("Wrong username");
        }
    }
}
