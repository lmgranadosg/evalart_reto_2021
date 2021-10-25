package evalart_reto;


public class filtros {
    String code;
    int TC_type, UG_location, company, encrypt, male;
    float total_balance;

    public filtros(String code, int company, int encrypt, int male ,int TC_type, int UG_location, float total_balance){
        this.code = code;
        this.company = company;
        this.encrypt = encrypt;
        this.male = male;
        this.TC_type = TC_type;
        this.UG_location = UG_location;
        this.total_balance = total_balance;
    }

    public String getcode(){
        return code;
    }

    public int getcompany(){
        return company;
    }

    public int getencrypt(){
        return encrypt;
    }

    public int getmale(){
        return male;
    }

    public int getTC_type(){
        return TC_type;
    }

    public int getUG_location(){
        return UG_location;
    }

    public float gettotal_balance(){
        return total_balance;
    }

    public void setcode(String code){
        this.code = code;
    }

    public void setcompany(int company){
        this.company = company;
    }

    public void setencrypt(int encrypt){
        this.encrypt = encrypt;
    }

    public void setmale(int male){
        this.male = male;
    }

    public void setTC_type(int TC_type){
        this.TC_type=TC_type;
    }

    public void setUG_location(int UG_location){
        this.UG_location=UG_location;
    }

    public void settotal_balance(float total_balance){
        this.total_balance=total_balance;
    }

}
