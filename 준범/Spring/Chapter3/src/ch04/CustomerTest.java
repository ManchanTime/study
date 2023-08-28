package ch04;

public class CustomerTest {
    public static void main(String[] args) {
        Customer customerLee = new Customer(10010,"이순신");
        customerLee.bonusPoint = 1000;
        int price = customerLee.calPrice(1000);
        System.out.println(customerLee.showCustomerInfo()+price);

        VIPCustomer customerKim = new VIPCustomer(10020,"김유신");
        customerKim.bonusPoint = 10000;
        price = customerKim.calPrice(1000);
        System.out.println(customerKim.showCustomerInfo()+price);

        Customer customer = new VIPCustomer(10030,"사용자");
        System.out.println(customer.calPrice(1000));
    }
}
