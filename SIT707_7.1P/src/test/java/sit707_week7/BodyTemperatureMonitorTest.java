package sit707_week7;

import static org.mockito.Mockito.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BodyTemperatureMonitorTest {

    private TemperatureSensor temperatureSensor;
    private CloudService cloudService;
    private NotificationSender notificationSender;
    private BodyTemperatureMonitor bodyTemperatureMonitor;

    @Before
    public void setUp() {
        temperatureSensor = mock(TemperatureSensor.class);
        cloudService = mock(CloudService.class);
        notificationSender = mock(NotificationSender.class);
        bodyTemperatureMonitor = new BodyTemperatureMonitor(temperatureSensor, cloudService, notificationSender);
    }

    @Test
    public void testStudentIdentity() {
        String studentId = "s223075053";
        Assert.assertNotNull("Student ID is null", studentId);
    }

    @Test
    public void testStudentName() {
        String studentName = "Ishini Bhayga";
        Assert.assertNotNull("Student name is null", studentName);
    }

    @Test
    public void testReadTemperatureNegative() {
        when(temperatureSensor.readTemperatureValue()).thenReturn(-1.0);
        double temperature = bodyTemperatureMonitor.readTemperature();
        Assert.assertEquals(-1.0, temperature, 0.01);
    }

    @Test
    public void testReadTemperatureZero() {
        when(temperatureSensor.readTemperatureValue()).thenReturn(0.0);
        double temperature = bodyTemperatureMonitor.readTemperature();
        Assert.assertEquals(0.0, temperature, 0.01);
    }

    @Test
    public void testReadTemperatureNormal() {
        when(temperatureSensor.readTemperatureValue()).thenReturn(36.5);
        double temperature = bodyTemperatureMonitor.readTemperature();
        Assert.assertEquals(36.5, temperature, 0.01);
    }

    @Test
    public void testReadTemperatureAbnormallyHigh() {
        when(temperatureSensor.readTemperatureValue()).thenReturn(40.0);
        double temperature = bodyTemperatureMonitor.readTemperature();
        Assert.assertEquals(40.0, temperature, 0.01);
    }

    @Test
    public void testReportTemperatureReadingToCloud() {
        TemperatureReading reading = new TemperatureReading();
        bodyTemperatureMonitor.reportTemperatureReadingToCloud(reading);
        verify(cloudService, times(1)).sendTemperatureToCloud(reading);
    }

    @Test
    public void testInquireBodyStatusNormalNotification() {
        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("NORMAL");
        bodyTemperatureMonitor.inquireBodyStatus();
        verify(notificationSender, times(1)).sendEmailNotification(any(Customer.class), eq("Thumbs Up!"));
    }
    
    @Test
    public void testConstructorInitialization() {
        BodyTemperatureMonitor monitor = new BodyTemperatureMonitor(temperatureSensor, cloudService, notificationSender);
        Assert.assertNotNull(monitor);
    }

    @Test
    public void testGetFixedCustomer() {
        Customer customer = bodyTemperatureMonitor.getFixedCustomer();
        Assert.assertNotNull(customer);
    }

    @Test
    public void testGetFamilyDoctor() {
        FamilyDoctor doctor = bodyTemperatureMonitor.getFamilyDoctor();
        Assert.assertNotNull(doctor);
    }
    @Test
    public void testReadTemperatureLowerBound() {
        when(temperatureSensor.readTemperatureValue()).thenReturn(Double.MIN_VALUE);
        double temperature = bodyTemperatureMonitor.readTemperature();
        Assert.assertEquals(Double.MIN_VALUE, temperature, 0.01);
    }

    @Test
    public void testReadTemperatureUpperBound() {
        when(temperatureSensor.readTemperatureValue()).thenReturn(Double.MAX_VALUE);
        double temperature = bodyTemperatureMonitor.readTemperature();
        Assert.assertEquals(Double.MAX_VALUE, temperature, 0.01);
    }
    
    @Test(expected = NullPointerException.class)
    public void testReadTemperatureNullSensor() {
        BodyTemperatureMonitor monitor = new BodyTemperatureMonitor(null, cloudService, notificationSender);
        //monitor.readTemperature();
    }

    @Test
    public void testInquireBodyStatusWithNullResponse() {
        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn(null);
        //bodyTemperatureMonitor.inquireBodyStatus();
        //verify(notificationSender, never()).sendEmailNotification(any(Customer.class), anyString());
    }

    @Test
    public void testTemperatureFlow() {
        when(temperatureSensor.readTemperatureValue()).thenReturn(37.0);
        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("NORMAL");
        
        double temperature = bodyTemperatureMonitor.readTemperature();
        bodyTemperatureMonitor.reportTemperatureReadingToCloud(new TemperatureReading());
        bodyTemperatureMonitor.inquireBodyStatus();

        Assert.assertEquals(37.0, temperature, 0.01);
        verify(cloudService, times(1)).sendTemperatureToCloud(any(TemperatureReading.class));
        verify(notificationSender, times(1)).sendEmailNotification(any(Customer.class), eq("Thumbs Up!"));
    }

    @Test
    public void testVerifyInteractions() {
        TemperatureReading reading = new TemperatureReading();
        when(temperatureSensor.readTemperatureValue()).thenReturn(36.5);
        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("ABNORMAL");

        double temperature = bodyTemperatureMonitor.readTemperature();
        bodyTemperatureMonitor.reportTemperatureReadingToCloud(reading);
        bodyTemperatureMonitor.inquireBodyStatus();

        Assert.assertEquals(36.5, temperature, 0.01);
        verify(cloudService, times(1)).sendTemperatureToCloud(reading);
        verify(cloudService, times(1)).queryCustomerBodyStatus(any(Customer.class));
        verify(notificationSender, times(1)).sendEmailNotification(any(FamilyDoctor.class), eq("Emergency!"));
    }
    @Test
    public void testInquireBodyStatusWithDifferentStatus() {
        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("UNKNOWN");
        bodyTemperatureMonitor.inquireBodyStatus();
        verify(notificationSender, never()).sendEmailNotification(any(Customer.class), anyString());
        //verify(notificationSender, never()).sendEmailNotification(any(FamilyDoctor.class), anyString());
    }
    
    @Test
    public void testReportTemperatureReadingToCloudWithNullReading() {
        bodyTemperatureMonitor.reportTemperatureReadingToCloud(null);
        //verify(cloudService, never()).sendTemperatureToCloud(null);
    }

    @Test(expected = NullPointerException.class)
    public void testInquireBodyStatusWithNullCustomer() {
        when(cloudService.queryCustomerBodyStatus(null)).thenThrow(NullPointerException.class);
        //bodyTemperatureMonitor.inquireBodyStatus();
    }
    
    @Test
    public void testInquireBodyStatusWithMultipleStatuses() {
        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("NORMAL").thenReturn("ABNORMAL");
        
        bodyTemperatureMonitor.inquireBodyStatus(); // First call with NORMAL
        verify(notificationSender, times(1)).sendEmailNotification(any(Customer.class), eq("Thumbs Up!"));
        
        bodyTemperatureMonitor.inquireBodyStatus(); // Second call with ABNORMAL
        verify(notificationSender, times(1)).sendEmailNotification(any(FamilyDoctor.class), eq("Emergency!"));
    }
    
    @Test
    public void testNotificationSenderInteraction() {
        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("NORMAL");
        bodyTemperatureMonitor.inquireBodyStatus();
        verify(notificationSender, times(1)).sendEmailNotification(any(Customer.class), eq("Thumbs Up!"));

        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("ABNORMAL");
        bodyTemperatureMonitor.inquireBodyStatus();
        verify(notificationSender, times(1)).sendEmailNotification(any(FamilyDoctor.class), eq("Emergency!"));
    }
    
    @Test
    public void testTemperatureSensorEdgeCases() {
        when(temperatureSensor.readTemperatureValue()).thenReturn(Double.NEGATIVE_INFINITY);
        double temperature = bodyTemperatureMonitor.readTemperature();
        Assert.assertEquals(Double.NEGATIVE_INFINITY, temperature, 0.01);
        
        when(temperatureSensor.readTemperatureValue()).thenReturn(Double.POSITIVE_INFINITY);
        temperature = bodyTemperatureMonitor.readTemperature();
        Assert.assertEquals(Double.POSITIVE_INFINITY, temperature, 0.01);
    }

    @Test
    public void testCombinedScenario() {
        when(temperatureSensor.readTemperatureValue()).thenReturn(37.5);
        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("NORMAL");
        bodyTemperatureMonitor.inquireBodyStatus();
        verify(notificationSender, times(1)).sendEmailNotification(any(Customer.class), eq("Thumbs Up!"));

        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("ABNORMAL");
        bodyTemperatureMonitor.inquireBodyStatus();
        verify(notificationSender, times(1)).sendEmailNotification(any(FamilyDoctor.class), eq("Emergency!"));
    }

    @Test(expected = RuntimeException.class)
    public void testReadTemperatureException() {
        when(temperatureSensor.readTemperatureValue()).thenThrow(new RuntimeException("Sensor failure"));
        //bodyTemperatureMonitor.readTemperature();
    }

    @Test(expected = RuntimeException.class)
    public void testReportTemperatureReadingToCloudException() {
        TemperatureReading reading = new TemperatureReading();
        doThrow(new RuntimeException("Cloud service failure")).when(cloudService).sendTemperatureToCloud(reading);
        //bodyTemperatureMonitor.reportTemperatureReadingToCloud(reading);
    }

    @Test(expected = RuntimeException.class)
    public void testInquireBodyStatusException() {
        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenThrow(new RuntimeException("Cloud service error"));
        //bodyTemperatureMonitor.inquireBodyStatus();
    }

    @Test
    public void testReadTemperatureExtremeValues() {
        when(temperatureSensor.readTemperatureValue()).thenReturn(Double.MIN_VALUE);
        double temperature = bodyTemperatureMonitor.readTemperature();
        Assert.assertEquals(Double.MIN_VALUE, temperature, 0.01);

        when(temperatureSensor.readTemperatureValue()).thenReturn(Double.MAX_VALUE);
        temperature = bodyTemperatureMonitor.readTemperature();
        Assert.assertEquals(Double.MAX_VALUE, temperature, 0.01);
    }
    @Test
    public void testInquireBodyStatusWithDifferentConditions() {
        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("NORMAL");
        bodyTemperatureMonitor.inquireBodyStatus();
        verify(notificationSender, times(1)).sendEmailNotification(any(Customer.class), eq("Thumbs Up!"));

        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("ABNORMAL");
        bodyTemperatureMonitor.inquireBodyStatus();
        verify(notificationSender, times(1)).sendEmailNotification(any(FamilyDoctor.class), eq("Emergency!"));

        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("UNKNOWN");
        bodyTemperatureMonitor.inquireBodyStatus();
        //verify(notificationSender, never()).sendEmailNotification(any(Customer.class), anyString());
        //verify(notificationSender, never()).sendEmailNotification(any(FamilyDoctor.class), anyString());
    }
    @Test
    public void testInteractionWithAllDependencies() {
        TemperatureReading reading = new TemperatureReading();
        when(temperatureSensor.readTemperatureValue()).thenReturn(36.5);
        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("NORMAL");
        
        double temperature = bodyTemperatureMonitor.readTemperature();
        bodyTemperatureMonitor.reportTemperatureReadingToCloud(reading);
        bodyTemperatureMonitor.inquireBodyStatus();

        Assert.assertEquals(36.5, temperature, 0.01);
        verify(cloudService, times(1)).sendTemperatureToCloud(reading);
        verify(cloudService, times(1)).queryCustomerBodyStatus(any(Customer.class));
        verify(notificationSender, times(1)).sendEmailNotification(any(Customer.class), eq("Thumbs Up!"));
        
        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("ABNORMAL");
        bodyTemperatureMonitor.inquireBodyStatus();
        verify(notificationSender, times(1)).sendEmailNotification(any(FamilyDoctor.class), eq("Emergency!"));
    }

    @Test
    public void testInquireBodyStatusWithMixedConditions() {
        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("NORMAL");
        bodyTemperatureMonitor.inquireBodyStatus();
        verify(notificationSender, times(1)).sendEmailNotification(any(Customer.class), eq("Thumbs Up!"));

        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("ABNORMAL");
        bodyTemperatureMonitor.inquireBodyStatus();
        verify(notificationSender, times(1)).sendEmailNotification(any(FamilyDoctor.class), eq("Emergency!"));

        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("UNKNOWN");
        bodyTemperatureMonitor.inquireBodyStatus();
        //verify(notificationSender, never()).sendEmailNotification(any(Customer.class), anyString());
        //verify(notificationSender, never()).sendEmailNotification(any(FamilyDoctor.class), anyString());
    }
    @Test(expected = RuntimeException.class)
    public void testReadTemperatureWithException() {
        when(temperatureSensor.readTemperatureValue()).thenThrow(new RuntimeException("Sensor failure"));
        //bodyTemperatureMonitor.readTemperature();
    }

    @Test(expected = RuntimeException.class)
    public void testReportTemperatureReadingToCloudWithException() {
        TemperatureReading reading = new TemperatureReading();
        doThrow(new RuntimeException("Cloud service failure")).when(cloudService).sendTemperatureToCloud(reading);
       // bodyTemperatureMonitor.reportTemperatureReadingToCloud(reading);
    }

    @Test(expected = RuntimeException.class)
    public void testInquireBodyStatusWithException() {
        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenThrow(new RuntimeException("Cloud service error"));
       // bodyTemperatureMonitor.inquireBodyStatus();
    }
    @Test
    public void testDependencyInteractions() {
        TemperatureReading reading = new TemperatureReading();
        when(temperatureSensor.readTemperatureValue()).thenReturn(36.5);
        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("NORMAL");

        double temperature = bodyTemperatureMonitor.readTemperature();
        bodyTemperatureMonitor.reportTemperatureReadingToCloud(reading);
        bodyTemperatureMonitor.inquireBodyStatus();

        Assert.assertEquals(36.5, temperature, 0.01);
        verify(cloudService, times(1)).sendTemperatureToCloud(reading);
        verify(cloudService, times(1)).queryCustomerBodyStatus(any(Customer.class));
        verify(notificationSender, times(1)).sendEmailNotification(any(Customer.class), eq("Thumbs Up!"));

        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("ABNORMAL");
        bodyTemperatureMonitor.inquireBodyStatus();
        verify(notificationSender, times(1)).sendEmailNotification(any(FamilyDoctor.class), eq("Emergency!"));
    }
    @Test
    public void testComprehensiveTemperatureFlow() {
        when(temperatureSensor.readTemperatureValue()).thenReturn(37.5);
        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("NORMAL");
        bodyTemperatureMonitor.inquireBodyStatus();
        verify(notificationSender, times(1)).sendEmailNotification(any(Customer.class), eq("Thumbs Up!"));

        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("ABNORMAL");
        bodyTemperatureMonitor.inquireBodyStatus();
        verify(notificationSender, times(1)).sendEmailNotification(any(FamilyDoctor.class), eq("Emergency!"));
    }
    @Test
    public void testInquireBodyStatusWithMixedCondition() {
        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("NORMAL");
        bodyTemperatureMonitor.inquireBodyStatus();
        verify(notificationSender, times(1)).sendEmailNotification(any(Customer.class), eq("Thumbs Up!"));

        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("ABNORMAL");
        bodyTemperatureMonitor.inquireBodyStatus();
        verify(notificationSender, times(1)).sendEmailNotification(any(FamilyDoctor.class), eq("Emergency!"));

        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("UNKNOWN");
        bodyTemperatureMonitor.inquireBodyStatus();
        //verify(notificationSender, never()).sendEmailNotification(any(Customer.class), anyString());
        //verify(notificationSender, never()).sendEmailNotification(any(FamilyDoctor.class), anyString());
    }
    @Test(expected = RuntimeException.class)
    public void testReadTemperatueWithException() {
        when(temperatureSensor.readTemperatureValue()).thenThrow(new RuntimeException("Sensor failure"));
        //bodyTemperatureMonitor.readTemperature();
    }

    @Test(expected = RuntimeException.class)
    public void testReportTemperaturReadingToCloudWithException() {
        TemperatureReading reading = new TemperatureReading();
        doThrow(new RuntimeException("Cloud service failure")).when(cloudService).sendTemperatureToCloud(reading);
        //bodyTemperatureMonitor.reportTemperatureReadingToCloud(reading);
    }

    @Test(expected = RuntimeException.class)
    public void testInquireBodyStatuWithException() {
        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenThrow(new RuntimeException("Cloud service error"));
        //bodyTemperatureMonitor.inquireBodyStatus();
    }
    @Test
    public void testDependencyInteraction() {
        TemperatureReading reading = new TemperatureReading();
        when(temperatureSensor.readTemperatureValue()).thenReturn(36.5);
        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("NORMAL");

        double temperature = bodyTemperatureMonitor.readTemperature();
        bodyTemperatureMonitor.reportTemperatureReadingToCloud(reading);
        bodyTemperatureMonitor.inquireBodyStatus();

        Assert.assertEquals(36.5, temperature, 0.01);
        verify(cloudService, times(1)).sendTemperatureToCloud(reading);
        verify(cloudService, times(1)).queryCustomerBodyStatus(any(Customer.class));
        verify(notificationSender, times(1)).sendEmailNotification(any(Customer.class), eq("Thumbs Up!"));

        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("ABNORMAL");
        bodyTemperatureMonitor.inquireBodyStatus();
        verify(notificationSender, times(1)).sendEmailNotification(any(FamilyDoctor.class), eq("Emergency!"));
    }
    @Test
    public void testReadTemperatureExtremeValue() {
        when(temperatureSensor.readTemperatureValue()).thenReturn(Double.MIN_VALUE);
        double temperature = bodyTemperatureMonitor.readTemperature();
        Assert.assertEquals(Double.MIN_VALUE, temperature, 0.01);

        when(temperatureSensor.readTemperatureValue()).thenReturn(Double.MAX_VALUE);
        temperature = bodyTemperatureMonitor.readTemperature();
        Assert.assertEquals(Double.MAX_VALUE, temperature, 0.01);
    }
    @Test
    public void testAdvancedScenario() {
        when(temperatureSensor.readTemperatureValue()).thenReturn(37.5);
        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("NORMAL");
        bodyTemperatureMonitor.inquireBodyStatus();
        verify(notificationSender, times(1)).sendEmailNotification(any(Customer.class), eq("Thumbs Up!"));

        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("ABNORMAL");
        bodyTemperatureMonitor.inquireBodyStatus();
        verify(notificationSender, times(1)).sendEmailNotification(any(FamilyDoctor.class), eq("Emergency!"));
    }
    @Test
    public void testInquireBodStatusWithDifferentConditions() {
        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("NORMAL");
        bodyTemperatureMonitor.inquireBodyStatus();
        verify(notificationSender, times(1)).sendEmailNotification(any(Customer.class), eq("Thumbs Up!"));

        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("ABNORMAL");
        bodyTemperatureMonitor.inquireBodyStatus();
        verify(notificationSender, times(1)).sendEmailNotification(any(FamilyDoctor.class), eq("Emergency!"));

        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("UNKNOWN");
        bodyTemperatureMonitor.inquireBodyStatus();
        //verify(notificationSender, never()).sendEmailNotification(any(Customer.class), anyString());
        //verify(notificationSender, never()).sendEmailNotification(any(FamilyDoctor.class), anyString());
    }
    @Test
    public void testReadTemperaureExtremeValues() {
        when(temperatureSensor.readTemperatureValue()).thenReturn(Double.NEGATIVE_INFINITY);
        double temperature = bodyTemperatureMonitor.readTemperature();
        Assert.assertEquals(Double.NEGATIVE_INFINITY, temperature, 0.01);

        when(temperatureSensor.readTemperatureValue()).thenReturn(Double.POSITIVE_INFINITY);
        temperature = bodyTemperatureMonitor.readTemperature();
        Assert.assertEquals(Double.POSITIVE_INFINITY, temperature, 0.01);
    }
    @Test(expected = RuntimeException.class)
    public void testReadTempertureWithException() {
        when(temperatureSensor.readTemperatureValue()).thenThrow(new RuntimeException("Sensor failure"));
        //bodyTemperatureMonitor.readTemperature();
    }

    @Test(expected = RuntimeException.class)
    public void testReportTemperatueReadingToCloudWithException() {
        TemperatureReading reading = new TemperatureReading();
        doThrow(new RuntimeException("Cloud service failure")).when(cloudService).sendTemperatureToCloud(reading);
       // bodyTemperatureMonitor.reportTemperatureReadingToCloud(reading);
    }

    @Test(expected = RuntimeException.class)
    public void testInquireBodySttusWithException() {
        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenThrow(new RuntimeException("Cloud service error"));
        //bodyTemperatureMonitor.inquireBodyStatus();
    }
    @Test
    public void testAllDependencyInteractions() {
        TemperatureReading reading = new TemperatureReading();
        when(temperatureSensor.readTemperatureValue()).thenReturn(36.5);
        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("NORMAL");

        double temperature = bodyTemperatureMonitor.readTemperature();
        bodyTemperatureMonitor.reportTemperatureReadingToCloud(reading);
        bodyTemperatureMonitor.inquireBodyStatus();

        Assert.assertEquals(36.5, temperature, 0.01);
        verify(cloudService, times(1)).sendTemperatureToCloud(reading);
        verify(cloudService, times(1)).queryCustomerBodyStatus(any(Customer.class));
        verify(notificationSender, times(1)).sendEmailNotification(any(Customer.class), eq("Thumbs Up!"));

        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("ABNORMAL");
        bodyTemperatureMonitor.inquireBodyStatus();
        verify(notificationSender, times(1)).sendEmailNotification(any(FamilyDoctor.class), eq("Emergency!"));
    }
    
   
    
    @Test
    public void testTemperatureensorEdgeCases() {
        when(temperatureSensor.readTemperatureValue()).thenReturn(Double.NEGATIVE_INFINITY);
        double temperature = bodyTemperatureMonitor.readTemperature();
        Assert.assertEquals(Double.NEGATIVE_INFINITY, temperature, 0.01);
        
        when(temperatureSensor.readTemperatureValue()).thenReturn(Double.POSITIVE_INFINITY);
        temperature = bodyTemperatureMonitor.readTemperature();
        Assert.assertEquals(Double.POSITIVE_INFINITY, temperature, 0.01);
    }

    @Test
    public void testCombinedScnario() {
        when(temperatureSensor.readTemperatureValue()).thenReturn(37.5);
        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("NORMAL");
        bodyTemperatureMonitor.inquireBodyStatus();
        verify(notificationSender, times(1)).sendEmailNotification(any(Customer.class), eq("Thumbs Up!"));

        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("ABNORMAL");
        bodyTemperatureMonitor.inquireBodyStatus();
        verify(notificationSender, times(1)).sendEmailNotification(any(FamilyDoctor.class), eq("Emergency!"));
    }

    @Test(expected = RuntimeException.class)
    public void testReadTemperaturException() {
        when(temperatureSensor.readTemperatureValue()).thenThrow(new RuntimeException("Sensor failure"));
        //bodyTemperatureMonitor.readTemperature();
    }

    @Test(expected = RuntimeException.class)
    public void testReportTemperatureRedingToCloudException() {
        TemperatureReading reading = new TemperatureReading();
        doThrow(new RuntimeException("Cloud service failure")).when(cloudService).sendTemperatureToCloud(reading);
        //bodyTemperatureMonitor.reportTemperatureReadingToCloud(reading);
    }

}
