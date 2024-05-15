package sit707_week7;


public class BodyTemperatureMonitor {
	
	// Dependency
	private TemperatureSensor temperatureSensor;
	
	// Dependency
	private NotificationSender notificationSender;
	
	// Dependency
	private CloudService cloudService;
	
	// Assume a fixed user where this mobile application is running.
	private Customer fixedCustomer = new Customer();
	
	// Assume a pre-configured family doctor instance.
	private FamilyDoctor familyDoctor = new FamilyDoctor();
	
	// Create instance by passing 3 dependencies.
	//
	public BodyTemperatureMonitor(
			TemperatureSensor temperatureSensor, 
			CloudService cloudService,
			NotificationSender notificationSender) {
		this.temperatureSensor = temperatureSensor;
		this.cloudService = cloudService;
		this.notificationSender = notificationSender;
		
	}
	
	/**
	 * Record sensor reading.
	 * @return
	 */
	public double readTemperature() {
		return temperatureSensor.readTemperatureValue();
	}
	
	/**
	 * Send sensor reading to the cloud service for synchronisation.
	 * @param temperatureReading
	 */
	public void reportTemperatureReadingToCloud(TemperatureReading temperatureReading) {		
		cloudService.sendTemperatureToCloud(temperatureReading);
	}
	
	/**
	 * Inquire body status from remote server - either Normal or Abnormal will be returned. 
	 * Send notification accordingly.
	 */
	public void inquireBodyStatus() {
		String status = cloudService.queryCustomerBodyStatus(fixedCustomer);
		if (status.equalsIgnoreCase("NORMAL")) {
			// notify user his/her body is normal
			notificationSender.sendEmailNotification(fixedCustomer, "Thumbs Up!");
			
		} else {  // Abnormal temperature
			notificationSender.sendEmailNotification(familyDoctor, "Emergency!");
		}
	}

	public Customer getFixedCustomer() {
		return fixedCustomer;
	}

	public FamilyDoctor getFamilyDoctor() {
		return familyDoctor;
	}
	
	
}
