package java4;

public class Item {
    private int weight;
    private int cost;
    public Item(int weight, int cost){
        this.weight = weight;
        this.cost = cost;

    }
    public void setWeight(int weight){this.weight = weight;}
    public void setCost(int cost){
        this.cost = cost;
    }
    public int getWeight(){
        return weight;
    }
    public int getCost(){
        return cost;
    }
}
