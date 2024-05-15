package sit707_week7.sample;


public class StudentReader {

	// Database service for Student info
	private StudentRepository studentReposiroty;
	
	// Email service, external communication
	private EmailSender emailSender;
	

	public String findFullName(Long studentID) {
		Student user = studentReposiroty.findByID(studentID);
		return user.getFirstName() + " " + user.getLastName();
	}
	
	public Long createNew(Student student) {
		return studentReposiroty.save(student);		
	}
	
	public void notifyStudent(Student student) {
		emailSender.sendEmail(student);
	}


	public void setStudentReposiroty(StudentRepository studentReposiroty) {
		this.studentReposiroty = studentReposiroty;
	}

	public void setEmailSender(EmailSender emailSender) {
		this.emailSender = emailSender;
	}
	
	
}
