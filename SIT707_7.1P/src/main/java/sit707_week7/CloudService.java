package sit707_week7;

public interface CloudService {

	/**
	 * Report temperature value to server.
	 * @param temperatureReading
	 */
	public void sendTemperatureToCloud(TemperatureReading temperatureReading);
	
	/**
	 * Inquire customer body status based on temperature.
	 * @param customer
	 * @return Status string - NORMAL, ABNORMAL
	 */
	public String queryCustomerBodyStatus(Customer customer);
}
