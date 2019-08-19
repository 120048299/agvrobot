package com.wootion.agvrobot.utils;

import com.alibaba.fastjson.JSON;
import com.wootion.model.Resource;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 支持排序
 * T 对象如果要排序，T类要实现compareTo方法，参照Resource类的实现。
 * 上级节点包含最后一层的values 由业务实现。
 * @param <T>
 */
public class CommonTree<T> implements Comparable<CommonTree<T>>{
    private String id;
    private String parentId;
    private String text;
    private String code;
    private T  data;

    public CommonTree() {
        super();
    }

    private List<CommonTree<T>> children = new ArrayList<>();

    private List values =new ArrayList<String>(); //所有下级id

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<CommonTree<T>> getChildren() {
        return children;
    }

    public CommonTree getChild(String id){
        for (CommonTree child:children){
            if(child.getId()==null){
                System.out.println("child id is null");
            }
            if(child.getId().equals(id))
            {
                return child;
            }
        }
        return null;
    }

    public void setChildren(List<CommonTree<T>> children) {
        this.children = children;
    }

    public void addChild(CommonTree child) {
        this.children.add(child);
    }

    public List getValues() {
        return values;
    }

    public void setValues(List values) {
        this.values = values;
    }

    public void addValue(String value) {
        this.values.add(value);
    }


    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public CommonTree(T data){
        super();
        String id=(String)getValueByKey(data,"uid");
        if(id == null || "".equals(id) ){
            id=(String)getValueByKey(data,"id");
        }
        this.id=id;
        String parentId=(String)getValueByKey(data,"parentId");
        this.parentId=parentId;
        Object text=getValueByKey(data,"text");
        if(text==null){
            text=getValueByKey(data,"name");
        }
        if(text!=null){
            this.text= (String)text;
        }
        this.code=(String)getValueByKey(data,"code");

        //this.addValue(id);
        this.data = data;
    }

    public int compareTo(CommonTree<T> other){
        try{
            Method m3 = data.getClass().getMethod("compareTo", data.getClass());
            Object ooo = m3.invoke(data, other.getData());
            return (int)ooo;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }



    /**
     * 用列表构造菜单,列表数据 T 中具备 id或者uid, parentId, text或者name ,orderNum字段。
     * @param list
     */
    public void addList(List<T> list) {
        if(list==null){
            return;
        }

        List<CommonTree> waitList=new ArrayList<>();

        for(T data:list){
            CommonTree newTree= new CommonTree(data);
            find=null;
            findParent(this,newTree.getParentId());
            if(find!=null){
                find.addChild(newTree);
            }else{
                waitList.add(newTree);
            }
        }
        int n=0;
        while (waitList.size()>0 && n<10){
            Iterator<CommonTree> it = waitList.iterator();
            while(it.hasNext()){
                CommonTree item=it.next();
                find=null;
                findParent(this,item.getParentId());
                if(find!=null){
                    find.addChild(item);
                    it.remove();
                }
            }
            n++;
        }
        //重新排序
        resort(this);

        resetValues(this);


    }

    /**
     * 添加一个节点
     * @param newTree
     */
    public void addTreeNode(CommonTree newTree) {
        find=null;
        findParent(this,newTree.id);
        if(find!=null){
            find.addChild(newTree);
        }else{
            this.addChild(newTree);
        }
    }

    CommonTree<T> find =null;
    private void findParent(CommonTree<T> tree,String id){
        if(tree.getId().equals(id)){
            find = tree;
            return;
        }
        if(tree.children==null){
            return;
        }
        for(CommonTree<T> child : tree.children){
            findParent(child,id);
        }
        return ;
    }


   /* public void resetValues(CommonTree<T> tree){
        if(tree.children==null){
            return;
        }
        if(tree.children!=null && tree.children.size()>1){
            Collections.sort(tree.children);
        }
        for (int i=0;i<tree.children.size();i++){
            CommonTree<T> child = tree.children.get(i);
            resort(child);
        }
    }
    */
    /**
     * 重设values值
     * 节点的values: 所有最底层的节点的id构成的list
     */
    public ArrayList<String> resetValues(CommonTree<T> tree){
        if(tree.children.size()==0){
            ArrayList<String> list=new ArrayList<>();
            list.add(tree.id);
            tree.setValues(list);
            return list;
        }else {
            ArrayList<String> allSubValues=new ArrayList();
            for (int i=0;i<tree.children.size();i++){
                CommonTree<T> child = tree.children.get(i);
                List values= resetValues(child);
                allSubValues.addAll(values);
            }
            tree.setValues(allSubValues);
            return allSubValues;
        }
    }

    public void resort(CommonTree<T> tree){
        if(tree.children==null){
            return;
        }
        if(tree.children!=null && tree.children.size()>1){
            Collections.sort(tree.children);
        }
        for (int i=0;i<tree.children.size();i++){
            CommonTree<T> child = tree.children.get(i);
            resort(child);
        }
    }


    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }


    /**
     * 单个对象的某个键的值
     * @return Object 键在对象中所对应得值 没有查到时返回空字符串
     */
    public static Object getValueByKey(Object obj, String key) {
        // 得到类对象
        Class userCla = (Class) obj.getClass();
        /* 得到类中的所有属性集合 */
        Field[] fs = userCla.getDeclaredFields();
        for (int i = 0; i < fs.length; i++) {
            Field f = fs[i];
            f.setAccessible(true); // 设置些属性是可以访问的
            try {

                if (f.getName().endsWith(key)) {
                    return f.get(obj);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        // 没有查到时返回空字符串
        return null;
    }


    public static void main (String args[]){
        List<Resource> list=new ArrayList<>();
        Resource r=new Resource();
        r.setUid("root");
        r.setName("主菜单");
        CommonTree<Resource> tree = new CommonTree<>(r);

        r=new Resource();
        r.setParentId("root");
        r.setUid("11");
        r.setName("子菜单1");
        r.setOrderNum(5);
        list.add(r);

        r=new Resource();
        r.setParentId("root");
        r.setUid("12");
        r.setName("子菜单2");
        r.setOrderNum(8);
        list.add(r);


        /*r=new Resource();
        r.setParentId("12");
        r.setUid("121");
        r.setName("子菜单21");
        r.setOrdernum(3);
        list.add(r);
*/

        tree.addList(list);
        tree.resort(tree);
        System.out.println(tree);

    }
}
