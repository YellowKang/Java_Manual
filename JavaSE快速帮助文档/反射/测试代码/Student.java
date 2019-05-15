package club.kang.blog.web.test.TestClassMy;

public class Student {
    private String name;
    private int age;
    public String gender;

    public Student() {
    }

    public Student(String name, int age, String gender) {
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                '}';
    }

    public void mySystem(String name){
        System.out.println("你好：" + name);
    }

    public void hello(){
        System.out.println("Hello World!");
    }

    private void helloPrivate(){
        System.out.println("Hello Private!");
    }
}
