package sit707_week7.sample;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import sit707_week7.sample.EmailSender;
import sit707_week7.sample.StudentReader;
import sit707_week7.sample.StudentRepository;


public class StudentReaderTest {

	@Test
	public void testFindStudent() {
		Student sampleStudent = new Student();
		sampleStudent.setFirstName("Ishini");
		sampleStudent.setLastName("Bhagya");

		StudentRepository studentRepository = Mockito.mock(StudentRepository.class);
		Mockito.when(studentRepository.findByID(1L)).thenReturn(sampleStudent);

		StudentReader studentReader = new StudentReader();
		studentReader.setStudentReposiroty(studentRepository);

		String fullName = studentReader.findFullName(1L);
		Assert.assertEquals("Ishini Bhagya", fullName);
	}
	
	
	@Test
	public void testStudentRepoSave() {
		Student sampleStudent = new Student();
		sampleStudent.setFirstName("Ishini");
		sampleStudent.setLastName("Bhagya");
		
		StudentRepository studentRepository = Mockito.mock(StudentRepository.class);
		Mockito.when(studentRepository.save(sampleStudent)).thenReturn(2L);
		
		StudentReader studentReader = new StudentReader();
		studentReader.setStudentReposiroty(studentRepository);
		
		//Mockito.verify(studentRepository).save(sampleStudent);
		Long savedID = studentReader.createNew(sampleStudent);
		Assert.assertEquals(Long.valueOf(2), savedID);
	}
	
	
	@Test
	public void testEmailSend() {
		Student sampleStudent = new Student();
		sampleStudent.setFirstName("Ishini");
		sampleStudent.setLastName("Bhagya");
		
		// EmailSender emailSender = new EmailSender();
		EmailSender emailSender = Mockito.mock(EmailSender.class);
		
		StudentReader studentReader = new StudentReader();
		studentReader.setEmailSender(emailSender);
		studentReader.notifyStudent(sampleStudent);
		
		// Test if emailSender.sendEmail is called once.
		//
		Mockito.verify(emailSender).sendEmail(sampleStudent);		
	}
	

	@Test
	public void testEmailNoSend() {
		Student sampleStudent = new Student();
		sampleStudent.setFirstName("Ishini");
		sampleStudent.setLastName("Bhagya");
		
		/*
		 * Real emailSender
		 */
		//EmailSender emailSender = new EmailSender();
		
		/*
		 * Mocked emailSender
		 */
		EmailSender emailSender = Mockito.mock(EmailSender.class);
		
		StudentReader studentReader = new StudentReader();
		studentReader.setEmailSender(emailSender);
		/*
		 * notifyStudent is commented, so emailSender.sendEmail never invoked in studentReader
		 */
		//studentReader.notifyStudent(sampleStudent);
		
		/*
		 * Test if emailSender.sendEmail is never called.
		 */
		Mockito.verify(emailSender, Mockito.times(0)).sendEmail(sampleStudent);
	}
}
