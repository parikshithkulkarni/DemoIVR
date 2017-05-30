package DemoIVRPackage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.perfecto.reportium.client.ReportiumClient;
import com.perfecto.reportium.client.ReportiumClientFactory;
import com.perfecto.reportium.model.Job;
import com.perfecto.reportium.model.PerfectoExecutionContext;
import com.perfecto.reportium.model.Project;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResultFactory;


public class DemoIVR {
	RemoteWebDriver driver;
	PerfectoExecutionContext perfectoExecutionContext;
	ReportiumClient reportiumClient;
	
	
	@Parameters({ "platformName", "platformVersion", "manufacturer", "model", "deviceName", "appID" })
	@BeforeTest
	public void beforeTest(String platformName, String platformVersion, String manufacturer, String model, String deviceName, String appID) throws IOException {
		driver = Utils.getRemoteWebDriver(platformName, platformVersion, manufacturer, model, deviceName, appID );        
      PerfectoExecutionContext perfectoExecutionContext = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
              .withProject(new Project("My Project", "1.0"))
              .withJob(new Job("My Job", 45))
              .withContextTags("tag1")
              .withWebDriver(driver)
              .build();
       reportiumClient = new ReportiumClientFactory().createPerfectoReportiumClient(perfectoExecutionContext);
	} 

	  @Test
	  public void test() {
	      try {
	    	  reportiumClient.testStart("IVR Test", new TestContext("DTMF-driven", "tag3"));
	    	  System.out.println("Yay");

	    	  PerfectoUtils.androidCall(driver, System.getenv().get("IVR_TEST_PHONE_NUMBER"));
	    	  //testIVRFlowViaDTMF(driver);
	    	  testIVRFlowViaT2S(driver);
	    	  PerfectoUtils.disconnectAndroidCall(driver);
	          // write your code here

	          // reportiumClient.testStep("step1"); // this is a logical step for reporting
	          // reportiumClient.testStep("step2");
	          reportiumClient.testStop(TestResultFactory.createSuccess());
	      } catch (Exception e) {
	          //reportiumClient.testStop(TestResultFactory.createFailure(e.getMessage(), e));
	          e.printStackTrace();
	      }
	  }

