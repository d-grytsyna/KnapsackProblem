package java4;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    private static ArrayList<ArrayList<Item>> Population = new ArrayList<>();
    private static final int backpack = 200;
    private static final int itemsAmount = 100;
    private static final int personsAmount = 100;
    private static F F = new F(Integer.MAX_VALUE,0, 0);
    private static int MutationCounter = 0;
    private static F FMAX = new F(Integer.MIN_VALUE, 0, 0);
    public static void main(String[] args)  throws IOException {
         randomPopulation();
         printPopulation();
         BufferedWriter FMAXwriter =  new BufferedWriter(new FileWriter("src/java4/FMAX.txt"));
         for(int i=0; i<1000; i++){
             if((i+1)%20==0){
                 getMaxF();
                 FMAXwriter.append("iteration " + (i+1) + " max function value: " + FMAX.getValue() + " weight " + FMAX.getWeight() + "\n");
             }
         geneticAlgorithm();
         }
        FMAXwriter.close();
        System.out.println(MutationCounter);
         maxWeightValue();
         printPopulation();
        System.out.println("population weights");
        for(int i=0; i<personsAmount; i++){
            System.out.println(checkWeight(Population.get(i)));
        }
    }
    public static void randomPopulation(){
      for(int i=0;i<personsAmount;i++){
          ArrayList<Item> Person = new ArrayList<>();
          Item randomItem = createItem();
          for(int j=0; j<itemsAmount; j++){
              if(j!=i) {
                  Item emptyItem = new Item(0,0);
                  Person.add(j, emptyItem);
              }else {
                  Person.add(j, randomItem);
              }
          }
          Population.add(i, Person);
      }
    }
    public static Item createItem(){
       Item item = new Item(ThreadLocalRandom.current().nextInt(1, 11), ThreadLocalRandom.current().nextInt(2, 21));
       return item;
    }
    public static void printPopulation(){
        System.out.println();
        for(int i=0; i<personsAmount; i++){
          String line = "Person " + Integer.toString(i) + " | ";
          for(int j=0; j<itemsAmount; j++){
              line += Population.get(i).get(j).getCost() + " ";
          }
          System.out.println(line);
      }
    }
    public static void geneticAlgorithm(){
         int[] parents = getParents();
         System.out.println();
         System.out.println("Parents at indexes " + Integer.toString(parents[0]) + " " + Integer.toString(parents[1]));
         printPerson(Population.get(parents[0]), "Parent 1");
         printPerson(Population.get(parents[1]), "Parent 2");
         ArrayList<Item> Child = crossing(Population.get(parents[0]), Population.get(parents[1]));
         printPerson(Child, "Child");
         getMinF();
         if(checkWeight(Child)<=backpack){
             ArrayList<Item> ChildCopy = new ArrayList<>(Child.size());
             ChildCopy.addAll(Child);
         int mutate =ThreadLocalRandom.current().nextInt(1, 11);
         if(mutate==10){
             System.out.println("Mutation");
             ArrayList<Item> ChildMutate = Mutation(Child);
             if(checkWeight(ChildMutate)<=backpack){
                 Child = ChildMutate;
             }else{
                 System.out.println("Mutation unsuccessful weight: " + checkWeight(ChildMutate));
             }
        }
         ArrayList<Item> ChildImprove = localImprovement(Child);
             if(checkWeight(ChildImprove)<=backpack){
                 Child = ChildImprove;
             }else{
                 System.out.println("Improvement unsuccessful weight: " + checkWeight(ChildImprove));
             }
             System.out.println("Set a child instead of min function");
             System.out.println("New population");
             getMinF();
             if(checkWeight(Child)<=200){
                 Population.set(F.getIndex(), Child);
             }else Population.set(F.getIndex(), ChildCopy);
         }else{

             System.out.println("Child is not fitting in backpack with weight: " + checkWeight(Child));
         }
        System.out.println("Min function at index " + Integer.toString(F.getIndex()) + " with value " + Integer.toString(F.getValue()) + " weight " + F.getWeight());
    }
    public static void printPerson(ArrayList<Item> Person, String name){
        String line = name + " | " ;
        for(int i=0; i<itemsAmount; i++){
            line += Integer.toString(Person.get(i).getCost()) + " ";
        }
        System.out.println(line);
    }
    public static ArrayList<Item> crossing(ArrayList<Item> P1,ArrayList<Item> P2){
        ArrayList<Item> Child = new ArrayList<>();
        for(int i=0; i<itemsAmount; i++){
            if(i<32||i>65){
                Item childItem = new Item(P1.get(i).getWeight(), P1.get(i).getCost());
                Child.add(i, childItem);
            }else{
                Item childItem = new Item(P2.get(i).getWeight(), P2.get(i).getCost());
                Child.add(i, childItem);
            }
        }
        return Child;
    }
    public static ArrayList<Item> Mutation(ArrayList<Item> Child){
        MutationCounter++;
       int randomIndex = ThreadLocalRandom.current().nextInt(0, 100);
       Item mutationItem = Child.get(randomIndex);
       System.out.println("Item at index " + Integer.toString(randomIndex) + " before mutation: cost " + mutationItem.getCost() + " weight: " + mutationItem.getWeight());
       if(mutationItem.getCost()==0) mutationItem = createItem();
       else mutationItem = new Item(0,0);
       Child.set(randomIndex, mutationItem);
       System.out.println("Item at index " + Integer.toString(randomIndex) + " after mutation: cost " + mutationItem.getCost() + " weight: " + mutationItem.getWeight());
       return Child;
    }
    public static void getMinF(){
        F.setValue(Integer.MAX_VALUE);
        F.setIndex(0);
        F.setWeight(0);
        for(int i=0; i<personsAmount; i++){
            int value = 0;
            int weight = 0;
                for(Item personItem:Population.get(i)){
                    value += personItem.getCost();
                    weight += personItem.getWeight();
                }

            if(value<F.getValue()){
                F.setIndex(i);
                F.setValue(value);
                F.setWeight(weight);
            }
        }
    }
    public static void getMaxF(){
        FMAX.setValue(Integer.MIN_VALUE);
        FMAX.setIndex(0);
        FMAX.setWeight(0);
        for(int i=0; i<personsAmount; i++){
            int value = 0;
            int weight = 0;
            for(Item personItem:Population.get(i)){
                value += personItem.getCost();
                weight += personItem.getWeight();
            }

            if(value>FMAX.getValue()){
                FMAX.setIndex(i);
                FMAX.setValue(value);
                FMAX.setWeight(weight);
            }
        }
    }
    public static int[] getParents(){
        int maxValue = 0;
        int maxIndex = 0;
        for(int i=0; i<personsAmount; i++){
            int currentValue = 0;
            for(Item personItem:Population.get(i)){
                currentValue += personItem.getCost();
            }
            if(currentValue>maxValue){
                maxValue = currentValue;
                maxIndex = i;
            }
        }
        int randomIndex = maxIndex;
        while (randomIndex==maxIndex) randomIndex = ThreadLocalRandom.current().nextInt(0, 100);
        int[] parents = {maxIndex, randomIndex};
        return parents;
    }
    public static void maxWeightValue(){
        int maxIndexW = 0;
        int maxWeight = 0;
        int valueM = 0;
        int maxIndexV = 0;
        int weightV = 0;
        int maxValue = 0;
        for(int i=0; i<personsAmount; i++){
            int value = 0;
            int weight = 0;
            for(Item personItem:Population.get(i)){
                value += personItem.getCost();
                weight += personItem.getWeight();
            }
            if(weight>maxWeight){
                maxWeight = weight;
                valueM = value;
                maxIndexW = i;
            }
            if(value>maxValue){
                weightV = weight;
                maxValue = value;
                maxIndexV = i;
            }
        }
        System.out.println("Max weight at index " + maxIndexW + " weight " + maxWeight + " value " + valueM);
        System.out.println("Max value at index " + maxIndexV + " weight " + weightV + " value " + maxValue);
    }
    public static ArrayList<Item> localImprovement(ArrayList<Item> Child){
        int valueChild = 0;
        int valueNeighbour = 0;
        int indexNeighbour = 0;
        for(int i=0; i<itemsAmount; i++){
          valueChild += Child.get(i).getCost();
        }
        for(int i=0; i<personsAmount; i++){
            int valueNeighbourX = 0;
            for(int j=0; j<itemsAmount; j++){
                valueNeighbourX += Population.get(i).get(j).getCost();
            }
            if((valueNeighbourX>=valueChild+1)&&valueNeighbourX<valueChild+6){
                valueNeighbour = valueNeighbourX;
                indexNeighbour = i;
                break;
            }
        }
        if(valueNeighbour!=0){
        System.out.println("Child value " + valueChild + " neighbour value " + valueNeighbour + " neighbour index " + indexNeighbour);
        ArrayList<Item> ChildN = new ArrayList<>();
        ArrayList<Item> ChildN2 = new ArrayList<>();
        for(int i=0; i<itemsAmount; i++){
            if(i<50){
                Item childItem = new Item(Child.get(i).getWeight(), Child.get(i).getCost());
                ChildN.add(i, childItem);
                childItem = new Item(Population.get(indexNeighbour).get(i).getWeight(), Population.get(indexNeighbour).get(i).getCost());
                ChildN2.add(i, childItem);
            }else{
                Item childItem = new Item(Population.get(indexNeighbour).get(i).getWeight(), Population.get(indexNeighbour).get(i).getCost());
                ChildN.add(i, childItem);
                childItem = new Item(Child.get(i).getWeight(), Child.get(i).getCost());
                ChildN2.add(i, childItem);
            }
        }
        int value1 = 0;
        int value2 = 0;
        for(int i=0; i<itemsAmount; i++){
            value1 += ChildN.get(i).getCost();
            value2 += ChildN2.get(i).getCost();
        }
        String line1 = " CHILD 1N ";
        String line2 = " CHILD 2N ";
        for(int i=0; i<itemsAmount; i++){
            line1+= ("(" + ChildN.get(i).getWeight() + " " + ChildN.get(i).getCost() + ")");
            line2+= ("(" + ChildN2.get(i).getWeight() + " " + ChildN2.get(i).getCost() + ")");
        }
        System.out.println(line1);
        System.out.println(line2);
        if(value2>value1) {
            value1 = value2;
            ChildN = ChildN2;
        }
            System.out.println("ChildN value " + value1);
        return ChildN;

        }else return Child;

    }
    public static int checkWeight(ArrayList<Item> Person){
        int weight = 0;
        for(int i=0; i<itemsAmount; i++){
            weight += Person.get(i).getWeight();
        }
        return weight;
    }
}
