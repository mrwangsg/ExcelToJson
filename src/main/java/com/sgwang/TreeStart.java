package com.sgwang;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @创建人 sgwang
 * @name TreeStart
 * @user 91119
 * @创建时间 2019/6/18
 * @描述
 */
public class TreeStart {
    private static Node rootNode = new Node("root", "obj");

    public static void main(String[] args) throws Exception {
        String[] initStr = new String[]{
                "userName", "passWord",
                "innerDemo.innerName", "innerDemo.innerPass",
                "outDemo.outName", "outDemo.outPass",
                "listDemo[0].list.name","listDemo[0].list.pass",
                "listDemo[1].list.name","listDemo[1].list.pass",
        };
        JSONObject rootNodeJson = new JSONObject();

        handlerTask(initStr);

        levelIteratorTree(TreeStart.rootNode, rootNodeJson, null);

        System.out.println(rootNodeJson.toJSONString());

//        test();
    }

    public static void test() {
        String test01 = "test[123]test";
        System.out.println(test01.substring(test01.indexOf("["), test01.indexOf("]") + 1));
        List<Node> list = new ArrayList<Node>();
        list.add(new Node("node01", "arr"));
        list.add(new Node("node02", "arr"));

        System.out.println(JSON.toJSONString(list));

        System.out.println(JSON.toJSONString(new Integer[]{1, 1, 1, 5}));


        Iterator<Node> iterator = rootNode.getChildrenNode().iterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            System.out.println("nodeName: " + node.getNameNode() + ";   nodeType: " + node.getTypeNode());
        }

        JSONArray jsonArray = new JSONArray();
        JSON json = (JSON) jsonArray;
        System.out.println("json instanceof JSONArray --> " + (json instanceof JSON));

        String test = "user[0].name";
        System.out.println("].  " + test.indexOf("]."));
        System.out.println(".   " + test.indexOf("."));
        String temp = test.substring(test.indexOf("."));
        System.out.println(temp);
        System.out.println(test.replace(temp, ""));

