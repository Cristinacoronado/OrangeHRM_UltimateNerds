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
import javax.xml.ws.Action;
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


    @Test
    public void addMapNews() throws InterruptedException {
        loginAsAdmin();
        driver.findElement(By.xpath("//a//span[text()='Admin']")).click();
        driver.findElement(By.xpath("//li[@id='menu_news_Announcements']//span[text()='Announcements']")).click();
        driver.findElement(By.xpath("//a[@id='menu_news_viewNewsList']//span[text()='News']")).click();
        driver.switchTo().parentFrame();
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
                list.add("Attachment - No");
            } else {
                list.add("Attachment - Yes");
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

    @Test
    public void addNewsitems() throws InterruptedException{
        loginAsAdmin();
        driver.findElement(By.xpath("//a//span[text()='Admin']")).click();
        driver.findElement(By.xpath("//li[@id='menu_news_Announcements']//span[text()='Announcements']")).click();
        driver.findElement(By.xpath("//a[@id='menu_news_viewNewsList']//span[text()='News']")).click();
        driver.switchTo().parentFrame();
        Thread.sleep(3000);
        driver.switchTo().frame("noncoreIframe");
        WebElement add = driver.findElement(By.xpath("//i[@class='large material-icons']"));
        add.click();
        WebElement topic = driver.findElement(By.xpath("//input[@class='formInputText']"));
        String name = "UltimateNerds";
        topic.sendKeys("Congratulations " + name);
        difference++;
        driver.switchTo().frame("news_description_ifr");
        WebElement description = driver.findElement(By.xpath("//body[@id='tinymce']"));
        description.sendKeys("Promotion was awarded to " + name + " 08/10/2020");

        driver.switchTo().parentFrame();
        WebElement nextBtn = driver.findElement(By.xpath("//button[@id='nextBtn']"));
        nextBtn.click();

        WebElement chkalluser = driver.findElement(By.xpath("//div[@class='input-field col s12 m12 l12']//label[@for='news_publish_all']"));
        Thread.sleep(3000);
        chkalluser.click();
        Thread.sleep(3000);
        WebElement publishbtn = driver.findElement(By.xpath("//div/button[@btn-type='publish']"));
        publishbtn.click();

        List<WebElement> key = driver.findElements(By.xpath("//td/a[@class='newsTopic']"));
        System.out.println(key.size());
        updatedsize = key.size();
        //verify
        Assert.assertEquals(initialsize+difference, updatedsize, "Size of the table is not updated");
        String expectedtopic = "Congratulations UltimateNerds";
        String actualtopic = driver.findElement(By.xpath("//td/a[text()='Congratulations UltimateNerds']")).getText();
        System.out.println("actualtopic: " + actualtopic);
        Assert.assertEquals(actualtopic,expectedtopic);
    }


//     @AfterMethod
//        public void tearDown(){
//        driver.close();
//        }
        //separate medthod to use
     //sepearate medthod for login

        public void loginAsAdmin() {

        WebElement login = driver.findElement(By.xpath("//button[@class='btn btn-primary dropdown-toggle']"));
        login.click();
        WebElement admin = driver.findElement(By.xpath("(//a[@class='login-as'])[2]"));
        admin.click();
        }

        //sepearate medthod for login

        public void logIn1stLevelSupervisor() throws InterruptedException{
            WebElement login = driver.findElement(By.xpath("//button[@class='btn btn-primary dropdown-toggle']"));
            login.click();
            WebElement levelSupervisor = driver.findElement(By.xpath("//a[text()='1st Level Supervisor']"));
            levelSupervisor.click();
        }




}
