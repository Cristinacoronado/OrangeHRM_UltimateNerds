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


    @Test(priority = 0)
    public void addMapNews() throws InterruptedException {
        logIn("Admin");
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

    @Test (priority = 1)
    public void addNewsitems() throws InterruptedException{

        logIn("Admin");
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
        Thread.sleep(2000);

        List<WebElement> key = driver.findElements(By.xpath("//td/a[@class='newsTopic']"));
        System.out.println(key.size());
        updatedsize = key.size();
        //verify if size of table increased after added
        Assert.assertEquals(initialsize+difference, updatedsize, "Size of the table is not updated");
        String expectedtopic = "Congratulations UltimateNerds";
        Thread.sleep(2000);
        //verify if new news is displayed on table
        String actualtopic = driver.findElement(By.xpath("//td/a[text()='Congratulations UltimateNerds']")).getText();
        System.out.println("actualtopic: " + actualtopic);
        Assert.assertEquals(actualtopic,expectedtopic);
    }

    @Test (priority = 2)
    public void verifyNewlyAdd() throws InterruptedException{
        logIn("1stLevelSupervisor");
        WebElement announcements = driver.findElement(By.xpath("//span[text()='Announcements']"));
        announcements.click();
        WebElement news = driver.findElement(By.xpath("//span[text()='News']"));
        news.click();
        //verify if new news is added to this section
        WebElement newsadd = driver.findElement(By.xpath("//div[contains(text(),'Congratulations UltimateNerds')]"));
        Assert.assertEquals(newsadd.getText(),"Congratulations UltimateNerds");

        //Verifytopic
        WebElement topic = driver.findElement(By.xpath("//div[contains(text(),'Congratulations UltimateNerds')]"));
        String expectedtopic = "Congratulations UltimateNerds";
        Assert.assertEquals(topic.getText(),expectedtopic);

        //VerifyDes.
        topic.click();
        Thread.sleep(2000);
        WebElement description = driver.findElement(By.xpath("//div[@class='tinymce-saved-content']//p[contains(text(),'UltimateNerds')]"));
        String expectedDescription = "Promotion was awarded to UltimateNerds 08/10/2020";
        System.out.println(description.getText());
        Assert.assertEquals(description.getText(),expectedDescription);

    }

    @Test(priority = 3)
    public void verifyDeleteNews() throws InterruptedException{

        logIn("Admin");
        driver.findElement(By.xpath("//a//span[text()='Admin']")).click();
        driver.findElement(By.xpath("//li[@id='menu_news_Announcements']//span[text()='Announcements']")).click();
        driver.findElement(By.xpath("//a[@id='menu_news_viewNewsList']//span[text()='News']")).click();
        driver.switchTo().parentFrame();
        Thread.sleep(2000);
        driver.switchTo().frame("noncoreIframe");
        Thread.sleep(2000);
        WebElement checkBox = driver.findElement(By.xpath("//td/a[contains(text(),'Congratulations UltimateNerds')]/../..//label"));
        checkBox.click();
        WebElement clickdot = driver.findElement(By.xpath("//th/a/i[@class='material-icons icons-color handCurser orange-text']"));
        clickdot.click();
        Thread.sleep(1000);
        WebElement deletebtn = driver.findElement(By.xpath("//a[@id='newsDelete']"));

        deletebtn.click();
        WebElement confirmDlt = driver.findElement(By.xpath("//a[@id='news-delete-button']"));
        confirmDlt.click();
        Thread.sleep(2000);
        List <WebElement> topicexist = driver.findElements(By.xpath("//td/a[contains(text(),'Congratulations UltimateNerds')]"));

        //verify item not exist after delete
        Assert.assertEquals(topicexist.size(),0);
        Thread.sleep(2000);

        List<WebElement> key = driver.findElements(By.xpath("//td/a[@class='newsTopic']"));
        System.out.println(key.size());
        //verify table size is less by one after delete
        Assert.assertEquals(key.size(),updatedsize-difference);

    }


     @AfterMethod
        public void tearDown() throws InterruptedException{
        Thread.sleep(3000);
        driver.quit();
        }


        //sepearate medthod for login1

//        public void loginAsAdmin() {
//
//        WebElement login = driver.findElement(By.xpath("//button[@class='btn btn-primary dropdown-toggle']"));
//        login.click();
//        WebElement admin = driver.findElement(By.xpath("(//a[@class='login-as'])[2]"));
//        admin.click();
//
//        }
//
//        //sepearate medthod for login2
//
//        public void logIn1stLevelSupervisor() {
//            WebElement login = driver.findElement(By.xpath("//button[@class='btn btn-primary dropdown-toggle']"));
//            login.click();
//            WebElement firstlevelSupervisor = driver.findElement(By.xpath("//a[text()='1st Level Supervisor']"));
//            firstlevelSupervisor.click();
//        }

        public void logIn(String a){
             if(a.equalsIgnoreCase("Admin")){
            WebElement login = driver.findElement(By.xpath("//button[@class='btn btn-primary dropdown-toggle']"));
            login.click();
            WebElement admin = driver.findElement(By.xpath("(//a[@class='login-as'])[2]"));
            admin.click();
            } else if
             (a.equalsIgnoreCase("1stLevelSupervisor")){
            WebElement login = driver.findElement(By.xpath("//button[@class='btn btn-primary dropdown-toggle']"));
            login.click();
            WebElement firstlevelSupervisor = driver.findElement(By.xpath("//a[text()='1st Level Supervisor']"));
            firstlevelSupervisor.click();
            } else {
            System.out.println("User is not allowed ");
        }

        }




}
