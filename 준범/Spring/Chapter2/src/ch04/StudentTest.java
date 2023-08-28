package ch04;

public class StudentTest {

    public static void main(String[] args)
    {
        Student studentLee = new Student();

        studentLee.studentId = 12345;
        studentLee.setStudent("Lee");
        studentLee.address = "서울 강남구";

        studentLee.showStudentInfo();
        Student studentKim = new Student();
        studentKim.studentId = 54321;
        studentKim.studentName = "Kim";
        studentKim.address = "경기도 성남시";

        studentKim.showStudentInfo();
        System.out.println(studentLee);
        System.out.println(studentKim);

    }
}
