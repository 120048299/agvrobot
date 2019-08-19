package com.wootion.agvrobot.utils;


        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;

        import com.alibaba.fastjson.JSON;

/**
 *  不再使用 。
 *  统一用CommonTree代替
 */
public class Tree<T> {
    /**
     * 节点ID
     */
    private String id;
    /**
     * 显示节点文本
     */
    private String text;

    /**
     * 节点类型 ：显示业务节点类型，在构造节点时业务决定这个字段的内容
     */
    private String nodeType;

    private List values =new ArrayList<String>();//包含所有下级的ptzsetId

    /**
     * 节点属性
     */
    private Map<String, Object> attributes=new HashMap<>();
    /**
     * 节点的子节点
     */
    private List<Tree<T>> children = new ArrayList<Tree<T>>();

    /**
     * 父ID
     */
    private String parentId;

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

    public Map<String, Object> getAttributes() {
        return attributes;
    }
    public Object getAttribute(String key) {
        return attributes.get(key);
    }
    public void setAttribute(String key,Object obj) {
        attributes.put(key,obj);
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public List<Tree<T>> getChildren() {
        return children;
    }

    public Tree<T> getChild(String id){
        for (Tree<T> child:children){
            if(child.getId().equals(id))
            {
                return child;
            }
        }
        return null;
    }

    public void setChildren(List<Tree<T>> children) {
        this.children = children;
    }

    public void addChild(Tree<T> child) {
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

    public Tree(String id, String text, String state, boolean checked,
                Map<String, Object> attributes, List<Tree<T>> children,
                boolean isParent, boolean isChildren, String parentID) {
        super();
        this.id = id;
        this.text = text;
        this.attributes = attributes;
        this.children = children;
        this.parentId = parentID;
    }

    public Tree() {
        super();
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    @Override
    public String toString() {

        return JSON.toJSONString(this);
    }

}
