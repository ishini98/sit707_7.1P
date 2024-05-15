package sit707_week7.sample;

public interface StudentRepository {

	public Student findByID(Long id);
	
	public Long save(Student student);
	
}
