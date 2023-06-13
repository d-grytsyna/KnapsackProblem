package java4;

public class F {
    private int value;
    private int weight;
    private int index;
    public F(int value, int index, int weight){
        this.value = value;
        this.index = index;
        this.weight = weight;
    }
    public void setIndex(int index){
        this.index = index;
    }
    public void setValue(int value){
        this.value = value;
    }
    public int getValue(){return value;}
    public int getIndex() {return index;}
    public int getWeight() {return weight;}
    public void setWeight(int weight){this.weight = weight;}
}
