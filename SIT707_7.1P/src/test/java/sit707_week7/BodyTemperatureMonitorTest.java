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
    public void testInquireBodyStatusAbnormalNotification() {
        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("ABNORMAL");
        bodyTemperatureMonitor.inquireBodyStatus();
        verify(notificationSender, times(1)).sendEmailNotification(any(FamilyDoctor.class), eq("Emergency!"));
    }
}
