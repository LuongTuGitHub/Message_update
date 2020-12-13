package application.tool.activity.message.object;

public class Person extends Profile {
    private String email;
    public Person(){
    }
    public Person(String name,String phone,String day,String address,String email){
        super(name,phone,day,address);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
