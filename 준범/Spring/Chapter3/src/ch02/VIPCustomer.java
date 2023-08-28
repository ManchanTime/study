package ch02;

public class VIPCustomer extends Customer{

    double salesRatio;
    private String agentID;

    public String getAgentID() {
        return agentID;
    }

    public void setAgentID(String agentID) {
        this.agentID = agentID;
    }

    public VIPCustomer(int customerID, String customerName) {
        super(customerID, customerName);
        salesRatio = 0.1;
        customerGrade = "VIP";
        bonusRatio = 0.05;
        System.out.println("VIPCustomer(int, String) call");
    }
}
