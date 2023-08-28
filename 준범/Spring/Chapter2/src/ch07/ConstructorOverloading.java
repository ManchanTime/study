package ch07;

public class ConstructorOverloading {

    public String userId;
    public String userPassword;
    public String userAddress;
    public String userName;
    public String phoneNumber;

    public ConstructorOverloading(){}

    public ConstructorOverloading(String userId, String userPassword, String userName)
    {
        this.userId = userId;
        this.userPassword = userPassword;
        this.userName = userName;

    }

    public String showUserName() {
        return "고객님의 아이디는" + userId + "이고, 등록된 이름은 " + userName + "입니다.";
    }
}
