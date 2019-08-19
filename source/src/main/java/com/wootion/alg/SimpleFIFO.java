package com.wootion.alg;

/**
 * 简易数组实现FIFO
 */
public class SimpleFIFO {
    private int length=10;
    private Object[] array;

    public SimpleFIFO(int length) {
        this.length = length;
        array=new Object[length];
        for(int i=0;i<length;i++){
            array[i]=null;
        }
    }

    public void put(Object obj){
        for (int i=0;i<length-1;i++){
            array[i]=array[i+1];
        }
        array[length-1]=obj;
    }

    public Object getFirst(){
        return array[0];
    }

    public Object getLast(){
        return array[length-1];
    }

    public String toString(){
        String s="SimpleFIFO:{";
        for (int i=0;i<length;i++){
            if(array[i]!=null)
            s+=array[i].toString();
        }
        s+="}";
        return s;
    }

    public static void main(String args[]){
        SimpleFIFO simpleFIFO=new SimpleFIFO(5);
        System.out.println(simpleFIFO.getFirst());
        simpleFIFO.put("a");
        simpleFIFO.put("b");
        simpleFIFO.put("c");
        simpleFIFO.put("d");
        simpleFIFO.put("e");
        simpleFIFO.put("f");
        simpleFIFO.put("g");
        System.out.println(simpleFIFO);
    }
}