        JSONObject obj = new JSONObject();
        obj.put("name", "test");
        obj.put("nextObj", new JSONObject());
        System.out.println(obj.toJSONString());
    }

    /**
     * @描述 将字符串 转化为 树型结构
     * @参数 String[] initStr
     * @返回值 void
     */
    public static void handlerTask(String[] initStr) {
        Node indexNode = rootNode;

        // 模拟循环 excel的列
        for (String indexStr : initStr) {
            // 切割字符串时 需要递归一下
            buildNodeTree(indexNode, indexStr);
        }
    }

    /**
     * @描述 输入当前Node 和 当前切割的字符串
     * @参数 Node indexNode && String indexStr
     * @返回值 void
     */
    public static void buildNodeTree(Node indexNode, String indexStr) {

        // 存在"." 说明类型为 obj || arr
        if (indexStr.contains(".")) {
            String indexNodeName = "";                  // 当前节点 名称
            String typedeNode = diffType(indexStr);     // 区别节点 类型
            Node nextNode = null;                       // 下一child节点
            String nextStr = "";                        // 下一child字符串

            if (typedeNode == "arr") {
                indexNodeName = indexStr.substring(0, indexStr.indexOf("["));
                nextStr = indexStr.substring(indexStr.indexOf(".") + 1);
            } else if (typedeNode == "obj") {
                indexNodeName = indexStr.substring(0, indexStr.indexOf("."));
                nextStr = indexStr.substring(indexStr.indexOf(".") + 1);
            }

            if (indexNode.isExistChildNode(indexNodeName)) {
                nextNode = indexNode.getChildNodeName(indexNodeName);
            } else {
                // 如果不存在 先新建Node节点插入 再递归子节点
                nextNode = new Node(indexNodeName, typedeNode);
                indexNode.addChildNode(nextNode);
            }

            if (typedeNode == "arr"){
                // 用于记录 特别arr的 数组对象个数
                signIndexArrSize(nextNode, indexStr);
            }

            buildNodeTree(nextNode, nextStr);
        } else {
            // 不存在"." 说明已经是尽头
            if (indexNode.isExistChildNode(indexStr)) {
                // 如果已经存在不做处理
            } else {
                // 如果不存在 新建Node节点 插入
                indexNode.addChildNode(new Node(indexStr, "attr"));
            }
        }

    }

    /**
     * @描述 层级遍历树结构
     * @参数 Node indexNode
     * @返回值 void
     */
    public static void levelIteratorTree(Node indexNode, JSONObject indexJsonObj, JSONArray indexJsonArr) {
        //  这里可以做构建 json的处理
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("nameNode: " + indexNode.getNameNode() + "   typeNode: " + indexNode.getTypeNode());
        System.out.println("indexJsonObj: " + indexJsonObj + "   indexJsonArr: " + indexJsonArr);
        if (indexNode.getTypeNode() == "arr")
            System.out.println("arrSize: " + indexNode.getArrSize().size() +"个siez！");

        List<Node> childrenNode = indexNode.getChildrenNode();
        if (!childrenNode.isEmpty()) {
            Iterator<Node> iterator = childrenNode.iterator();
            while (iterator.hasNext()) {
                Node nextNode = iterator.next();
                String nameNode = nextNode.getNameNode();
                String typeNode = nextNode.getTypeNode();

                if (indexJsonObj != null){
                    if (typeNode == "attr") {
                        indexJsonObj.put(nameNode, typeNode);
                    } else if (typeNode == "obj") {
                        JSONObject nextJsonObject = new JSONObject();

                        indexJsonObj.put(nameNode, nextJsonObject);
                        levelIteratorTree(nextNode, nextJsonObject, null);
                    } else if (typeNode == "arr") {
                        JSONArray nextJsonArray = new JSONArray();

                        indexJsonObj.put(nameNode, nextJsonArray);
                        levelIteratorTree(nextNode, null, nextJsonArray);
                    }
                }else if (indexJsonArr != null){
                    if (typeNode == "attr") {
                        JSONObject nextJsonObject = (JSONObject) indexJsonArr.get(0);

                        nextJsonObject.put(nameNode, typeNode);
                    } else if (typeNode == "obj") {
                        JSONObject nextJsonObject = new JSONObject();

                        indexJsonArr.add(nextJsonObject);
                        levelIteratorTree(nextNode, nextJsonObject, null);
                    } else if (typeNode == "arr") {
                        JSONArray nextJsonArray = new JSONArray();

                        indexJsonArr.add(nextJsonArray);
                        levelIteratorTree(nextNode, null, nextJsonArray);
                    }
                }
            }
        }
    }

    /**
     * @描述 用于记录 特别arr的节点 数组对象个数
     * @参数   Node indexNode, String indexStr
     * @返回值  void
    */
    public static void signIndexArrSize(Node indexNode, String indexStr){
        String signSize = indexStr.substring(indexStr.indexOf("["), indexStr.indexOf("]") + 1);
        Set<String> ret = indexNode.getArrSize();
        ret.add(signSize);
    }

    /**
     * @描述 根据字符串 返回arr | obj | attr
     * @参数 String
     * @返回值 String 备注：arr | obj | attr
     */
    public static String diffType(String indexStr) {

        if (indexStr.contains(".")) {
            // 继续判断 obj | arr
            if (!indexStr.contains("].")) {
                return "obj";
            } else if (indexStr.indexOf(".") < indexStr.indexOf("].")) {
                return "obj";
            } else {
                return "arr";
            }
        } else {
            return "attr";
        }
    }

    // 转obj类型
    public JSONObject toJsonObject() {
        JSONObject temp = new JSONObject();

        return temp;
    }

    // 转arr类型
    public JSONArray toJsonArray() {
        JSONArray temp = new JSONArray();

        return temp;
    }

}