	  @AfterTest
	  public void afterTest() {
	      try {
	          // Retrieve the URL of the Single Test Report, can be saved to your execution summary and used to download the report at a later point
	          String reportURL = reportiumClient.getReportUrl();

	          // For documentation on how to export reporting PDF, see https://github.com/perfectocode/samples/wiki/reporting
	          // String reportPdfUrl = (String)(driver.getCapabilities().getCapability("reportPdfUrl"));

	          driver.close();
	          System.out.println("Report: "+ reportURL);


	          // In case you want to download the report or the report attachments, do it here.
	          // PerfectoLabUtils.downloadAttachment(driver, "video", "C:\\test\\report\\video", "flv");
	          // PerfectoLabUtils.downloadAttachment(driver, "image", "C:\\test\\report\\images", "jpg");

	      } catch (Exception e) {
	          e.printStackTrace();
	      }

	      driver.quit();
	  }
	  private static void testIVRFlowViaDTMF(RemoteWebDriver driver) throws InterruptedException, IOException{
		  ArrayList<String> response=new ArrayList<String>(),
				  recordings = new ArrayList<String>();
		  recordings.add(listen(driver, 15));
		  PerfectoUtils.ocrTextClick(driver, "Keypad", 99, 20);
    	  
		  
		  AppiumTest.switchToContext(driver, "NATIVE_APP");
		  // "other Options"
		  driver.findElementByXPath("//*[@text=\"1\"]").click();
		  driver.findElementByXPath("//*[@text=\"#\"]").click();
		  recordings.add(listen(driver, 15));
		  
		  // "find ATM"
		  driver.findElementByXPath("//*[@text=\"2\"]").click();
		  recordings.add(listen(driver, 15));
		  
		  // "by branch"
		  driver.findElementByXPath("//*[@text=\"1\"]").click();
		  recordings.add(listen(driver, 15));
		  
		  // "by zipcode"
		  driver.findElementByXPath("//*[@text=\"1\"]").click();
		  recordings.add(listen(driver, 15));
		  
		  // "60606#"
		  driver.findElementByXPath("//*[@text=\"6\"]").click();
		  driver.findElementByXPath("//*[@text=\"0\"]").click();
		  driver.findElementByXPath("//*[@text=\"6\"]").click();
		  driver.findElementByXPath("//*[@text=\"0\"]").click();
		  driver.findElementByXPath("//*[@text=\"6\"]").click();
		  driver.findElementByXPath("//*[@text=\"#\"]").click();
		  recordings.add(listen(driver, 15));
		  
		  Thread.sleep(10000);
		  for (String s:recordings){
			String temp = VoiceServices.speechToText(s);  
			  response.add(temp);
			  System.out.println("response string: " + temp );
		  }
			int x = 0;
		  for (String s:response){
			String expected = validate(x).toLowerCase(),
					gotten = s.toLowerCase();
			System.out.println("expected: " + expected + " gotten: "+ gotten);
			  if (gotten.contains(expected))
				  System.out.println("Success!  " );
			  else
				  System.out.println("failed!  " );
				  
			  x= x+1;
		  }
	  }
		  private static void testIVRFlowViaT2S(RemoteWebDriver driver) throws InterruptedException, IOException{
			  ArrayList<String> response=new ArrayList<String>(),
					  recordings = new ArrayList<String>();
			  recordings.add(listen(driver, 15));
	    	  
			  
			  // "other Options"
			  VoiceServices.textToSpeech("Other Options", System.getenv().get("Project_Path")+"/media"+"/Other_Options.wav", true, System.getenv().get("PERFECTO_CLOUD_REPOSITORY_KEY")+"other_options.wav");
			  PerfectoUtils.injectAudio(driver, System.getenv().get("PERFECTO_CLOUD_REPOSITORY_KEY")+"other_options.wav");	  
			  recordings.add(listen(driver, 15));
			  
			  // "find ATM"
			  VoiceServices.textToSpeech("find ATM", System.getenv().get("Project_Path")+"/media"+"/Find_ATM.wav", true, System.getenv().get("PERFECTO_CLOUD_REPOSITORY_KEY")+"find_atm.wav");
			  PerfectoUtils.injectAudio(driver, System.getenv().get("PERFECTO_CLOUD_REPOSITORY_KEY")+"find_atm.wav");	  
			  recordings.add(listen(driver, 15));
			  			  
			  // "by zipcode"
			  VoiceServices.textToSpeech("zip code", System.getenv().get("Project_Path")+"/media"+"/zipcode.wav", true, System.getenv().get("PERFECTO_CLOUD_REPOSITORY_KEY")+"zipcode.wav");
			  PerfectoUtils.injectAudio(driver, System.getenv().get("PERFECTO_CLOUD_REPOSITORY_KEY")+"zipcode.wav");	  
			  recordings.add(listen(driver, 15));
			  
			  // "60606#"
			  VoiceServices.textToSpeech("6 0 6 0 6", System.getenv().get("Project_Path")+"/media"+"/60606.wav", true, System.getenv().get("PERFECTO_CLOUD_REPOSITORY_KEY")+"60606.wav");
			  PerfectoUtils.injectAudio(driver, System.getenv().get("PERFECTO_CLOUD_REPOSITORY_KEY")+"60606.wav");	  
//			  driver.findElementByXPath("//*[@text=\"#\"]").click();
			  recordings.add(listen(driver, 15));

			  // "60606#"
			  VoiceServices.textToSpeech("more information", System.getenv().get("Project_Path")+"/media"+"/more_information.wav", true, System.getenv().get("PERFECTO_CLOUD_REPOSITORY_KEY")+"more_information.wav");
			  PerfectoUtils.injectAudio(driver, System.getenv().get("PERFECTO_CLOUD_REPOSITORY_KEY")+"more_information.wav");	  
//			  driver.findElementByXPath("//*[@text=\"#\"]").click();
			  recordings.add(listen(driver, 15));

			  
			  Thread.sleep(10000);
			  for (String s:recordings){
				String temp = VoiceServices.speechToText(s);  
				  response.add(temp);
				  System.out.println("response string: " + temp );
			  }
				int x = 0;
			  for (String s:response){
				String expected = validate(x).toLowerCase(),
						gotten = s.toLowerCase();
				System.out.println("expected: " + expected + " gotten: "+ gotten);
				  if (gotten.contains(expected))
					  System.out.println("Success!  " );
				  else
					  System.out.println("failed!  " );
					  
				  x= x+1;
			  }
	  
	  }
	  private static String validate(int step){
		  switch (step){
		  case 0: return "other options";
		  case 1: return "ATM";
		  case 2: return "branch";
		  case 3: return "zip code";
		  case 4: return "five digit";
		  case 5: return "two eighty nine howard";
		  case 6: return "nine pm";
		  default: return "";
		  }
	  }
	  private static String listen(RemoteWebDriver driver, int seconds) throws IOException, InterruptedException{

		  String response = "";
		  try {
			  String audioFileRecording = PerfectoUtils.startAudioRecording(driver);
			  Thread.sleep(seconds * 1000);
			  PerfectoUtils.stopAudioRecording(driver);
			  return audioFileRecording;
		  } catch (Exception e) {
			  // TODO Auto-generated catch block
			  e.printStackTrace();
		  }
		  return response;

	  }

}
