package sit707_week7;

public interface NotificationSender {

	/**
	 * Notify customer him/herself of a message.
	 * @param customer
	 * @param msg
	 */
	public void sendEmailNotification(Customer customer, String msg);
	
	/**
	 * Notify family physician alert message.
	 * @param familyDoctor
	 * @param msg
	 */
	public void sendEmailNotification(FamilyDoctor familyDoctor, String msg);
}
